//package com.shenghao.blesdkdemo.event;
//
//import com.clj.fastble.data.BleDevice;
//import com.shenghao.blesdk.enums.BluetoothStatus;
//
//public class BleStatusEvent {
//    private BluetoothStatus status;   //是否处于后台
//    private String mac;
//    private BleDevice bleDevice;
//    public BleStatusEvent(BluetoothStatus status, String mac) {
//        this(status,mac,null);
//    }
//    public BleStatusEvent(BluetoothStatus status, String mac, BleDevice bleDevice) {
//        this.status = status;
//        this.mac = mac;
//        this.bleDevice = bleDevice;
//    }
//
//    public BluetoothStatus getStatus() {
//        return status;
//    }
//
//    public void setStatus(BluetoothStatus status) {
//        this.status = status;
//    }
//
//    public String getMac() {
//        return mac;
//    }
//
//    public void setMac(String mac) {
//        this.mac = mac;
//    }
//
//    public BleDevice getBleDevice() {
//        return bleDevice;
//    }
//
//    public void setBleDevice(BleDevice bleDevice) {
//        this.bleDevice = bleDevice;
//    }
//}
