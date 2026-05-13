package com.shenghao.blesdk.api;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.shenghao.blesdk.BleConstant;

public class CommandApi {

    private static final String TAG = "CommandApi";

    public static void sendCommand(BleDevice bleDevice, String command, com.shenghao.blesdk.callback.BleWriteCallback callback) {
        if (command == null) {
            command = "";
        }
        BleManager.getInstance().write(
                bleDevice,
                BleConstant.SERVICE_UUID_SH,
                BleConstant.WRITE_UUID_SH,
                command.getBytes(),
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        if (callback != null) {
                            callback.onWriteSuccess(current, total, justWrite);
                        }
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        if (callback != null) {
                            callback.onWriteFailed(exception);
                        }
                    }
                }
        );
    }

    public static void sendCommand(BleDevice bleDevice, byte[] data, com.shenghao.blesdk.callback.BleWriteCallback callback) {
        BleManager.getInstance().write(
                bleDevice,
                BleConstant.SERVICE_UUID_SH,
                BleConstant.WRITE_UUID_SH,
                data,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        if (callback != null) {
                            callback.onWriteSuccess(current, total, justWrite);
                        }
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        if (callback != null) {
                            callback.onWriteFailed(exception);
                        }
                    }
                }
        );
    }

    public static void setNotifyListener(com.shenghao.blesdk.entity.BleSdkDevice bleDevice, com.shenghao.blesdk.callback.BleNotifyCallback callback) {
        BleManager.getInstance().notify(
                bleDevice.getOriginalDevice(),
                BleConstant.SERVICE_UUID_SH,
                BleConstant.NOTIFY_UUID_SH,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        if (callback != null) {
                            callback.onNotifySuccess();
                        }
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        if (callback != null) {
                            callback.onNotifyFailed(exception);
                        }
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        if (callback != null) {
                            callback.onCharacteristicChanged(data);
                        }
                    }
                }
        );
    }

    public static void setNotifyListener(BleDevice bleDevice, String serviceUUID, String notifyUUID,
            com.shenghao.blesdk.callback.BleNotifyCallback callback) {
        BleManager.getInstance().notify(
                bleDevice,
                serviceUUID,
                notifyUUID,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        if (callback != null) {
                            callback.onNotifySuccess();
                        }
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        if (callback != null) {
                            callback.onNotifyFailed(exception);
                        }
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        if (callback != null) {
                            callback.onCharacteristicChanged(data);
                        }
                    }
                }
        );
    }

    public static String buildRssiLimitCommand(boolean connect, int value) {
        String prefix = connect ? BleConstant.LIMIT_STR_CONNECT : BleConstant.LIMIT_STR_DISCONNECT;
        return prefix + value;
    }

    public static String buildRssiCheckCommand(boolean connect) {
        return connect ? BleConstant.LIMIT_STR_CONNECT_CHECK : BleConstant.LIMIT_STR_DISCONNECT_CHECK;
    }

    public static String buildRssiQueryCommand() {
        return BleConstant.LIMIT_STR_RSSI;
    }

    public static void sendRssiLimitCommand(BleDevice bleDevice, boolean connect, int value,
            com.shenghao.blesdk.callback.BleWriteCallback callback) {
        String command = buildRssiLimitCommand(connect, value);
        sendCommand(bleDevice, command, callback);
    }

    public static void sendRssiCheckCommand(BleDevice bleDevice, boolean connect,
            com.shenghao.blesdk.callback.BleWriteCallback callback) {
        String command = buildRssiCheckCommand(connect);
        sendCommand(bleDevice, command, callback);
    }

    public static void sendRssiQueryCommand(BleDevice bleDevice, com.shenghao.blesdk.callback.BleWriteCallback callback) {
        String command = buildRssiQueryCommand();
        sendCommand(bleDevice, command, callback);
    }

    public static void writeWithCustomUUID(BleDevice bleDevice, String serviceUUID, String characteristicUUID,
            byte[] data, com.shenghao.blesdk.callback.BleWriteCallback callback) {
        BleManager.getInstance().write(
                bleDevice,
                serviceUUID,
                characteristicUUID,
                data,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        if (callback != null) {
                            callback.onWriteSuccess(current, total, justWrite);
                        }
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        if (callback != null) {
                            callback.onWriteFailed(exception);
                        }
                    }
                }
        );
    }
}