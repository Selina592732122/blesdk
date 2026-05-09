package com.shenghao.blesdk.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shenghao.blesdk.listener.BluetoothStateChangeListener;

public class BluetoothReceiver extends BroadcastReceiver {

    private BluetoothStateChangeListener listener;

    public BluetoothReceiver(BluetoothStateChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
                if (listener != null) {
                    listener.onBluetoothOff();
                }
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                if (listener != null) {
                    listener.onBluetoothTurningOff();
                }
                break;
            case BluetoothAdapter.STATE_ON:
                if (listener != null) {
                    listener.onBluetoothOn();
                }
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                if (listener != null) {
                    listener.onBluetoothTurningOn();
                }
                break;
        }
    }
}