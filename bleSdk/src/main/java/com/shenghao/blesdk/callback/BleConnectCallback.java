package com.shenghao.blesdk.callback;

import com.shenghao.blesdk.entity.BleSdkDevice;
import com.shenghao.blesdk.exception.BleSdkException;

public interface BleConnectCallback {

    void onSuccess(BleSdkDevice device);

    void onFailed(BleSdkException exception);

    void onDisconnected();
}