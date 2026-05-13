package com.shenghao.blesdkdemo.bean;


import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;

/**
 * 最新位置resp
 */
public class QRCodeResp extends OkHttpBaseResp {
    private QRCode data;

    public QRCode getData() {
        return data;
    }

    public void setData(QRCode data) {
        this.data = data;
    }
}
