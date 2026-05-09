package com.shenghao.blesdk.command;

import com.shenghao.blesdk.utils.ByteUtils;
import com.shenghao.blesdk.utils.LogUtils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandUtils {
    private static final AtomicInteger serialCounter = new AtomicInteger(0);

    public static byte[] generateCommand(byte flag, byte code, byte[] body) {
        int serialInt = serialCounter.getAndUpdate((current) -> (current + 1) & 0xFF);
        byte serial = (byte) serialInt;

        int bodyLength = body != null ? body.length : 0;
        byte[] dataPart = new byte[2 + bodyLength];
        dataPart[0] = code;
        dataPart[1] = serial;
        if (body != null && bodyLength > 0) {
            System.arraycopy(body, 0, dataPart, 2, bodyLength);
        }

        int paddingLen = dataPart.length < 16 ? 16 - dataPart.length : 0;
        byte[] padding = new byte[paddingLen];
        Arrays.fill(padding, (byte) 0xFF);

        byte[] encryptionInput = new byte[dataPart.length + paddingLen];
        System.arraycopy(dataPart, 0, encryptionInput, 0, dataPart.length);
        if (paddingLen > 0) {
            System.arraycopy(padding, 0, encryptionInput, dataPart.length, paddingLen);
        }

        LogUtils.e("CommandUtils加密前数据：", ByteUtils.bytes2HexStr(encryptionInput));

        byte[] encrypted = new byte[0];
        try {
            if (flag == 0x11) {
                encrypted = encryptionInput;
            } else if (flag == 0x12) {
                encrypted = encryptionInput;
            } else if (flag == 0x00) {
                encrypted = encryptionInput;
            }
            LogUtils.e("CommandUtils加密后数据：", ByteUtils.bytes2HexStr(encrypted));
        } catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }

        int totalLen = 1 + 1 + 1 + bodyLength + paddingLen + 1;
        if (totalLen > 255) {
            throw new IllegalArgumentException("Total length exceeds 255 bytes");
        }

        byte[] packet = new byte[1 + 1 + 1 + encrypted.length + 1];
        int pos = 0;

        packet[pos++] = (byte) 0xFF;
        packet[pos++] = (byte) totalLen;
        packet[pos++] = flag;

        System.arraycopy(encrypted, 0, packet, pos, encrypted.length);
        pos += encrypted.length;

        byte[] checksumData = new byte[1 + 1 + 1 + encryptionInput.length];
        int checksumPos = 0;
        checksumData[checksumPos++] = (byte) 0xFF;
        checksumData[checksumPos++] = (byte) totalLen;
        checksumData[checksumPos++] = flag;
        System.arraycopy(encryptionInput, 0, checksumData, checksumPos, encryptionInput.length);
        checksumPos += encryptionInput.length;

        LogUtils.e("CommandUtils原始数据：", ByteUtils.bytes2HexStr(checksumData));
        byte sum = checkSumCnc(checksumData, 0, checksumPos);
        packet[pos] = sum;
        LogUtils.e("CommandUtils最终数据：", ByteUtils.bytes2HexStr(packet));
        return packet;
    }

    public static byte[] generateCommandWithKey(byte flag, byte code, byte[] body, String rootKey, String exchangeKey) {
        int serialInt = serialCounter.getAndUpdate((current) -> (current + 1) & 0xFF);
        byte serial = (byte) serialInt;

        int bodyLength = body != null ? body.length : 0;
        byte[] dataPart = new byte[2 + bodyLength];
        dataPart[0] = code;
        dataPart[1] = serial;
        if (body != null && bodyLength > 0) {
            System.arraycopy(body, 0, dataPart, 2, bodyLength);
        }

        int paddingLen = dataPart.length < 16 ? 16 - dataPart.length : 0;
        byte[] padding = new byte[paddingLen];
        Arrays.fill(padding, (byte) 0xFF);

        byte[] encryptionInput = new byte[dataPart.length + paddingLen];
        System.arraycopy(dataPart, 0, encryptionInput, 0, dataPart.length);
        if (paddingLen > 0) {
            System.arraycopy(padding, 0, encryptionInput, dataPart.length, paddingLen);
        }

        LogUtils.e("CommandUtils加密前数据：", ByteUtils.bytes2HexStr(encryptionInput));

        byte[] encrypted = new byte[0];
        try {
            if (flag == 0x11 && rootKey != null) {
                encrypted = AESUtils.aes128Encrypt(encryptionInput, rootKey);
                LogUtils.e("CommandUtils加密根密码数据：", rootKey);
            } else if (flag == 0x12 && exchangeKey != null) {
                encrypted = AESUtils.aes128Encrypt(encryptionInput, exchangeKey);
                LogUtils.e("CommandUtils加密交换密码数据：", exchangeKey);
            } else if (flag == 0x00) {
                encrypted = encryptionInput;
            }
            LogUtils.e("CommandUtils加密后数据：", ByteUtils.bytes2HexStr(encrypted));
        } catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }

        int totalLen = 1 + 1 + 1 + bodyLength + paddingLen + 1;
        if (totalLen > 255) {
            throw new IllegalArgumentException("Total length exceeds 255 bytes");
        }

        byte[] packet = new byte[1 + 1 + 1 + encrypted.length + 1];
        int pos = 0;

        packet[pos++] = (byte) 0xFF;
        packet[pos++] = (byte) totalLen;
        packet[pos++] = flag;

        System.arraycopy(encrypted, 0, packet, pos, encrypted.length);
        pos += encrypted.length;

        byte[] checksumData = new byte[1 + 1 + 1 + encryptionInput.length];
        int checksumPos = 0;
        checksumData[checksumPos++] = (byte) 0xFF;
        checksumData[checksumPos++] = (byte) totalLen;
        checksumData[checksumPos++] = flag;
        System.arraycopy(encryptionInput, 0, checksumData, checksumPos, encryptionInput.length);
        checksumPos += encryptionInput.length;

        LogUtils.e("CommandUtils原始数据：", ByteUtils.bytes2HexStr(checksumData));
        byte sum = checkSumCnc(checksumData, 0, checksumPos);
        packet[pos] = sum;
        LogUtils.e("CommandUtils最终数据：", ByteUtils.bytes2HexStr(packet));
        return packet;
    }

    public static byte checkSumCnc(byte[] buf, int start, int nword) {
        int sum = 0;
        for (int i = start; nword > 0; nword--, i++) {
            sum += buf[i] & 0xFF;
        }
        return (byte) (sum & 0xFF);
    }

    public static void resetSerial() {
        serialCounter.set(0);
    }

    public static void setSerial(int value) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException("Serial must be between 0 and 255");
        }
        serialCounter.set(value);
    }

    public static int getCurrentSerial() {
        return serialCounter.get();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}