package com.shenghao.ui;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.shenghao.R;
import com.shenghao.blesdk.BleConstant;
import com.shenghao.blesdk.command.AESUtils;
import com.shenghao.blesdk.command.CommandUtils;
import com.shenghao.blesdk.enums.BlueRssiPke;
import com.shenghao.utils.ByteUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.SPUtils;
import com.shenghao.utils.ToastUtils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

public class BlueToothRssiPkeDialog extends Dialog {
    public final String TAG = this.getClass().getSimpleName();
    private final Context mContext;
    private BleDevice bleDevice;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BlueRssiPke blueRssi = BlueRssiPke.MIDDLE;
    Button btnConfirmTop,btnConfirmBottom,btnConfirm;
    private TextView tvMin,tvMax,tvTop,tvBottom;
    private int unLockRssi,lockRssi;
    private Deque<Integer> rssiQueue = new ArrayDeque<>(3);
    private Handler handler = new Handler();
    private Runnable readRssiRunnable = new Runnable() {
        @Override
        public void run() {
            sendCMDRead();
            handler.postDelayed(readRssiRunnable, 1000); // 1秒后再次读取
        }
    };
    private boolean isFirstTime = true;
    private boolean isPkeConnected,pkeSate;
    private boolean isBond;
    private Button btnPair;
    private Switch switchPKE;
    private byte flowFlag;
    private ProgressBar progressBar;
    LinearLayout llWeak;
    RelativeLayout llMiddle;
    LinearLayout llStrong;
//    LinearLayout llConfig;

    TextView tvWeak;
    TextView tvMid;
    TextView tvStrong;
//    TextView tvConfigDistance,tvConfig;

    TextView tvConnectWeakValue;
    TextView tvConnectMidValue;
    TextView tvConnectStrongValue;

    TextView tvDisConnectWeakValue;
    TextView tvDisConnectMidValue;
    TextView tvDisConnectStrongValue;
    private View viewMask;
    private int unlockDb = blueRssi.getLowRssi();
    private int lockDb = blueRssi.getHighRssi();
    private long lastOnCheckedChangedTime;
    private CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            lastOnCheckedChangedTime = System.currentTimeMillis();
            byte[] bytes = generatePKECommand(isChecked, false, false, false, 0, 0, 1, "0000");
//            byte[] bytes = generatePKECommand(switchPKE.isChecked(), false, false, false, unlockDb, lockDb, 1, "0000");
            write(bytes);
        }
    };
    private void updateAverageDisplay() {
        int average;
        synchronized (rssiQueue) {
            LogUtils.e(TAG,"当前线程："+Thread.currentThread().getName()+","+rssiQueue.size());
            if (rssiQueue.isEmpty()) {
                average = 0;
            } else {
                int sum = 0;
                for (int value : rssiQueue) {
                    sum += value;
                }
                average = sum / rssiQueue.size();
            }
        }

        final String formattedAverage = String.format(Locale.getDefault(), "%d", average);
        if(btnConfirmTop.isEnabled())
            tvMin.setText(formattedAverage);
        if(btnConfirmBottom.isEnabled())
            tvMax.setText(formattedAverage);

    }
    // 开始读取（在连接成功后调用）
    public void startRssiUpdates() {
        handler.postDelayed(readRssiRunnable,500);
    }

    // 停止读取（在断开连接或销毁时调用）
    public void stopRssiUpdates() {
        handler.removeCallbacks(readRssiRunnable);
    }
    public BlueToothRssiPkeDialog(@NonNull Context context, int themeResId, int layout, int gravity) {
        super(context, themeResId);
        View view = LayoutInflater.from(context).inflate(layout,null);
        setContentView(view);
        Window mWindow = getWindow();
        WindowManager.LayoutParams params = mWindow.getAttributes();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        params.width = (int) (displayMetrics.widthPixels * 0.9);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = gravity;
        mWindow.setAttributes(params);
        this.mContext = context;
        initView(view);

        lockDb = SPUtils.getInstance().getInt(SPUtils.SP_BLE_LOCK_RSSI, BlueRssiPke.MIDDLE.getHighRssi());
        unlockDb = SPUtils.getInstance().getInt(SPUtils.SP_BLE_UNLOCK_RSSI,BlueRssiPke.MIDDLE.getLowRssi());

//        initHelper();
    }

    private final BroadcastReceiver pairingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.e(TAG,"action"+action);
            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                // 处理配对请求
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int variant = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);
                Log.d(TAG, "配对请求: " + device.getName() + ", 类型: " + variant);
                progressBar.setVisibility(View.VISIBLE);
                // 自动响应配对请求并提供密码
//                if (pairingPin != null && !pairingPin.isEmpty()) {
//                    handlePairingRequest(device, variant, pairingPin);
//                } else if (listener != null) {
//                    // 如果没有预设密码，通知监听器需要用户输入
//                    listener.onPinRequired(device, variant);
//                }
            }
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                Log.d(TAG, "绑定状态变化: " + device.getName() +
                        ", 新状态: " + bondState + ", 旧状态: " + previousBondState);
                    switch (bondState) {
                        case BluetoothDevice.BOND_BONDED:
                            ToastUtils.showShort(getContext(),"配对成功，08断开");
//                            BluetoothService.updateAutoConnectStatus(getContext(), false);//先停止自动连接
                            SPUtils.getInstance().putString(SPUtils.SP_BLE_MAC,device.getAddress());
                            isBond = true;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                        byte[] bytes = generatePKECommand(false, false, false, true, unlockDb, lockDb, 1, "0000");
                                        write(bytes);
                                    progressBar.setVisibility(View.GONE);
                                }
                            },3000);
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    BluetoothService.updateAutoConnectStatus(getContext(), true);//先停止自动连接
//                                }
//                            },3500);
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            break;
                        case BluetoothDevice.BOND_NONE:
                            if (previousBondState == BluetoothDevice.BOND_BONDING) {
                            }
//                            BleManager.getInstance().stopNotify(bleDevice,
//                                    BleConstant.SERVICE_UUID,
//                                    BleConstant.NOTIFY_UUID);
//                            BleManager.getInstance().disconnect(bleDevice);
                            progressBar.setVisibility(View.GONE);
                            dismiss();
                            break;
                    }
            }
        }
    };

    @Override
    public void show() {
        startRssiUpdates();
        super.show();
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getContext().registerReceiver(pairingReceiver, filter);
    }

    @Override
    public void dismiss() {
        stopRssiUpdates();
        try{
            getContext().unregisterReceiver(pairingReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.dismiss();
    }

    private void notifyBle() {
        BleManager.getInstance().notify(
                bleDevice,
                BleConstant.SERVICE_UUID_SH,
                BleConstant.NOTIFY_UUID_SH,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        ToastUtils.showShort(getContext(),"onNotifySuccess");
                        btnConfirm.setEnabled(isBond);
                        viewMask.setVisibility(isBond?View.GONE:View.VISIBLE);
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        ToastUtils.showShort(getContext(),"onNotifyFailure:"+exception.getDescription());
                        BleManager.getInstance().disconnect(bleDevice);
                        BleManager.getInstance().destroy();
                        dismiss();
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        String hex = ByteUtils.bytes2HexStr(data);
                        LogUtils.e(TAG, "回复："+ hex);
                        if(hex.startsWith("ff1212")){
                            //解密
                            try {
//                                LogUtils.e(TAG, "解密秘钥："+ ByteUtils.bytes2HexStr(new byte[16])+","+ByteUtils.bytes2HexStr(subarray(data,3,16)));
                                byte[] bytes = AESUtils.aes128Decrypt(subarray(data,3,16), ByteUtils.hexStr2Bytes("ffffffffffffffffffffffffffffffff"));
                                LogUtils.e(TAG, "回复解密后数据："+ ByteUtils.bytes2HexStr(bytes));
                                if(bytes[0] == 0x69 && bytes[2] == 0){
                                    //此时为pek信息，第一个开始算，第四个是db值
                                    int rssi = bytes[6];
                                    synchronized (rssiQueue) {
                                        // 维护最近三次数据
                                        if (rssiQueue.size() == 1) {
//                                            updateAverageDisplay(); // 更新显示
                                            rssiQueue.removeFirst();
                                        }
                                        rssiQueue.addLast(rssi);
                                        updateAverageDisplay();
                                        LogUtils.e(TAG,"加完后："+rssiQueue.size());
                                    }
                                    unLockRssi = bytes[4];
                                    lockRssi = bytes[5];
                                    tvTop.setText("历史启动值："+unLockRssi);
                                    tvBottom.setText("历史关锁值："+lockRssi);
                                    isPkeConnected = (bytes[3]& 0x02)!= 0x00;//pke是否连接
                                    pkeSate =(bytes[3] & 0x01)!= 0x00;//pke是否开启

                                    updatePairState();

                                    if((System.currentTimeMillis() - lastOnCheckedChangedTime) > 2000){
                                        // 先移除监听器
                                        switchPKE.setOnCheckedChangeListener(null);
                                        switchPKE.setChecked(pkeSate);
                                        // 再恢复监听器
                                        switchPKE.setOnCheckedChangeListener(listener);
                                    }

                                    if(isBond && isFirstTime){//首次更新档位选择就好
                                        blueRssi = BlueRssiPke.fromRssi(lockRssi,unLockRssi);
                                        switchAlias(blueRssi);
                                        if(lockRssi < 0 || unLockRssi < 0)//不正常
                                            write(generatePKECommand(unlockDb, lockDb));
                                        else {
                                            SPUtils.getInstance().putInt(SPUtils.SP_BLE_LOCK_RSSI,blueRssi.getHighRssi());
                                            SPUtils.getInstance().putInt(SPUtils.SP_BLE_UNLOCK_RSSI,blueRssi.getLowRssi());
                                        }
                                        isFirstTime = false;
                                    }
                                }
                            } catch (Exception e) {
                                LogUtils.e(TAG, "解密失败");
                                throw new RuntimeException(e);
                            }
                        }else if(hex.startsWith("ff120080")){
                            LogUtils.e(TAG, "回复1："+ hex);
                            //明文
                            byte[] bytes = generatePKECommand(false, false, true, false, unlockDb, lockDb, 1, "0000");
                            flowFlag = bytes[4];//记下流水号
                            write(bytes);
                        }else if(hex.startsWith("ff120018")){
                            byte[] bytes = ByteUtils.hexStr2Bytes(hex);
                            if(!isBond && flowFlag == bytes[4]){//流水号相同且还未配对
                                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(bleDevice.getMac());
                                device.createBond();
                            }
                        }
                    }
                }
        );
    }

    private void updatePairState() {
        btnPair.setText(isBond?"已配对":"去配对");
        btnPair.setBackgroundResource(isBond?R.drawable.bg_radius_shape_paired:R.drawable.bg_radius_shape_no_pair);
    }

    public byte[] subarray(byte[] data, int startIndex, int length) {
        if (data == null) {
            throw new IllegalArgumentException("数据数组不能为null");
        }
        if (startIndex < 0) {
            throw new IllegalArgumentException("起始下标不能为负数");
        }
        if (length < 0) {
            throw new IllegalArgumentException("长度不能为负数");
        }
        if (startIndex + length > data.length) {
            throw new IllegalArgumentException(
                    String.format("请求的截取范围[%d, %d)超出了数组长度(%d)",
                            startIndex, startIndex + length, data.length)
            );
        }

        byte[] result = new byte[length];
        System.arraycopy(data, startIndex, result, 0, length);
        return result;
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(bleDevice.getMac());
        isBond = device.getBondState() == BluetoothDevice.BOND_BONDED;
        if(btnPair!=null){
            updatePairState();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyBle();
            }
        },500);
    }

    private void initView(View view) {
        llWeak = view.findViewById(R.id.blue_tooth_weak);
        llMiddle = view.findViewById(R.id.blue_tooth_middle);
        llStrong = view.findViewById(R.id.blue_tooth_strong);
//        llConfig = view.findViewById(R.id.blue_tooth_config);

        tvWeak = view.findViewById(R.id.gear_weak_text);
        tvMid = view.findViewById(R.id.gear_mid_text);
        tvStrong = view.findViewById(R.id.gear_strong_text);
//        tvConfig = view.findViewById(R.id.gear_config_text);
//        tvConfigDistance = view.findViewById(R.id.tvConfigDistance);

        tvConnectWeakValue = view.findViewById(R.id.gear_connect_weak_value);
        tvConnectMidValue = view.findViewById(R.id.gear_connect_middle_value);
        tvConnectStrongValue = view.findViewById(R.id.gear_connect_strong_value);

        tvDisConnectWeakValue = view.findViewById(R.id.gear_disconnect_weak_value);
        tvDisConnectMidValue = view.findViewById(R.id.gear_disconnect_middle_value);
        tvDisConnectStrongValue = view.findViewById(R.id.gear_disconnect_strong_value);


        viewMask = view.findViewById(R.id.viewMask);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        btnConfirmTop = view.findViewById(R.id.btnConfirmTop);
        btnConfirmBottom = view.findViewById(R.id.btnConfirmBottom);
        progressBar = view.findViewById(R.id.progressBar);
        view.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvMin = view.findViewById(R.id.tvMin);
        tvMax = view.findViewById(R.id.tvMax);
        tvTop = view.findViewById(R.id.tvTop);
        tvBottom = view.findViewById(R.id.tvBottom);
        switchPKE = view.findViewById(R.id.switchPKE);
        switchPKE.setOnCheckedChangeListener(listener);
        btnPair = view.findViewById(R.id.btnPair);
        btnPair.setOnClickListener(v -> {
            if(!BleManager.getInstance().isConnected(bleDevice)){
                ToastUtils.showShort(mContext,"设备已断开");
            }
            if(!isBond){
                byte[] command = CommandUtils.generateCommand((byte) 0x00, (byte) 0x80, null);//清空配对
                write(command);
            }
        });
        btnConfirmTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(tvMin.getText().toString()))
                    return;
                //ULock
//                String ATConnectOrder = (BleConstant.LIMIT_STR_DISCONNECT+Integer.parseInt(tvMin.getText().toString()) +"\r\n");
//                byte[] limitNumberConnect = ATConnectOrder.getBytes();
//                sendCMD(limitNumberConnect);
                int unlock = Integer.parseInt(tvMin.getText().toString());
                byte[] bytes = generatePKECommand(unlock, lockRssi);
                write(bytes);
                btnConfirmTop.setText("已确认");
                SPUtils.getInstance().putInt(SPUtils.SP_CUSTOM_UNLOCK_RSSI,unlock);//存储一下，自定义的值
                btnConfirmTop.setEnabled(false);
            }
        });
        btnConfirmBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(tvMax.getText().toString()))
                    return;
                //ULock
//                String ATConnectOrder = (BleConstant.LIMIT_STR_CONNECT+Integer.parseInt(tvMax.getText().toString()) +"\r\n");
//                byte[] limitNumberConnect = ATConnectOrder.getBytes();
//                sendCMD(limitNumberConnect);
                int lock = Integer.parseInt(tvMax.getText().toString());
                byte[] bytes = generatePKECommand(unLockRssi, lock);
                write(bytes);
                btnConfirmBottom.setText("已确认");
                SPUtils.getInstance().putInt(SPUtils.SP_CUSTOM_LOCK_RSSI,lock);//存储一下，自定义的值
                btnConfirmBottom.setEnabled(false);
            }
        });

        viewMask.setOnClickListener(view1 -> {});
        llWeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blueRssi = BlueRssiPke.WEAK;
                switchAlias(blueRssi);
            }
        });
        llMiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blueRssi = BlueRssiPke.MIDDLE;
                switchAlias(blueRssi);
            }
        });
        llStrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blueRssi = BlueRssiPke.STRONG;
                switchAlias(blueRssi);
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(blueRssi == null){
                    return;
                }
                try{
                    write(generatePKECommand(blueRssi.getLowRssi(), blueRssi.getHighRssi()));
                    SPUtils.getInstance().putInt(SPUtils.SP_BLE_UNLOCK_RSSI,blueRssi.getLowRssi());
                    SPUtils.getInstance().putInt(SPUtils.SP_BLE_LOCK_RSSI,blueRssi.getHighRssi());
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showShort(getContext(),"设置失败");
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                },500);

            }
        });
    }

    //读取rssi数据设置的开锁关锁db
    private void sendCMDRead(){
        // 生成完整指令 (假设使用0x21作为指令类型)
        byte[] command = CommandUtils.generateCommand((byte) 0x00, (byte) 0x69, new byte[]{0x00});
        write(command);
    }
    public static byte[]  generatePKECommand(int unlockDb, int lockDb) {
       return generatePKECommand(true,true,true,false,unlockDb,lockDb,1,"0000");
    }
    /**
     * 生成PKE相关配置指令
     *
     * @param enablePKE          是否打开PKE (对应第一个字节的bit0)
     * @param requestPairing     设备发起配对请求 (对应第一个字节的bit1)
     * @param allowPairing       允许设备发起配对 (对应第一个字节的bit2)
     * @param disconnect         发起断开连接 (对应第一个字节的bit3)
     * @param unlockDb           开锁db值 (第二个字节)
     * @param lockDb             关锁db值 (第三个字节)
     * @param lockDelay          关锁延时，单位秒 (第四个字节)
     * @param password           PKE蓝牙密码，4位数字 (第5-8字节)
     */
    private static byte[] generatePKECommand(boolean enablePKE, boolean requestPairing, boolean allowPairing,
                                    boolean disconnect, int unlockDb, int lockDb, int lockDelay,
                                    String password) {
        // 参数验证
        if (unlockDb < 0 || unlockDb > 255) {
            throw new IllegalArgumentException("开锁db值超出范围(0-255)");
        }
        if (lockDb < 0 || lockDb > 255) {
            throw new IllegalArgumentException("关锁db值超出范围(0-255)");
        }
        if (lockDelay < 0 || lockDelay > 255) {
            throw new IllegalArgumentException("关锁延时超出范围(0-255)");
        }
        if (password == null || password.length() != 4 || !password.matches("\\d{4}")) {
            throw new IllegalArgumentException("密码必须为4位数字");
        }

        // 1. 控制位1 (1字节)
        byte control1 = 0;
        if (enablePKE) {
            control1 |= 0x01; // BIT0 = 1 (打开PKE)
        }
        if (requestPairing) {
            control1 |= 0x02; // BIT1 = 1 (设备发起配对请求)
        }
        if (allowPairing) {
            control1 |= 0x04; // BIT2 = 1 (允许设备发起配对)
        }
        if (disconnect) {
            control1 |= 0x08; // BIT3 = 1 (发起断开连接)
        }

        // 2. 开锁db值 (1字节)
        byte unlockDbByte = (byte) unlockDb;

        // 3. 关锁db值 (1字节)
        byte lockDbByte = (byte) lockDb;

        // 4. 关锁延时 (1字节)
        byte lockDelayByte = (byte) lockDelay;

        // 5. PKE蓝牙密码 (4字节)
        byte[] passwordBytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            char c = password.charAt(i);
            passwordBytes[i] = (byte) (c - '0'); // 将数字字符转换为对应的数值
        }

        // 构建指令体 (8字节)
        byte[] body = new byte[]{
                control1,           // 控制位1
                unlockDbByte,       // 开锁db值
                lockDbByte,         // 关锁db值
                lockDelayByte,      // 关锁延时
                passwordBytes[0],   // 密码第1位
                passwordBytes[1],   // 密码第2位
                passwordBytes[2],   // 密码第3位
                passwordBytes[3]    // 密码第4位
        };

        // 生成完整指令 (假设使用0x21作为指令类型)
        return CommandUtils.generateCommand((byte) 0x00, (byte) 0x18, body);
//        write(command);
    }

    private void write(byte[] command) {
        LogUtils.e(TAG,"写入："+ByteUtils.bytes2HexStr(command));
        if(!BleManager.getInstance().isConnected(bleDevice.getMac()))
            return;
        BleManager.getInstance().write(
                bleDevice,
                BleConstant.SERVICE_UUID_SH,
                BleConstant.NOTIFY_UUID_SH,
                command,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
//                        ToastUtils.showShort(ShengHaoApp.getInstance(),"onWriteSuccess!");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
//                        ToastUtils.showShort(ShengHaoApp.getInstance(),"onWriteFailure!"+exception.getCode());
                        // 发送数据到设备失败
                    }
                });
    }

    void switchAlias(BlueRssiPke blueAlias){
        LogUtils.e(TAG, "当前线程: " + Thread.currentThread().getName());
        llWeak.setBackgroundResource(R.drawable.bg_tooth_gear_unselect);
        llMiddle.setBackgroundResource(R.drawable.bg_tooth_gear_unselect);
        llStrong.setBackgroundResource(R.drawable.bg_tooth_gear_unselect);
//        llConfig.setBackgroundResource(R.drawable.bg_tooth_gear_unselect);

        tvWeak.setTextColor(Color.parseColor("#99000000"));
        tvMid.setTextColor(Color.parseColor("#99000000"));
        tvStrong.setTextColor(Color.parseColor("#99000000"));

//        tvConfig.setTextColor(Color.parseColor("#99000000"));
//        tvConfigDistance.setTextColor(Color.parseColor("#99000000"));

        tvConnectWeakValue.setTextColor(Color.parseColor("#99000000"));
        tvConnectMidValue.setTextColor(Color.parseColor("#99000000"));
        tvConnectStrongValue.setTextColor(Color.parseColor("#99000000"));

        tvDisConnectWeakValue.setTextColor(Color.parseColor("#99000000"));
        tvDisConnectMidValue.setTextColor(Color.parseColor("#99000000"));
        tvDisConnectStrongValue.setTextColor(Color.parseColor("#99000000"));


        if(blueAlias == BlueRssiPke.WEAK){
            llWeak.setBackgroundResource(R.drawable.bg_tooth_gear_selected);
            tvWeak.setTextColor(Color.parseColor("#FF1C70E6"));
            tvConnectWeakValue.setTextColor(Color.parseColor("#FF1C70E6"));
            tvDisConnectWeakValue.setTextColor(Color.parseColor("#FF1C70E6"));

        }else if(blueAlias == BlueRssiPke.MIDDLE){
            llMiddle.setBackgroundResource(R.drawable.bg_tooth_gear_selected);
            tvMid.setTextColor(Color.parseColor("#FF1C70E6"));
            tvConnectMidValue.setTextColor(Color.parseColor("#FF1C70E6"));
            tvDisConnectMidValue.setTextColor(Color.parseColor("#FF1C70E6"));

        }else if(blueAlias == BlueRssiPke.STRONG){
            llStrong.setBackgroundResource(R.drawable.bg_tooth_gear_selected);
            tvStrong.setTextColor(Color.parseColor("#FF1C70E6"));
            tvConnectStrongValue.setTextColor(Color.parseColor("#FF1C70E6"));
            tvDisConnectStrongValue.setTextColor(Color.parseColor("#FF1C70E6"));

        }else if(blueAlias == null){
//            llConfig.setBackgroundResource(R.drawable.bg_tooth_gear_selected);
//            tvConfigDistance.setTextColor(Color.parseColor("#FF1C70E6"));
//            tvConfig.setTextColor(Color.parseColor("#FF1C70E6"));
        }
    }
}







