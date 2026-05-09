package com.shenghao.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shenghao.ShengHaoApp;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class SPUtils {
    private SharedPreferences sp;
    public static final String FILE_SP_NAME = "haibao_config"; //文件名
    public static final String SP_TOKEN = "token";
    public static final String SP_USER_NAME = "user_name";
    public static final String SP_TERMINAL = "terminal";
    public static final String SP_USER_INFO = "user_info";
    public static final String SP_TERMINAL_ID = "terminal_id";
    public static final String SP_TERMINAL_NO = "terminal_no";
    public static final String SP_TERMINAL_NAME = "terminal_name";
    public static final String SP_BATTERY_COUNT = "battery_count";
    public static final String SP_DEVICE_IMEI = "device_imei";
    public static final String SP_HAS_SHOW_PRIVACY = "has_show_privacy";
    public static final String SP_WX_APP_ID = "wx_app_id";
    public static final String SP_NEED_SHOW_SCAN_PERMISSION_DIALOG = "need_show_scan_permission_dialog";
    public static final String SP_NEED_SHOW_LOCATION_PERMISSION_DIALOG = "need_show_location_permission_dialog";
    public static final String SP_BLE_LIST_MAC = "ble_list_mac";
    public static final String SP_BLE_MAC = "ble_mac";
    public static final String SP_BLE_UNLOCK_RSSI = "unlock_rssi";
    public static final String SP_BLE_LOCK_RSSI = "lock_rssi";
    public static final String SP_ONE_KEY_GEN_PWD = "";//根密码，平台给的
    public static final String SP_ONE_KEY_PWD = "";//蓝牙密码自己随机
    public static final String SP_AUTO_CONNECT_ENABLED = "auto_connect_enabled";//是否自动连接
    public static final String SP_CUSTOM_UNLOCK_RSSI = "custom_unlock_rssi";//开锁值
    public static final String SP_CUSTOM_LOCK_RSSI = "custom_lock_rssi";//关锁值
    private static SPUtils mSPUtils;

    public SPUtils() {
        sp = ShengHaoApp.getInstance().getApplicationContext().getSharedPreferences(FILE_SP_NAME, Context.MODE_PRIVATE);
    }

    public static SPUtils getInstance() {
        if (mSPUtils == null) {
            synchronized (SPUtils.class) {
                if (mSPUtils == null) {
                    mSPUtils = new SPUtils();
                }
            }
        }
        return mSPUtils;
    }

    //    ---------------------  String ----------------------------

    /**
     * SP中写入String
     *
     * @param key   键
     * @param value 值
     */
    public void putString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    /**
     * SP中读取String
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值{@code ""}
     */
    public String getString(String key) {
        return getString(key, "");
    }

    /**
     * SP中读取String
     *
     * @param key 键
     * @return 存在返回对应值，存在返回默认值{@code defaultValue}
     */
    public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }


    //    ---------------------  int ----------------------------

    /**
     * SP中写入int
     *
     * @param key   键
     * @param value 值
     */
    public void putInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }


    /**
     * SP中读取int
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值{@code ""}
     */
    public int getInt(String key) {
        return getInt(key, -1);
    }

    /**
     * SP中读取int
     *
     * @param key 键
     * @return 存在返回对应值，存在返回默认值{@code defaultValue}
     */
    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }


    //    ---------------------  long ----------------------------

    /**
     * SP中写入long
     *
     * @param key   键
     * @param value 值
     */
    public void putLong(String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    /**
     * SP中读取long
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    public long getLong(String key) {
        return getLong(key, -1L);
    }

    /**
     * SP中读取long
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public long getLong(String key, long defaultValue) {
        return sp.getLong(key, defaultValue);
    }


    //    ---------------------  float ----------------------------

    /**
     * SP中写入float
     *
     * @param key   键
     * @param value 值
     */
    public void putFloat(String key, float value) {
        sp.edit().putFloat(key, value).apply();
    }

    /**
     * SP中读取float
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    public float getFloat(String key) {
        return getFloat(key, -1f);
    }

    /**
     * SP中读取float
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public float getFloat(String key, float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }


    //    ---------------------  boolean ----------------------------

    /**
     * SP中写入long
     *
     * @param key   键
     * @param value 值
     */
    public void putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }


    /**
     * SP中读取boolean
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值{@code false}
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * SP中读取boolean
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    /**
     * SP中是否存在该key
     *
     * @param key
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public boolean contains(String key) {
        return sp.contains(key);
    }

    /**
     * SP中移除该key
     *
     * @param key 键
     */
    public void remove(String key) {
        sp.edit().remove(key).apply();
    }


    /**
     * SP中清除所有数据
     */
    public void clear() {
        sp.edit().clear().apply();
    }
    public void putMacList(String mac) {
        List<String> list = getMacList();
        if(list.contains(mac)) return;
        list.add(mac);
        Gson gson = new Gson();
        String json = gson.toJson(list);
        SPUtils.getInstance().putString(SPUtils.SP_BLE_LIST_MAC,json);
    }

    public List<String> getMacList(){
        String json = SPUtils.getInstance().getString(SPUtils.SP_BLE_LIST_MAC);//读取
        if(TextUtils.isEmpty(json))
            return new ArrayList<>();
        // 使用 Gson 将 JSON 字符串转换为 List<String>
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> list = gson.fromJson(json, type);
        return list;
    }
}
