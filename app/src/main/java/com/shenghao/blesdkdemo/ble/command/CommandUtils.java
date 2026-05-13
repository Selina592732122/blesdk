//package com.shenghao.blesdkdemo.ble.command;
//
//import com.shenghao.blesdkdemo.utils.ByteUtils;
//import com.shenghao.blesdkdemo.utils.LogUtils;
//import com.shenghao.blesdkdemo.utils.SPUtils;
//
//import java.util.Arrays;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class CommandUtils {
//    // 序列号计数器 (0-255循环)
//    private static final AtomicInteger serialCounter = new AtomicInteger(0);
//
//    /**
//     * 生成指令
//     *
//     * @param flag    1字节标志位
//     * @param code    1字节指令码
//     * @param body    可变长度的数据体
//     * @return 完整的指令字节数组
//     */
//    public static byte[] generateCommand(byte flag, byte code, byte[] body) {
//        // 获取并递增序列号 (0-255循环)
//        int serialInt = serialCounter.getAndUpdate((current) -> (current + 1) & 0xFF);
//        byte serial = (byte) serialInt;
//
//        // 计算数据部分（code + serial + body）
//        int bodyLength = body != null ? body.length : 0;
//        byte[] dataPart = new byte[2 + bodyLength];
//        dataPart[0] = code;
//        dataPart[1] = serial;
//        if (body != null && bodyLength > 0) {
//            System.arraycopy(body, 0, dataPart, 2, bodyLength);
//        }
//
//        // 计算填充长度（如果数据部分不足16字节则填充）
//        int paddingLen = dataPart.length < 16 ? 16 - dataPart.length : 0;
//        byte[] padding = new byte[paddingLen];
//        Arrays.fill(padding, (byte) 0xFF);
//
//        // 创建加密前的数据（code + serial + body + full）
//        byte[] encryptionInput = new byte[dataPart.length + paddingLen];
//        System.arraycopy(dataPart, 0, encryptionInput, 0, dataPart.length);
//        if (paddingLen > 0) {
//            System.arraycopy(padding, 0, encryptionInput, dataPart.length, paddingLen);
//        }
//
//        // 打印加密前的数据
//        LogUtils.e("CommandUtils加密前数据：", ByteUtils.bytes2HexStr(encryptionInput));
//
//        // 执行AES加密
//        byte[] encrypted = new byte[0];
//        try {
//            if(flag == 0x11)//用根密码
//            {
//                encrypted = AESUtils.aes128Encrypt(encryptionInput, SPUtils.getInstance().getString(SPUtils.SP_ONE_KEY_GEN_PWD)); // 使用您的实际密钥
//                LogUtils.e("CommandUtils加密根密码数据：", SPUtils.getInstance().getString(SPUtils.SP_ONE_KEY_GEN_PWD));
//            }
//            else if(flag == 0x12)//交换密码加密
//            {
//                encrypted = AESUtils.aes128Encrypt(encryptionInput, SPUtils.getInstance().getString(SPUtils.SP_ONE_KEY_PWD)); // 使用您的实际密钥
//                LogUtils.e("CommandUtils加密交换密码数据：", SPUtils.getInstance().getString(SPUtils.SP_ONE_KEY_PWD));
//            }else if(flag == 0x00){
//                encrypted = encryptionInput;
//            }
//            LogUtils.e("CommandUtils加密后数据：", ByteUtils.bytes2HexStr(encrypted));
//        } catch (Exception e) {
//            throw new RuntimeException("AES encryption failed", e);
//        }
//
//        // 计算总长度（从flag到sum的长度）
//        // 注意：这里使用加密前的长度计算，因为校验和基于原始数据
//        int totalLen = 1  // flag
//                + 1  // code
//                + 1  // serial
//                + bodyLength
//                + paddingLen
//                + 1; // sum
//        if (totalLen > 255) {
//            throw new IllegalArgumentException("Total length exceeds 255 bytes");
//        }
//
//        // 构建指令缓冲区
//        // 注意：实际数据长度会变化，因为加密后数据是16字节
//        byte[] packet = new byte[1 + 1 + 1 + encrypted.length + 1]; // 头部(1) + 长度(1) + flag(1) + 加密数据 + sum(1)
//        int pos = 0;
//
//        // 填充数据
//        packet[pos++] = (byte) 0xFF;          // 头部
//        packet[pos++] = (byte) totalLen;      // 长度（基于原始数据长度）
//        packet[pos++] = flag;                 // flag
//
//        // 使用加密数据替换原始数据部分
//        System.arraycopy(encrypted, 0, packet, pos, encrypted.length);
//        pos += encrypted.length;
//
//        // 创建用于校验和计算的临时数组（基于原始数据）
//        byte[] checksumData = new byte[1 + 1 + 1 + encryptionInput.length]; // 头部(1) + 长度(1) + flag(1) + 原始数据
//        int checksumPos = 0;
//        checksumData[checksumPos++] = (byte) 0xFF;
//        checksumData[checksumPos++] = (byte) totalLen;
//        checksumData[checksumPos++] = flag;
//        System.arraycopy(encryptionInput, 0, checksumData, checksumPos, encryptionInput.length);
//        checksumPos += encryptionInput.length;
//
//        LogUtils.e("CommandUtils原始数据：", ByteUtils.bytes2HexStr(checksumData));
//        // 计算校验和（基于原始数据）
//        byte sum = checkSumCnc(checksumData, 0, checksumPos);
//        packet[pos] = sum;
//        LogUtils.e("CommandUtils最终数据：", ByteUtils.bytes2HexStr(packet));
//        return packet;
//    }
//
//    /**
//     * 与C语言完全一致的校验和计算方法
//     *
//     * @param buf     字节数组
//     * @param start   起始位置
//     * @param nword   需要计算的字节数
//     * @return        校验和结果
//     */
//    public static byte checkSumCnc(byte[] buf, int start, int nword) {
//        int sum = 0;
//        for (int i = start; nword > 0; nword--, i++) {
//            // 将字节视为无符号值进行计算（使用0xFF掩码）
//            sum += buf[i] & 0xFF;
//        }
//        // 只取低8位（自动溢出处理，与C语言行为一致）
//        return (byte) (sum & 0xFF);
//    }
//
//    // 重置序列号计数器
//    public static void resetSerial() {
//        serialCounter.set(0);
//    }
//
//    // 设置序列号值
//    public static void setSerial(int value) {
//        if (value < 0 || value > 255) {
//            throw new IllegalArgumentException("Serial must be between 0 and 255");
//        }
//        serialCounter.set(value);
//    }
//
//    // 获取当前序列号
//    public static int getCurrentSerial() {
//        return serialCounter.get();
//    }
//
//    // 测试用例
//    public static void main(String[] args) {
//        // 测试校验和计算
//        byte[] testData = {(byte) 0x01, (byte) 0x02, (byte) 0x03};
//        byte sum = checkSumCnc(testData, 0, testData.length);
//        System.out.println("Test checksum: " + String.format("%02X", sum));
//
//        // 测试指令生成
//        byte[] body = {0x01, 0x02};
//        for (int i = 0; i < 3; i++) {
//            byte[] cmd = generateCommand((byte) 0xA0, (byte) 0xB1, body);
//            System.out.printf("Command %d: %s%n",
//                    i + 1,
//                    bytesToHex(cmd));
//        }
//    }
//
//    // 辅助方法：字节数组转十六进制字符串
//    private static String bytesToHex(byte[] bytes) {
//        StringBuilder sb = new StringBuilder();
//        for (byte b : bytes) {
//            sb.append(String.format("%02X ", b));
//        }
//        return sb.toString().trim();
//    }
//}