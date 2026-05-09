package com.shenghao.blesdk.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.shenghao.blesdk.BleSdk;
import com.shenghao.blesdk.manager.BleConnectionManager;
import com.shenghao.blesdk.utils.BleConfigManager;

public class BluetoothService extends Service {

    private static final String TAG = "BleService";
    private BleConnectionManager bleConnectionManager;
    private static BluetoothService instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        bleConnectionManager = BleSdk.getInstance().getBleConnectionManager();
        bleConnectionManager.startAutoConnectLoop();
        Log.d(TAG, "BleService started");
    }

    public static void updateAutoConnectStatus(Context context, boolean enabled) {
        if (instance != null && instance.bleConnectionManager != null) {
            instance.bleConnectionManager.setAutoConnectEnabled(enabled);
        } else {
            BleConfigManager.getInstance(context).setAutoConnectEnabled(enabled);
        }
    }

    public static BluetoothService getInstance() {
        return instance;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bleConnectionManager != null) {
            bleConnectionManager.stopAutoConnectLoop();
        }
        instance = null;
        Log.d(TAG, "BleService stopped");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}