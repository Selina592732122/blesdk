package com.shenghao.bean;



import com.shenghao.okhttp.OkHttpBaseResp;

import java.util.List;

/**
 * 最新位置resp
 */
public class SysNoticeResp extends OkHttpBaseResp {
    private List<SysNotice> data;

    public List<SysNotice> getData() {
        return data;
    }

    public void setData(List<SysNotice> data) {
        this.data = data;
    }
}
