package com.shenghao.blesdkdemo.bean;


import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;

public class UpgradeVersionResp extends OkHttpBaseResp {
    private UpgradeVersionBean data;

    public UpgradeVersionBean getData() {
        return data;
    }

    public void setData(UpgradeVersionBean data) {
        this.data = data;
    }
}
