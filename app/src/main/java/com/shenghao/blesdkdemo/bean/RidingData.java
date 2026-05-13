package com.shenghao.blesdkdemo.bean;

import java.io.Serializable;
import java.util.List;

public class RidingData implements Serializable {
    private String date;
    private String startTime;
    private String endTime;
    private String startPoint;
    private String endPoint;
    private String distance;
    private String duration;
    private String speed;
    private List<RidingPoint> ridingPointList;

    public RidingData() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public List<RidingPoint> getRidingPointList() {
        return ridingPointList;
    }

    public void setRidingPointList(List<RidingPoint> ridingPointList) {
        this.ridingPointList = ridingPointList;
    }

    @Override
    public String toString() {
        return "RidingData{" +
                "date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", startPoint='" + startPoint + '\'' +
                ", endPoint='" + endPoint + '\'' +
                ", distance='" + distance + '\'' +
                ", duration='" + duration + '\'' +
                ", speed='" + speed + '\'' +
                ", ridingPointList=" + ridingPointList +
                '}';
    }
}
