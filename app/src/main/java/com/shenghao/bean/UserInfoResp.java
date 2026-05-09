package com.shenghao.bean;


import com.shenghao.okhttp.OkHttpBaseResp;

public class UserInfoResp extends OkHttpBaseResp {
    private UserInfo data;

    public UserInfo getData() {
        return data;
    }

    public void setData(UserInfo data) {
        this.data = data;
    }
}
