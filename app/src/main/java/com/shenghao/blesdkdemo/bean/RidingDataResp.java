package com.shenghao.blesdkdemo.bean;


import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;

public class RidingDataResp extends OkHttpBaseResp {
    private RidingDataWithTotal data;

    public RidingDataWithTotal getData() {
        return data;
    }

    public void setData(RidingDataWithTotal data) {
        this.data = data;
    }
}
