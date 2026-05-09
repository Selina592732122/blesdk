package com.shenghao.bean;


import com.shenghao.okhttp.OkHttpBaseResp;

public class GeoFenceResp extends OkHttpBaseResp {
    private GeoFenceBean data;

    public GeoFenceBean getData() {
        return data;
    }

    public void setData(GeoFenceBean data) {
        this.data = data;
    }
}
