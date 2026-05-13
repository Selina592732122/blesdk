package com.shenghao.blesdkdemo.utils;

public class MapUtils {
    public static final String PACK_GAODE = "com.autonavi.minimap";
    public static final String PACK_BAIDU = "com.baidu.BaiduMap";

    /**
     * 根据距离计算缩放zoom
     */
    public static float calculateZoomByDistance(float distance) {
        float zoom;
        if (distance <= 200) {
            zoom = 18f;
        } else if (distance <= 500) {
            zoom = 17f;
        } else if (distance <= 1000) {
            zoom = 16f;
        } else if (distance <= 2000) {
            zoom = 15f;
        } else if (distance <= 4000) {
            zoom = 14f;
        } else if (distance <= 6000) {
            zoom = 13.5f;
        } else if (distance <= 8000) {
            zoom = 13f;
        } else if (distance <= 12000) {
            zoom = 12.5f;
        } else if (distance <= 16000) {
            zoom = 12f;
        } else if (distance <= 24000) {
            zoom = 11.5f;
        } else if (distance <= 32000) {
            zoom = 11f;
        } else {
            zoom = 10f;
        }
        return zoom;
    }

}
