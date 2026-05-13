package com.shenghao.blesdkdemo.bean;

import java.io.Serializable;

/**
 * 骑行轨迹点
 */
public class RidingPoint implements Serializable {
    private double lng; //经度
    private double lat; //纬度
    private long timeMillis;    //定位时间
    private float speed;    //速度
    private int direction;  //方向
    private int accState;   //acc状态（0-关闭；1-开启）

    public RidingPoint() {
    }

    public RidingPoint(double lng, double lat, long timeMillis, float speed, int direction, int accState) {
        this.lng = lng;
        this.lat = lat;
        this.timeMillis = timeMillis;
        this.speed = speed;
        this.direction = direction;
        this.accState = accState;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getAccState() {
        return accState;
    }

    public void setAccState(int accState) {
        this.accState = accState;
    }

    @Override
    public String toString() {
        return "RidingPoint{" +
                "lng=" + lng +
                ", lat=" + lat +
                ", timeMillis=" + timeMillis +
                ", speed=" + speed +
                ", direction=" + direction +
                ", accState=" + accState +
                '}';
    }
}
