package com.shenghao.blesdk.callback;

import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

public interface BleConnectCallback {

    void onSuccess(BleDevice device);

    void onFailed(BleException exception);

    void onDisconnected();
}