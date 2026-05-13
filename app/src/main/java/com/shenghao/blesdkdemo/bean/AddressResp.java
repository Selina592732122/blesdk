package com.shenghao.blesdkdemo.bean;


import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;

import java.util.List;

public class AddressResp extends OkHttpBaseResp {
    private List<AddressBean> data;    //token

    public List<AddressBean> getData() {
        return data;
    }

    public void setData(List<AddressBean> data) {
        this.data = data;
    }
}
