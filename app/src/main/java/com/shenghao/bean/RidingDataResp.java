package com.shenghao.bean;


import com.shenghao.okhttp.OkHttpBaseResp;

public class RidingDataResp extends OkHttpBaseResp {
    private RidingDataWithTotal data;

    public RidingDataWithTotal getData() {
        return data;
    }

    public void setData(RidingDataWithTotal data) {
        this.data = data;
    }
}
