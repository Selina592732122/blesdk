package com.shenghao.blesdk.manager;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.shenghao.blesdk.BleConstant;
import com.shenghao.blesdk.command.CommandUtils;
import com.shenghao.blesdk.utils.LogUtils;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class OneKeyParkingManager {

    private static final String TAG = "OneKeyParkingManager";
    private static volatile OneKeyParkingManager instance;
    
    private BleDevice bleDevice;
    private final Object queueLock = new Object();
    private final LinkedBlockingQueue<CommandWrapper> commandQueue = new LinkedBlockingQueue<>(1);
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledTask;
    private volatile boolean isSending = false;

    private static class CommandWrapper {
        byte[] command;
        boolean isHighPriority;
        long timestamp;

        CommandWrapper(byte[] command, boolean isHighPriority) {
            this.command = command;
            this.isHighPriority = isHighPriority;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private OneKeyParkingManager() {
        initCommandScheduler();
    }

    public static OneKeyParkingManager getInstance() {
        if (instance == null) {
            synchronized (OneKeyParkingManager.class) {
                if (instance == null) {
                    instance = new OneKeyParkingManager();
                }
            }
        }
        return instance;
    }

    public void setBleDevice(BleDevice device) {
        this.bleDevice = device;
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    private void initCommandScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduledTask = scheduler.scheduleWithFixedDelay(() -> {
            synchronized (this) {
                if (!isSending && !commandQueue.isEmpty()) {
                    isSending = true;
                    CommandWrapper wrapper = commandQueue.peek();
                    if (wrapper != null && !wrapper.isHighPriority) {
                        wrapper = commandQueue.poll();
                        write(wrapper.command);
                        LogUtils.e("Scheduler", "调度发送普通角度指令");
                    }
                    isSending = false;
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void trySendImmediately() {
        if (!isSending && !commandQueue.isEmpty()) {
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                synchronized (this) {
                    if (!isSending && !commandQueue.isEmpty()) {
                        isSending = true;
                        CommandWrapper wrapper = commandQueue.peek();
                        if (wrapper != null && wrapper.isHighPriority) {
                            wrapper = commandQueue.poll();
                            write(wrapper.command);
                            LogUtils.e("trySendImmediately", "立即发送高优先级指令");
                            commandQueue.clear();
                        }
                        isSending = false;
                    }
                }
            }, 0, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 生成自动泊车指令
     *
     * @param direction         方向：0-停止，1-前进，2-后退
     * @param angle             角度：-32768~32767，实际限制在-25~25
     * @param isManualMode      是否手动模式，界面退出时调用true
     * @param isDirectionStopped 是否停止（左边手抬起）
     * @param needAngle         是否需要角度调节
     */
    public void generateAutoParkingCommand(int direction, int angle, boolean isManualMode, 
                                           boolean isDirectionStopped, boolean needAngle) {
        synchronized (queueLock) {
            if (direction < 0 || direction > 2) {
                throw new IllegalArgumentException("方向参数错误：0-停止，1-前进，2-后退");
            }
            if (angle < -32768 || angle > 32767) {
                throw new IllegalArgumentException("角度超出范围(-32768~32767)");
            }

            if (angle < -25) angle = -25;
            else if (angle > 25) angle = 25;

            String dir = direction == 0 ? "停止" : (direction == 1 ? "前进" : "后退");
            LogUtils.e("generateAutoParkingCommand", dir + "角度：" + angle);

            byte control1 = 0;
            if (direction == 1) {
                control1 |= 0x01;
            } else if (direction == 2) {
                control1 |= 0x02;
            }

            if (needAngle) {
                control1 |= 0x04;
            }

            if (isDirectionStopped) {
                control1 |= 0x08;
            }

            byte control2 = 0;
            byte control3 = 0;
            byte param1 = isManualMode ? (byte) 0x00 : (byte) 0x01;
            byte angleHigh = (byte) ((angle + 1024) >> 8);
            byte angleLow = (byte) ((angle + 1024) & 0xFF);
            byte angularVelocity = 25;

            byte[] body = new byte[]{
                    control1, control2, control3, param1, angleHigh, angleLow, angularVelocity
            };

            byte[] command = CommandUtils.generateCommand((byte) 0x00, (byte) 0x20, body);

            boolean isHighPriority = isDirectionStopped || direction != 0 || isManualMode;
            CommandWrapper wrapper = new CommandWrapper(command, isHighPriority);

            if (isHighPriority) {
                boolean shouldSkip = false;
                for (CommandWrapper existingWrapper : commandQueue) {
                    if (Arrays.equals(existingWrapper.command, command)) {
                        shouldSkip = true;
                        break;
                    }
                }
                if (shouldSkip) {
                    LogUtils.e("generateAutoParkingCommand", "跳过重复指令");
                    return;
                }

                commandQueue.clear();
                commandQueue.offer(wrapper);
                LogUtils.e("generateAutoParkingCommand", "加入高优先级指令到队列");
                trySendImmediately();
            } else {
                if (commandQueue.remainingCapacity() == 0) {
                    commandQueue.poll();
                }
                commandQueue.offer(wrapper);
                LogUtils.e("generateAutoParkingCommand", "加入角度调整指令到队列");
            }
        }
    }

    /**
     * 停止车辆
     */
    public void stop() {
        generateAutoParkingCommand(0, 0, false, true, false);
    }

    /**
     * 前进
     */
    public void forward() {
        generateAutoParkingCommand(1, 0, false, false, false);
    }

    /**
     * 后退
     */
    public void backward() {
        generateAutoParkingCommand(2, 0, false, false, false);
    }

    /**
     * 仅调节角度
     *
     * @param angle 角度值
     */
    public void adjustAngle(int angle) {
        generateAutoParkingCommand(0, angle, false, false, true);
    }

    /**
     * 前进并调节角度
     *
     * @param angle 角度值
     */
    public void forwardWithAngle(int angle) {
        generateAutoParkingCommand(1, angle, false, false, true);
    }

    /**
     * 后退并调节角度
     *
     * @param angle 角度值
     */
    public void backwardWithAngle(int angle) {
        generateAutoParkingCommand(2, angle, false, false, true);
    }

    /**
     * 退出手动模式（界面退出时调用）
     */
    public void exitManualMode() {
        generateAutoParkingCommand(0, 0, true, false, false);
    }

    private void write(byte[] bytes) {
        if (bleDevice == null || !BleManager.getInstance().isConnected(bleDevice)) {
            LogUtils.e(TAG, "蓝牙设备未连接");
            return;
        }
        LogUtils.e(TAG, "发送指令: " + com.shenghao.blesdk.utils.ByteUtils.bytes2HexStr(bytes));
        
        BleManager.getInstance().write(
                bleDevice,
                BleConstant.SERVICE_UUID_SH,
                BleConstant.NOTIFY_UUID_SH,
                bytes,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        LogUtils.e(TAG, "发送成功");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        LogUtils.e(TAG, "发送失败: " + exception.getDescription());
                    }
                }
        );
    }

    /**
     * 释放资源
     */
    public void release() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
        }
        commandQueue.clear();
    }
}