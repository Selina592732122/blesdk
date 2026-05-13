package com.shenghao.blesdkdemo.okhttp;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public abstract class OkHttpResultCallBack {

    protected void start() {
    }

    protected void end() {
    }

    protected void asyncResponse(Response response, String body) throws IOException {
    }

    protected abstract void onResponse(Response response, String body) throws IOException;

    protected void onFailed(Request request, Exception e) {
    }

    public void onReqProgress(long cur, long total, boolean finish) {
    }

    public void onRespProgress(int progress) {
    }

}
