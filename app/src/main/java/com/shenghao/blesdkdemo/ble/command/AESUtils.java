//package com.shenghao.blesdkdemo.ble.command;
//
//import com.shenghao.blesdkdemo.utils.ByteUtils;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.SecretKeySpec;
//import java.security.MessageDigest;
//import java.util.Arrays;
//
//public class AESUtils {
//
//    /**
//     * AES-128加密方法（固定16字节输出）
//     *
//     * @param input 原始数据字节数组
//     * @param key 加密密钥（任意长度，会转换为128位密钥）
//     * @return 16字节的加密结果
//     * @throws Exception 加密过程中可能出现的异常
//     */
//    public static byte[] aes128Encrypt(byte[] input, String key) throws Exception {
//        // 1. 从密钥生成128位（16字节）AES密钥
////        byte[] keyBytes = generateAesKey(key);
//        byte[] keyBytes = ByteUtils.hexStr2Bytes(key);
//        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
//
//        // 2. 创建AES加密器（ECB模式，PKCS5填充）
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//
//        // 3. 执行加密
//        byte[] encrypted = cipher.doFinal(input);
//
//        // 4. 返回固定16字节结果（截取前16字节）
//        return Arrays.copyOf(encrypted, 16);
//    }
//
//    /**
//     * AES-128解密方法
//     *
//     * @param encrypted 加密数据（16字节）
//     * @param key 解密密钥（与加密密钥相同）
//     * @return 解密后的原始数据
//     * @throws Exception 解密过程中可能出现的异常
//     */
//    public static byte[] aes128Decrypt(byte[] encrypted, String key) throws Exception {
//        // 1. 从密钥生成128位（16字节）AES密钥
//        byte[] keyBytes = generateAesKey(key);
//        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
//
//        // 2. 创建AES解密器（ECB模式，PKCS5填充）
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
//
//        // 3. 执行解密
//        return cipher.doFinal(encrypted);
//    }
//    public static byte[] aes128Decrypt(byte[] encrypted, byte[] keyBytes) throws Exception {
//        // 1. 从密钥生成128位（16字节）AES密钥
////        byte[] keyBytes = generateAesKey(key);
//        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
//
//        // 2. 创建AES解密器（ECB模式，PKCS5填充）
//        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
//
//        // 3. 执行解密
//        return cipher.doFinal(encrypted);
//    }
//    /**
//     * 从任意字符串生成128位AES密钥
//     *
//     * @param key 原始密钥字符串
//     * @return 16字节的AES密钥
//     */
//    private static byte[] generateAesKey(String key) throws Exception {
//        // 使用SHA-256哈希确保密钥长度一致
//        MessageDigest sha = MessageDigest.getInstance("SHA-256");
//        byte[] hash = sha.digest(key.getBytes("UTF-8"));
//
//        // 取前16字节作为AES-128密钥
//        return Arrays.copyOf(hash, 16);
//    }
//
//    // 测试方法
//    public static void main(String[] args) throws Exception {
//        String key = "MySecretKey123!"; // 16字符密钥
//        String testData = "Hello AES-128!"; // 测试数据
//
//        // 测试1: 加密固定文本
//        byte[] encrypted = aes128Encrypt(testData.getBytes("UTF-8"), key);
//        System.out.println("Encrypted (" + encrypted.length + " bytes): " + bytesToHex(encrypted));
//
//        // 测试2: 解密加密结果
//        byte[] decrypted = aes128Decrypt(encrypted, key);
//        String decryptedStr = new String(decrypted, "UTF-8");
//        System.out.println("Decrypted: " + decryptedStr);
//
//        // 测试3: 加密解密长文本
//        String longText = "This is a longer text that will be padded to multiple blocks";
//        byte[] longEncrypted = aes128Encrypt(longText.getBytes("UTF-8"), key);
//        byte[] longDecrypted = aes128Decrypt(longEncrypted, key);
//        System.out.println("Long text decrypted: " + new String(longDecrypted, "UTF-8"));
//
//        // 测试4: 加密解密空数据
//        byte[] emptyEncrypted = aes128Encrypt(new byte[0], key);
//        byte[] emptyDecrypted = aes128Decrypt(emptyEncrypted, key);
//        System.out.println("Empty decrypted length: " + emptyDecrypted.length);
//    }
//
//    // 字节数组转十六进制字符串
//    private static String bytesToHex(byte[] bytes) {
//        StringBuilder sb = new StringBuilder();
//        for (byte b : bytes) {
//            sb.append(String.format("%02X ", b));
//        }
//        return sb.toString().trim();
//    }
//}