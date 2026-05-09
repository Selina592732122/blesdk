package com.shenghao.bean;

import android.text.TextUtils;

public class TerminalBean {
    private String phone;
    private String terminalNo;
    private String terminalId;
    private String terminalType;
    private String carIdentifier;
    private int defenceStatus;
    private int batteries;
    private double voltage;
    private String name;    //设备名称
    private boolean isSelected; //是否为当前设备
    private int status; //0-非默认应用；1-默认应用
    private String carPicture;
    private String doorType;//1有门 0没有门
    private String carControlType;//1有车控
    private String carName;//车型名称
    private String manual;//说明书url
    private String protocol;//808/MQTT就是旧蓝牙，can就是一键挪车新蓝牙
    private String vin;//车架号
    private int vehicleModel;
    private int isQuantity;//0-后端给 1-前端自己算,电量是否平台获取还是自己算

    public int getIsQuantity() {
        return isQuantity;
    }

    public void setIsQuantity(int isQuantity) {
        this.isQuantity = isQuantity;
    }

    public int getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(int vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * 共享状态（0-未共享 1-共享 2-被共享）
     */
    private int shareStatus;

    public int getShareStatus() {
        return shareStatus;
    }

    public void setShareStatus(int shareStatus) {
        this.shareStatus = shareStatus;
    }
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getManual() {
        return manual;
    }

    public void setManual(String manual) {
        this.manual = manual;
    }
    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarControlType() {
        return carControlType;
    }

    public void setCarControlType(String carControlType) {
        this.carControlType = carControlType;
    }

    public String getCarPicture() {
        return carPicture;
    }

    public void setCarPicture(String carPicture) {
        this.carPicture = carPicture;
    }

    public String getDoorType() {
        return doorType;
    }

    public void setDoorType(String doorType) {
        this.doorType = doorType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTerminalNo() {
        return terminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
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

    public int getDefenceStatus() {
        return defenceStatus;
    }

    public void setDefenceStatus(int defenceStatus) {
        this.defenceStatus = defenceStatus;
    }

    public int getBatteries() {
        return batteries;
    }

    public void setBatteries(int batteries) {
        this.batteries = batteries;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDisplayName() {
        if (!TextUtils.isEmpty(name)) {
            return name;
        }
        return terminalNo;
    }
}
