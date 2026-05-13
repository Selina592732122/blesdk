package com.shenghao.blesdkdemo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private static final String TAG = "JsonUtils";
    public static String toStr(Object object) {
        try {
            return JSON.toJSONString(object);
        } catch (JSONException e) {
            LogUtils.d(TAG, "toStr: " + e);
            return null;
        }
    }

    public static byte[] toByte(Object object) {
        try {
            return JSON.toJSONBytes(object);
        } catch (JSONException e) {
            LogUtils.d(TAG, "toByte: " + e);
            return null;
        }
    }

    public static <T> T parseT(String json,Class<T> clazz) {
        try {
            return JSON.parseObject(json, clazz);
        } catch (JSONException e) {
            LogUtils.d(TAG, "parseT: " + e);
            return null;
        }
    }

    public static <T> List<T> parseArray(String json,Class<T> clazz) {
        try {
            return JSON.parseArray(json, clazz);
        } catch (JSONException e) {
            LogUtils.d(TAG, "parseArray: " + e);
            return new ArrayList<T>(0);
        }
    }

    public static <T> T parseObject(String json, TypeReference<T> type) {
        try {
            return JSON.parseObject(json, type);
        } catch (JSONException e) {
            LogUtils.d(TAG, "parseObject: " + e);
            return null;
        }
    }

    public static <T> T parseData(String json) {
        try {
            return JSON.parseObject(json, new TypeReference<T>() {
            });
//            return JSON.parseObject(json, type);
        } catch (JSONException e) {
            LogUtils.d(TAG, "parseObject: " + e);
            return null;
        }
    }

}
