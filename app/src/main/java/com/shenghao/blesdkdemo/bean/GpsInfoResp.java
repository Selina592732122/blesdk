package com.shenghao.blesdkdemo.bean;


import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;

/**
 * 最新位置resp
 */
public class GpsInfoResp extends OkHttpBaseResp {
    private GpsInfo data;

    public GpsInfo getData() {
        return data;
    }

    public void setData(GpsInfo data) {
        this.data = data;
    }
}
