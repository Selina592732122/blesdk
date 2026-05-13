package com.shenghao.blesdk.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.shenghao.blesdk.BleConstant;
import com.shenghao.blesdk.command.CommandUtils;
import com.shenghao.blesdk.utils.BleConfigManager;
import com.shenghao.blesdk.utils.ByteUtils;

public class PairingManager {

    private static final String TAG = "PairingManager";
    private static volatile PairingManager instance;

    private Context context;
    private Handler handler;
    private BleDevice bleDevice;
    private PairingCallback pairingCallback;
    private byte flowFlag;
    private boolean isBonded = false;

    private final BroadcastReceiver pairingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "Broadcast action: " + action);

            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int variant = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);
                Log.d(TAG, "配对请求: " + device.getName() + ", 类型: " + variant);
                if (pairingCallback != null) {
                    pairingCallback.onPairingRequest(device, variant);
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                Log.d(TAG, "绑定状态变化: " + device.getName() +
                        ", 新状态: " + bondState + ", 旧状态: " + previousBondState);

                switch (bondState) {
                    case BluetoothDevice.BOND_BONDED:
                        isBonded = true;
                        BleConfigManager.getInstance().setBleMac(device.getAddress());

                        if (pairingCallback != null) {
                            pairingCallback.onPairingSuccess();
                        }

                        handler.postDelayed(() -> {
//                            sendDisconnectCommand();
                            BleConnectionManager connectionManager = com.shenghao.blesdk.BleSdk.getInstance().getBleConnectionManager();
                            connectionManager.setAutoConnectEnabled(true);
                        }, 3000);
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        if (pairingCallback != null) {
                            pairingCallback.onPairingInProgress();
                        }
                        break;
                    case BluetoothDevice.BOND_NONE:
                        isBonded = false;
                        if (previousBondState == BluetoothDevice.BOND_BONDING) {
                            if (pairingCallback != null) {
                                pairingCallback.onPairingCancelled();
                            }
                            BleConnectionManager connectionManager = com.shenghao.blesdk.BleSdk.getInstance().getBleConnectionManager();
                            connectionManager.setAutoConnectEnabled(true);
                        }
                        break;
                }
            }
        }
    };

    private PairingManager(Context context) {
        this.context = context.getApplicationContext();
        this.handler = new Handler(Looper.getMainLooper());
        registerReceiver();
    }

    public static PairingManager getInstance(Context context) {
        if (instance == null) {
            synchronized (PairingManager.class) {
                if (instance == null) {
                    instance = new PairingManager(context);
                }
            }
        }
        return instance;
    }

    public static PairingManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("PairingManager not initialized. Call getInstance(Context) first.");
        }
        return instance;
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(pairingReceiver, filter);
    }

    public void unregisterReceiver() {
        try {
            context.unregisterReceiver(pairingReceiver);
        } catch (Exception e) {
            Log.e(TAG, "Failed to unregister receiver", e);
        }
    }

    public void setBleDevice(com.shenghao.blesdk.entity.BleSdkDevice bleDevice) {
        this.bleDevice = bleDevice.getOriginalDevice();
        updateBondState();
    }

    private void updateBondState() {
        if (bleDevice == null) return;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            BluetoothDevice device = adapter.getRemoteDevice(bleDevice.getMac());
            isBonded = device.getBondState() == BluetoothDevice.BOND_BONDED;
        }
    }

    public boolean isBonded() {
        return isBonded;
    }

    public void startPairing(PairingCallback callback) {
        this.pairingCallback = callback;

        if (bleDevice == null) {
            if (callback != null) {
                callback.onPairingFailed("设备未设置");
            }
            return;
        }

        if (!BleManager.getInstance().isConnected(bleDevice.getMac())) {
            if (callback != null) {
                callback.onPairingFailed("设备未连接");
            }
            return;
        }

        if (isBonded) {
            if (callback != null) {
                callback.onPairingSuccess();
            }
            return;
        }

        BleConnectionManager connectionManager = com.shenghao.blesdk.BleSdk.getInstance().getBleConnectionManager();
        connectionManager.setAutoConnectEnabled(false);

        setupNotification();
    }

    private void setupNotification() {
        BleManager.getInstance().notify(
                bleDevice,
                BleConstant.SERVICE_UUID_SH,
                BleConstant.NOTIFY_UUID_SH,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        Log.d(TAG, "通知注册成功");
                        sendClearPairingCommand();
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        Log.e(TAG, "通知注册失败: " + exception.getDescription());
                        if (pairingCallback != null) {
                            pairingCallback.onPairingFailed("通知注册失败: " + exception.getDescription());
                        }
                        BleConnectionManager connectionManager = com.shenghao.blesdk.BleSdk.getInstance().getBleConnectionManager();
                        connectionManager.setAutoConnectEnabled(true);
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        String hex = ByteUtils.bytes2HexStr(data);
                        Log.d(TAG, "收到数据: " + hex);
                        handleNotificationData(hex);
                    }
                }
        );
    }

    private void handleNotificationData(String hex) {
        if (hex.startsWith("ff120080")) {
            Log.d(TAG, "收到配对请求响应");
            sendAllowPairingCommand();
        } else if (hex.startsWith("ff120018")) {
            byte[] bytes = ByteUtils.hexStr2Bytes(hex);
            if (!isBonded && flowFlag == bytes[4]) {
                Log.d(TAG, "流水号匹配，开始蓝牙配对");
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (adapter != null && bleDevice != null) {
                    BluetoothDevice device = adapter.getRemoteDevice(bleDevice.getMac());
                    device.createBond();
                }
            }
        } else if (hex.startsWith("ff1212")) {
            Log.d(TAG, "收到PKE状态数据");
        }
    }

    private void sendClearPairingCommand() {
        Log.d(TAG, "发送清空配对指令");
        byte[] command = CommandUtils.generateCommand((byte) 0x00, (byte) 0x80, null);
        sendCommand(command);
    }

    private void sendAllowPairingCommand() {
        Log.d(TAG, "发送允许配对指令");
        byte[] bytes = com.shenghao.blesdk.api.PkeCommandApi.generatePKECommand(
                false, false, true, false,
                BleConfigManager.getInstance().getBleUnlockRssi(),
                BleConfigManager.getInstance().getBleLockRssi(), 1, "0000");
        flowFlag = bytes[4];
        sendCommand(bytes);
    }

    private void sendDisconnectCommand() {
        Log.d(TAG, "发送断开连接指令");
        byte[] bytes = com.shenghao.blesdk.api.PkeCommandApi.generatePKECommand(
                false, false, false, true,
                BleConfigManager.getInstance().getBleUnlockRssi(),
                BleConfigManager.getInstance().getBleLockRssi(), 1, "0000");
        sendCommand(bytes);
    }

    private void sendCommand(byte[] command) {
        if (bleDevice == null) {
            Log.e(TAG, "设备为空，无法发送指令");
            if (pairingCallback != null) {
                pairingCallback.onPairingFailed("设备为空");
            }
            return;
        }
        if (!BleManager.getInstance().isConnected(bleDevice.getMac())) {
            Log.e(TAG, "设备未连接，无法发送指令");
            if (pairingCallback != null) {
                pairingCallback.onPairingFailed("设备未连接");
            }
            return;
        }

        Log.d(TAG, "发送指令: " + ByteUtils.bytes2HexStr(command));
        Log.d(TAG, "服务UUID: " + BleConstant.SERVICE_UUID_SH);
        Log.d(TAG, "特征值UUID: " + BleConstant.NOTIFY_UUID_SH);
        BleManager.getInstance().write(
                bleDevice,
                BleConstant.SERVICE_UUID_SH,
                BleConstant.NOTIFY_UUID_SH,
                command,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.d(TAG, "指令发送成功");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Log.e(TAG, "指令发送失败: " + exception.getDescription());
                        if (pairingCallback != null) {
                            pairingCallback.onPairingFailed("指令发送失败: " + exception.getDescription());
                        }
                    }
                }
        );
    }

    public void setPairingCallback(PairingCallback callback) {
        this.pairingCallback = callback;
    }

    public interface PairingCallback {
        void onPairingRequest(BluetoothDevice device, int variant);
        void onPairingInProgress();
        void onPairingSuccess();
        void onPairingFailed(String errorMessage);
        void onPairingCancelled();
    }

    public static abstract class SimplePairingCallback implements PairingCallback {
        @Override
        public void onPairingRequest(BluetoothDevice device, int variant) {}

        @Override
        public void onPairingInProgress() {}

        @Override
        public void onPairingSuccess() {}

        @Override
        public void onPairingFailed(String errorMessage) {}

        @Override
        public void onPairingCancelled() {}
    }
}