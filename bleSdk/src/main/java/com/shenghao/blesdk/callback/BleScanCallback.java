package com.shenghao.blesdk.callback;

import com.shenghao.blesdk.entity.BleSdkDevice;

import java.util.List;

public abstract class BleScanCallback {

    public void onScanStarted(boolean success) {
    }

    public void onLeScan(BleSdkDevice device) {
    }

    public void onScanning(BleSdkDevice device) {
    }

    public void onScanFinished(List<BleSdkDevice> devices) {
    }
}