package com.shenghao.blesdk.manager;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.shenghao.blesdk.BleConstant;
import com.shenghao.blesdk.BleSdk;
import com.shenghao.blesdk.beacon.Beacon;
import com.shenghao.blesdk.beacon.BeaconItem;
import com.shenghao.blesdk.callback.BleConnectCallback;
import com.shenghao.blesdk.callback.BleStateListener;
import com.shenghao.blesdk.entity.BleSdkDevice;
import com.shenghao.blesdk.exception.BleSdkException;
import com.shenghao.blesdk.utils.BleConfigManager;
import com.shenghao.blesdk.utils.LogUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BleConnectionManager {

    private static final String TAG = "BleConnectionManager";
    private static final int RECONNECT_INTERVAL = 5000;

    private Context context;
    private Handler handler;
    private Runnable autoConnectRunnable;
    private Runnable connectTimeoutRunnable;
    private boolean isAutoConnectEnabled = true;
    private boolean isConnecting = false;
    private boolean isLoopRunning = false;
    private BleStateListener stateListener;
    private String lastConnectedMac;
    private BluetoothAdapter bluetoothAdapter;

    public BleConnectionManager(Context context) {
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.isAutoConnectEnabled = BleConfigManager.getInstance(context).isAutoConnectEnabled();
        initAutoConnectRunnable();
        initConnectTimeoutRunnable();
        if (isAutoConnectEnabled) {
            startAutoConnectLoop();
        }
    }

    private void initAutoConnectRunnable() {
        autoConnectRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isAutoConnectEnabled) {
                        Log.d(TAG, "自动连接已关闭，跳过检测");
                        handler.postDelayed(this, RECONNECT_INTERVAL);
                        return;
                    }
                    LogUtils.e(TAG, "自动检测连接线程running:" + Thread.currentThread().getName());

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                            != PackageManager.PERMISSION_GRANTED) {
                        handler.postDelayed(this, RECONNECT_INTERVAL);
                        return;
                    }

                    String mac = BleConfigManager.getInstance().getBleMac();
                    if (!TextUtils.isEmpty(mac)) {
                        BluetoothDevice deviceble = bluetoothAdapter.getRemoteDevice(mac);
                        if (deviceble.getBondState() == BluetoothDevice.BOND_BONDED) {
                            connect(mac, null);
                        } else {
                            tryConnect2();
                        }
                    }
                    handler.postDelayed(this, RECONNECT_INTERVAL);
                } catch (Exception e) {
                    Log.e(TAG, "Auto connect runnable error", e);
                    handler.postDelayed(this, RECONNECT_INTERVAL);
                }
            }
        };
    }

    private void initConnectTimeoutRunnable() {
        connectTimeoutRunnable = new Runnable() {
            @Override
            public void run() {
                if (isConnecting) {
                    Log.e(TAG, "Connect timeout, resetting isConnecting flag");
                    isConnecting = false;
                    if (stateListener != null) {
                        stateListener.onConnectFailed(lastConnectedMac, "Connect timeout");
                    }
                }
            }
        };
    }

    private void tryConnect2() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                Beacon beacon = new Beacon(bleDevice.getScanRecord());
                List<BeaconItem> mItems = beacon.mItems;
                String uid = "";
                for (int i = 0; i < mItems.size(); i++) {
                    BeaconItem item = mItems.get(i);
                    if (item.type == 255) {
                        uid = bytesToHex(item.bytes);
                    }
                }
                String name = bleDevice.getName();
                if (TextUtils.isEmpty(name)) {
                    name = "";
                }
                if (uid.startsWith(BleConstant.UID) || uid.toUpperCase().startsWith(BleConstant.UID_SH)) {
                    String mac = BleConfigManager.getInstance().getBleMac();
                    if (!TextUtils.isEmpty(mac) && !BleManager.getInstance().isConnected(mac)
                            && mac.equals(bleDevice.getMac()) && !isConnecting) {
                        LogUtils.e(TAG, "尝试连接：" + mac + ",");
                        BleManager.getInstance().cancelScan();
                        connect(mac, null);
                    }
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
            }
        });
    }

    public void cancelCurrentScan() {
        BleManager.getInstance().cancelScan();
    }

    public void connect(String mac, final BleConnectCallback callback) {
        if (TextUtils.isEmpty(mac)) {
            if (callback != null) {
                callback.onFailed(new BleSdkException(BleSdkException.CODE_NULL, "MAC address cannot be empty"));
            }
            if (stateListener != null) {
                stateListener.onConnectFailed(mac, "MAC address cannot be empty");
            }
            return;
        }

        if (BleManager.getInstance().isConnected(mac)) {
            BleDevice device = findConnectedDevice(mac);
            BleSdkDevice sdkDevice = device != null ? new BleSdkDevice(device) : null;
            if (callback != null) {
                callback.onSuccess(sdkDevice);
            }
            if (stateListener != null) {
                stateListener.onConnected(mac, sdkDevice);
            }
            return;
        }

        if (isConnecting) {
            if (callback != null) {
                callback.onFailed(new BleSdkException(BleSdkException.CODE_CONNECTING, "Already connecting"));
            }
            return;
        }

        isConnecting = true;
        lastConnectedMac = mac;

        if (stateListener != null) {
            stateListener.onConnecting(mac);
        }

        handler.removeCallbacks(connectTimeoutRunnable);
        handler.postDelayed(connectTimeoutRunnable, 15000);

        BleManager.getInstance().destroy();
        BleManager.getInstance().connect(mac, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                Log.d(TAG, "Start connecting to: " + mac);
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, com.clj.fastble.exception.BleException exception) {
                handler.removeCallbacks(connectTimeoutRunnable);
                isConnecting = false;
                String errorMsg = exception != null ? exception.getDescription() : "Unknown error";

                if (callback != null) {
                    callback.onFailed(new BleSdkException(BleSdkException.CODE_CONNECT_ERROR, errorMsg));
                }
                if (stateListener != null) {
                    stateListener.onConnectFailed(mac, errorMsg);
                }
                Log.e(TAG, "Connect failed: " + errorMsg);
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, android.bluetooth.BluetoothGatt gatt, int status) {
                handler.removeCallbacks(connectTimeoutRunnable);
                isConnecting = false;
                BleConfigManager.getInstance().setBleMac(bleDevice.getMac());
                BleSdkDevice sdkDevice = new BleSdkDevice(bleDevice);

                if (callback != null) {
                    callback.onSuccess(sdkDevice);
                }
                if (stateListener != null) {
                    stateListener.onConnected(bleDevice.getMac(), sdkDevice);
                }
                Log.d(TAG, "Connect success: " + bleDevice.getName() + " (" + bleDevice.getMac() + ")");
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice,
                    android.bluetooth.BluetoothGatt gatt, int status) {
                handler.removeCallbacks(connectTimeoutRunnable);
                isConnecting = false;

                if (callback != null) {
                    callback.onDisconnected();
                }
                if (stateListener != null) {
                    stateListener.onDisconnected(bleDevice != null ? bleDevice.getMac() : mac);
                }
                Log.d(TAG, "Disconnected: " + (bleDevice != null ? bleDevice.getMac() : mac));
            }
        });
    }

    public void disconnect(String mac) {
        if (!TextUtils.isEmpty(mac)) {
            BleDevice device = findConnectedDevice(mac);
            if (device != null) {
                BleManager.getInstance().disconnect(device);
            }
        }
    }

    public void disconnectAll() {
        BleManager.getInstance().disconnectAllDevice();
    }

    public boolean isConnected(String mac) {
        return !TextUtils.isEmpty(mac) && BleManager.getInstance().isConnected(mac);
    }

    private BleDevice findConnectedDevice(String mac) {
        List<BleDevice> connectedDevices = BleManager.getInstance().getAllConnectedDevice();
        if (connectedDevices != null) {
            for (BleDevice device : connectedDevices) {
                if (mac.equals(device.getMac())) {
                    return device;
                }
            }
        }
        return null;
    }

    public BleSdkDevice getConnectedDevice(String mac) {
        if (TextUtils.isEmpty(mac)) {
            return null;
        }
        BleDevice device = findConnectedDevice(mac);
        return device != null ? new BleSdkDevice(device) : null;
    }

    public void setAutoConnectEnabled(boolean enabled) {
        this.isAutoConnectEnabled = enabled;
        BleConfigManager.getInstance().setAutoConnectEnabled(enabled);

        if (enabled && !isLoopRunning) {
            startAutoConnectLoop();
        }
    }

    public boolean isAutoConnectEnabled() {
        return isAutoConnectEnabled;
    }

    public void startAutoConnectLoop() {
        if (!isLoopRunning) {
            handler.post(autoConnectRunnable);
            isLoopRunning = true;
            Log.d(TAG, "Auto connect loop started");
        }
    }

    public void stopAutoConnectLoop() {
        if (isLoopRunning) {
            handler.removeCallbacks(autoConnectRunnable);
            isLoopRunning = false;
            Log.d(TAG, "Auto connect loop stopped");
        }
    }

    public boolean isLoopRunning() {
        return isLoopRunning;
    }

    public void setStateListener(BleStateListener listener) {
        this.stateListener = listener;
        if (listener != null) {
            List<BleDevice> connectedDevices = BleManager.getInstance().getAllConnectedDevice();
            if (connectedDevices != null && !connectedDevices.isEmpty()) {
                BleDevice device = connectedDevices.get(0);
                listener.onConnected(device.getMac(), new BleSdkDevice(device));
            }
        }
    }

    public BleStateListener getStateListener() {
        return stateListener;
    }

    public Set<BluetoothDevice> getPairedDevices() {
        if (bluetoothAdapter == null) {
            return null;
        }
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            return bluetoothAdapter.getBondedDevices();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get paired devices", e);
            return null;
        }
    }

    public List<BluetoothDevice> getConnectedDevices() {
        List<BluetoothDevice> list = new ArrayList<>();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "设备不支持蓝牙");
            return list;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Log.e(TAG, "蓝牙未开启");
            return list;
        }

        Set<BluetoothDevice> bondedDevices = getPairedDevices();
        if (bondedDevices == null || bondedDevices.isEmpty()) {
            Log.d(TAG, "没有已绑定的设备");
            return list;
        }

        try {
            Method method = BluetoothAdapter.class.getDeclaredMethod("getConnectionState", (Class[]) null);
            method.setAccessible(true);
            int state = (int) method.invoke(bluetoothAdapter, (Object[]) null);

            if (state == BluetoothAdapter.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothAdapter.STATE_CONNECTED");
                Log.i(TAG, "devices:" + bondedDevices.size());
                for (BluetoothDevice device : bondedDevices) {
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    isConnectedMethod.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    if (isConnected) {
                        Log.i(TAG, "connected:" + device.getName());
                        list.add(device);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<BleSdkDevice> getSdkConnectedDevices() {
        List<BleSdkDevice> list = new ArrayList<>();
        List<BleDevice> connectedDevices = BleManager.getInstance().getAllConnectedDevice();
        if (connectedDevices != null && !connectedDevices.isEmpty()) {
            for (BleDevice device : connectedDevices) {
                list.add(new BleSdkDevice(device));
            }
        }
        return list;
    }

    public static boolean isContains(List<BleDevice> allConnectedDevice, String mac) {
        LogUtils.e("isContains", "已连接蓝牙个数" + allConnectedDevice.size());
        for (BleDevice item : allConnectedDevice) {
            if (TextUtils.isEmpty(item.getName())) {
                return false;
            }
            if (item.getMac().equals(mac)) {
                LogUtils.e("isContains", "true");
                return true;
            }
        }
        LogUtils.e("isContains", "false");
        return false;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b & 0xFF));
        }
        return sb.toString();
    }

    public String getLastConnectedMac() {
        return lastConnectedMac;
    }
}