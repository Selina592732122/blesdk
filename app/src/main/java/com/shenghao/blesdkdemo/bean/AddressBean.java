package com.shenghao.blesdkdemo.bean;

import java.io.Serializable;
import java.util.List;

public class AddressBean implements Serializable {
    private int id;
    private int parentId;
    private String appFlag;
    private String regionName;
    private List<AddressBean> children;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getAppFlag() {
        return appFlag;
    }

    public void setAppFlag(String appFlag) {
        this.appFlag = appFlag;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public List<AddressBean> getChildren() {
        return children;
    }

    public void setChildren(List<AddressBean> children) {
        this.children = children;
    }
}
