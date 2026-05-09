package com.shenghao.blesdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class BleConfigManager {

    private static final String PREF_NAME = "ble_sdk_config";
    
    private static final String KEY_BLE_MAC = "ble_mac";
    private static final String KEY_AUTO_CONNECT_ENABLED = "auto_connect_enabled";
    private static final String KEY_BLE_UNLOCK_RSSI = "ble_unlock_rssi";
    private static final String KEY_BLE_LOCK_RSSI = "ble_lock_rssi";
    private static final String KEY_CUSTOM_UNLOCK_RSSI = "custom_unlock_rssi";
    private static final String KEY_CUSTOM_LOCK_RSSI = "custom_lock_rssi";
    private static final String KEY_ONE_KEY_GEN_PWD = "one_key_gen_pwd";
    private static final String KEY_ONE_KEY_PWD = "one_key_pwd";

    private static BleConfigManager instance;
    private final SharedPreferences sharedPreferences;

    private BleConfigManager(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized BleConfigManager getInstance(Context context) {
        if (instance == null) {
            instance = new BleConfigManager(context);
        }
        return instance;
    }

    public static synchronized BleConfigManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("BleConfigManager has not been initialized. " +
                    "Call getInstance(Context) first.");
        }
        return instance;
    }

    public String getBleMac() {
        return sharedPreferences.getString(KEY_BLE_MAC, "");
    }

    public void setBleMac(String mac) {
        sharedPreferences.edit().putString(KEY_BLE_MAC, mac).apply();
    }

    public boolean isAutoConnectEnabled() {
        return sharedPreferences.getBoolean(KEY_AUTO_CONNECT_ENABLED, true);
    }

    public void setAutoConnectEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_AUTO_CONNECT_ENABLED, enabled).apply();
    }

    public int getBleUnlockRssi() {
        return sharedPreferences.getInt(KEY_BLE_UNLOCK_RSSI, 70);
    }

    public void setBleUnlockRssi(int rssi) {
        sharedPreferences.edit().putInt(KEY_BLE_UNLOCK_RSSI, rssi).apply();
    }

    public int getBleLockRssi() {
        return sharedPreferences.getInt(KEY_BLE_LOCK_RSSI, 80);
    }

    public void setBleLockRssi(int rssi) {
        sharedPreferences.edit().putInt(KEY_BLE_LOCK_RSSI, rssi).apply();
    }

    public int getCustomUnlockRssi() {
        return sharedPreferences.getInt(KEY_CUSTOM_UNLOCK_RSSI, 70);
    }

    public void setCustomUnlockRssi(int rssi) {
        sharedPreferences.edit().putInt(KEY_CUSTOM_UNLOCK_RSSI, rssi).apply();
    }

    public int getCustomLockRssi() {
        return sharedPreferences.getInt(KEY_CUSTOM_LOCK_RSSI, 80);
    }

    public void setCustomLockRssi(int rssi) {
        sharedPreferences.edit().putInt(KEY_CUSTOM_LOCK_RSSI, rssi).apply();
    }

    public String getRootKey() {
        return sharedPreferences.getString(KEY_ONE_KEY_GEN_PWD, "");
    }

    public void setRootKey(String key) {
        sharedPreferences.edit().putString(KEY_ONE_KEY_GEN_PWD, key).apply();
    }

    public String getExchangeKey() {
        return sharedPreferences.getString(KEY_ONE_KEY_PWD, "");
    }

    public void setExchangeKey(String key) {
        sharedPreferences.edit().putString(KEY_ONE_KEY_PWD, key).apply();
    }

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }
}