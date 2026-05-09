package com.shenghao.bean;


import com.shenghao.okhttp.OkHttpBaseResp;

public class UpgradeVersionResp extends OkHttpBaseResp {
    private UpgradeVersionBean data;

    public UpgradeVersionBean getData() {
        return data;
    }

    public void setData(UpgradeVersionBean data) {
        this.data = data;
    }
}
