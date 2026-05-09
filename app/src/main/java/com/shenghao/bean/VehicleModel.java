package com.shenghao.bean;

import java.io.Serializable;
import java.util.List;

public class VehicleModel implements Serializable {
    private int id;
    private String name;
    private String picture;
    private List<VehicleModelBean> children;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<VehicleModelBean> getChildren() {
        return children;
    }

    public void setChildren(List<VehicleModelBean> children) {
        this.children = children;
    }
}
