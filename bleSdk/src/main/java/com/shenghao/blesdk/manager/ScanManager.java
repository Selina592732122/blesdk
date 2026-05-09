package com.shenghao.blesdk.manager;

import android.text.TextUtils;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScanManager {

    private static ScanManager instance;
    private boolean isScanning = false;
    private com.shenghao.blesdk.callback.BleScanCallback currentCallback;
    private List<com.shenghao.blesdk.callback.BleScanCallback> pendingCallbacks = new ArrayList<>();
    private Set<String> scannedMacSet = new HashSet<>();

    private ScanManager() {
    }

    public static ScanManager getInstance() {
        if (instance == null) {
            synchronized (ScanManager.class) {
                if (instance == null) {
                    instance = new ScanManager();
                }
            }
        }
        return instance;
    }

    public synchronized void startScan(com.shenghao.blesdk.callback.BleScanCallback callback) {
        if (isScanning) {
            pendingCallbacks.add(callback);
            return;
        }

        try {
            BleManager.getInstance().cancelScan();
        }catch (Exception e){
            e.printStackTrace();
        }

        scannedMacSet.clear();
        isScanning = true;
        currentCallback = callback;

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                if (currentCallback != null) {
                    currentCallback.onScanStarted(success);
                }
                for (com.shenghao.blesdk.callback.BleScanCallback pending : pendingCallbacks) {
                    pending.onScanStarted(success);
                }
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                handleDeviceFound(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                handleDeviceFound(bleDevice);
            }

            private void handleDeviceFound(BleDevice bleDevice) {
                if (TextUtils.isEmpty(bleDevice.getName())) {
                    return;
                }
                String mac = bleDevice.getMac();
                if (scannedMacSet.contains(mac)) {
                    return;
                }
                scannedMacSet.add(mac);
                if (currentCallback != null) {
                    currentCallback.onLeScan(bleDevice);
                    currentCallback.onScanning(bleDevice);
                }
                for (com.shenghao.blesdk.callback.BleScanCallback pending : pendingCallbacks) {
                    pending.onLeScan(bleDevice);
                    pending.onScanning(bleDevice);
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                if (currentCallback != null) {
                    currentCallback.onScanFinished(scanResultList);
                }
                for (com.shenghao.blesdk.callback.BleScanCallback pending : pendingCallbacks) {
                    pending.onScanFinished(scanResultList);
                }

                isScanning = false;
                currentCallback = null;
                pendingCallbacks.clear();
                scannedMacSet.clear();
            }
        });
    }

    public synchronized void stopScan() {
        if (isScanning) {
            BleManager.getInstance().cancelScan();
            isScanning = false;
            currentCallback = null;
            pendingCallbacks.clear();
            scannedMacSet.clear();
        }
    }

    public boolean isScanning() {
        return isScanning;
    }

    public void startScanWithFilter(com.shenghao.blesdk.callback.BleScanCallback callback, String nameFilter) {
        if (isScanning) {
            pendingCallbacks.add(callback);
            return;
        }

        scannedMacSet.clear();
        isScanning = true;
        currentCallback = callback;

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                if (currentCallback != null) {
                    currentCallback.onScanStarted(success);
                }
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                handleDeviceFound(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                handleDeviceFound(bleDevice);
            }

            private void handleDeviceFound(BleDevice bleDevice) {
                String name = bleDevice.getName();
                String mac = bleDevice.getMac();
                if (TextUtils.isEmpty(name) || !name.contains(nameFilter)) {
                    return;
                }
                if (scannedMacSet.contains(mac)) {
                    return;
                }
                scannedMacSet.add(mac);
                if (currentCallback != null) {
                    currentCallback.onScanning(bleDevice);
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                List<BleDevice> filteredList = new ArrayList<>();
                for (BleDevice device : scanResultList) {
                    String name = device.getName();
                    if (!TextUtils.isEmpty(name) && name.contains(nameFilter)) {
                        filteredList.add(device);
                    }
                }

                if (currentCallback != null) {
                    currentCallback.onScanFinished(filteredList);
                }

                isScanning = false;
                currentCallback = null;
                pendingCallbacks.clear();
                scannedMacSet.clear();
            }
        });
    }
}