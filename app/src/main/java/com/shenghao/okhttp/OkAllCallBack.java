package com.shenghao.okhttp;

import android.os.Handler;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class OkAllCallBack extends OkCallBack {

    public OkAllCallBack(Handler handler, OkHttpResultCallBack okHttpResultCallBack) {
        super(handler, okHttpResultCallBack);
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if (okHttpResultCallBack != null) {
            if (response.isSuccessful()) {
                try {
                    String body = response.body() != null ? response.body().string() : null;
                    succCallBack(call, response, body, okHttpResultCallBack);
                } catch (IOException e) {
                    failedCallBack(call, e, okHttpResultCallBack);
                }
            } else {
                failedCallBack(call, response, okHttpResultCallBack);
            }
        }
    }
}
