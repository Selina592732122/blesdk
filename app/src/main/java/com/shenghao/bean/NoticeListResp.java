package com.shenghao.bean;


import com.shenghao.okhttp.OkHttpBaseResp;

import java.util.List;

public class NoticeListResp extends OkHttpBaseResp {
    private List<NoticeData> data;

    public List<NoticeData> getData() {
        return data;
    }

    public void setData(List<NoticeData> data) {
        this.data = data;
    }
}
