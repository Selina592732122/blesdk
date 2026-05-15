package com.shenghao.blesdk;

import android.app.Application;
import android.content.Context;

import com.clj.fastble.BleManager;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.shenghao.blesdk.manager.BleConnectionManager;
import com.shenghao.blesdk.manager.OneKeyParkingManager;
import com.shenghao.blesdk.manager.PairingManager;
import com.shenghao.blesdk.manager.ScanManager;

public class BleSdk {

    private static volatile BleSdk instance;
    private Application appContext;
    private BleConnectionManager bleConnectionManager;
    private ScanManager scanManager;
    private OneKeyParkingManager oneKeyParkingManager;
    private boolean isInitialized = false;

    private BleSdk() {
    }

    public static BleSdk getInstance() {
        if (instance == null) {
            synchronized (BleSdk.class) {
                if (instance == null) {
                    instance = new BleSdk();
                }
            }
        }
        return instance;
    }

    public void initialize(Application application) {
        if (application == null) {
            throw new IllegalArgumentException("Application cannot be null");
        }
        this.appContext = application;
        
        BleManager.getInstance().init(appContext);
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setOperateTimeout(5000);
        
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setScanTimeOut(15000)
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
        
        bleConnectionManager = new BleConnectionManager(appContext);
        scanManager = ScanManager.getInstance();
        oneKeyParkingManager = OneKeyParkingManager.getInstance();
        isInitialized = true;
    }

    public BleConnectionManager getBleConnectionManager() {
        checkInitialized();
        return bleConnectionManager;
    }

    public ScanManager getScanManager() {
        checkInitialized();
        return scanManager;
    }

    public OneKeyParkingManager getOneKeyParkingManager() {
        checkInitialized();
        return oneKeyParkingManager;
    }

    public PairingManager getPairingManager() {
        checkInitialized();
        return PairingManager.getInstance(appContext);
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    private void checkInitialized() {
        if (!isInitialized) {
            throw new IllegalStateException("BleSdk has not been initialized. Call initialize() first.");
        }
    }

    public Context getContext() {
        return appContext;
    }

    public void destroy() {
        BleManager.getInstance().destroy();
    }
}