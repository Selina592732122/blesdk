package com.shenghao.blesdk.command;

import com.shenghao.blesdk.utils.ByteUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;

public class AESUtils {

    public static byte[] aes128Encrypt(byte[] input, String key) throws Exception {
        byte[] keyBytes = ByteUtils.hexStr2Bytes(key);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encrypted = cipher.doFinal(input);
        return Arrays.copyOf(encrypted, 16);
    }

    public static byte[] aes128Decrypt(byte[] encrypted, String key) throws Exception {
        byte[] keyBytes = generateAesKey(key);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(encrypted);
    }

    public static byte[] aes128Decrypt(byte[] encrypted, byte[] keyBytes) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(encrypted);
    }

    private static byte[] generateAesKey(String key) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] hash = sha.digest(key.getBytes("UTF-8"));

        return Arrays.copyOf(hash, 16);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}