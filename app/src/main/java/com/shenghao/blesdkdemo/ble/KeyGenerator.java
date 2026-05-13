//package com.shenghao.blesdkdemo.ble;
//
//import com.shenghao.blesdkdemo.utils.ByteUtils;
//
//import java.security.SecureRandom;
//
//public class KeyGenerator {
//
//    // 生成14字节随机密钥
//    public static byte[] generate14ByteKey() {
//        // 1. 创建加密安全的随机数生成器
//        SecureRandom secureRandom = new SecureRandom();
//
//        // 2. 创建14字节的字节数组
//        byte[] key = new byte[14]; // 14字节 = 112位
//
//        // 3. 用安全随机数填充数组
//        secureRandom.nextBytes(key);
//
//        return ByteUtils.hexStr2Bytes("000102030405060708090a0b0c0d");
////        return key;
//    }
//
//    // 辅助方法：将字节数组转换为十六进制字符串（用于显示）
//    public static String bytesToHex(byte[] bytes) {
//        StringBuilder hexString = new StringBuilder();
//        for (byte b : bytes) {
//            String hex = Integer.toHexString(0xff & b);
//            if (hex.length() == 1) {
//                hexString.append('0');
//            }
//            hexString.append(hex);
//        }
//        return hexString.toString();
//    }
//
//    // 使用示例
//    public static void main(String[] args) {
//        // 生成密钥
//        byte[] secretKey = generate14ByteKey();
//
//        // 转换为十六进制字符串显示
//        String hexKey = bytesToHex(secretKey);
//        System.out.println("Generated 14-byte Key: " + hexKey);
//        System.out.println("Key Length: " + hexKey.length()/2 + " bytes");
//    }
//}