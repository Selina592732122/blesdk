package com.shenghao.blesdkdemo.bean;

import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;
import java.util.List;

public class ThemeResp extends OkHttpBaseResp {
    private List<ThemeBean> data;    //token

    public List<ThemeBean> getData() {
        return data;
    }

    public void setData(List<ThemeBean> data) {
        this.data = data;
    }
}
