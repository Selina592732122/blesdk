package com.shenghao.blesdkdemo.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    /**
     * 对字符串进行MD5加密
     *
     * @param input 需要加密的字符串
     * @return 加密后的MD5字符串
     */
    public static String md5(String input) {
        try {
            // 获取MD5算法实例
            MessageDigest digest = MessageDigest.getInstance("MD5");
            // 将输入字符串转换为字节数组并计算哈希值
            byte[] messageDigest = digest.digest(input.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e); // MD5算法不可用
        }
    }
}