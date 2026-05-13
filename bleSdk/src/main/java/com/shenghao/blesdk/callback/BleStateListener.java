package com.shenghao.blesdk.callback;

import com.shenghao.blesdk.entity.BleSdkDevice;

public interface BleStateListener {

    void onConnecting(String mac);

    void onConnected(String mac, BleSdkDevice device);

    void onDisconnected(String mac);

    void onConnectFailed(String mac, String errorMessage);
}