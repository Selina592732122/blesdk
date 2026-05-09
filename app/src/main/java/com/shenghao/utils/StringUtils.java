package com.shenghao.utils;

import java.text.DecimalFormat;

public class StringUtils {
    /**
     * 将手机号中间四位替换为****
     * @param phone 原始手机号
     * @return 替换后的手机号
     */
    public static String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() != 11) {
            throw new IllegalArgumentException("手机号必须是11位");
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
    /**
     * 字节数组转16进制
     * @param bytes 需要转换的byte数组
     * @return  转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    public static byte[] hexStrToByteArray(String str)
    {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[str.length() / 2];
        for (int i = 0; i < byteArray.length; i++){
            String subStr = str.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte)Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }
    public static String stringToHex(String input) {
        StringBuilder hex = new StringBuilder();
        for (char c : input.toCharArray()) {
            hex.append(String.format("%02X", (int) c));
        }
        return hex.toString();
    }

    /**
     * 保留两位小数
     */
    public static String getFormatNumber(double number) {
        //保留两位小数
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(number);
    }
}
