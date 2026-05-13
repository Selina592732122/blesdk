package com.shenghao.blesdkdemo.bean;


import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;

public class MailLoginResp extends OkHttpBaseResp {
    private MailLogin data;    //token

    public MailLogin getData() {
        return data;
    }

    public void setData(MailLogin data) {
        this.data = data;
    }
}
