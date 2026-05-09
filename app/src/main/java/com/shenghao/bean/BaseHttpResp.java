package com.shenghao.bean;


import com.shenghao.okhttp.OkHttpBaseResp;

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
