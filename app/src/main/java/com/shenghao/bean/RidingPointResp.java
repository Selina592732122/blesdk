package com.shenghao.bean;


import com.shenghao.okhttp.OkHttpBaseResp;

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
