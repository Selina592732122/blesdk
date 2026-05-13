package com.shenghao.blesdkdemo.bean;


import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;

import java.util.List;

public class StoreResp extends OkHttpBaseResp {
    private List<StoreBean> data;    //token

    public List<StoreBean> getData() {
        return data;
    }

    public void setData(List<StoreBean> data) {
        this.data = data;
    }
}
