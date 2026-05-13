package com.shenghao.blesdkdemo.bean;


import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;

public class BaseHttpResp<T> extends OkHttpBaseResp {
    private T data;

    public BaseHttpResp() {
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
