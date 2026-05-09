package com.shenghao.blesdk.listener;

public interface BluetoothStateChangeListener {
    void onBluetoothOff();
    void onBluetoothTurningOff();
    void onBluetoothOn();
    void onBluetoothTurningOn();
}