//package com.shenghao.blesdkdemo.ble;
//
//public enum BlueRssi {
////    WEAK("WEAK",-70,-80),
////    MIDDLE("MIDDLE",-75,-85),
////    STRONG("STRONG",-80,-85);
//    WEAK("WEAK",-57,-77),
//    MIDDLE("MIDDLE",-78,-88),
//    STRONG("STRONG",-88,-95);
//
//    private String value;
//    private int lowRssi;
//    private int highRssi;
//
//    BlueRssi(String value, int lowRssi, int highRssi ) {
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
//    public static BlueRssi fromRssi(int lrssi, int urssi) {
//        for (BlueRssi rssi : BlueRssi.values()) {
//            if (rssi.lowRssi == lrssi && rssi.highRssi == urssi) {
//                return rssi;
//            }
//        }
//        return BlueRssi.MIDDLE;
//    }
//}
