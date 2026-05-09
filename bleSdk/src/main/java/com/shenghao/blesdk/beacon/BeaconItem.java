package com.shenghao.blesdk.beacon;

import com.shenghao.blesdk.utils.ByteUtils;

public class BeaconItem {

    public int len;
    public int type;
    public byte[] bytes;

    @Override
    public String toString() {
        String format = "";
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("@Len = %02X, @Type = 0x%02X", len, type));

        switch (type) {
            case 8:
            case 9:
                format = "%c";
                break;
            default:
                format = "%02X ";
                break;
        }

        sb.append(" -> ");

        StringBuilder sbSub = new StringBuilder();
        try {
            for (byte b : bytes) {
                sbSub.append(String.format(format, b & 0xff));
            }
            sb.append(sbSub.toString());
        } catch (Exception e) {
            sb.append(ByteUtils.byteToString(bytes));
        }

        return sb.toString();
    }
}