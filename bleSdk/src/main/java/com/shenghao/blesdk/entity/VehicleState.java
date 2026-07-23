package com.shenghao.blesdk.entity;

import java.util.HashSet;
import java.util.Set;

/**
 * 车辆状态实体类
 * 包含PKE状态、周期上传数据(0x69)和挪车反馈数据(0x7A)
 */
public class VehicleState {

    // ==================== PKE状态字段 ====================
    private boolean pkeEnabled;
    private boolean pkeConnected;
    private int currentRssi;
    private int unlockRssi;
    private int lockRssi;

    // ==================== 周期上传数据(0x69)字段 ====================
    // 状态位1
    private boolean rearRightDoor;      // 右后门开关
    private boolean rearLeftDoor;       // 左后门开关
    private boolean frontRightDoor;     // 右前门开关
    private boolean frontLeftDoor;      // 左前门开关
    private boolean trunk;              // 尾箱开关
    private boolean hood;               // 引擎盖开关
    private boolean headlight;          // 大灯(近光灯)开关
    private boolean smallLight;         // 小灯(示廓灯)开关

    // 状态位2
    private boolean rearRightWindow;    // 右后窗开关
    private boolean rearLeftWindow;     // 左后窗开关
    private boolean frontRightWindow;   // 右前窗开关
    private boolean frontLeftWindow;    // 左前窗开关
    private boolean sunroof;            // 天窗开关
    private boolean leftTurnSignal;     // 左转向灯开关
    private boolean rightTurnSignal;    // 右转向灯开关

    // 状态位3
    private boolean handbrake;          // 手刹信号已拉
    private boolean brakePedal;         // 刹车信号踩下
    private boolean airConditioner;     // 空调打开关闭
    private boolean accOn;              // ACC ON/OFF
    private boolean engineOn;           // ENG ON/OFF
    private boolean centralLock;        // 中控锁打开关闭
    private boolean repairMode;         // 修车模式打开关闭
    private boolean powerOn;           // 通油电/断油电

    // 状态位4
    private boolean lockedNotClosed;    // 上锁未关门报警
    private boolean vibrationAlarm;     // 振动报警
    private boolean illegalDoor;        // 非法开门报警
    private boolean illegalIgnition;    // 非法点火报警
    private boolean powerCutAlarm;     // 断电报警
    private boolean tirePressureAlarm;  // 胎压报警

    // 状态位5
    private boolean gpsPower;           // GPS电源
    private boolean gpsPositioned;     // GPS定位标志
    private boolean gpsAntennaOpen;    // GPS天线开路
    private boolean gpsAntennaShort;   // GPS天线短路
    private int gpsSignal;              // GPS信号强度 (0-3)
    private int gsmSignal;              // GSM信号强度 (0-3)

    // 状态位6
    private boolean movingForward;      // 前进中
    private boolean movingBackward;     // 后退中
    private boolean stopped;            // 停止中

    // 其他数据
    private int voltage;               // 电压 (0.1V, 0xFF为无效)
    private int temperature;            // 温度 (-127无效数据)
    private int speed;                  // 车速 (0xFF无效)
    private int fuel;                  // 剩余油量 (0xFF无效)

    // ==================== 挪车反馈数据(0x7A)字段 ====================
    private boolean parkingFeedbackEnabled;  // 挪车请求反馈有效
    private ParkingStatus parkingStatus;     // 挪车状态（三态）
    private Set<ParkingFailureReason> parkingFailureReasons; // 挪车失效原因集合

    /**
     * 挪车状态枚举
     */
    public enum ParkingStatus {
        NOT_ENTERED(0x00, "未进入挪车状态"),
        VALID(0x01, "进入挪车反馈有效"),
        INVALID(0x02, "进入挪车反馈无效");

        private final int value;
        private final String description;

        ParkingStatus(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static ParkingStatus fromValue(int value) {
            for (ParkingStatus status : values()) {
                if (status.value == value) {
                    return status;
                }
            }
            return NOT_ENTERED;
        }
    }

    /**
     * 挪车失效原因枚举
     */
    public enum ParkingFailureReason {
        DOOR_OPEN(0, "车门开启"),
        VEHICLE_NOT_STATIONARY(1, "车辆非静止"),
        VEHICLE_FAULT(2, "车辆故障"),
        PARKED_ON_SLOPE(3, "坡道驻停中"),
        BRAKE_SWITCH_VALID(4, "刹车开关有效"),
        ABNORMAL_DISPLACEMENT(5, "无指令异常位移"),
        MANUAL_CONTROL(6, "人为控车"),
        BLUETOOTH_SIGNAL_ABNORMAL(7, "蓝牙信号异常"),
        CONTROLLER_COMM_ERROR(8, "控制器通讯异常"),
        USER_MANUAL_EXIT(9, "用户手动退出"),
        NODE_COMM_ERROR(10, "节点通讯异常"),
        TIMEOUT_EXIT(11, "超时退出"),
        PARKING_SPEED_ABNORMAL(12, "挪车车速异常");

        private final int bit;
        private final String description;

        ParkingFailureReason(int bit, String description) {
            this.bit = bit;
            this.description = description;
        }

        public int getBit() {
            return bit;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==================== 构造函数 ====================

    public VehicleState() {
        parkingFailureReasons = new HashSet<>();
    }

    // ==================== PKE状态 Getter/Setter ====================

    public boolean isPkeEnabled() {
        return pkeEnabled;
    }

    public void setPkeEnabled(boolean pkeEnabled) {
        this.pkeEnabled = pkeEnabled;
    }

    public boolean isPkeConnected() {
        return pkeConnected;
    }

    public void setPkeConnected(boolean pkeConnected) {
        this.pkeConnected = pkeConnected;
    }

    public int getCurrentRssi() {
        return currentRssi;
    }

    public void setCurrentRssi(int currentRssi) {
        this.currentRssi = currentRssi;
    }

    public int getUnlockRssi() {
        return unlockRssi;
    }

    public void setUnlockRssi(int unlockRssi) {
        this.unlockRssi = unlockRssi;
    }

    public int getLockRssi() {
        return lockRssi;
    }

    public void setLockRssi(int lockRssi) {
        this.lockRssi = lockRssi;
    }

    // ==================== 周期上传数据(0x69) Getter/Setter ====================

    public boolean isRearRightDoor() {
        return rearRightDoor;
    }

    public void setRearRightDoor(boolean rearRightDoor) {
        this.rearRightDoor = rearRightDoor;
    }

    public boolean isRearLeftDoor() {
        return rearLeftDoor;
    }

    public void setRearLeftDoor(boolean rearLeftDoor) {
        this.rearLeftDoor = rearLeftDoor;
    }

    public boolean isFrontRightDoor() {
        return frontRightDoor;
    }

    public void setFrontRightDoor(boolean frontRightDoor) {
        this.frontRightDoor = frontRightDoor;
    }

    public boolean isFrontLeftDoor() {
        return frontLeftDoor;
    }

    public void setFrontLeftDoor(boolean frontLeftDoor) {
        this.frontLeftDoor = frontLeftDoor;
    }

    public boolean isTrunk() {
        return trunk;
    }

    public void setTrunk(boolean trunk) {
        this.trunk = trunk;
    }

    public boolean isHood() {
        return hood;
    }

    public void setHood(boolean hood) {
        this.hood = hood;
    }

    public boolean isHeadlight() {
        return headlight;
    }

    public void setHeadlight(boolean headlight) {
        this.headlight = headlight;
    }

    public boolean isSmallLight() {
        return smallLight;
    }

    public void setSmallLight(boolean smallLight) {
        this.smallLight = smallLight;
    }

    public boolean isRearRightWindow() {
        return rearRightWindow;
    }

    public void setRearRightWindow(boolean rearRightWindow) {
        this.rearRightWindow = rearRightWindow;
    }

    public boolean isRearLeftWindow() {
        return rearLeftWindow;
    }

    public void setRearLeftWindow(boolean rearLeftWindow) {
        this.rearLeftWindow = rearLeftWindow;
    }

    public boolean isFrontRightWindow() {
        return frontRightWindow;
    }

    public void setFrontRightWindow(boolean frontRightWindow) {
        this.frontRightWindow = frontRightWindow;
    }

    public boolean isFrontLeftWindow() {
        return frontLeftWindow;
    }

    public void setFrontLeftWindow(boolean frontLeftWindow) {
        this.frontLeftWindow = frontLeftWindow;
    }

    public boolean isSunroof() {
        return sunroof;
    }

    public void setSunroof(boolean sunroof) {
        this.sunroof = sunroof;
    }

    public boolean isLeftTurnSignal() {
        return leftTurnSignal;
    }

    public void setLeftTurnSignal(boolean leftTurnSignal) {
        this.leftTurnSignal = leftTurnSignal;
    }

    public boolean isRightTurnSignal() {
        return rightTurnSignal;
    }

    public void setRightTurnSignal(boolean rightTurnSignal) {
        this.rightTurnSignal = rightTurnSignal;
    }

    public boolean isHandbrake() {
        return handbrake;
    }

    public void setHandbrake(boolean handbrake) {
        this.handbrake = handbrake;
    }

    public boolean isBrakePedal() {
        return brakePedal;
    }

    public void setBrakePedal(boolean brakePedal) {
        this.brakePedal = brakePedal;
    }

    public boolean isAirConditioner() {
        return airConditioner;
    }

    public void setAirConditioner(boolean airConditioner) {
        this.airConditioner = airConditioner;
    }

    public boolean isAccOn() {
        return accOn;
    }

    public void setAccOn(boolean accOn) {
        this.accOn = accOn;
    }

    public boolean isEngineOn() {
        return engineOn;
    }

    public void setEngineOn(boolean engineOn) {
        this.engineOn = engineOn;
    }

    public boolean isCentralLock() {
        return centralLock;
    }

    public void setCentralLock(boolean centralLock) {
        this.centralLock = centralLock;
    }

    public boolean isRepairMode() {
        return repairMode;
    }

    public void setRepairMode(boolean repairMode) {
        this.repairMode = repairMode;
    }

    public boolean isPowerOn() {
        return powerOn;
    }

    public void setPowerOn(boolean powerOn) {
        this.powerOn = powerOn;
    }

    public boolean isLockedNotClosed() {
        return lockedNotClosed;
    }

    public void setLockedNotClosed(boolean lockedNotClosed) {
        this.lockedNotClosed = lockedNotClosed;
    }

    public boolean isVibrationAlarm() {
        return vibrationAlarm;
    }

    public void setVibrationAlarm(boolean vibrationAlarm) {
        this.vibrationAlarm = vibrationAlarm;
    }

    public boolean isIllegalDoor() {
        return illegalDoor;
    }

    public void setIllegalDoor(boolean illegalDoor) {
        this.illegalDoor = illegalDoor;
    }

    public boolean isIllegalIgnition() {
        return illegalIgnition;
    }

    public void setIllegalIgnition(boolean illegalIgnition) {
        this.illegalIgnition = illegalIgnition;
    }

    public boolean isPowerCutAlarm() {
        return powerCutAlarm;
    }

    public void setPowerCutAlarm(boolean powerCutAlarm) {
        this.powerCutAlarm = powerCutAlarm;
    }

    public boolean isTirePressureAlarm() {
        return tirePressureAlarm;
    }

    public void setTirePressureAlarm(boolean tirePressureAlarm) {
        this.tirePressureAlarm = tirePressureAlarm;
    }

    public boolean isGpsPower() {
        return gpsPower;
    }

    public void setGpsPower(boolean gpsPower) {
        this.gpsPower = gpsPower;
    }

    public boolean isGpsPositioned() {
        return gpsPositioned;
    }

    public void setGpsPositioned(boolean gpsPositioned) {
        this.gpsPositioned = gpsPositioned;
    }

    public boolean isGpsAntennaOpen() {
        return gpsAntennaOpen;
    }

    public void setGpsAntennaOpen(boolean gpsAntennaOpen) {
        this.gpsAntennaOpen = gpsAntennaOpen;
    }

    public boolean isGpsAntennaShort() {
        return gpsAntennaShort;
    }

    public void setGpsAntennaShort(boolean gpsAntennaShort) {
        this.gpsAntennaShort = gpsAntennaShort;
    }

    public int getGpsSignal() {
        return gpsSignal;
    }

    public void setGpsSignal(int gpsSignal) {
        this.gpsSignal = gpsSignal;
    }

    public int getGsmSignal() {
        return gsmSignal;
    }

    public void setGsmSignal(int gsmSignal) {
        this.gsmSignal = gsmSignal;
    }

    public boolean isMovingForward() {
        return movingForward;
    }

    public void setMovingForward(boolean movingForward) {
        this.movingForward = movingForward;
    }

    public boolean isMovingBackward() {
        return movingBackward;
    }

    public void setMovingBackward(boolean movingBackward) {
        this.movingBackward = movingBackward;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    // ==================== 挪车反馈数据(0x7A) Getter/Setter ====================

    public boolean isParkingFeedbackEnabled() {
        return parkingFeedbackEnabled;
    }

    public void setParkingFeedbackEnabled(boolean parkingFeedbackEnabled) {
        this.parkingFeedbackEnabled = parkingFeedbackEnabled;
    }

    public ParkingStatus getParkingStatus() {
        return parkingStatus;
    }

    public void setParkingStatus(ParkingStatus parkingStatus) {
        this.parkingStatus = parkingStatus;
    }

    public Set<ParkingFailureReason> getParkingFailureReasons() {
        return parkingFailureReasons;
    }

    public void setParkingFailureReasons(Set<ParkingFailureReason> parkingFailureReasons) {
        this.parkingFailureReasons = parkingFailureReasons;
    }

    /**
     * 添加挪车失效原因
     */
    public void addParkingFailureReason(ParkingFailureReason reason) {
        if (parkingFailureReasons == null) {
            parkingFailureReasons = new HashSet<>();
        }
        parkingFailureReasons.add(reason);
    }

    /**
     * 检查是否有特定失效原因
     */
    public boolean hasParkingFailureReason(ParkingFailureReason reason) {
        return parkingFailureReasons != null && parkingFailureReasons.contains(reason);
    }

    /**
     * 获取失效原因的描述信息
     */
    public String getParkingFailureDescription() {
        if (parkingFailureReasons == null || parkingFailureReasons.isEmpty()) {
            return "无";
        }
        StringBuilder sb = new StringBuilder();
        for (ParkingFailureReason reason : parkingFailureReasons) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(reason.getDescription());
        }
        return sb.toString();
    }

    // ==================== 辅助方法 ====================

    /**
     * 检查所有车门是否关闭
     */
    public boolean areAllDoorsClosed() {
        return !frontLeftDoor && !frontRightDoor && !rearLeftDoor && !rearRightDoor && !trunk && !hood;
    }

    /**
     * 检查是否有任何车门开启
     */
    public boolean isAnyDoorOpen() {
        return frontLeftDoor || frontRightDoor || rearLeftDoor || rearRightDoor || trunk || hood;
    }

    /**
     * 获取车辆状态描述
     */
    public String getStatusDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("车门:").append(areAllDoorsClosed() ? "全部关闭" : "有车门开启").append(", ");
        sb.append("中控锁:").append(centralLock ? "锁定" : "解锁").append(", ");
        sb.append("手刹:").append(handbrake ? "拉起" : "释放").append(", ");
        sb.append("引擎:").append(engineOn ? "运行" : "关闭").append(", ");
        sb.append("GPS:").append(gpsPositioned ? "已定位" : "未定位");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "VehicleState{" +
                "pkeEnabled=" + pkeEnabled +
                ", pkeConnected=" + pkeConnected +
                ", currentRssi=" + currentRssi +
                ", unlockRssi=" + unlockRssi +
                ", lockRssi=" + lockRssi +
                ", frontLeftDoor=" + frontLeftDoor +
                ", frontRightDoor=" + frontRightDoor +
                ", rearLeftDoor=" + rearLeftDoor +
                ", rearRightDoor=" + rearRightDoor +
                ", trunk=" + trunk +
                ", hood=" + hood +
                ", centralLock=" + centralLock +
                ", engineOn=" + engineOn +
                ", powerOn=" + powerOn +
                ", gpsPositioned=" + gpsPositioned +
                ", gpsSignal=" + gpsSignal +
                ", parkingFeedbackEnabled=" + parkingFeedbackEnabled +
                ", parkingStatus=" + (parkingStatus != null ? parkingStatus.getDescription() : "null") +
                ", parkingFailureReasons=" + getParkingFailureDescription() +
                '}';
    }
}