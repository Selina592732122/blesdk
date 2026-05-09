package com.shenghao.okhttp;


import static com.shenghao.okhttp.OkHttpMethod.M_DELETE;
import static com.shenghao.okhttp.OkHttpMethod.M_GET;
import static com.shenghao.okhttp.OkHttpMethod.M_POST;
import static com.shenghao.okhttp.OkHttpMethod.M_PUT;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;


import com.shenghao.utility.AppSingleton;
import com.shenghao.utils.AppUtils;
import com.shenghao.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpHelper {

    private static final String TAG = "OkHttpHelper";
    private static OkHttpHelper okHttpHelper;
    private static OkHttpClient okHttpClient;
    private static OkHttpClient okHttpUploadClient;
    private static Handler handler;

    private static final int UPLOAD_TIMEOUT = 60;

    public static OkHttpHelper getInstance() {
        if (okHttpHelper == null) {
            synchronized (OkHttpHelper.class) {
                if (okHttpHelper == null) {
                    okHttpHelper = new OkHttpHelper();
                }
            }
        }
        return okHttpHelper;
    }

    private OkHttpHelper() {
        if (okHttpClient == null) {
            LogInterceptor logInterceptor = new LogInterceptor();
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logInterceptor)
                    .build();
        }

        if (okHttpUploadClient == null) {
            okHttpUploadClient = new OkHttpClient.Builder()
                    .readTimeout(UPLOAD_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(UPLOAD_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(UPLOAD_TIMEOUT, TimeUnit.SECONDS)
                    .build();
        }

        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
    }

    public String execute(@OkHttpMethod.RequestMethod String method, String url, OkHttpRequestParams params) {
        return execute(method, url, null, params);
    }

    public String execute(@OkHttpMethod.RequestMethod String method, String url, Object tag,
                          OkHttpRequestParams params) {
        if (url == null || HttpUrl.parse(url) == null) {
            LogUtils.d(TAG, "okhttp请求失败：无效的url");
            return null;
        }
        Request request = createRequest(method, url, tag, params);
        Call call = okHttpClient.newCall(request);
        try {
            String result = null;
            Response response = call.execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            }
            return result;
        } catch (IOException e) {
            LogUtils.d(TAG, "okhttp请求失败：" + e);
            return null;
        }
    }

    public void enqueue(@OkHttpMethod.RequestMethod String method, String url,
                        OkHttpRequestParams params, OkHttpResultCallBack okHttpResultCallBack) {
        enqueue(method, url, null, params, okHttpResultCallBack);
    }

    public void enqueue(@OkHttpMethod.RequestMethod String method, String url, Object tag,
                        OkHttpRequestParams params, OkHttpResultCallBack okHttpResultCallBack) {
        if (url == null || HttpUrl.parse(url) == null) {
            new OkCallBack(handler, okHttpResultCallBack).failedCallBack(null,
                    new NullPointerException("无效的url"), okHttpResultCallBack);
            return;
        }
        try {
            Request request = createRequest(method, url, tag, params);
            Call call = okHttpClient.newCall(request);
            call.enqueue(new OkCallBack(handler, okHttpResultCallBack));
        } catch (Exception e) {
            new OkCallBack(handler, okHttpResultCallBack).failedCallBack(null, e,
                    okHttpResultCallBack);
        }
    }

    public void enqueueAll(@OkHttpMethod.RequestMethod String method, String url,
                           OkHttpRequestParams params, OkHttpResultCallBack okHttpResultCallBack) {
        enqueueAll(method, url, null, params, okHttpResultCallBack);
    }

    public void enqueueAll(@OkHttpMethod.RequestMethod String method, String url, Object tag,
                           OkHttpRequestParams params, OkHttpResultCallBack okHttpResultCallBack) {
        if (url == null || HttpUrl.parse(url) == null) {
            new OkCallBack(handler, okHttpResultCallBack).failedCallBack(null,
                    new NullPointerException("无效的url"), okHttpResultCallBack);
            return;
        }
        try {
            Request request = createRequest(method, url, tag, params);
            okHttpClient.newCall(request).enqueue(new OkAllCallBack(handler, okHttpResultCallBack));
        } catch (Exception e) {
            new OkAllCallBack(handler, okHttpResultCallBack).failedCallBack(null, e,
                    okHttpResultCallBack);
        }
    }

    public void postJson(String url, OkHttpRequestParams params, String json, OkHttpResultCallBack okHttpResultCallBack) {
        if (url == null || HttpUrl.parse(url) == null) {
            new OkCallBack(handler, okHttpResultCallBack).failedCallBack(null,
                    new NullPointerException("无效的url"), okHttpResultCallBack);
            return;
        }
        try {
            Request request = createBuilder(params == null ? null : params.getHeaderItemList())
                    .url(url)
                    .post(createRequestJsonBody(json))
                    .build();
            okHttpClient.newCall(request).enqueue(new OkAllCallBack(handler, okHttpResultCallBack));
        } catch (Exception e) {
            new OkAllCallBack(handler, okHttpResultCallBack).failedCallBack(null, e,
                    okHttpResultCallBack);
        }
    }

    public void uploadFile(String url, OkHttpRequestParams params, OkHttpResultCallBack okHttpResultCallBack) {
        if (url == null || HttpUrl.parse(url) == null) {
            new OkCallBack(handler, okHttpResultCallBack).failedCallBack(null,
                    new NullPointerException("无效的url"), okHttpResultCallBack);
            return;
        }
        try {
            Request request = createBuilder(params == null ? null : params.getHeaderItemList())
                    .url(url)
                    .post(new ProgressRequestBody(handler,
                            createFileBody(params == null ? null : params.getBodyItemList()),
                            okHttpResultCallBack))
                    .build();
            okHttpUploadClient.newCall(request)
                    .enqueue(new OkAllCallBack(handler, okHttpResultCallBack));
        } catch (Exception e) {
            new OkAllCallBack(handler, okHttpResultCallBack).failedCallBack(null, e,
                    okHttpResultCallBack);
        }
    }

    private Request createRequest(@OkHttpMethod.RequestMethod String method, String url, Object tag,
                                  OkHttpRequestParams params) {
        Builder builder = createBuilder(
                params == null ? null : params.getHeaderItemList()).tag(tag);
        Request request = null;
        switch (method) {
            case M_GET:
                request = builder.url(
                                createGetUrl(url, params == null ? null : params.getBodyItemList()))
                        .build();
                break;
            case M_POST:
                request = builder.url(url)
                        .post(createRequestBody(params == null ? null : params.getBodyItemList()))
                        .build();
                break;
            case M_DELETE:
                request = builder.url(url)
//                request = builder.url(
//                                createGetUrl(url, params == null ? null : params.getBodyItemList()))
                        .delete(new FormBody.Builder().build()).build();
                break;
            case M_PUT:
                request = builder.url(url)
                        .put(createRequestBody(params == null ? null : params.getBodyItemList()))
                        .build();
                break;
        }
        return request;
    }

    private Builder createBuilder(List<OkHttpItem> header) {
        Builder builder = new Builder();
        if (header != null && !header.isEmpty()) {
            for (OkHttpItem item : header) {
                builder.addHeader(item.getKey(), item.getValue());
            }
        }
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
        builder.addHeader("appFlag", "shenghao");

//        builder.addHeader("Content-Type", "application/json");
//        builder.addHeader("Content-Type", "application/json; charset=utf8");
        return builder;
    }

    private String createGetUrl(String url, List<OkHttpItem> itemList) {
        if (itemList != null && !itemList.isEmpty()) {
            StringBuilder stringBuilder;
            if (url.contains("?")) {
                if (url.endsWith("?")) {
                    stringBuilder = new StringBuilder(url);
                } else {
                    stringBuilder = new StringBuilder(url + "&");
                }
            } else {
                stringBuilder = new StringBuilder(url + "?");
            }
            for (OkHttpItem item : itemList) {
                stringBuilder.append(item.getKey())
                        .append("=")
                        .append(item.getValue())
                        .append("&");
            }
            if (stringBuilder.lastIndexOf("&") == stringBuilder.length() - 1) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            url = stringBuilder.toString();
        }
        return url;
    }

    private RequestBody createRequestBody(List<OkHttpItem> itemList) {
        FormBody.Builder builder = new FormBody.Builder();
        if (itemList != null && !itemList.isEmpty()) {
            for (OkHttpItem item : itemList) {
                builder.add(item.getKey(), item.getValue());
            }
        }
        RequestBody requestBody = builder.build();
        return requestBody;
    }

    private RequestBody createRequestJsonBody(String json) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(json, mediaType);
        return requestBody;
    }

    private RequestBody createFileBody(List<OkHttpItem> itemList) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (itemList != null && !itemList.isEmpty()) {
            for (OkHttpItem item : itemList) {
                File file = item.getFile();
                if (file == null) {
                    builder.addFormDataPart(item.getKey(), item.getValue());
                } else {
                    MediaType mediaType = MediaType.parse(guessMimeType(file.getPath()));
                    builder.addFormDataPart(item.getKey(), file.getName(),
                            RequestBody.create(file, mediaType));
                }
            }
        }
        RequestBody requestBody = builder.build();
        return requestBody;
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = null;
        try {
            type = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (type == null) {
            type = "application/octet-stream";
        }
        return type;
    }

    public void cancelTag(Object tag) {
        if (tag == null) {
            return;
        }
        if (okHttpClient != null) {
            for (Call call : okHttpClient.dispatcher().queuedCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
            for (Call call : okHttpClient.dispatcher().runningCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
        }
    }

}
