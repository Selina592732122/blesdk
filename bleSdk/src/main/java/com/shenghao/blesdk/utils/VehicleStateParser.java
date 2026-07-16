package com.shenghao.blesdk.utils;

import com.shenghao.blesdk.command.AESUtils;
import com.shenghao.blesdk.entity.VehicleState;

import java.util.HashSet;
import java.util.Set;

/**
 * 车辆状态解析器
 * 支持解析 0x69 周期更新和 0x7A 挪车反馈数据
 */
public class VehicleStateParser {

    private static final String TAG = "VehicleStateParser";

    // 报文头前缀
    private static final String VEHICLE_STATE_PREFIX_ENCRYPTED = "ff1212";  // 加密包
    private static final String VEHICLE_STATE_PREFIX_69 = "ff120069";       // 0x69 明文周期更新
    private static final String VEHICLE_STATE_PREFIX_7A = "ff12007a";       // 0x7A 明文挪车反馈
    private static final String DEFAULT_KEY = "ffffffffffffffffffffffffffffffff";

    // 协议命令码
    private static final byte CMD_VEHICLE_STATUS_UPDATE = 0x69;  // 周期更新
    private static final byte CMD_PARKING_FEEDBACK = 0x7A;      // 挪车反馈

    /**
     * 解析蓝牙接收的原始数据
     *
     * @param data 原始字节数组
     * @return VehicleState 解析后的车辆状态，如果解析失败返回null
     */
    public static VehicleState parse(byte[] data) {
        if (data == null || data.length < 7) {
            LogUtils.e(TAG, "数据长度不足，最少需要7字节");
            return null;
        }

        String hex = ByteUtils.bytes2HexStr(data);
        LogUtils.e(TAG, "收到数据: " + hex);

        // 检查报文头
        if (!hex.startsWith("ff12")) {
            LogUtils.e(TAG, "无效的报文头");
            return null;
        }

        // 获取命令码（解密后的第3个字节）
        byte commandCode = data[3];

        try {
            if (commandCode == CMD_PARKING_FEEDBACK) {
                // 解析 0x7A 挪车反馈
                return parseParkingFeedback(data, hex);
            } else if (commandCode == CMD_VEHICLE_STATUS_UPDATE) {
                // 解析 0x69 周期更新
                return parseVehicleStatusUpdate(data, hex);
            } else {
                LogUtils.e(TAG, "未知命令码: 0x" + String.format("%02X", commandCode));
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "解析异常: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解析 0x7A 挪车反馈数据
     *
     * 协议结构:
     * FF 12 00 7A [流水号] [反馈数据] [失效原因1] [失效原因2] [保留] [SUM]
     * - 反馈数据: BIT0=有效/无效
     * - 失效原因1: BIT0=车门开启, BIT1=车辆非静止, BIT2=车辆故障, BIT3=坡道驻停,
     *              BIT4=无指令异常位移, BIT5=人为控车, BIT6=蓝牙信号异常, BIT7=控制器通讯异常
     * - 失效原因2: BIT0=用户手动退出, BIT1=节点通讯异常, BIT2=超时退出, BIT3=挪车车速异常
     */
    private static VehicleState parseParkingFeedback(byte[] data, String hex) {
        LogUtils.e(TAG, "解析0x7A挪车反馈数据");

        VehicleState state = new VehicleState();
        state.setParkingFeedbackEnabled(true);

        // 解密数据（如果需要）
        byte[] decrypted;
        if (data[2] != 0x00) {
            // 加密模式，使用默认密钥解密
            try {
                byte[] encryptedData = subarray(data, 3, 16);
                decrypted = AESUtils.aes128Decrypt(encryptedData, ByteUtils.hexStr2Bytes(DEFAULT_KEY));
            } catch (Exception e) {
                LogUtils.e(TAG, "解密失败: " + e.getMessage());
                return state;
            }
        } else {
            // 明文模式
            decrypted = subarray(data, 3, 16);
        }

        if (decrypted == null || decrypted.length < 4) {
            LogUtils.e(TAG, "解密后数据长度不足");
            return state;
        }

        // decrypted[0] = 0x7A (命令码)
        // decrypted[1] = 流水号
        // decrypted[2] = 挪车反馈数据
        // decrypted[3] = 失效原因1
        // decrypted[4] = 失效原因2

        byte feedbackData = decrypted.length > 2 ? decrypted[2] : 0;
        byte failureReason1 = decrypted.length > 3 ? decrypted[3] : 0;
        byte failureReason2 = decrypted.length > 4 ? decrypted[4] : 0;

        // 解析反馈状态
        boolean isValid = (feedbackData & 0x01) != 0;
        state.setParkingValid(isValid);

        LogUtils.e(TAG, "挪车反馈: " + (isValid ? "有效" : "无效"));

        // 解析失效原因
        Set<VehicleState.ParkingFailureReason> reasons = new HashSet<>();

        // 失效原因1 (0-7位)
        if ((failureReason1 & 0x01) != 0) {
            reasons.add(VehicleState.ParkingFailureReason.DOOR_OPEN);
        }
        if ((failureReason1 & 0x02) != 0) {
            reasons.add(VehicleState.ParkingFailureReason.VEHICLE_NOT_STATIONARY);
        }
        if ((failureReason1 & 0x04) != 0) {
            reasons.add(VehicleState.ParkingFailureReason.VEHICLE_FAULT);
        }
        if ((failureReason1 & 0x08) != 0) {
            reasons.add(VehicleState.ParkingFailureReason.PARKED_ON_SLOPE);
        }
        if ((failureReason1 & 0x10) != 0) {
            reasons.add(VehicleState.ParkingFailureReason.ABNORMAL_DISPLACEMENT);
        }
        if ((failureReason1 & 0x20) != 0) {
            reasons.add(VehicleState.ParkingFailureReason.MANUAL_CONTROL);
        }
        if ((failureReason1 & 0x40) != 0) {
            reasons.add(VehicleState.ParkingFailureReason.BLUETOOTH_SIGNAL_ABNORMAL);
        }
        if ((failureReason1 & 0x80) != 0) {
            reasons.add(VehicleState.ParkingFailureReason.CONTROLLER_COMM_ERROR);
        }

        // 失效原因2 (0-3位)
        if ((failureReason2 & 0x01) != 0) {
            reasons.add(VehicleState.ParkingFailureReason.USER_MANUAL_EXIT);
        }
        if ((failureReason2 & 0x02) != 0) {
            reasons.add(VehicleState.ParkingFailureReason.NODE_COMM_ERROR);
        }
        if ((failureReason2 & 0x04) != 0) {
            reasons.add(VehicleState.ParkingFailureReason.TIMEOUT_EXIT);
        }
        if ((failureReason2 & 0x08) != 0) {
            reasons.add(VehicleState.ParkingFailureReason.PARKING_SPEED_ABNORMAL);
        }

        state.setParkingFailureReasons(reasons);

        // 打印失效原因
        if (!reasons.isEmpty()) {
            LogUtils.e(TAG, "失效原因: " + state.getParkingFailureDescription());
        }

        return state;
    }

    /**
     * 解析 0x69 车辆状态周期更新数据
     *
     * 协议结构:
     * FF 12 [Flag] 69 [流水号] [数据位图1] [主机数据] [GPS数据] [OBD数据] ... [SUM]
     *
     * 数据位图1:
     * - BIT0: 空调数据
     * - BIT1: 主机数据
     * - BIT2: GPS数据
     * - BIT3: OBD数据
     * - BIT4: 胎压数据
     * - BIT5: OBD故障码数据
     * - BIT6: 基站数据
     * - BIT7: 版本上报 (为0时=BIT0=PKE信息)
     */
    private static VehicleState parseVehicleStatusUpdate(byte[] data, String hex) {
        LogUtils.e(TAG, "解析0x69周期更新数据");

        VehicleState state = new VehicleState();

        // 解密数据（如果需要）
        byte[] decrypted;
        if (data[2] != 0x00) {
            // 加密模式
            try {
                byte[] encryptedData = subarray(data, 3, 16);
                decrypted = AESUtils.aes128Decrypt(encryptedData, ByteUtils.hexStr2Bytes(DEFAULT_KEY));
            } catch (Exception e) {
                LogUtils.e(TAG, "解密失败: " + e.getMessage());
                return null;
            }
        } else {
            // 明文模式
            decrypted = subarray(data, 3, 16);
        }

        if (decrypted == null || decrypted.length < 2) {
            LogUtils.e(TAG, "解密后数据长度不足");
            return null;
        }

        // decrypted[0] = 0x69 (命令码)
        // decrypted[1] = 流水号
        // decrypted[2] = 数据位图1
        int pos = 2;

        // 检查是否有数据
        if (decrypted.length <= pos) {
            return null;
        }

        byte dataBitmap = decrypted[pos];
        pos++;

        LogUtils.e(TAG, "数据位图: 0x" + String.format("%02X", dataBitmap));

        // 检查是否版本上报 (BIT7=1) 或 PKE信息 (BIT7=0)
        boolean isVersionReport = (dataBitmap & 0x80) != 0;

        if (isVersionReport) {
            // 版本上报数据 - 暂不解析
            LogUtils.e(TAG, "版本上报数据，暂不解析");
        } else {
            // PKE信息数据
            // decrypted[2] = PKE开关状态 (当BIT7=0时)
            byte pkeInfo = dataBitmap;
            state.setPkeEnabled((pkeInfo & 0x01) != 0);
            state.setPkeConnected((pkeInfo & 0x02) != 0);

            LogUtils.e(TAG, "PKE开关: " + state.isPkeEnabled() + ", PKE连接: " + state.isPkeConnected());

            // 继续解析PKE周期数据 (5字节)
            // [0]=开锁DB, [1]=关锁DB, [2]=当前终端DB, [3]=PKE延时, [4]=遥控通电距离, [5]=NFC状态
            if (decrypted.length > pos + 5) {
                state.setUnlockRssi(decrypted[pos] & 0xFF);
                state.setLockRssi(decrypted[pos + 1] & 0xFF);
                state.setCurrentRssi(decrypted[pos + 2] & 0xFF);
                pos += 6;
            }
        }

        // 主机数据 (当BIT1=1时有数据)
        if ((dataBitmap & 0x02) != 0 && decrypted.length > pos + 5) {
            parseHostData(state, decrypted, pos);
            pos += 6;
        }

        // GPS数据 (当BIT2=1时有数据)
        if ((dataBitmap & 0x04) != 0 && decrypted.length > pos + 8) {
            parseGpsData(state, decrypted, pos);
            pos += 9;
        }

        // OBD数据 (当BIT3=1时有数据)
        if ((dataBitmap & 0x08) != 0 && decrypted.length > pos + 3) {
            parseObdData(state, decrypted, pos);
            pos += 4;
        }

        return state;
    }

    /**
     * 解析主机周期更新数据(V1)
     * 状态位1: 1字节 - 车门、尾箱、引擎盖、大灯、小灯
     * 状态位2: 1字节 - 车窗、天窗、转向灯
     * 状态位3: 1字节 - 手刹、刹车、空调、ACC、引擎、中控锁、修车模式、通油电
     * 状态位4: 1字节 - 报警相关
     * 状态位5: 1字节 - GPS状态、信号强度
     * 状态位6: 1字节 - 行驶状态
     */
    private static void parseHostData(VehicleState state, byte[] data, int offset) {
        if (data.length < offset + 6) return;

        // 状态位1
        byte status1 = data[offset];
        state.setRearRightDoor((status1 & 0x01) != 0);
        state.setRearLeftDoor((status1 & 0x02) != 0);
        state.setFrontRightDoor((status1 & 0x04) != 0);
        state.setFrontLeftDoor((status1 & 0x08) != 0);
        state.setTrunk((status1 & 0x10) != 0);
        state.setHood((status1 & 0x20) != 0);
        state.setHeadlight((status1 & 0x40) != 0);
        state.setSmallLight((status1 & 0x80) != 0);

        // 状态位2
        byte status2 = data[offset + 1];
        state.setRearRightWindow((status2 & 0x01) != 0);
        state.setRearLeftWindow((status2 & 0x02) != 0);
        state.setFrontRightWindow((status2 & 0x04) != 0);
        state.setFrontLeftWindow((status2 & 0x08) != 0);
        state.setSunroof((status2 & 0x10) != 0);
        state.setLeftTurnSignal((status2 & 0x40) != 0);
        state.setRightTurnSignal((status2 & 0x80) != 0);

        // 状态位3
        byte status3 = data[offset + 2];
        state.setHandbrake((status3 & 0x01) != 0);
        state.setBrakePedal((status3 & 0x02) != 0);
        state.setAirConditioner((status3 & 0x04) != 0);
        state.setAccOn((status3 & 0x08) != 0);
        state.setEngineOn((status3 & 0x10) != 0);
        state.setCentralLock((status3 & 0x20) != 0);
        state.setRepairMode((status3 & 0x40) != 0);
        state.setPowerOn((status3 & 0x80) != 0);

        // 状态位4
        byte status4 = data[offset + 3];
        state.setLockedNotClosed((status4 & 0x01) != 0);
        state.setVibrationAlarm((status4 & 0x04) != 0);
        state.setIllegalDoor((status4 & 0x08) != 0);
        state.setIllegalIgnition((status4 & 0x10) != 0);
        state.setPowerCutAlarm((status4 & 0x40) != 0);
        state.setTirePressureAlarm((status4 & 0x80) != 0);

        // 状态位5
        byte status5 = data[offset + 4];
        state.setGpsPower((status5 & 0x01) != 0);
        state.setGpsPositioned((status5 & 0x02) != 0);
        state.setGpsAntennaOpen((status5 & 0x04) != 0);
        state.setGpsAntennaShort((status5 & 0x08) != 0);
        int gpsSignal = (status5 >> 4) & 0x03;
        int gsmSignal = (status5 >> 6) & 0x03;
        state.setGpsSignal(gpsSignal);
        state.setGsmSignal(gsmSignal);

        // 状态位6
        byte status6 = data[offset + 5];
        state.setMovingForward((status6 & 0x02) != 0);
        state.setMovingBackward((status6 & 0x04) != 0);
        state.setStopped((status6 & 0x08) != 0);
    }

    /**
     * 解析GPS数据
     * 状态位5: GPS电源、定位、天线状态、信号
     * 电压、温度、车速、油量
     */
    private static void parseGpsData(VehicleState state, byte[] data, int offset) {
        if (data.length < offset + 9) return;

        // GPS状态在主机数据中已解析
        // 这里解析电压、温度、车速、油量

        byte voltage = data[offset];
        byte temperature = data[offset + 1];
        byte speed = data[offset + 2];
        byte fuel = data[offset + 3];

        // 0xFF 表示无效数据
        state.setVoltage(voltage == (byte) 0xFF ? 0 : (voltage & 0xFF));
        state.setTemperature(temperature == (byte) 0x7F ? 0 : (temperature & 0xFF));
        state.setSpeed(speed == (byte) 0xFF ? 0 : (speed & 0xFF));
        state.setFuel(fuel == (byte) 0xFF ? 0 : (fuel & 0xFF));

        LogUtils.e(TAG, "GPS数据 - 电压:" + state.getVoltage() + " 温度:" + state.getTemperature() +
                " 车速:" + state.getSpeed() + " 油量:" + state.getFuel());
    }

    /**
     * 解析OBD数据
     * 电压、温度 (0.1V, 0.1度)
     */
    private static void parseObdData(VehicleState state, byte[] data, int offset) {
        if (data.length < offset + 4) return;

        // 如果需要更详细的OBD数据可以在这里扩展
        // 目前协议中OBD数据与GPS数据有重叠
    }

    /**
     * 数组截取辅助方法
     */
    private static byte[] subarray(byte[] data, int startIndex, int length) {
        if (data == null || startIndex < 0 || length < 0 || startIndex + length > data.length) {
            return new byte[0];
        }
        byte[] result = new byte[length];
        System.arraycopy(data, startIndex, result, 0, length);
        return result;
    }

    /**
     * 判断是否为挪车反馈数据
     */
    public static boolean isParkingFeedback(byte[] data) {
        if (data == null || data.length < 4) {
            return false;
        }
        return data[3] == CMD_PARKING_FEEDBACK;
    }

    /**
     * 判断是否为车辆状态周期更新数据
     */
    public static boolean isVehicleStatusUpdate(byte[] data) {
        if (data == null || data.length < 4) {
            return false;
        }
        return data[3] == CMD_VEHICLE_STATUS_UPDATE;
    }
}
