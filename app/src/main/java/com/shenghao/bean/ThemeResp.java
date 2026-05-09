package com.shenghao.bean;

import com.shenghao.okhttp.OkHttpBaseResp;
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
