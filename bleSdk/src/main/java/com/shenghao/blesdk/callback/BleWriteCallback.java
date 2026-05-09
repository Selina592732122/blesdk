package com.shenghao.blesdk.callback;

import com.clj.fastble.exception.BleException;

public interface BleWriteCallback {

    void onWriteSuccess(int current, int total, byte[] justWrite);

    void onWriteFailed(BleException exception);
}