package com.shenghao.blesdkdemo.utils;



public class LogUtils {
//    private static final boolean IS_DEBUG = BuildConfig.DEBUG; // 使用BuildConfig.DEBUG来判断
    private static final boolean IS_DEBUG = true; // 使用BuildConfig.DEBUG来判断

    public static void v(String tag, String message) {
        if (IS_DEBUG) {
            android.util.Log.v(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (IS_DEBUG) {
            android.util.Log.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (IS_DEBUG) {
            android.util.Log.i(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (IS_DEBUG) {
            android.util.Log.w(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (IS_DEBUG) {
            android.util.Log.e(tag, message);
        }
    }
}
