package com.shenghao.bean;


import com.shenghao.okhttp.OkHttpBaseResp;

public class MailLoginResp extends OkHttpBaseResp {
    private MailLogin data;    //token

    public MailLogin getData() {
        return data;
    }

    public void setData(MailLogin data) {
        this.data = data;
    }
}
