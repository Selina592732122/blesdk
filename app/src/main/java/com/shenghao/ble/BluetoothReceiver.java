//package com.shenghao.ble;
//
//import android.bluetooth.BluetoothAdapter;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//public class BluetoothReceiver extends BroadcastReceiver {
//
//    private BluetoothStateChangeListener listener;
//
//    public BluetoothReceiver(BluetoothStateChangeListener listener) {
//        this.listener = listener;
//    }
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
//        switch (state) {
//            case BluetoothAdapter.STATE_OFF:
//                listener.onBluetoothOff();
//                break;
//            case BluetoothAdapter.STATE_TURNING_OFF:
//                listener.onBluetoothTurningOff();
//                break;
//            case BluetoothAdapter.STATE_ON:
//                listener.onBluetoothOn();
//                break;
//            case BluetoothAdapter.STATE_TURNING_ON:
//                listener.onBluetoothTurningOn();
//                break;
//        }
//    }
//
//    public interface BluetoothStateChangeListener {
//        void onBluetoothOff();
//        void onBluetoothTurningOff();
//        void onBluetoothOn();
//        void onBluetoothTurningOn();
//    }
//}