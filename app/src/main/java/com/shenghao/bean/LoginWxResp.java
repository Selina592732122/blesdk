package com.shenghao.bean;


import com.shenghao.okhttp.OkHttpBaseResp;

public class LoginWxResp extends OkHttpBaseResp {
    private BindWxBean data;    //token

    public BindWxBean getData() {
        return data;
    }

    public void setData(BindWxBean data) {
        this.data = data;
    }
}
