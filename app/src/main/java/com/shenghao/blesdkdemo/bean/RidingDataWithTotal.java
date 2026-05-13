package com.shenghao.blesdkdemo.bean;

import java.io.Serializable;
import java.util.List;

public class RidingDataWithTotal implements Serializable {
    private int total;
    private List<RidingDataBean> list;

    public List<RidingDataBean> getList() {
        return list;
    }

    public void setList(List<RidingDataBean> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
