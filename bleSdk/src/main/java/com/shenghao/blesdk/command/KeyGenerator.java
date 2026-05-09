package com.shenghao.blesdk.command;

import com.shenghao.blesdk.utils.ByteUtils;

import java.security.SecureRandom;

public class KeyGenerator {

    public static byte[] generate14ByteKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[14];
        secureRandom.nextBytes(key);

        return ByteUtils.hexStr2Bytes("000102030405060708090a0b0c0d");
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}