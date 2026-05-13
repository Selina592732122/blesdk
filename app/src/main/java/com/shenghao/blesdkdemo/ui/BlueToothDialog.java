//package com.shenghao.blesdkdemo.ui;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.graphics.Color;
//import android.os.Handler;
//import android.os.Looper;
//import android.text.TextUtils;
//import android.util.DisplayMetrics;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//
//import com.clj.fastble.BleManager;
//import com.clj.fastble.callback.BleNotifyCallback;
//import com.clj.fastble.callback.BleWriteCallback;
//import com.clj.fastble.data.BleDevice;
//import com.clj.fastble.exception.BleException;
//import com.shenghao.blesdkdemo.R;
//import com.shenghao.blesdkdemo.ShengHaoApp;
//import com.shenghao.blesdk.BleConstant;
//import com.shenghao.blesdk.enums.BlueRssi;
//import com.shenghao.blesdkdemo.utils.LogUtils;
//import com.shenghao.blesdkdemo.utils.ToastUtils;
//
//import java.io.UnsupportedEncodingException;
//import java.math.BigInteger;
//
//public class BlueToothDialog extends Dialog {
//    public final String TAG = this.getClass().getSimpleName();
//    private BleDevice bleDevice;
//    private BlueRssi blueRssi;
//
//    LinearLayout llWeak;
//    RelativeLayout llMiddle;
//    LinearLayout llStrong;
//    LinearLayout llConfig;
//
//    TextView tvWeak;
//    TextView tvMid;
//    TextView tvStrong;
//    TextView tvConfigDistance,tvConfig;
//
//    TextView tvConnectWeakValue;
//    TextView tvConnectMidValue;
//    TextView tvConnectStrongValue;
//
//    TextView tvDisConnectWeakValue;
//    TextView tvDisConnectMidValue;
//    TextView tvDisConnectStrongValue;
//
//    Button gearConfirm;
//
//    boolean isConnectSettingWrite = false;
//    boolean isDisConnectSettingWrite = false;
//
//    boolean connectSettingOk = false;
//    boolean disConnectSettingOk = false;
//    private static int lrssi,ulrssi;
//    private TextView tvSet;
//
//    public BlueToothDialog(@NonNull Context context, int themeResId, int layout, int gravity) {
//
//        super(context, themeResId);
//        View view = LayoutInflater.from(context).inflate(R.layout.dialog_blue_tooth,null);
//        setContentView(view);
//        Window mWindow = getWindow();
//        WindowManager.LayoutParams params = mWindow.getAttributes();
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        params.width = (int) (displayMetrics.widthPixels * 0.9);
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        params.gravity = gravity;
//        mWindow.setAttributes(params);
//
//        initView(view);
//    }
//
//    @Override
//    public void show() {
//        super.show();
//    }
//
//    private void notifyBle() {
//        BleManager.getInstance().notify(
//                bleDevice,
//                BleConstant.SERVICE_UUID,
//                BleConstant.NOTIFY_UUID,
//                new BleNotifyCallback() {
//
//                    @Override
//                    public void onNotifySuccess() {
//                        String ATConnectOrder = (BleConstant.LIMIT_STR_CONNECT_CHECK +"\r\n");
//                        byte[] limitNumberConnect = ATConnectOrder.getBytes();
//
//                        String ATDisConnectOrder = (BleConstant.LIMIT_STR_DISCONNECT_CHECK +"\r\n");
//                        byte[] limitNumberDisConnect = ATDisConnectOrder.getBytes();
//
//                        sendCMD2(limitNumberConnect,limitNumberDisConnect);
//                    }
//
//                    @Override
//                    public void onNotifyFailure(BleException exception) {
//                        ToastUtils.showShort(getContext(),"onNotifyFailure:"+exception.getDescription());
//                    }
//
//                    @Override
//                    public void onCharacteristicChanged(byte[] data) {
//                        String str = null;
//                        try {
//                            str = new String(data,"UTF-8");
////                                    ToastUtils.showShort(WanShiDaApp.getInstance(),str);
//                            LogUtils.e(TAG,str);
//                        } catch (UnsupportedEncodingException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                        if(str.startsWith("+LRSSI")){
//                            lrssi = Integer.parseInt(str.split(":")[1].trim());
//                        }else if(str.startsWith("+ULRSSI")){
//                            ulrssi = Integer.parseInt(str.split(":")[1].trim());
//
//                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    switchAlias(BlueRssi.fromRssi(lrssi,ulrssi));
//                                }
//                            },0);
//                        }
//
//                        if(!TextUtils.isEmpty(str) && str.contains("OK") && isConnectSettingWrite ){
//                            connectSettingOk = true;
//                        }
//
//                        if(!TextUtils.isEmpty(str) && str.contains("OK") && isDisConnectSettingWrite){
//                            disConnectSettingOk = true;
//                        }
//
//                        if(isConnectSettingWrite && isDisConnectSettingWrite){
//                            if(connectSettingOk && disConnectSettingOk){
////                                        mmkv.encode(bleDevice.getDevice().getAddress(),blueRssi.getValue());
//                                ToastUtils.showShort(ShengHaoApp.getInstance(),"设置成功！");
//                            }else{
//                                ToastUtils.showShort(ShengHaoApp.getInstance(),"设置失败！");
//                            }
//                            isConnectSettingWrite = false;
//                            isDisConnectSettingWrite = false;
//                            connectSettingOk = false;
//                            disConnectSettingOk = false;
//                        }
//                    }
//                }
//        );
//    }
//
//    public void setBleDevice(BleDevice bleDevice) {
//        this.bleDevice = bleDevice;
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                notifyBle();
//            }
//        },500);
//
//    }
//
//    private void initView(View view) {
//        llWeak = view.findViewById(R.id.blue_tooth_weak);
//        llMiddle = view.findViewById(R.id.blue_tooth_middle);
//        llStrong = view.findViewById(R.id.blue_tooth_strong);
//        llConfig = view.findViewById(R.id.blue_tooth_config);
//
//        tvWeak = view.findViewById(R.id.gear_weak_text);
//        tvMid = view.findViewById(R.id.gear_mid_text);
//        tvStrong = view.findViewById(R.id.gear_strong_text);
//        tvConfig = view.findViewById(R.id.gear_config_text);
//        tvConfigDistance = view.findViewById(R.id.tvConfigDistance);
//
//        tvConnectWeakValue = view.findViewById(R.id.gear_connect_weak_value);
//        tvConnectMidValue = view.findViewById(R.id.gear_connect_middle_value);
//        tvConnectStrongValue = view.findViewById(R.id.gear_connect_strong_value);
//
//        tvDisConnectWeakValue = view.findViewById(R.id.gear_disconnect_weak_value);
//        tvDisConnectMidValue = view.findViewById(R.id.gear_disconnect_middle_value);
//        tvDisConnectStrongValue = view.findViewById(R.id.gear_disconnect_strong_value);
//
//        gearConfirm = view.findViewById(R.id.gear_confirm);
////        tvSet = view.findViewById(R.id.tvSet);
////        tvSet.setOnClickListener(v ->{
////
////        });
//
//        llWeak.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                blueRssi = BlueRssi.WEAK;
//                switchAlias(blueRssi);
//            }
//        });
//        llMiddle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                blueRssi = BlueRssi.MIDDLE;
//                switchAlias(blueRssi);
//            }
//        });
//        llStrong.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                blueRssi = BlueRssi.STRONG;
//                switchAlias(blueRssi);
//            }
//        });
//        llConfig.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                blueRssi = null;
//                switchAlias(blueRssi);
//                BlueToothRssiDialog blueToothDialog = new BlueToothRssiDialog(getContext(), R.style.Common_Dialog, R.layout.dialog_blue_tooth_rssi, Gravity.CENTER);
//                blueToothDialog.setBleDevice(bleDevice);
//                blueToothDialog.setCanceledOnTouchOutside(true);
//                blueToothDialog.show();
//            }
//        });
//        gearConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(blueRssi == null){
//                    dismiss();
//                    return;
//                }
//                String ATConnectOrder = (BleConstant.LIMIT_STR_CONNECT + blueRssi.getLowRssi()+"\r\n");
//                byte[] limitNumberConnect = ATConnectOrder.getBytes();
//
//                String ATDisConnectOrder = (BleConstant.LIMIT_STR_DISCONNECT + blueRssi.getHighRssi()+"\r\n");
//                byte[] limitNumberDisConnect = ATDisConnectOrder.getBytes();
//
//                sendCMD(limitNumberConnect,limitNumberDisConnect);
//                dismiss();
//            }
//        });
//    }
//
//    private void sendCMD2(byte[] limitNumberConnect, byte[] limitNumberDisConnect) {
//        Handler handler = new Handler();
//        Runnable delayeConnectSettingdRunnable = new Runnable() {
//            @Override
//            public void run() {
//                BleManager.getInstance().write(
//                        bleDevice,
//                        BleConstant.SERVICE_UUID,
//                        BleConstant.NOTIFY_UUID,
//                        limitNumberConnect,
//                        new BleWriteCallback() {
//                            @Override
//                            public void onWriteSuccess(int current, int total, byte[] justWrite) {
//                                // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
//                            }
//
//                            @Override
//                            public void onWriteFailure(BleException exception) {
//                                ToastUtils.showShort(ShengHaoApp.getInstance(),"蓝牙设置距离超出!");
//                                // 发送数据到设备失败
//                            }
//                        });
//
//            }
//        };
//        handler.postDelayed(delayeConnectSettingdRunnable,200);
//
//
//        // 延迟执行的Runnable
//        Runnable delayeDisConnectSettingdRunnable = new Runnable() {
//            @Override
//            public void run() {
//                BleManager.getInstance().write(
//                        bleDevice,
//                        BleConstant.SERVICE_UUID,
//                        BleConstant.NOTIFY_UUID,
//                        limitNumberDisConnect,
//                        new BleWriteCallback() {
//                            @Override
//                            public void onWriteSuccess(int current, int total, byte[] justWrite) {
//                                // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
//                            }
//
//                            @Override
//                            public void onWriteFailure(BleException exception) {
//                                ToastUtils.showShort(ShengHaoApp.getInstance(),"蓝牙设置距离超出!");
//                                // 发送数据到设备失败
//                            }
//                        });
//                // 这里是你想要延迟执行的代码
//                // 例如：更新UI或处理数据
//            }
//        };
//        handler.postDelayed(delayeDisConnectSettingdRunnable,400);
//
//    }
//    private void sendCMD(byte[] limitNumberConnect, byte[] limitNumberDisConnect) {
//        Handler handler = new Handler();
//        Runnable delayeConnectSettingdRunnable = new Runnable() {
//            @Override
//            public void run() {
//                BleManager.getInstance().write(
//                        bleDevice,
//                        BleConstant.SERVICE_UUID,
//                        BleConstant.NOTIFY_UUID,
//                        limitNumberConnect,
//                        new BleWriteCallback() {
//                            @Override
//                            public void onWriteSuccess(int current, int total, byte[] justWrite) {
//                                isConnectSettingWrite = true;
//                                // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
//                            }
//
//                            @Override
//                            public void onWriteFailure(BleException exception) {
//                                ToastUtils.showShort(ShengHaoApp.getInstance(),"蓝牙设置距离超出!");
//                                isConnectSettingWrite = true;
//                                // 发送数据到设备失败
//                            }
//                        });
//
//            }
//        };
//        handler.postDelayed(delayeConnectSettingdRunnable,200);
//
//
//        // 延迟执行的Runnable
//        Runnable delayeDisConnectSettingdRunnable = new Runnable() {
//            @Override
//            public void run() {
//                BleManager.getInstance().write(
//                        bleDevice,
//                        BleConstant.SERVICE_UUID,
//                        BleConstant.NOTIFY_UUID,
//                        limitNumberDisConnect,
//                        new BleWriteCallback() {
//                            @Override
//                            public void onWriteSuccess(int current, int total, byte[] justWrite) {
//                                isDisConnectSettingWrite = true;
//                                // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
//                            }
//
//                            @Override
//                            public void onWriteFailure(BleException exception) {
//                                ToastUtils.showShort(ShengHaoApp.getInstance(),"蓝牙设置距离超出!");
//                                isDisConnectSettingWrite = true;
//                                // 发送数据到设备失败
//                            }
//                        });
//                // 这里是你想要延迟执行的代码
//                // 例如：更新UI或处理数据
//            }
//        };
//        handler.postDelayed(delayeDisConnectSettingdRunnable,400);
//
//    }
//
//    void switchAlias(BlueRssi blueAlias){
//        LogUtils.e(TAG, "当前线程: " + Thread.currentThread().getName());
//        llWeak.setBackgroundResource(R.drawable.bg_tooth_gear_unselect);
//        llMiddle.setBackgroundResource(R.drawable.bg_tooth_gear_unselect);
//        llStrong.setBackgroundResource(R.drawable.bg_tooth_gear_unselect);
//        llConfig.setBackgroundResource(R.drawable.bg_tooth_gear_unselect);
//
//        tvWeak.setTextColor(Color.parseColor("#99000000"));
//        tvMid.setTextColor(Color.parseColor("#99000000"));
//        tvStrong.setTextColor(Color.parseColor("#99000000"));
//
//        tvConfig.setTextColor(Color.parseColor("#99000000"));
//        tvConfigDistance.setTextColor(Color.parseColor("#99000000"));
//
//        tvConnectWeakValue.setTextColor(Color.parseColor("#99000000"));
//        tvConnectMidValue.setTextColor(Color.parseColor("#99000000"));
//        tvConnectStrongValue.setTextColor(Color.parseColor("#99000000"));
//
//        tvDisConnectWeakValue.setTextColor(Color.parseColor("#99000000"));
//        tvDisConnectMidValue.setTextColor(Color.parseColor("#99000000"));
//        tvDisConnectStrongValue.setTextColor(Color.parseColor("#99000000"));
//
//
//        if(blueAlias == BlueRssi.WEAK){
//            llWeak.setBackgroundResource(R.drawable.bg_tooth_gear_selected);
//            tvWeak.setTextColor(Color.parseColor("#FF1C70E6"));
//            tvConnectWeakValue.setTextColor(Color.parseColor("#FF1C70E6"));
//            tvDisConnectWeakValue.setTextColor(Color.parseColor("#FF1C70E6"));
//
//        }else if(blueAlias == BlueRssi.MIDDLE){
//            llMiddle.setBackgroundResource(R.drawable.bg_tooth_gear_selected);
//            tvMid.setTextColor(Color.parseColor("#FF1C70E6"));
//            tvConnectMidValue.setTextColor(Color.parseColor("#FF1C70E6"));
//            tvDisConnectMidValue.setTextColor(Color.parseColor("#FF1C70E6"));
//
//        }else if(blueAlias == BlueRssi.STRONG){
//            llStrong.setBackgroundResource(R.drawable.bg_tooth_gear_selected);
//            tvStrong.setTextColor(Color.parseColor("#FF1C70E6"));
//            tvConnectStrongValue.setTextColor(Color.parseColor("#FF1C70E6"));
//            tvDisConnectStrongValue.setTextColor(Color.parseColor("#FF1C70E6"));
//
//        }else if(blueAlias == null){
//            llConfig.setBackgroundResource(R.drawable.bg_tooth_gear_selected);
//            tvConfigDistance.setTextColor(Color.parseColor("#FF1C70E6"));
//            tvConfig.setTextColor(Color.parseColor("#FF1C70E6"));
//        }
//    }
//
//    public  String toHexString(String input) {
//        byte[] bytes = input.getBytes();
//        BigInteger bigInteger = new BigInteger(1, bytes);
//        String hexString = bigInteger.toString(16);
//        return hexString;
//    }
//
//
//    public static byte[] hexStringToByteArray(String s) {
//        int len = s.length();
//        byte[] data = new byte[len / 2];
//        for (int i = 0; i < len; i += 2) {
//            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
//                    + Character.digit(s.charAt(i+1), 16));
//        }
//        return data;
//    }
//
//
//
//
//
//
//
//}
//
//
//
