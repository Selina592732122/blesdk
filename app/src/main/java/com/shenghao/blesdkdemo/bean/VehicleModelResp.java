package com.shenghao.blesdkdemo.bean;



import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;

import java.util.List;

public class VehicleModelResp extends OkHttpBaseResp {
    private List<VehicleModel> data;

    public List<VehicleModel> getData() {
        return data;
    }

    public void setData(List<VehicleModel> data) {
        this.data = data;
    }
}
