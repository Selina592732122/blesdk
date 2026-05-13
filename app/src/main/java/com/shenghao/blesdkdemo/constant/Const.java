package com.shenghao.blesdkdemo.constant;

public class Const {
    public static String TYPE_MAP_GAODE = "GAODE";    //地图类型-高德
    public static String TYPE_LANGUAGE_CN = "cn";    //语言类型-中文

    public static String TYPE_GEOFENCE_CIRCULAR = "CIRCULAR";   //圆形围栏
    public static String TYPE_GEOFENCE_RECTANGLE = "RECTANGLE";   //矩形围栏
    public static String TYPE_GEOFENCE_POLYGON = "POLYGON";   //多边形围栏

    public static int COMMAND_POWER_CONTROL_OFF = 64;   //断开油电
    public static int COMMAND_POWER_CONTROL_ON = 65;   //恢复油电
    public static int COMMAND_LOCK_OFF = 100;   //关锁
    public static int COMMAND_LOCK_ON = 101;   //开锁
    public static int COMMAND_ACC_OFF = 116;   //关闭车辆
    public static int COMMAND_ACC_ON = 117;   //启动车辆
    public static int COMMAND_LOST_ALARM = 145;   //丢车报警
    public static String USER_ROLE_NORMAL = "NORMAL";   //普通用户
    public static String USER_ROLE_MANUFACTURER = "MANUFACTURER";   //车辆生产厂家
    public static String USER_ROLE_GPS_DEVICE_MANAGER = "GPS_DEVICE_MANAGER";   //GPS设备管理员

    /**
     * 告警通知类型
     */
    public static String NOTICE_TYPE_MOVE_ALARM = "MOVE_ALARM"; //震动告警
    public static String NOTICE_TYPE_EXTERNAL_POWER_ALARM = "EXTERNAL_POWER_ALARM"; //电瓶被拆告警
    public static String NOTICE_TYPE_ELECTRONIC_FENCE_ALARM = "ELECTRONIC_FENCE_ALARM"; //电子围栏告警
    public static String NOTICE_TYPE_LOW_VOLTAGE_ALARM = "LOW_VOLTAGE_ALARM"; //低电量告警
    public static String NOTICE_TYPE_HIGH_VOLTAGE_ALARM = "HIGH_VOLTAGE_ALARM"; //高电压告警

    /**
     * 车型常量
     */
    public static final int VEHICLE_MODEL_WENQUXING = 184; //文曲星车型
    public static final int VEHICLE_MODEL_G7NOMI = 177; //文曲星车型
    public static final int VEHICLE_MODEL_E01 = 176; //E01
    public static final int VEHICLE_MODEL_T30 = 183; //T30
    public static final int VEHICLE_MODEL_QingTing = 141; //蜻蜓

}
