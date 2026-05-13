package com.shenghao.blesdkdemo.bean;


import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;

import java.util.List;

public class GeoFenceListResp extends OkHttpBaseResp {
    private List<GeoFenceBean> data;

    public List<GeoFenceBean> getData() {
        return data;
    }

    public void setData(List<GeoFenceBean> data) {
        this.data = data;
    }
}
