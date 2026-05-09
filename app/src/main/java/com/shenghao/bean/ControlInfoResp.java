package com.shenghao.bean;


import com.shenghao.okhttp.OkHttpBaseResp;

public class ControlInfoResp extends OkHttpBaseResp {
    private ControlInfo data;

    public ControlInfo getData() {
        return data;
    }

    public void setData(ControlInfo data) {
        this.data = data;
    }
}
