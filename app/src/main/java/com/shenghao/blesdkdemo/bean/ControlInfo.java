package com.shenghao.blesdkdemo.bean;

public class ControlInfo {
    //{"terminalNo":"17077039591","controlSys":"1","motorSensor":"1","turnAround":"1","brakingSys":"1","highvoltageProtect":"1","undervoltageProtect":"1","highProtect":"1","overcurrentProtect":"1","voltageSelect":"1","startWay":"1","asternwayStartWay":"1","forwardRegulate":"1","asternwayRegulate":"1","reversible":"0","startMode":"0","ebsSelect":"1","cruiseSelect":"1","currentLimitingSet":"1","pselect":"1","fvolRegulate":"1","dvolRegulate":"1"}
   //control_sys 控制系统(0-异常1正常),motor_sensor电机传感器(0-异常1正常),turn_around 转把，braking_sys刹车系统(0-异常1正常)，
//highvoltage_protect 高压保护(0-异常1正常),undervoltage protect欠压保护(0-异常1正常),high_protect高温保护,overcurrent protect过流保护

//        lsternwayStartWay=倒车起步方式
//        voltageSelect=电压选择
//        startWay=起步方式
//        liupoVolRegulate=防溜坡强度的调节
//            doupoVolRegulate=陡坡缓降强度调节大
//        forwardRegulate=前进速度调节
//            asternwayRegllate=倒车速度调节
//            reversible=正反转
//            startMode=启动方式
//        pgearSelect=P档选择
//            ebsSelect=EBS选择
//        cruiseSelect=巡航选择
//            currentLimitingSet=限流设置
//        00=车辆信息页面 01=状态调节页面 02=速度调节页面03=功能状态页面
//    @param status 0=48V 1=60V 2=70V    0=舒适 1=标准 2=运动 0=舒适or标准 1=运动
//    0=正转 1=反转 0=软启动 1=硬启动 0=关 1=开
//    0=恢复出厂设置type=00,01,02,03时)
        private String terminalNo;
        private int controlSys = 1;
        private int motorSensor = 1;
        private int turnAround = 1;
        private int brakingSys = 1;
        private int highvoltageProtect = 1;
        private int undervoltageProtect = 1;
        private int highProtect = 1;
        private int overcurrentProtect = 1;
        private int voltageSelect;
        private int startWay;
        private int asternwayStartWay;
        private int forwardRegulate;
        private int asternwayRegulate;
        private int reversible;
        private int startMode;
        private int ebsSelect;
        private int cruiseSelect;
        private int currentLimitingSet;
        private int pgearSelect;
        private int liupoVolRegulate;
        private int doupoVolRegulate;

    public String getTerminalNo() {
        return terminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    public int getControlSys() {
        return controlSys;
    }

    public void setControlSys(int controlSys) {
        this.controlSys = controlSys;
    }

    public int getMotorSensor() {
        return motorSensor;
    }

    public void setMotorSensor(int motorSensor) {
        this.motorSensor = motorSensor;
    }

    public int getTurnAround() {
        return turnAround;
    }

    public void setTurnAround(int turnAround) {
        this.turnAround = turnAround;
    }

    public int getBrakingSys() {
        return brakingSys;
    }

    public void setBrakingSys(int brakingSys) {
        this.brakingSys = brakingSys;
    }

    public int getHighvoltageProtect() {
        return highvoltageProtect;
    }

    public void setHighvoltageProtect(int highvoltageProtect) {
        this.highvoltageProtect = highvoltageProtect;
    }

    public int getUndervoltageProtect() {
        return undervoltageProtect;
    }

    public void setUndervoltageProtect(int undervoltageProtect) {
        this.undervoltageProtect = undervoltageProtect;
    }

    public int getHighProtect() {
        return highProtect;
    }

    public void setHighProtect(int highProtect) {
        this.highProtect = highProtect;
    }

    public int getOvercurrentProtect() {
        return overcurrentProtect;
    }

    public void setOvercurrentProtect(int overcurrentProtect) {
        this.overcurrentProtect = overcurrentProtect;
    }

    public int getVoltageSelect() {
        return voltageSelect;
    }

    public void setVoltageSelect(int voltageSelect) {
        this.voltageSelect = voltageSelect;
    }

    public int getStartWay() {
        return startWay;
    }

    public void setStartWay(int startWay) {
        this.startWay = startWay;
    }

    public int getAsternwayStartWay() {
        return asternwayStartWay;
    }

    public void setAsternwayStartWay(int asternwayStartWay) {
        this.asternwayStartWay = asternwayStartWay;
    }

    public int getForwardRegulate() {
        return forwardRegulate;
    }

    public void setForwardRegulate(int forwardRegulate) {
        this.forwardRegulate = forwardRegulate;
    }

    public int getAsternwayRegulate() {
        return asternwayRegulate;
    }

    public void setAsternwayRegulate(int asternwayRegulate) {
        this.asternwayRegulate = asternwayRegulate;
    }

    public int getReversible() {
        return reversible;
    }

    public void setReversible(int reversible) {
        this.reversible = reversible;
    }

    public int getStartMode() {
        return startMode;
    }

    public void setStartMode(int startMode) {
        this.startMode = startMode;
    }

    public int getEbsSelect() {
        return ebsSelect;
    }

    public void setEbsSelect(int ebsSelect) {
        this.ebsSelect = ebsSelect;
    }

    public int getCruiseSelect() {
        return cruiseSelect;
    }

    public void setCruiseSelect(int cruiseSelect) {
        this.cruiseSelect = cruiseSelect;
    }

    public int getCurrentLimitingSet() {
        return currentLimitingSet;
    }

    public void setCurrentLimitingSet(int currentLimitingSet) {
        this.currentLimitingSet = currentLimitingSet;
    }

    public int getPgearSelect() {
        return pgearSelect;
    }

    public void setPgearSelect(int pgearSelect) {
        this.pgearSelect = pgearSelect;
    }

    public int getLiupoVolRegulate() {
        return liupoVolRegulate;
    }

    public void setLiupoVolRegulate(int liupoVolRegulate) {
        this.liupoVolRegulate = liupoVolRegulate;
    }

    public int getDoupoVolRegulate() {
        return doupoVolRegulate;
    }

    public void setDoupoVolRegulate(int doupoVolRegulate) {
        this.doupoVolRegulate = doupoVolRegulate;
    }
}
