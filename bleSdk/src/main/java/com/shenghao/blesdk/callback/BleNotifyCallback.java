package com.shenghao.blesdk.callback;

import com.shenghao.blesdk.exception.BleSdkException;

public interface BleNotifyCallback {

    void onNotifySuccess();

    void onNotifyFailed(BleSdkException exception);

    void onCharacteristicChanged(byte[] data);
}