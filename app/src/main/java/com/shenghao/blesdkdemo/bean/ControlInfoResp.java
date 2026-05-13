package com.shenghao.blesdkdemo.bean;


import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;

public class ControlInfoResp extends OkHttpBaseResp {
    private ControlInfo data;

    public ControlInfo getData() {
        return data;
    }

    public void setData(ControlInfo data) {
        this.data = data;
    }
}
