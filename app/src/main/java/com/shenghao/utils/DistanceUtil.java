package com.shenghao.utils;

import java.text.DecimalFormat;

public class DistanceUtil {
    /**
     * m转km并保留一位小数
     */
    public static String getKM(float distanceM) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(distanceM / 1000f);
    }

    /**
     * 计算速度（千米/小时）。
     *
     * @param distance 米为单位的距离
     * @param time     毫秒为单位的时间
     * @return 速度，单位为千米每小时（km/h）
     */
    public static String calculateSpeed(double distance, long time) {
        if (time <= 0) {
            return "0";
        }

        // 计算速度的公式
        double speedKmPerHour = (distance / 1000.0) / ((time / (double) 3600000));
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(speedKmPerHour);
    }
}
