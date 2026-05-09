//package com.shenghao.ui;
//
//import android.content.pm.ActivityInfo;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.clj.fastble.BleManager;
//import com.clj.fastble.callback.BleNotifyCallback;
//import com.clj.fastble.callback.BleWriteCallback;
//import com.clj.fastble.data.BleDevice;
//import com.clj.fastble.exception.BleException;
//import com.shenghao.R;
//import com.shenghao.ShengHaoApp;
//import com.shenghao.ble.BleConstant;
//import com.shenghao.ble.command.CommandUtils;
//import com.shenghao.okhttp.OkHttpBaseResp;
//import com.shenghao.okhttp.OkHttpResultCallBack;
//import com.shenghao.present.OkHttpPresent;
//import com.shenghao.utility.AppSingleton;
//import com.shenghao.utils.ByteUtils;
//import com.shenghao.utils.JsonUtils;
//import com.shenghao.utils.LogUtils;
//import com.shenghao.utils.StatusBarUtils;
//import com.shenghao.utils.ToastUtils;
//import com.shenghao.widget.JoystickView;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.TimeUnit;
//
//import androidx.annotation.Nullable;
//import okhttp3.Request;
//import okhttp3.Response;
//
//import static com.shenghao.widget.JoystickView.DIRECTION_HORIZONTAL;
//import java.util.concurrent.Executors;
//
//public class OneKeyActivity extends BaseActivity {
//    private JoystickView joystickRight, joystickLeft;
//    private ImageView ivCar;
//    private BleDevice bleDevice;
//    private int direction, angle;
//    private boolean isBle = true;
//
//    // 添加指令队列和调度器
//    // 创建指令包装类
//    private static class CommandWrapper {
//        byte[] command;
//        boolean isHighPriority;
//        long timestamp;
//
//        CommandWrapper(byte[] command, boolean isHighPriority) {
//            this.command = command;
//            this.isHighPriority = isHighPriority;
//            this.timestamp = System.currentTimeMillis();
//        }
//    }
//    // 修改队列类型
//    private final LinkedBlockingQueue<CommandWrapper> commandQueue = new LinkedBlockingQueue<>(1);
//    private ScheduledExecutorService scheduler;
//    private ScheduledFuture<?> scheduledTask;
//    private volatile boolean isSending = false;
//
//    // 添加状态跟踪变量
//    private Handler handler = new Handler();
//    private long lastTime;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//        setContentView(R.layout.activity_one_key);
//        StatusBarUtils.setStatusBarColor(this, R.color.transparent);
//        StatusBarUtils.statusBarLightMode(this);
//
//        // 初始化指令发送调度器
//        initCommandScheduler();
//        initViews();
//
//        List<BleDevice> allConnectedDevice = BleManager.getInstance().getAllConnectedDevice();
//        if (!allConnectedDevice.isEmpty()) {
//            bleDevice = allConnectedDevice.get(0);
//            isBle = true;
//            notifyBle();
//            ToastUtils.showShort(this, "当前蓝牙设备：" + bleDevice.getName());
//        } else {
//            ToastUtils.showShort(this, "未连接蓝牙设备,4G模式");
//            oneKey4G(DIRECTION_HORIZONTAL, 0, 0); // 校准0
//        }
//    }
//
//    private void trySendImmediately() {
//        if (!isSending && !commandQueue.isEmpty()) {
//            // 创建一个临时调度器立即执行发送
//            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
//                synchronized (this) {
//                    if (!isSending && !commandQueue.isEmpty()) {
//                        isSending = true;
//                        CommandWrapper wrapper = commandQueue.peek(); // 查看队列头部
//
//                        if (wrapper != null && wrapper.isHighPriority) {
//                            wrapper = commandQueue.poll(); // 移除高优先级指令
//                            write(wrapper.command);
//                            LogUtils.e("trySendImmediately", "立即发送高优先级指令");
//
//                            // 高优先级指令发送后清空队列
//                            commandQueue.clear();
//                        }
//                        isSending = false;
//                    }
//                }
//            }, 0, TimeUnit.MILLISECONDS);
//        }
//    }
//
//    private void initCommandScheduler() {
//        scheduler = Executors.newSingleThreadScheduledExecutor();
//        scheduledTask = scheduler.scheduleWithFixedDelay(() -> {
//            synchronized (this) {
//                if (!isSending && !commandQueue.isEmpty()) {
//                    isSending = true;
//                    CommandWrapper wrapper = commandQueue.peek(); // 查看队列头部
//
//                    // 调度器只处理普通指令，不处理高优先级指令
//                    if (wrapper != null && !wrapper.isHighPriority) {
//                        wrapper = commandQueue.poll(); // 移除普通指令
//                        write(wrapper.command);
//                        LogUtils.e("Scheduler", "调度发送普通角度指令");
//                    }
//                    isSending = false;
//                }
//            }
//        }, 0, 500, TimeUnit.MILLISECONDS);
//    }
//
//    private void notifyBle() {
//        BleManager.getInstance().notify(
//                bleDevice,
//                BleConstant.SERVICE_UUID_SH,
//                BleConstant.NOTIFY_UUID_SH,
//                new BleNotifyCallback() {
//                    @Override
//                    public void onNotifySuccess() {
////                        byte[] body = KeyGenerator.generate14ByteKey();
////                        byte[] bytes = CommandUtils.generateCommand((byte) 0x00, (byte) 0x31, body);
////                        write(bytes);
////                        SPUtils.getInstance().putString(SPUtils.SP_ONE_KEY_PWD, ByteUtils.byteToString(body) + "ffff");
//                        byte[] bytes = BlueToothRssiPkeDialog.generatePKECommand(100, 20);
//                        write(bytes);
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                generateAutoParkingCommand(0);
//                            }
//                        },500);
//                    }
//
//                    @Override
//                    public void onNotifyFailure(BleException exception) {
//                        ToastUtils.showShort(OneKeyActivity.this, "onNotifyFailure:" + exception.getDescription());
//                    }
//
//                    @Override
//                    public void onCharacteristicChanged(byte[] data) {
//                        String str = ByteUtils.byteToString(data);
//                        LogUtils.e("onCharacteristicChanged", str);
//                    }
//                }
//        );
//    }
//
//    /**
//     * 统一的指令生成方法
//     *
//     * @param direction   方向：0-停止，1-前进，2-后退
//     * @param angle       角度：-32768~32767
//     * @param isManualMode      是否恢复手动模式，界面退出时候调用true
//     * @param isDirectionStopped     是否停止（左边手抬起）
//     * @param needAngle 是否需要角度调节
//     */
//    private void generateAutoParkingCommand(int direction, int angle, boolean isManualMode, boolean isDirectionStopped,boolean needAngle) {
//        // 参数验证
//        if (direction < 0 || direction > 2) {
//            throw new IllegalArgumentException("方向参数错误：0-停止，1-前进，2-后退");
//        }
//        if (angle < -32768 || angle > 32767) {
//            throw new IllegalArgumentException("角度超出范围(-32768~32767)");
//        }
//
//        // 限制角度范围
//        if (angle < -25) angle = -25;
//        else if (angle > 25) angle = 25;
//
//        String dir = "";
//        if (direction == 0) {
//            dir = "停止";
//        } else if (direction == 1) {
//            dir = "前进";
//        } else {
//            dir = "后退";
//        }
//        LogUtils.e("generateAutoParkingCommand", dir + "角度：" + angle);
//
//        // 1. 控制位1 (1字节)
//        byte control1 = 0;
//        if (direction == 1) { // 前进
//            control1 |= 0x01; // BIT0 = 1
//        } else if (direction == 2) { // 后退
//            control1 |= 0x02; // BIT1 = 1
//        }
//
//        // 总是启用角度调整
//        if(needAngle)
//            control1 |= 0x04; // BIT2 = 1 (角度调整)
//
//        // 根据参数设置BIT3
//        if (isDirectionStopped) {
//            control1 |= 0x08;
//        }
//
//        // 2. 控制位2和3 (各1字节，备用)
//        byte control2 = 0;
//        byte control3 = 0;
//
//        // 3. 参数1 (1字节)：0x01开始调整 00 停止
//        byte param1 = isManualMode ? (byte) 0x00 : (byte) 0x01;
//
//        // 4. 角度参数 (2字节，大端模式)
//        byte angleHigh = (byte) ((angle + 1024) >> 8);
//        byte angleLow = (byte) ((angle + 1024) & 0xFF);
//
//        // 5. 角速度 (1字节)
//        byte angularVelocity = 25;
//
//        // 构建指令体 (7字节)
//        byte[] body = new byte[]{
//                control1,          // 控制位1
//                control2,          // 控制位2 (备用)
//                control3,          // 控制位3 (备用)
//                param1,            // 参数1 (开始/停止调整)
//                angleHigh,         // 角度高位
//                angleLow,          // 角度低位
//                angularVelocity    // 角速度
//        };
//
//        // 生成完整指令
//        byte[] command = CommandUtils.generateCommand((byte) 0x00, (byte) 0x20, body);
//
//        // 判断指令优先级
//        boolean isHighPriority = isDirectionStopped || direction != 0 || isManualMode;
//
//        // 创建指令包装
//        CommandWrapper wrapper = new CommandWrapper(command, isHighPriority);
//
//        if (isHighPriority) {
//            // 检查队列中是否已有相同指令
//            boolean shouldSkip = false;
//            for (CommandWrapper existingWrapper : commandQueue) {
//                if (Arrays.equals(existingWrapper.command, command)) {
//                    shouldSkip = true;
//                    break;
//                }
//            }
//            if (shouldSkip) {
//                LogUtils.e("generateAutoParkingCommand", "跳过重复指令");
//                return;
//            }
//
//            commandQueue.clear(); // 清空队列
//            commandQueue.offer(wrapper); // 加入高优先级指令
//            LogUtils.e("generateAutoParkingCommand", "加入高优先级指令到队列");
//
//            // 立即尝试发送高优先级指令
//            trySendImmediately();
//        } else {
//            // 普通指令处理
//            if (commandQueue.remainingCapacity() == 0) {
//                commandQueue.poll(); // 移除最旧的指令
//            }
//            commandQueue.offer(wrapper);
//            LogUtils.e("generateAutoParkingCommand", "加入角度调整指令到队列");
//        }
//    }
//
//    // 重载方法 - 默认不停止
//    private void generateAutoParkingCommand(int direction, int angle) {
//        generateAutoParkingCommand(direction, angle, false, false,false);
//    }
//
//    // 重载方法 - 只调节角度
//    private void generateAutoParkingCommand(int angle) {
//        generateAutoParkingCommand(0, angle, false, false,true);
//    }
//
//    // 设置圆心
//    private void updatePivotPoint(float x, float y) {
//        ivCar.setPivotX(x - ivCar.getLeft());
//        ivCar.setPivotY(y - ivCar.getTop());
//    }
//
//    @Override
//    protected void initViews() {
//        super.initViews();
//        ivCar = findViewById(R.id.ivCar);
//        joystickRight = findViewById(R.id.joystickRight);
//        joystickLeft = findViewById(R.id.joystickLeft);
//        View statusBarView = findViewById(R.id.statusBarView);
//        StatusBarUtils.setStatusBarHeight(this, statusBarView);
//
//        joystickRight.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {
//            @Override
//            public void onJoystickMoved(int directionType, int direction, float angle, int level) {
//                ivCar.setRotation(angle);
//                int abs = Math.abs((OneKeyActivity.this.angle - (int) angle));
//                if (abs > 5) { // 差距大于5度时候发
//                    OneKeyActivity.this.angle = (int) angle;
//                    if (isBle) {
//                        generateAutoParkingCommand(OneKeyActivity.this.angle);
//                    } else {
//                        oneKey4G(directionType, OneKeyActivity.this.direction, OneKeyActivity.this.angle);
//                    }
//                }
//            }
//
//            @Override
//            public void onJoystickReleased(int directionType) {
//                ivCar.setRotation(0);
//                OneKeyActivity.this.angle = 0;
//                if (isBle) {
//                    handler.postDelayed(() -> generateAutoParkingCommand(0), 500);
//                }
//            }
//        });
//
//        joystickLeft.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {
//            @Override
//            public void onJoystickMoved(int directionType, int direction, float angle, int level) {
//                if (OneKeyActivity.this.direction != direction) {
////                    LogUtils.e("前进或者后退CommandUtils最终数据：", direction == 1 ? "前进" : "后退" + direction);
//                    OneKeyActivity.this.direction = direction;
//                    if (isBle) {
//                        generateAutoParkingCommand(direction, OneKeyActivity.this.angle);
//                    } else {
//                        oneKey4G(directionType, direction, OneKeyActivity.this.angle);
//                    }
//                }
//            }
//
//            @Override
//            public void onJoystickReleased(int directionType) {
//                direction = 0; // 停止前进后退
//                if (isBle) {
//                    // 立即发送停止指令，不延迟
//                    generateAutoParkingCommand(direction, OneKeyActivity.this.angle, false, true,false);
//                    // 额外保障：500ms后再发送一次停止指令
////                    handler.postDelayed(() -> {
////                        generateAutoParkingCommand(direction, OneKeyActivity.this.angle, false, true);
////                    }, 250);
//                } else {
//                    oneKey4G(directionType, direction, OneKeyActivity.this.angle);
//                }
//            }
//        });
//    }
//
//    @Override
//    protected boolean allowActivityOrientation() {
//        return true; // 可以自己自定义横竖屏
//    }
//
//    private void write(byte[] bytes) {
//        LogUtils.e(TAG, "CommandUtils最终数据发出去的数据："+ByteUtils.bytes2HexStr(bytes));
//        BleManager.getInstance().write(
//                bleDevice,
//                BleConstant.SERVICE_UUID_SH,
//                BleConstant.NOTIFY_UUID_SH,
//                bytes,
//                new BleWriteCallback() {
//                    @Override
//                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
//                        // 发送数据到设备成功
//                    }
//
//                    @Override
//                    public void onWriteFailure(BleException exception) {
//                        ToastUtils.showShort(ShengHaoApp.getInstance(), "onWriteFailure!");
//                    }
//                });
//    }
//
//    private void oneKey4G(int directionType, int direction, int angle) {
//        long currentTime = System.currentTimeMillis();
//        if (currentTime - lastTime < 1 * 1000) {
//            return;
//        } else {
//            lastTime = currentTime;
//        }
//
//        String type = "23";
//        if (directionType == DIRECTION_HORIZONTAL) {
//            type = "24";
//        } else {
//            if (direction == 0) {
//                return;
//            }
//        }
//
//        if (angle < -25) angle = -25;
//        else if (angle > 25) angle = 25;
//
//        LogUtils.e("oneKey4G", type + "," + String.valueOf(direction - 1) + "," + angle + "度");
//
//        OkHttpPresent.oneKey(AppSingleton.getInstance().getTerminalNo(), type, direction - 1, angle, new OkHttpResultCallBack() {
//            @Override
//            protected void start() {
//                super.start();
//            }
//
//            @Override
//            protected void onResponse(Response response, String body) throws IOException {
//                LogUtils.e(TAG, "登录成功: " + body);
//                OkHttpBaseResp loginResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
//                if (loginResp != null) {
//                    if (!loginResp.isSuccess()) {
//                        ToastUtils.showShort(OneKeyActivity.this, loginResp.getMsg());
//                    }
//                } else {
//                    ToastUtils.showShort(OneKeyActivity.this, getString(R.string.request_retry));
//                }
//            }
//
//            @Override
//            protected void onFailed(Request request, Exception e) {
//                super.onFailed(request, e);
//                LogUtils.e(TAG, "登录失败: " + e);
//                ToastUtils.showShort(OneKeyActivity.this, getString(R.string.request_retry));
//            }
//        });
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        try {
//            if (isBle) {
//                generateAutoParkingCommand(0, 0, true, false,false);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // 清理资源
//        if (scheduledTask != null) {
//            scheduledTask.cancel(true);
//        }
//        if (scheduler != null) {
//            scheduler.shutdown();
//        }
//    }
//}