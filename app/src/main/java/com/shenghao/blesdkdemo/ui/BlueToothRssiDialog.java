package com.shenghao.blesdkdemo.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.ShengHaoApp;
import com.shenghao.blesdk.BleConstant;
import com.shenghao.blesdk.enums.BlueRssi;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

public class BlueToothRssiDialog extends Dialog {
    public final String TAG = this.getClass().getSimpleName();
    private BleDevice bleDevice;
    private BlueRssi blueRssi = BlueRssi.MIDDLE;
    Button btnConfirmTop,btnConfirmBottom;
    private TextView tvMin,tvMax;
    private TextView tvTop,tvBottom;
    private Deque<Integer> rssiQueue = new ArrayDeque<>(3);
    private Handler handler = new Handler();
    private Runnable readRssiRunnable = new Runnable() {
        @Override
        public void run() {
            sendCMD((BleConstant.LIMIT_STR_RSSI + "\r\n").getBytes());
            handler.postDelayed(readRssiRunnable, 1000); // 1秒后再次读取
        }
    };
    private void updateAverageDisplay() {
        int average;
        synchronized (rssiQueue) {
//            LogUtils.e(TAG,"当前线程："+Thread.currentThread().getName()+","+rssiQueue.size());
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

        final String formattedAverage = String.format(Locale.getDefault(), "%d", -average);
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
    public BlueToothRssiDialog(@NonNull Context context, int themeResId, int layout, int gravity) {

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

        initView(view);
    }

    @Override
    public void show() {
        startRssiUpdates();
        super.show();
    }

    @Override
    public void dismiss() {
        stopRssiUpdates();
        super.dismiss();
    }

    private void notifyBle() {
        if(bleDevice == null)
            return;
        BleManager.getInstance().notify(
                bleDevice,
                BleConstant.SERVICE_UUID,
                BleConstant.NOTIFY_UUID,
                new BleNotifyCallback() {

                    @Override
                    public void onNotifySuccess() {
                        sendCMD((BleConstant.LIMIT_STR_CONNECT_CHECK + "\r\n").getBytes());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendCMD((BleConstant.LIMIT_STR_DISCONNECT_CHECK + "\r\n").getBytes());
                            }
                        },500);

                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        ToastUtils.showShort(getContext(),"onNotifyFailure:"+exception.getDescription());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        String str = null;
                        try {
                            str = new String(data,"UTF-8");
//                                    ToastUtils.showShort(WanShiDaApp.getInstance(),str);
                            LogUtils.e(TAG,str);
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }

                        if(str.startsWith("+RSSI")){
                            int rssi = Integer.parseInt(str.split(":")[1].trim());
                            synchronized (rssiQueue) {
                                // 维护最近三次数据
                                if (rssiQueue.size() == 3) {
                                    updateAverageDisplay(); // 更新显示
                                    rssiQueue.removeFirst();
                                }
                                rssiQueue.addLast(rssi);
//                                LogUtils.e(TAG,"加完后："+rssiQueue.size());
                            }
                        } else if(str.startsWith("+LRSSI")){
                            int lrssi = -Integer.parseInt(str.split(":")[1].trim());
                            tvBottom.setText("历史关锁值："+lrssi);
                        }else if(str.startsWith("+ULRSSI")){
                            int ulrssi = -Integer.parseInt(str.split(":")[1].trim());
                            tvTop.setText("历史启动值："+ulrssi);
                        }
                    }
                }
        );
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
        notifyBle();
    }

    private void initView(View view) {
        btnConfirmTop = view.findViewById(R.id.btnConfirmTop);
        btnConfirmBottom = view.findViewById(R.id.btnConfirmBottom);
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

        btnConfirmTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = tvMin.getText().toString();
                if(TextUtils.isEmpty(string))
                    return;
                //ULock
                String ATConnectOrder = (BleConstant.LIMIT_STR_CONNECT+"-"+ Integer.parseInt(string) +"\r\n");
                byte[] limitNumberConnect = ATConnectOrder.getBytes();
                sendCMD(limitNumberConnect);
                btnConfirmTop.setEnabled(false);
                btnConfirmTop.setText("已确认");
                tvMin.setText(string);
            }
        });
        btnConfirmBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = tvMax.getText().toString();
                if(TextUtils.isEmpty(string))
                    return;
                //Lock
                String ATConnectOrder = (BleConstant.LIMIT_STR_DISCONNECT+"-"+Integer.parseInt(string) +"\r\n");
                byte[] limitNumberConnect = ATConnectOrder.getBytes();
                sendCMD(limitNumberConnect);
                btnConfirmBottom.setEnabled(false);
                btnConfirmBottom.setText("已确认");
                tvMax.setText(string);
            }
        });
    }

    private void sendCMD(byte[] bytes) {
        LogUtils.e(TAG,"sendCMD"+new String(bytes));
        BleManager.getInstance().write(
                bleDevice,
                BleConstant.SERVICE_UUID,
                BleConstant.NOTIFY_UUID,
                bytes,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        ToastUtils.showShort(ShengHaoApp.getInstance(),"蓝牙设置距离超出!");
                        // 发送数据到设备失败
                    }
                });
    }

    public  String toHexString(String input) {
        byte[] bytes = input.getBytes();
        BigInteger bigInteger = new BigInteger(1, bytes);
        String hexString = bigInteger.toString(16);
        return hexString;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }







}







