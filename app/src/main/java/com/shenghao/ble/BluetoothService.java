//package com.shenghao.ble;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.IBinder;
//import android.util.Log;
//
//import com.shenghao.utils.SPUtils;
//
//public class BluetoothService extends Service {
//    private BluetoothUtils bluetoothUtils;
//    private static BluetoothService instance;
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        instance = this;
//        bluetoothUtils = new BluetoothUtils(this);
//        bluetoothUtils.start();
//        Log.d("BluetoothService", "Service started");
//    }
//
//    // 添加静态方法供外部调用
//    public static void updateAutoConnectStatus(Context context, boolean enabled) {
//        if (instance != null && instance.bluetoothUtils != null) {
//            instance.bluetoothUtils.setAutoConnectEnabled(enabled);
//        } else {
//            // 如果服务未启动，先保存设置，等服务启动后会读取这个设置
//            SPUtils.getInstance().putBoolean(SPUtils.SP_AUTO_CONNECT_ENABLED, enabled);
//        }
//    }
//
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // 如果服务被杀死后重新创建，可以在这里重新启动蓝牙连接逻辑
//        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        bluetoothUtils.stop();
//        Log.d("BluetoothService", "Service stopped");
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null; // 如果不需要绑定服务，返回 null
//    }
//}
