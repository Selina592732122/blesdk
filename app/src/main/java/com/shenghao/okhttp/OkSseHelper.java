package com.shenghao.okhttp;

import android.text.TextUtils;


import com.shenghao.utility.AppSingleton;
import com.shenghao.utils.AppUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSourceListener;

public class OkSseHelper {
    private static final String TAG = "OkSseHelper";
    private static OkSseHelper okSseHelper;
    private static OkHttpClient okHttpClient;
    private RealEventSource realEventSource;

    private static final int DEFAULT_TIMEOUT = 24 * 60 * 60 * 1000;

    public static OkSseHelper getInstance() {
        if (okSseHelper == null) {
            synchronized (OkSseHelper.class) {
                if (okSseHelper == null) {
                    okSseHelper = new OkSseHelper();
                }
            }
        }
        return okSseHelper;
    }

    private OkSseHelper() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .build();
        }
    }

    /**
     * 连接SSE
     */
    public void connectSSE(String url, EventSourceListener eventSourceListener) {
        try {
            closeSSE();
            Request request = createRequest(url);
            realEventSource = new RealEventSource(request, eventSourceListener);
            realEventSource.connect(okHttpClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭SSE连接
     */
    public void closeSSE() {
        if (realEventSource != null) {
            realEventSource.cancel();
        }
    }

    private Request createRequest(String url) {
        Request.Builder builder = createBuilder();
        Request request = builder.url(url).build();
        return request;
    }

    private Request.Builder createBuilder() {
        Request.Builder builder = new Request.Builder();
        String imei = AppSingleton.getInstance().getImei();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String sign = AppUtils.getHttpSign(imei, timeStamp);
        String token = AppSingleton.getInstance().getToken();
        if (!TextUtils.isEmpty(imei)) {
            builder.addHeader("imei", imei);
        }
        builder.addHeader("timestamp", timeStamp);
        if (!TextUtils.isEmpty(sign)) {
            builder.addHeader("sign", sign);
        }
        if (!TextUtils.isEmpty(token)) {
            builder.addHeader("token", token);
        }

//        builder.addHeader("Content-Type", "application/json");
//        builder.addHeader("Content-Type", "application/json; charset=utf8");
        return builder;
    }


}
