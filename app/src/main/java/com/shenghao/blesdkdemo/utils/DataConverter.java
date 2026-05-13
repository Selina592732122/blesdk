package com.shenghao.blesdkdemo.utils;

import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class DataConverter {
    public static LatLng toLatLng(String pointStr) {
        try {
            String[] pointArray = pointStr.split(",");
            if (pointArray.length == 2) {
                return new LatLng(Double.parseDouble(pointArray[1]), Double.parseDouble(pointArray[0]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<LatLng> toLatLngList(String pointStr) {
        try {
            List<LatLng> latLngList = new ArrayList<>();
            String[] pointStrArray = pointStr.split("\\|");
            for (String pointSplitStr : pointStrArray) {
                String[] pointArray = pointSplitStr.split(",");
                if (pointArray.length == 2) {
                    latLngList.add(new LatLng(Double.parseDouble(pointArray[1]), Double.parseDouble(pointArray[0])));
                }
            }
            return latLngList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
