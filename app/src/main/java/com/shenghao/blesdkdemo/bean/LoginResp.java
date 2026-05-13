package com.shenghao.blesdkdemo.bean;


import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;

public class LoginResp extends OkHttpBaseResp {
    private String data;    //token

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
