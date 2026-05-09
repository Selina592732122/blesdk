package com.shenghao.blesdk.enums;

public enum BlueRssi {
    WEAK("WEAK", -57, -77),
    MIDDLE("MIDDLE", -78, -88),
    STRONG("STRONG", -88, -95);

    private String value;
    private int lowRssi;
    private int highRssi;

    BlueRssi(String value, int lowRssi, int highRssi) {
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

    public static BlueRssi fromRssi(int lrssi, int urssi) {
        for (BlueRssi rssi : BlueRssi.values()) {
            if (rssi.lowRssi == lrssi && rssi.highRssi == urssi) {
                return rssi;
            }
        }
        return BlueRssi.MIDDLE;
    }
}