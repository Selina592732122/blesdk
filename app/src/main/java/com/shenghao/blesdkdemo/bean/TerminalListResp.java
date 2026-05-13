package com.shenghao.blesdkdemo.bean;


import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;

import java.util.List;

/**
 * 设备列表
 */
public class TerminalListResp extends OkHttpBaseResp {
    private List<TerminalBean> data;

    public List<TerminalBean> getData() {
        return data;
    }

    public void setData(List<TerminalBean> data) {
        this.data = data;
    }
}
