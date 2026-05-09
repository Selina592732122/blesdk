package com.shenghao.blesdk.callback;

import com.clj.fastble.data.BleDevice;

import java.util.List;

public interface BleScanCallback {

    void onScanStarted(boolean success);

    void onLeScan(BleDevice bleDevice);

    void onScanning(BleDevice device);

    void onScanFinished(List<BleDevice> devices);
}