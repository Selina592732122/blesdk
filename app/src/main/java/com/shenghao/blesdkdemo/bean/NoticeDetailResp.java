package com.shenghao.blesdkdemo.bean;


import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;

public class NoticeDetailResp extends OkHttpBaseResp {
    private NoticeData data;

    public NoticeData getData() {
        return data;
    }

    public void setData(NoticeData data) {
        this.data = data;
    }
}
