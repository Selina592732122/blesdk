package com.shenghao.blesdkdemo.bean;

public class SimpleLocationBean {
    private double lat; //纬度
    private double lng; //精度
    private String address; //经纬度对应的地理位置

    public SimpleLocationBean(double lat, double lng, String address) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
