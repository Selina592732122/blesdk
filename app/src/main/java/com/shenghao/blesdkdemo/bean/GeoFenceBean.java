package com.shenghao.blesdkdemo.bean;

public class GeoFenceBean {
    private int id;
    private String terminalNo;
    private String fenceName;
    private String fenceType;
    private String points;
    private int radius;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTerminalNo() {
        return terminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    public String getFenceName() {
        return fenceName;
    }

    public void setFenceName(String fenceName) {
        this.fenceName = fenceName;
    }

    public String getFenceType() {
        return fenceType;
    }

    public void setFenceType(String fenceType) {
        this.fenceType = fenceType;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
