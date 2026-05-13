package com.shenghao.blesdkdemo.bean;


import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;

import java.util.List;

/**
 * 骑行轨迹resp
 */
public class RidingPointResp extends OkHttpBaseResp {
    private List<RidingPointBean> data;

    public List<RidingPointBean> getData() {
        return data;
    }

    public void setData(List<RidingPointBean> data) {
        this.data = data;
    }
}
