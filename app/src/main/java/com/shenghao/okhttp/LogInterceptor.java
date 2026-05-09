package com.shenghao.okhttp;

import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okio.Buffer;

/**
 *  日志拦截
 */

public class LogInterceptor implements Interceptor {

    public static String TAG = "LogInterceptor";

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = System.currentTimeMillis();
        okhttp3.Response response = chain.proceed(chain.request());
        long endTime = System.currentTimeMillis();
        long duration=endTime-startTime;
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        Log.d(TAG,"\n");
        Log.d(TAG,"----------Start----------------");
        Log.d(TAG, "| "+request.toString());
        String method=request.method();
        Request copyRequest = request.newBuilder().build();
        String requestBody = "";
        if (copyRequest.body() != null) {
            Buffer buffer = new Buffer();
            copyRequest.body().writeTo(buffer);
            requestBody = buffer.readUtf8(); // 获取原始请求体字符串
        }

        if("PUT".equals(method) ||"DELETE".equals(method) || "POST".equals(method)){
            StringBuilder sb = new StringBuilder();
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                for (int i = 0; i < body.size(); i++) {
                    sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                }
                sb.delete(sb.length() - 1, sb.length());
                Log.d(TAG, "| RequestParams:{"+sb.toString()+"}");
            }else {//json形式
                // 打印原始请求体（如 JSON）
                Log.d(TAG,"| Request Body: "+requestBody);
            }
        }
        Log.d(TAG, "| Response:" + content);
        Log.d(TAG,"----------End:"+duration+"毫秒----------");
        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    }
}

