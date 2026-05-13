package com.shenghao.blesdkdemo.bean;

/**
 * 行程轨迹点
 */
public class RidingPointBean {
    private int id;
    private String phone;
    private String terminalId;
    private String terminalNo;
    private String terminalType;
    private String carIdentifier;
    private String positioningTime;
    private int defenceStatus;
    private int accState;
    private double lat;
    private double lng;
    private double voltage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getTerminalNo() {
        return terminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public String getCarIdentifier() {
        return carIdentifier;
    }

    public void setCarIdentifier(String carIdentifier) {
        this.carIdentifier = carIdentifier;
    }

    public String getPositioningTime() {
        return positioningTime;
    }

    public void setPositioningTime(String positioningTime) {
        this.positioningTime = positioningTime;
    }

    public int getDefenceStatus() {
        return defenceStatus;
    }

    public void setDefenceStatus(int defenceStatus) {
        this.defenceStatus = defenceStatus;
    }

    public int getAccState() {
        return accState;
    }

    public void setAccState(int accState) {
        this.accState = accState;
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

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }
}
