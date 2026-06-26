package com.shenghao.blesdk;

/**
 * 蓝牙SDK常量定义
 * 包含UUID定义、命令前缀、错误码等
 */
public class BleConstant {

    // ==================== 蓝牙服务UUID ====================

    /** 蓝牙服务UUID (旧版) */
    public final static String SERVICE_UUID = "970fafad-f144-357f-7888-d5754aa78400";

    /** 写入特征UUID (旧版) */
    public final static String WRITE_UUID = "970fafad-f144-357f-7888-d5754aa78401";

    /** 通知特征UUID (旧版) */
    public final static String NOTIFY_UUID = "970fafad-f144-357f-7888-d5754aa78401";

    /** 设备UID */
    public static final String UID = "9527";

    /** HID设备UUID */
    public static final String HID_DEVICE_UUID = "00001812-0000-1000-8000-00805f9b34fb";

    /** 蓝牙服务UUID (SH方案) */
    public final static String SERVICE_UUID_SH = "0000FFE0-0000-1000-8000-00805F9B34FB";

    /** 写入特征UUID (SH方案) */
    public final static String WRITE_UUID_SH = "0000FFE2-0000-1000-8000-00805F9B34FB";

    /** 通知特征UUID (SH方案) */
    public final static String NOTIFY_UUID_SH = "0000FFE1-0000-1000-8000-00805F9B34FB";

    /** 设备UID (SH方案) */
    public static final String UID_SH = "FFFF";

    // ==================== RSSI限值命令 ====================

    public static String LIMIT_STR_DISCONNECT = "AT+LRSSI=";
    public static String LIMIT_STR_CONNECT = "AT+ULRSSI=";
    public static String LIMIT_STR_DISCONNECT_CHECK = "AT+LRSSI?";
    public static String LIMIT_STR_CONNECT_CHECK = "AT+ULRSSI?";
    public static String LIMIT_STR_RSSI = "AT+RSSI?";

    // ==================== 协议命令码 ====================

    /** 蓝牙密码 - 根密码修改 */
    public static final byte CMD_MODIFY_ROOT_PASSWORD = 0x30;

    /** 获取蓝牙通迅密钥 */
    public static final byte CMD_GET_BT_KEY = 0x31;

    /** 主机参数查询 */
    public static final byte CMD_HOST_PARAM_QUERY = 0x10;

    /** 主机参数设置 */
    public static final byte CMD_HOST_PARAM_SET = 0x11;

    /** 车辆控制 */
    public static final byte CMD_VEHICLE_CONTROL = 0x12;

    /** 车辆状态查询 */
    public static final byte CMD_VEHICLE_STATUS_QUERY = 0x13;

    /** 车辆控制2 */
    public static final byte CMD_VEHICLE_CONTROL_2 = 0x14;

    /** 文本下发 */
    public static final byte CMD_TEXT_SEND = 0x15;

    /** 透传 */
    public static final byte CMD_PASSTHROUGH = 0x16;

    /** 学习遥控器 */
    public static final byte CMD_LEARN_REMOTE = 0x17;

    /** 蓝牙PKE功能设置 */
    public static final byte CMD_PKE_SET = 0x18;

    /** 扩展参数设置 */
    public static final byte CMD_EXT_PARAM_SET = 0x19;

    /** 自动驾驶/挪车控制 */
    public static final byte CMD_AUTO_PARKING = 0x20;

    /** 查询IMEI */
    public static final byte CMD_QUERY_IMEI = 0x21;

    /** 车辆状态周期更新 */
    public static final byte CMD_VEHICLE_STATUS_PERIODIC = 0x69;

    /** NFC_CARD主动上报 */
    public static final byte CMD_NFC_REPORT = 0x70;

    /** 下发更新包信息 */
    public static final byte CMD_UPDATE_PACKET_INFO = 0x71;

    /** 下发更新包 */
    public static final byte CMD_UPDATE_PACKET = 0x72;

    /** 挪车反馈周期更新 */
    public static final byte CMD_PARKING_FEEDBACK = (byte) 0x7A;

    /** 清除PKE保存信息 */
    public static final byte CMD_CLEAR_PKE_INFO = (byte) 0x80;

    /** 下发灯语命令 */
    public static final byte CMD_LIGHT_EFFECT = (byte) 0x91;

    // ==================== 加密标志 ====================

    /** 明文传输 */
    public static final byte FLAG_PLAIN = 0x00;

    /** 更改根密钥明文 */
    public static final byte FLAG_MODIFY_ROOT_KEY = 0x02;

    /** 蓝牙根密钥加密 */
    public static final byte FLAG_BT_ROOT_KEY = 0x11;

    /** 蓝牙交换密钥加密 */
    public static final byte FLAG_BT_EXCHANGE_KEY = 0x12;

    /** 主机升级密钥加密 */
    public static final byte FLAG_HOST_UPGRADE_KEY = 0x21;

    // ==================== 错误码定义 (协议文档第8.5节) ====================

    /** 成功 */
    public static final byte ERR_SUCCESS = 0x00;

    /** 失败 */
    public static final byte ERR_FAILED = (byte) 0xFF;

    /** 功能不支持 */
    public static final byte ERR_NOT_SUPPORTED = 0x01;

    /** 解密失败 */
    public static final byte ERR_DECRYPT_FAILED = 0x02;

    /** 主机应答超时 */
    public static final byte ERR_HOST_TIMEOUT = 0x05;

    /** 车辆已在运行中 */
    public static final byte ERR_VEHICLE_RUNNING = 0x06;

    /** 车辆已在熄火中 */
    public static final byte ERR_VEHICLE_SHUTDOWN = 0x07;

    /** 车辆倒档 */
    public static final byte ERR_VEHICLE_REVERSE = 0x08;

    /** 车辆非空档 */
    public static final byte ERR_VEHICLE_NOT_NEUTRAL = 0x09;

    /** 已经是最新版本 */
    public static final byte ERR_ALREADY_LATEST = 0x10;

    /** 原车钥匙已接管 */
    public static final byte ERR_ORIGINAL_KEY_TAKEN = 0x11;

    /** 车门未关好 */
    public static final byte ERR_DOOR_NOT_CLOSED = 0x12;

    /** 引擎盖未关好 */
    public static final byte ERR_HOOD_NOT_CLOSED = 0x13;

    /** 尾箱未关好 */
    public static final byte ERR_TRUNK_NOT_CLOSED = 0x14;

    /** 车门、引擎盖未关好 */
    public static final byte ERR_DOOR_HOOD_NOT_CLOSED = 0x15;

    /** 车门、尾箱未关好 */
    public static final byte ERR_DOOR_TRUNK_NOT_CLOSED = 0x16;

    /** 引擎盖、尾箱未关好 */
    public static final byte ERR_HOOD_TRUNK_NOT_CLOSED = 0x17;

    /** 车门、引擎盖、尾箱未关好 */
    public static final byte ERR_DOOR_HOOD_TRUNK_NOT_CLOSED = 0x18;

    /** 当前车辆处于解锁状态,请先上锁 */
    public static final byte ERR_VEHICLE_UNLOCKED = 0x19;

    /** 原车钥匙已经启动车辆 */
    public static final byte ERR_ORIGINAL_KEY_STARTED = 0x20;

    /** 功能未开放 */
    public static final byte ERR_NOT_ENABLED = 0x21;

    /** 尾箱已开启 */
    public static final byte ERR_TRUNK_ALREADY_OPEN = 0x22;

    /** 车辆启动中 */
    public static final byte ERR_VEHICLE_STARTING = 0x23;

    /** 尾箱已关闭 */
    public static final byte ERR_TRUNK_ALREADY_CLOSED = 0x24;

    /** 和校验错误 */
    public static final byte ERR_CHECKSUM = 0x25;

    /** 操作过快 */
    public static final byte ERR_OPERATION_TOO_FAST = 0x26;

    /** 密码全是FF */
    public static final byte ERR_PASSWORD_ALL_FF = 0x27;

    /** 密码不正确 */
    public static final byte ERR_PASSWORD_INCORRECT = 0x28;

    // ==================== 错误码描述 ====================

    /**
     * 获取错误码描述
     * @param errorCode 错误码
     * @return 错误描述
     */
    public static String getErrorDescription(byte errorCode) {
        switch (errorCode) {
            case ERR_SUCCESS:
                return "成功";
            case ERR_FAILED:
                return "失败";
            case ERR_NOT_SUPPORTED:
                return "功能不支持";
            case ERR_DECRYPT_FAILED:
                return "解密失败";
            case ERR_HOST_TIMEOUT:
                return "主机应答超时";
            case ERR_VEHICLE_RUNNING:
                return "车辆已在运行中";
            case ERR_VEHICLE_SHUTDOWN:
                return "车辆已在熄火中";
            case ERR_VEHICLE_REVERSE:
                return "车辆倒档";
            case ERR_VEHICLE_NOT_NEUTRAL:
                return "车辆非空档";
            case ERR_ALREADY_LATEST:
                return "已经是最新版本";
            case ERR_ORIGINAL_KEY_TAKEN:
                return "原车钥匙已接管";
            case ERR_DOOR_NOT_CLOSED:
                return "车门未关好";
            case ERR_HOOD_NOT_CLOSED:
                return "引擎盖未关好";
            case ERR_TRUNK_NOT_CLOSED:
                return "尾箱未关好";
            case ERR_DOOR_HOOD_NOT_CLOSED:
                return "车门、引擎盖未关好";
            case ERR_DOOR_TRUNK_NOT_CLOSED:
                return "车门、尾箱未关好";
            case ERR_HOOD_TRUNK_NOT_CLOSED:
                return "引擎盖、尾箱未关好";
            case ERR_DOOR_HOOD_TRUNK_NOT_CLOSED:
                return "车门、引擎盖、尾箱未关好";
            case ERR_VEHICLE_UNLOCKED:
                return "当前车辆处于解锁状态,请先上锁";
            case ERR_ORIGINAL_KEY_STARTED:
                return "原车钥匙已经启动车辆";
            case ERR_NOT_ENABLED:
                return "功能未开放";
            case ERR_TRUNK_ALREADY_OPEN:
                return "尾箱已开启";
            case ERR_VEHICLE_STARTING:
                return "车辆启动中";
            case ERR_TRUNK_ALREADY_CLOSED:
                return "尾箱已关闭";
            case ERR_CHECKSUM:
                return "和校验错误";
            case ERR_OPERATION_TOO_FAST:
                return "操作过快";
            case ERR_PASSWORD_ALL_FF:
                return "密码全是FF";
            case ERR_PASSWORD_INCORRECT:
                return "密码不正确";
            default:
                return "未知错误: 0x" + String.format("%02X", errorCode);
        }
    }

    /**
     * 判断是否为成功响应
     * @param errorCode 错误码
     * @return 是否成功
     */
    public static boolean isSuccess(byte errorCode) {
        return errorCode == ERR_SUCCESS;
    }

    // ==================== 挪车控制位定义 ====================

    /** 前进 */
    public static final byte PARKING_FORWARD = 0x01;

    /** 后退 */
    public static final byte PARKING_BACKWARD = 0x02;

    /** 角度调整 */
    public static final byte PARKING_ANGLE_ADJUST = 0x04;

    /** 停止前进后退（刹车） */
    public static final byte PARKING_STOP = 0x08;

    /** 挪车请求进入 */
    public static final byte PARKING_ENTER = 0x10;

    /** 挪车请求退出 */
    public static final byte PARKING_EXIT = 0x00;

    // ==================== 车辆控制位定义 ====================

    /** 控制位1 - 开锁 */
    public static final byte CTRL_UNLOCK = 0x01;
    /** 控制位1 - 启动 */
    public static final byte CTRL_START = 0x02;
    /** 控制位1 - 降窗 */
    public static final byte CTRL_WINDOW_DOWN = 0x04;
    /** 控制位1 - 开尾箱 */
    public static final byte CTRL_TRUNK_OPEN = 0x08;
    /** 控制位1 - 通油电 */
    public static final byte CTRL_POWER_ON = 0x10;
    /** 控制位1 - 修车模式 */
    public static final byte CTRL_REPAIR_MODE = 0x20;
    /** 控制位1 - 禁用遥控器 */
    public static final byte CTRL_REMOTE_DISABLE = 0x40;
    /** 控制位1 - 静音撤防 */
    public static final byte CTRL_SILENT_DISARM = (byte) 0x80;

    /** 控制位2 - 关锁 */
    public static final byte CTRL_LOCK = 0x01;
    /** 控制位2 - 熄火 */
    public static final byte CTRL_SHUTDOWN = 0x02;
    /** 控制位2 - 升窗 */
    public static final byte CTRL_WINDOW_UP = 0x04;
    /** 控制位2 - 关尾箱 */
    public static final byte CTRL_TRUNK_CLOSE = 0x08;
    /** 控制位2 - 断油电 */
    public static final byte CTRL_POWER_OFF = 0x10;
    /** 控制位2 - 关闭修车 */
    public static final byte CTRL_REPAIR_MODE_OFF = 0x20;
    /** 控制位2 - 启用遥控器 */
    public static final byte CTRL_REMOTE_ENABLE = 0x40;
    /** 控制位2 - 静音设防 */
    public static final byte CTRL_SILENT_ARM = (byte) 0x80;

    /** 控制位3 - 寻车 */
    public static final byte CTRL_FIND_CAR = 0x01;
    /** 控制位3 - 转向灯 */
    public static final byte CTRL_TURN_SIGNAL = 0x02;
    /** 控制位3 - 喇叭 */
    public static final byte CTRL_HORN = 0x04;
    /** 控制位3 - 报警 */
    public static final byte CTRL_ALARM = 0x08;
    /** 控制位3 - 深度休眠 */
    public static final byte CTRL_DEEP_SLEEP = 0x10;
    /** 控制位3 - 强制熄火 */
    public static final byte CTRL_FORCE_SHUTDOWN = 0x20;
    /** 控制位3 - 开天窗 */
    public static final byte CTRL_SUNROOF_OPEN = 0x40;
    /** 控制位3 - 关天窗 */
    public static final byte CTRL_SUNROOF_CLOSE = (byte) 0x80;

    // ==================== 空调控制位定义 ====================

    /** 空调(1) - 空调开 */
    public static final byte AC_ON = 0x01;
    /** 空调(1) - 压缩机开 */
    public static final byte AC_COMPRESSOR_ON = 0x02;
    /** 空调(1) - 内循环开/外循环关 */
    public static final byte AC_INTERNAL_CYCLE = 0x04;
    /** 空调(1) - 温度+0.5度 */
    public static final byte AC_TEMP_UP = 0x08;
    /** 空调(1) - 风速调+1 */
    public static final byte AC_FAN_SPEED_UP = 0x10;
    /** 空调(1) - 开风机 */
    public static final byte AC_FAN_ON = 0x20;
    /** 空调(1) - 空调制热 */
    public static final byte AC_HEAT = 0x40;

    /** 空调(2) - 空调关 */
    public static final byte AC_OFF = 0x01;
    /** 空调(2) - 压缩机关 */
    public static final byte AC_COMPRESSOR_OFF = 0x02;
    /** 空调(2) - 内循环关/外循环开 */
    public static final byte AC_EXTERNAL_CYCLE = 0x04;
    /** 空调(2) - 温度-0.5度 */
    public static final byte AC_TEMP_DOWN = 0x08;
    /** 空调(2) - 风速调-1 */
    public static final byte AC_FAN_SPEED_DOWN = 0x10;
    /** 空调(2) - 关风机 */
    public static final byte AC_FAN_OFF = 0x20;
    /** 空调(2) - 空调制冷 */
    public static final byte AC_COOL = 0x40;

    // ==================== DTC控制位定义 ====================

    /** 清除故障码 */
    public static final byte DTC_CLEAR = 0x01;
    /** 扫描故障码 */
    public static final byte DTC_SCAN = 0x02;

    // ==================== 默认密钥 ====================

    /** 默认通讯密钥 (全F) */
    public static final String DEFAULT_KEY = "ffffffffffffffffffffffffffffffff";

    /** 默认根密钥 */
    public static final String DEFAULT_ROOT_KEY = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
}
