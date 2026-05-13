package com.shenghao.blesdkdemo.ui;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdk.BleConstant;
import com.shenghao.blesdk.command.CommandUtils;
import com.shenghao.blesdk.enums.BlueRssiPke;
import com.shenghao.blesdkdemo.utils.ByteUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.MultiTouchDelegate;
import com.shenghao.blesdkdemo.utils.SPUtils;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;
import com.shenghao.blesdkdemo.widget.CommonDialog;
import com.shenghao.blesdkdemo.widget.LongPressImageView;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;

import static com.shenghao.blesdkdemo.widget.JoystickView.DIRECTION_HORIZONTAL;

public class OneKeyPortraitActivity extends BaseActivity {
    public final String TAG = this.getClass().getSimpleName();
    private BleDevice bleDevice;
    private int direction, angle;
    private boolean isBle = true;
    private TextView tvHint,tvMoving;
    private View llMoving;
    private View ivStop;
    private int unlockDb;
    private int lockDb;
    private int failureCount;
    private boolean isNotify;
    private long onCreateTime;
    private final Object queueLock = new Object();
    private boolean isExitByUser;

    // 添加指令队列和调度器
    // 创建指令包装类
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
    // 修改队列类型
    private final LinkedBlockingQueue<CommandWrapper> commandQueue = new LinkedBlockingQueue<>(1);
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledTask;
    private volatile boolean isSending = false;
    private ImageView ivMoving;
    // 添加状态跟踪变量
    private Handler handler = new Handler();
    private boolean isMonitoring;
    private Runnable monitorRunnable = new Runnable() {
        @Override
        public void run() {
            // 监控任务
            performMonitoringTask();

            // 如果还在监控状态，继续执行
            if (isMonitoring) {
                handler.postDelayed(this, 1000);
            }
        }
    };
    private long lastTime;
    private LongPressImageView ivUp,ivDown;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_key_portrait);
        StatusBarUtils.setStatusBarColor(this, R.color.transparent);
        StatusBarUtils.statusBarLightMode(this);
        onCreateTime = System.currentTimeMillis();

        lockDb = SPUtils.getInstance().getInt(SPUtils.SP_BLE_LOCK_RSSI, BlueRssiPke.MIDDLE.getHighRssi());
        unlockDb = SPUtils.getInstance().getInt(SPUtils.SP_BLE_UNLOCK_RSSI, BlueRssiPke.MIDDLE.getLowRssi());

        // 初始化指令发送调度器
        initCommandScheduler();
        initViews();
        //扩大触摸区域
        setupMultiTouchDelegate();

        initAnim();
        showNotice();
    }

    private void setupArea(LongPressImageView longPressImageView){
        // 在Activity或Fragment中使用
        ViewTreeObserver vto = longPressImageView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                longPressImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // 计算扩大后的矩形区域（例如扩大20dp）
                int extraArea = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

                Rect delegateArea = new Rect();
                longPressImageView.getHitRect(delegateArea);

                // 扩大触摸区域
                delegateArea.left -= extraArea;
                delegateArea.top -= extraArea;
                delegateArea.right += extraArea;
                delegateArea.bottom += extraArea;

                // 设置TouchDelegate
                TouchDelegate touchDelegate = new TouchDelegate(delegateArea, longPressImageView);
                if (View.class.isInstance(longPressImageView.getParent())) {
                    ((View) longPressImageView.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }

    // 使用自定义 MultiTouchDelegate
    private void setupMultiTouchDelegate() {
        View parent = (View) ivUp.getParent();
        ViewTreeObserver vto = parent.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int extraArea = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

                MultiTouchDelegate multiTouchDelegate = new MultiTouchDelegate(parent);

                // 为 ivUp 添加委托
                Rect delegateArea1 = new Rect();
                ivUp.getHitRect(delegateArea1);
                delegateArea1.left -= extraArea;
                delegateArea1.top -= extraArea;
                delegateArea1.right += extraArea;
                delegateArea1.bottom += extraArea;
                multiTouchDelegate.addDelegate(delegateArea1, ivUp);

                // 为 ivDown 添加委托
                Rect delegateArea2 = new Rect();
                ivDown.getHitRect(delegateArea2);
                delegateArea2.left -= extraArea;
                delegateArea2.top -= extraArea;
                delegateArea2.right += extraArea;
                delegateArea2.bottom += extraArea;
                multiTouchDelegate.addDelegate(delegateArea2, ivDown);

                parent.setTouchDelegate(multiTouchDelegate);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        List<BleDevice> allConnectedDevice = BleManager.getInstance().getAllConnectedDevice();
        if (!allConnectedDevice.isEmpty()) {
            bleDevice = allConnectedDevice.get(0);
            isBle = true;
            if(!isNotify){
                BleManager.getInstance().stopNotify(bleDevice, BleConstant.SERVICE_UUID_SH,BleConstant.NOTIFY_UUID_SH);
                startMonitoring();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notifyBle();
                    }
                },500);
                ToastUtils.showShort(this, "当前蓝牙设备：" + bleDevice.getName());
            }else {
                //从后台返回需重新发送，pke和自动指令
                byte[] bytes = BlueToothRssiPkeDialog.generatePKECommand(200, 200);
                write(bytes);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        generateAutoParkingCommand(0);
                    }
                },500);
            }
        } else {
            ToastUtils.showShort(this, "未连接蓝牙设备");
            oneKey4G(DIRECTION_HORIZONTAL, 0, 0); // 校准0
        }
    }

    private void showNotice() {
        String fullText = "操作车辆前，需全面检查车身1-2米内及盲区，确认无行人和障碍物，确保环境安全。\n" +
                "注意:防止出现车辆溜车安全问题，禁止在坡道路面使用！";
        SpannableString spannableString = new SpannableString(fullText);
        int newLineIndex = fullText.indexOf("\n");
        // 亮色模式：第一行黑色，第二行红色
        spannableString.setSpan(
                new ForegroundColorSpan(Color.parseColor("#FF181818")),
                0,
                newLineIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannableString.setSpan(
                new ForegroundColorSpan(Color.parseColor("#FFEE5F00")),
                newLineIndex,
                fullText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        CommonDialog commonDialog = new CommonDialog(this, new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {  //确定
                    dialog.dismiss();
                    SPUtils.getInstance().putBoolean(SPUtils.SP_HAS_SHOW_PRIVACY, true);
//                        init();//TODO
                } else {
                    finish();
                }
            }
        })
                .setMessageType(CommonDialog.MessageType.INFO)
                .setIKnowButton("确认")
                .setTitle("温馨提示")
                .setContent(spannableString)
                .setContentVisibility(View.VISIBLE)
                .setDialogCancelable(false);
        commonDialog.show();

    }

    private void initAnim() {
        // 创建旋转动画
        ObjectAnimator rotation = ObjectAnimator.ofFloat(ivMoving, "rotation", 0f, 360f);
        rotation.setDuration(2000); // 2秒完成一次旋转
        rotation.setRepeatCount(ObjectAnimator.INFINITE); // 无限循环
        rotation.setInterpolator(new LinearInterpolator()); // 匀速旋转
        rotation.start();
    }

    private void trySendImmediately() {
        if (!isSending && !commandQueue.isEmpty()) {
            // 创建一个临时调度器立即执行发送
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                synchronized (this) {
                    if (!isSending && !commandQueue.isEmpty()) {
                        isSending = true;
                        CommandWrapper wrapper = commandQueue.peek(); // 查看队列头部

                        if (wrapper != null && wrapper.isHighPriority) {
                            wrapper = commandQueue.poll(); // 移除高优先级指令
                            write(wrapper.command);
                            LogUtils.e("trySendImmediately", "立即发送高优先级指令");

                            // 高优先级指令发送后清空队列
                            commandQueue.clear();
                        }
                        isSending = false;
                    }
                }
            }, 0, TimeUnit.MILLISECONDS);
        }
    }

    private void initCommandScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduledTask = scheduler.scheduleWithFixedDelay(() -> {
            synchronized (this) {
                if (!isSending && !commandQueue.isEmpty()) {
                    isSending = true;
                    CommandWrapper wrapper = commandQueue.peek(); // 查看队列头部

                    // 调度器只处理普通指令，不处理高优先级指令
                    if (wrapper != null && !wrapper.isHighPriority) {
                        wrapper = commandQueue.poll(); // 移除普通指令
                        write(wrapper.command);
                        LogUtils.e("Scheduler", "调度发送普通角度指令");
                    }
                    isSending = false;
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void notifyBle() {
        BleManager.getInstance().notify(
                bleDevice,
                BleConstant.SERVICE_UUID_SH,
                BleConstant.NOTIFY_UUID_SH,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        isNotify = true;
//                        ToastUtils.showShort(OneKeyPortraitActivity.this, "onNotifySuccess");

//                        byte[] body = KeyGenerator.generate14ByteKey();
//                        byte[] bytes = CommandUtils.generateCommand((byte) 0x00, (byte) 0x31, body);
//                        write(bytes);
//                        SPUtils.getInstance().putString(SPUtils.SP_ONE_KEY_PWD, ByteUtils.byteToString(body) + "ffff");
                        byte[] bytes = BlueToothRssiPkeDialog.generatePKECommand(200, 200);
                        write(bytes);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                generateAutoParkingCommand(0);
                            }
                        },500);
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        isNotify = false;
                        ToastUtils.showShort(OneKeyPortraitActivity.this, "onNotifyFailure:" + exception.getDescription());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        String hex = ByteUtils.bytes2HexStr(data);
                        LogUtils.e(TAG, "回复："+ hex);
                    }
                }
        );
    }

    /**
     * 统一的指令生成方法
     *
     * @param direction   方向：0-停止，1-前进，2-后退
     * @param angle       角度：-32768~32767
     * @param isManualMode      是否恢复手动模式，界面退出时候调用true
     * @param isDirectionStopped     是否停止（左边手抬起）
     * @param needAngle 是否需要角度调节
     */
    private void generateAutoParkingCommand(int direction, int angle, boolean isManualMode, boolean isDirectionStopped,boolean needAngle) {
        synchronized (queueLock) {
            // 参数验证
            if (direction < 0 || direction > 2) {
                throw new IllegalArgumentException("方向参数错误：0-停止，1-前进，2-后退");
            }
            if (angle < -32768 || angle > 32767) {
                throw new IllegalArgumentException("角度超出范围(-32768~32767)");
            }

            // 限制角度范围
            if (angle < -25) angle = -25;
            else if (angle > 25) angle = 25;

            String dir = "";
            if (direction == 0) {
                dir = "停止";
            } else if (direction == 1) {
                dir = "前进";
            } else {
                dir = "后退";
            }
            LogUtils.e("generateAutoParkingCommand", dir + "角度：" + angle);

            // 1. 控制位1 (1字节)
            byte control1 = 0;
            if (direction == 1) { // 前进
                control1 |= 0x01; // BIT0 = 1
            } else if (direction == 2) { // 后退
                control1 |= 0x02; // BIT1 = 1
            }

            // 总是启用角度调整
            if(needAngle)
                control1 |= 0x04; // BIT2 = 1 (角度调整)

            // 根据参数设置BIT3
            if (isDirectionStopped) {
                control1 |= 0x08;
            }

            // 2. 控制位2和3 (各1字节，备用)
            byte control2 = 0;
            byte control3 = 0;

            // 3. 参数1 (1字节)：0x01开始调整 00 停止(是否手动模式，退出界面发0)
            byte param1 = isManualMode ? (byte) 0x00 : (byte) 0x01;

            // 4. 角度参数 (2字节，大端模式)
            byte angleHigh = (byte) ((angle + 1024) >> 8);
            byte angleLow = (byte) ((angle + 1024) & 0xFF);

            // 5. 角速度 (1字节)
            byte angularVelocity = 25;

            // 构建指令体 (7字节)
            byte[] body = new byte[]{
                    control1,          // 控制位1
                    control2,          // 控制位2 (备用)
                    control3,          // 控制位3 (备用)
                    param1,            // 参数1 (开始/停止调整)
                    angleHigh,         // 角度高位
                    angleLow,          // 角度低位
                    angularVelocity    // 角速度
            };

            // 生成完整指令
            byte[] command = CommandUtils.generateCommand((byte) 0x00, (byte) 0x20, body);

            // 判断指令优先级
            boolean isHighPriority = isDirectionStopped || direction != 0 || isManualMode;

            // 创建指令包装
            CommandWrapper wrapper = new CommandWrapper(command, isHighPriority);

            if (isHighPriority) {
                // 检查队列中是否已有相同指令
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

                commandQueue.clear(); // 清空队列
                commandQueue.offer(wrapper); // 加入高优先级指令
                LogUtils.e("generateAutoParkingCommand", "加入高优先级指令到队列");

                // 立即尝试发送高优先级指令
                trySendImmediately();
            } else {
                // 普通指令处理
                if (commandQueue.remainingCapacity() == 0) {
                    commandQueue.poll(); // 移除最旧的指令
                }
                commandQueue.offer(wrapper);
                LogUtils.e("generateAutoParkingCommand", "加入角度调整指令到队列");
            }
        }
    }

    // 重载方法 - 默认不停止
    private void generateAutoParkingCommand(int direction, int angle) {
        generateAutoParkingCommand(direction, angle, false, false,false);
    }

    // 重载方法 - 只调节角度
    private void generateAutoParkingCommand(int angle) {
        generateAutoParkingCommand(0, angle, false, false,true);
    }

    @Override
    protected void initViews() {
        super.initViews();
        View statusBarView = findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(this, statusBarView);
        ivMoving = findViewById(R.id.ivMoving);
        tvHint = findViewById(R.id.tvHint);
        llMoving = findViewById(R.id.llMoving);
        tvMoving = findViewById(R.id.tvMoving);
        ivUp = findViewById(R.id.ivUp);
        ivDown = findViewById(R.id.ivDown);
        ivStop = findViewById(R.id.ivStop);
        ivStop.setOnClickListener(view -> {
            generateAutoParkingCommand(0, 0, false, true,false);
        });
        findViewById(R.id.titleBackBtn).setOnClickListener(view -> {
            onBackPressed();
        });
        ivUp.setOnImageActionListener(new LongPressImageView.OnImageActionListener() {
            @Override
            public void onDown() {
                stopMonitoring();
                if (isBle) {
                    generateAutoParkingCommand(1, 0);
                } else {
//                    oneKey4G(directionType, direction, OneKeyActivity.this.angle);
                }
            }

            @Override
            public void onUp() {
                startMonitoring();
                showMoving(false,0);
            }

            @Override
            public void onLongPress() {
                showMoving(true,0);
                generateAutoParkingCommand(1, 0);
            }
        });
        ivDown.setOnImageActionListener(new LongPressImageView.OnImageActionListener() {
            @Override
            public void onDown() {
                stopMonitoring();
                generateAutoParkingCommand(2, 0);
            }

            @Override
            public void onUp() {
                startMonitoring();
                showMoving(false,1);
            }

            @Override
            public void onLongPress() {
                showMoving(true,1);
                generateAutoParkingCommand(2, 0);
            }
        });
    }

    /**
     *
     * @param isLongPress
     * @param type 1后退 0前进
     */
    private void showMoving(boolean isLongPress, int type) {
        if(isLongPress){
            llMoving.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.INVISIBLE);
            if(type == 0) {
                tvMoving.setText("车辆前进中，松手可暂停");
            }
            else tvMoving.setText("车辆后退中，松手可暂停");
        }else {
            llMoving.setVisibility(View.INVISIBLE);
            tvHint.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected boolean allowActivityOrientation() {
        return false; // 可以自己自定义横竖屏
    }

    private void write(byte[] bytes) {
        if(bleDevice == null || !BleManager.getInstance().isConnected(bleDevice))
            return;
        LogUtils.e(TAG, "CommandUtils最终数据发出去的数据："+ByteUtils.bytes2HexStr(bytes));
        BleManager.getInstance().write(
                bleDevice,
                BleConstant.SERVICE_UUID_SH,
                BleConstant.NOTIFY_UUID_SH,
                bytes,
//                false, true, 200,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        // 发送数据到设备成功
//                        ToastUtils.showShort(ShengHaoApp.getInstance(), "onWriteSuccess!"+current+","+total+ByteUtils.bytes2HexStr(justWrite));
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
//                        failureCount ++;
//                        if(failureCount > 5)
                            ToastUtils.showShort(OneKeyPortraitActivity.this, "onWriteFailure!");
                            LogUtils.e(TAG,"onWriteFailure!"+ByteUtils.bytes2HexStr(bytes)+","+BleManager.getInstance().isConnected(bleDevice));
                    }
                });
    }

    private void oneKey4G(int directionType, int direction, int angle) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime < 1 * 1000) {
            return;
        } else {
            lastTime = currentTime;
        }

        String type = "23";
        if (directionType == DIRECTION_HORIZONTAL) {
            type = "24";
        } else {
            if (direction == 0) {
                return;
            }
        }

        if (angle < -25) angle = -25;
        else if (angle > 25) angle = 25;

        LogUtils.e("oneKey4G", type + "," + String.valueOf(direction - 1) + "," + angle + "度");
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
//                        ToastUtils.showShort(OneKeyPortraitActivity.this, loginResp.getMsg());
//                    }
//                } else {
//                    ToastUtils.showShort(OneKeyPortraitActivity.this, getString(R.string.request_retry));
//                }
//            }
//
//            @Override
//            protected void onFailed(Request request, Exception e) {
//                super.onFailed(request, e);
//                LogUtils.e(TAG, "登录失败: " + e);
//                ToastUtils.showShort(OneKeyPortraitActivity.this, getString(R.string.request_retry));
//            }
//        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isExitByUser)
            return;
        try {
            if (isBle) {
                generateAutoParkingCommand(0, 0, true, false,false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        byte[] bytes = BlueToothRssiPkeDialog.generatePKECommand(unlockDb, lockDb);
                        write(bytes);
                    }
                },200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clear();
    }

    private void clear() {
        // 清理资源
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow(); // 强制停止所有任务
        }
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
        }
        // 退出Activity时停止监控
        stopMonitoring();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 停止监控
     */
    private void stopMonitoring() {
        isMonitoring = false;
        if (handler != null && monitorRunnable != null) {
            handler.removeCallbacks(monitorRunnable);
        }
    }

    /**
     * 开始监控（如果需要的话）
     */
    private void startMonitoring() {
        if (!isMonitoring) {
            isMonitoring = true;
            handler.postDelayed(monitorRunnable,1000);
        }
    }

    /**
     * 执行监控任务
     */
    private void performMonitoringTask() {
        // 在这里执行你的监控逻辑,发送停止指令
        generateAutoParkingCommand(0, 0, false, true,false);
//        runOnUiThread(() -> {
//            // 更新UI的操作
//        });
    }

    @Override
    public void onBackPressed() {
//        if(System.currentTimeMillis() - onCreateTime > 3000)
        isExitByUser = true;
        clear();
        generateAutoParkingCommand(0, 0, true, false,false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = BlueToothRssiPkeDialog.generatePKECommand(unlockDb, lockDb);
                write(bytes);
            }
        },200);
        showLoadingDialog("正在退出...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoadingDialog();
                OneKeyPortraitActivity.super.onBackPressed();
            }
        },1000);


    }
}