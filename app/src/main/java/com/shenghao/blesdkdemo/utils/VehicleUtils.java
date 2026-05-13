package com.shenghao.blesdkdemo.utils;


import com.shenghao.blesdkdemo.utility.AppSingleton;

public class VehicleUtils {
    public static final float DEFAULT_SINGLE_POWER = 13.5f;    //单个电池默认电压数
    public static final float DEFAULT_SINGLE_POWER_LIFE = 3.0f;    //单个电池满电续航电压
    public static final float DEFAULT_POWER_AGING_RATE = 0.03f;    //电压衰减速率

    /**
     * 获取电量百分比
     *
     * @param power 总电源电压
     */
    public static double getPowerPercent(float power) {
        if (power > getMaxPower()) {
            return 100;
        }
        if (power < getMinPower()) {
            return 0;
        }

        float restOfPower = power - getMinPower();   //剩余电压数
        float agingPowerPercent = restOfPower / (DEFAULT_POWER_AGING_RATE * getCountBattery());  //转换成百分比
        return Math.min(agingPowerPercent, 100);
    }

    /**
     * 电压峰值
     */
    public static float getMaxPower() {
        return getCountBattery() * DEFAULT_SINGLE_POWER;
    }

    /**
     * 电压谷值
     */
    public static float getMinPower() {
        return getCountBattery() * DEFAULT_SINGLE_POWER - DEFAULT_SINGLE_POWER_LIFE * getCountBattery();
    }

    /**
     * 获取电池个数
     */
    public static int getCountBattery() {
        return AppSingleton.getInstance().getBatteryCount();
    }
}
