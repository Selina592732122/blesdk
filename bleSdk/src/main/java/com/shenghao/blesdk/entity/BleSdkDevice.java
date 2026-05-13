package com.shenghao.blesdk.entity;

import android.bluetooth.BluetoothDevice;

import com.clj.fastble.data.BleDevice;

public class BleSdkDevice {

    private BleDevice bleDevice;

    public BleSdkDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public String getName() {
        return bleDevice.getName();
    }

    public String getMac() {
        return bleDevice.getMac();
    }

    public int getRssi() {
        return bleDevice.getRssi();
    }

    public byte[] getScanRecord() {
        return bleDevice.getScanRecord();
    }

    public BleDevice getOriginalDevice() {
        return bleDevice;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BleSdkDevice that = (BleSdkDevice) obj;
        return getMac() != null ? getMac().equals(that.getMac()) : that.getMac() == null;
    }

    @Override
    public int hashCode() {
        return getMac() != null ? getMac().hashCode() : 0;
    }
}