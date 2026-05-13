package com.shenghao.blesdkdemo.bean;

import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;

import java.util.List;

/**
 * 最新位置resp
 */
public class ShareUserResp extends OkHttpBaseResp {
    private List<ShareUser> data;

    public List<ShareUser> getData() {
        return data;
    }

    public void setData(List<ShareUser> data) {
        this.data = data;
    }
}
