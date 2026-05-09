package com.shenghao.blesdk.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.Set;

public class BluetoothUtils {
    public static Set<BluetoothDevice> getPairedDevices(Context context) {
        // 获取蓝牙适配器
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 检查蓝牙是否可用
        if (bluetoothAdapter == null) {
            // 设备不支持蓝牙
            return null;
        }

        try {
            return bluetoothAdapter.getBondedDevices();
        }catch (Exception e){
            e.printStackTrace();
        }

        // 获取已配对设备
        return null;
    }
}
