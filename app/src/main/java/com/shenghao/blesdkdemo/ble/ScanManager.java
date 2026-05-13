//package com.shenghao.blesdkdemo.ble;
//
//import android.text.TextUtils;
//
//import com.clj.fastble.BleManager;
//import com.clj.fastble.callback.BleScanCallback;
//import com.clj.fastble.data.BleDevice;
//import com.shenghao.blesdkdemo.utils.LogUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ScanManager {
//    private static ScanManager instance;
//    private boolean isScanning = false;
//    private BleScanCallback currentCallback;
//    private List<BleScanCallback> pendingCallbacks = new ArrayList<>();
//
//    public static ScanManager getInstance() {
//        if (instance == null) {
//            instance = new ScanManager();
//        }
//        return instance;
//    }
//
//    public synchronized void startScan(BleScanCallback callback) {
//        if (isScanning) {
//            LogUtils.e("ScanManager","正在扫描");
//            // 如果已经在扫描，将回调加入等待队列
//            pendingCallbacks.add(callback);
//            return;
//        }
//
//        isScanning = true;
//        currentCallback = callback;
//
//        BleManager.getInstance().scan(new BleScanCallback() {
//            @Override
//            public void onScanStarted(boolean success) {
//                callback.onScanStarted(success);
//            }
//
//            @Override
//            public void onLeScan(BleDevice bleDevice) {
//                LogUtils.e("ScanManager","onLeScan:"+bleDevice.getName());
//                if(TextUtils.isEmpty(bleDevice.getName()))
//                    return;
//                callback.onLeScan(bleDevice);
//                for (BleScanCallback pending : pendingCallbacks) {
//                    pending.onScanning(bleDevice);
//                }
//            }
//
//            @Override
//            public void onScanning(BleDevice bleDevice) {
//                LogUtils.e("ScanManager","onScanning:"+bleDevice.getName()+","+Thread.currentThread().getName());
//                if(TextUtils.isEmpty(bleDevice.getName()))
//                    return;
//                // 分发扫描结果给所有注册的回调
//                callback.onScanning(bleDevice);
////                for (BleScanCallback pending : pendingCallbacks) {
////                    pending.onScanning(bleDevice);
////                }
//            }
//
//            @Override
//            public void onScanFinished(List<BleDevice> scanResultList) {
//                callback.onScanFinished(scanResultList);
//                for (BleScanCallback pending : pendingCallbacks) {
//                    pending.onScanFinished(scanResultList);
//                }
//
//                // 清理状态
//                isScanning = false;
//                currentCallback = null;
//                pendingCallbacks.clear();
//            }
//        });
//    }
//
//    public synchronized void stopScan() {
//        if (isScanning) {
//            BleManager.getInstance().cancelScan();
//            isScanning = false;
//            currentCallback = null;
//            pendingCallbacks.clear();
//        }
//    }
//
//    public boolean isScanning() {
//        return isScanning;
//    }
//}