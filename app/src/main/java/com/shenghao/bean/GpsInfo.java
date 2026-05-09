package com.shenghao.bean;

public class GpsInfo {
    private int id;
    private String phone;
    private String terminalId;
    private String terminalNo;
    private String terminalType;
    private String carIdentifier;
    private long positioningTime;
    private int defenceStatus;
    private int accState;   //0-关闭;1-启动
    private double lat; //纬度
    private double lng; //经度
    private float voltage; //电压
    private double quantity;//电量
    private String address; //位置信息

    private int lockStatus;//锁状态 0-开锁 1-关锁

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public int getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(int lockStatus) {
        this.lockStatus = lockStatus;
    }

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

    public long getPositioningTime() {
        return positioningTime;
    }

    public void setPositioningTime(long positioningTime) {
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

    public float getVoltage() {
        return voltage;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "GpsInfo{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", terminalNo='" + terminalNo + '\'' +
                ", terminalType='" + terminalType + '\'' +
                ", carIdentifier='" + carIdentifier + '\'' +
                ", positioningTime='" + positioningTime + '\'' +
                ", defenceStatus=" + defenceStatus +
                ", accState=" + accState +
                ", lat=" + lat +
                ", lng=" + lng +
                ", voltage=" + voltage +
                '}';
    }
}
