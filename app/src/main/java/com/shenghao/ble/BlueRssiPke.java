//package com.shenghao.ble;
//
//public enum BlueRssiPke {
////    WEAK("WEAK",-70,-80),
////    MIDDLE("MIDDLE",-75,-85),
////    STRONG("STRONG",-80,-85);
//    WEAK("WEAK",52,70),
//    MIDDLE("MIDDLE",62,82),
//    STRONG("STRONG",70,90);
//
//    private String value;
//    private int lowRssi;
//    private int highRssi;
//
//    BlueRssiPke(String value, int lowRssi, int highRssi ) {
//        this.value = value;
//        this.lowRssi = lowRssi;
//        this.highRssi = highRssi;
//    }
//
//    public String getValue() {
//        return value;
//    }
//
//    public int getLowRssi() {
//        return lowRssi;
//    }
//
//    public int getHighRssi() {
//        return highRssi;
//    }
//    // 根据输入的 lowRssi 和 highRssi 匹配对应的枚举值
//    public static BlueRssiPke fromRssi(int lrssi, int urssi) {
//        for (BlueRssiPke rssi : BlueRssiPke.values()) {
//            if (rssi.lowRssi == urssi && rssi.highRssi == lrssi) {
//                return rssi;
//            }
//        }
//        return BlueRssiPke.MIDDLE;
//    }
//}
