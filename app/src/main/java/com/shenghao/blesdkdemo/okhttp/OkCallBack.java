package com.shenghao.blesdkdemo.okhttp;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OkCallBack implements Callback {

    private static final String TAG = "OkCallBack";

    protected OkHttpResultCallBack okHttpResultCallBack;
    protected Handler handler;

    public OkCallBack(Handler handler, OkHttpResultCallBack okHttpResultCallBack) {
        this.handler = handler;
        this.okHttpResultCallBack = okHttpResultCallBack;
        if (okHttpResultCallBack != null) {
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        okHttpResultCallBack.start();
                    } catch (Exception e) {
                        okHttpResultCallBack.onFailed(null, e);
                        okHttpResultCallBack.end();
                    }
                }
            });
        }
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        failedCallBack(call, e, okHttpResultCallBack);
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if (okHttpResultCallBack != null) {
            if (response.isSuccessful()) {
                try {
                    String body = response.body() != null ? response.body().string() : null;
                    OkHttpBaseResp resp = parseT(body, OkHttpBaseResp.class);
                    if (resp != null && resp.isSuccess()) {
                        succCallBack(call, response, body, okHttpResultCallBack);
                    } else {
                        failedCallBack(call, response, okHttpResultCallBack);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    failedCallBack(call, e, okHttpResultCallBack);
                }
            } else {
                failedCallBack(call, response, okHttpResultCallBack);
            }
        }
    }

    public void succCallBack(Call call, Response response, String body,
            OkHttpResultCallBack okHttpResultCallBack) {
        try {
            okHttpResultCallBack.asyncResponse(response, body);
        } catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    okHttpResultCallBack.onFailed(call.request(), e);
                    okHttpResultCallBack.end();
                }
            });
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    okHttpResultCallBack.onResponse(response, body);
                } catch (Exception e) {
                    okHttpResultCallBack.onFailed(call.request(), e);
                }
                okHttpResultCallBack.end();
            }
        });
    }

    public void failedCallBack(Call call, Exception e, OkHttpResultCallBack okHttpResultCallBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (okHttpResultCallBack != null) {
                    if (call == null) {
                        try {
                            okHttpResultCallBack.onFailed(null, e);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        okHttpResultCallBack.end();
                        return;
                    }
                    if (call.isCanceled()) {
                        return;
                    }
                    okHttpResultCallBack.onFailed(call.request(), e);
                    okHttpResultCallBack.end();
                }
            }
        });
    }

    public void failedCallBack(Call call, Response response,
            OkHttpResultCallBack okHttpResultCallBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (call.isCanceled()) {
                    return;
                }
                String eMessage = String.valueOf(
                        response != null ? response.code() : "response为null");
                okHttpResultCallBack.onFailed(call.request(), new Exception("请求出错：" + eMessage));
                okHttpResultCallBack.end();
            }
        });
    }

    private <T> T parseT(String json, Class<T> clazz) {
        try {
            return JSON.parseObject(json, clazz);
        } catch (JSONException e) {
            return null;
        }
    }

}
