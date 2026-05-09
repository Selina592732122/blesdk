package com.shenghao.blesdk.enums;

public enum BlueRssiPke {
    WEAK("WEAK", 52, 70),
    MIDDLE("MIDDLE", 62, 82),
    STRONG("STRONG", 70, 90);

    private String value;
    private int lowRssi;
    private int highRssi;

    BlueRssiPke(String value, int lowRssi, int highRssi) {
        this.value = value;
        this.lowRssi = lowRssi;
        this.highRssi = highRssi;
    }

    public String getValue() {
        return value;
    }

    public int getLowRssi() {
        return lowRssi;
    }

    public int getHighRssi() {
        return highRssi;
    }

    public static BlueRssiPke fromRssi(int lrssi, int urssi) {
        for (BlueRssiPke rssi : BlueRssiPke.values()) {
            if (rssi.lowRssi == urssi && rssi.highRssi == lrssi) {
                return rssi;
            }
        }
        return BlueRssiPke.MIDDLE;
    }
}