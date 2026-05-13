package com.shenghao.blesdk.api;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.shenghao.blesdk.BleConstant;
import com.shenghao.blesdk.command.CommandUtils;
import com.shenghao.blesdk.enums.BlueRssiPke;
import com.shenghao.blesdk.utils.LogUtils;

public class PkeCommandApi {

    private static final String TAG = "PkeCommandApi";

    /**
     * 生成PKE配置指令（简化版）
     *
     * @param unlockDb 开锁db值
     * @param lockDb   关锁db值
     * @return 指令字节数组
     */
    public static byte[] generatePKECommand(int unlockDb, int lockDb) {
        return generatePKECommand(true, true, true, false, unlockDb, lockDb, 1, "0000");
    }

    /**
     * 生成PKE相关配置指令
     *
     * @param enablePKE          是否打开PKE
     * @param requestPairing     设备发起配对请求
     * @param allowPairing       允许设备发起配对
     * @param disconnect         发起断开连接
     * @param unlockDb           开锁db值 (0-255)
     * @param lockDb             关锁db值 (0-255)
     * @param lockDelay          关锁延时，单位秒 (0-255)
     * @param password           PKE蓝牙密码，4位数字
     * @return 指令字节数组
     */
    public static byte[] generatePKECommand(boolean enablePKE, boolean requestPairing, 
                                             boolean allowPairing, boolean disconnect,
                                             int unlockDb, int lockDb, int lockDelay,
                                             String password) {
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

        byte control1 = 0;
        if (enablePKE) {
            control1 |= 0x01;
        }
        if (requestPairing) {
            control1 |= 0x02;
        }
        if (allowPairing) {
            control1 |= 0x04;
        }
        if (disconnect) {
            control1 |= 0x08;
        }

        byte unlockDbByte = (byte) unlockDb;
        byte lockDbByte = (byte) lockDb;
        byte lockDelayByte = (byte) lockDelay;

        byte[] passwordBytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            passwordBytes[i] = (byte) (password.charAt(i) - '0');
        }

        byte[] body = new byte[]{
                control1, unlockDbByte, lockDbByte, lockDelayByte,
                passwordBytes[0], passwordBytes[1], passwordBytes[2], passwordBytes[3]
        };

        return CommandUtils.generateCommand((byte) 0x00, (byte) 0x18, body);
    }

    /**
     * 生成读取PKE配置指令
     *
     * @return 指令字节数组
     */
    public static byte[] generateReadPKECommand() {
        return CommandUtils.generateCommand((byte) 0x00, (byte) 0x69, new byte[]{0x00});
    }

    /**
     * 生成清空配对指令
     *
     * @return 指令字节数组
     */
    public static byte[] generateClearPairingCommand() {
        return CommandUtils.generateCommand((byte) 0x00, (byte) 0x80, null);
    }

    /**
     * 发送PKE指令
     *
     * @param device     蓝牙设备
     * @param command    指令字节数组
     * @param callback   写入回调
     */
    public static void sendPKECommand(com.shenghao.blesdk.entity.BleSdkDevice device, byte[] command, BleWriteCallback callback) {
        if (device == null || !BleManager.getInstance().isConnected(device.getOriginalDevice())) {
            LogUtils.e(TAG, "设备未连接");
            return;
        }
        LogUtils.e(TAG, "发送PKE指令: " + com.shenghao.blesdk.utils.ByteUtils.bytes2HexStr(command));
        
        BleManager.getInstance().write(
                device.getOriginalDevice(),
                BleConstant.SERVICE_UUID_SH,
                BleConstant.NOTIFY_UUID_SH,
                command,
                callback
        );
    }

    /**
     * 设置PKE通知监听
     *
     * @param device   蓝牙设备
     * @param callback 通知回调
     */
    public static void setPkeNotifyListener(BleDevice device, BleNotifyCallback callback) {
        BleManager.getInstance().notify(
                device,
                BleConstant.SERVICE_UUID_SH,
                BleConstant.NOTIFY_UUID_SH,
                callback
        );
    }

    /**
     * 根据档位获取RSSI配置
     *
     * @param rssiLevel RSSI档位
     * @return 包含unlockDb和lockDb的int数组
     */
    public static int[] getRssiConfig(BlueRssiPke rssiLevel) {
        return new int[]{rssiLevel.getLowRssi(), rssiLevel.getHighRssi()};
    }

    /**
     * 根据RSSI值获取档位
     *
     * @param unlockDb 开锁db值
     * @param lockDb   关锁db值
     * @return RSSI档位
     */
    public static BlueRssiPke getRssiLevel(int unlockDb, int lockDb) {
        return BlueRssiPke.fromRssi(unlockDb, lockDb);
    }
}