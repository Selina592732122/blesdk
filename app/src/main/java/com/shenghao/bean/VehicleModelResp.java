package com.shenghao.bean;



import com.shenghao.okhttp.OkHttpBaseResp;

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
