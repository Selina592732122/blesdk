package com.shenghao.blesdk.callback;

import com.clj.fastble.exception.BleException;

public interface BleNotifyCallback {

    void onNotifySuccess();

    void onNotifyFailed(BleException exception);

    void onCharacteristicChanged(byte[] data);
}