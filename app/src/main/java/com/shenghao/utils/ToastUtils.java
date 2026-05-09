package com.shenghao.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ToastUtils {
    public static void showShort(Context context, final String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        } else {
            if (context instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) context;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public static void showLong(Context context, final String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        } else {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

}
