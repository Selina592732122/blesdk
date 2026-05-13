package com.shenghao.blesdk.callback;

import com.shenghao.blesdk.exception.BleSdkException;

public interface BleWriteCallback {

    void onWriteSuccess(int current, int total, byte[] justWrite);

    void onWriteFailed(BleSdkException exception);
}