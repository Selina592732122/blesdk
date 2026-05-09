package com.shenghao.blesdk.callback;

import com.clj.fastble.data.BleDevice;

public interface BleStateListener {

    void onConnecting(String mac);

    void onConnected(String mac, BleDevice device);

    void onDisconnected(String mac);

    void onConnectFailed(String mac, String errorMessage);
}