package com.shenghao.bean;


import com.shenghao.okhttp.OkHttpBaseResp;

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
