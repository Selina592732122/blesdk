# BleSdk

蓝牙SDK封装库，提供蓝牙连接、扫描、一键挪车、PKE等功能。

## 功能特性

- ✅ 蓝牙设备扫描与连接
- ✅ 自动重连机制
- ✅ 一键挪车（前进/后退/停止）
- ✅ PKE蓝牙钥匙功能
- ✅ 配对流程封装

## 集成方式

### Maven 依赖

```gradle
repositories {
    maven {
        url 'https://gitee.com/api/v5/repos/yourusername/blesdk/packages/maven'
    }
}

dependencies {
    implementation 'com.shenghao:blesdk:1.0.0'
}
```

## 使用示例

### 初始化 SDK

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BleSdk.getInstance().initialize(this);
    }
}
```

### 扫描设备

```java
ScanManager scanManager = BleSdk.getInstance().getScanManager();
scanManager.startScan(new BleScanCallback() {
    @Override
    public void onScanning(BleDevice device) {
        // 处理扫描到的设备
    }
});
```

### 连接设备

```java
BleConnectionManager connectionManager = BleSdk.getInstance().getBleConnectionManager();
connectionManager.connect(mac, new BleConnectCallback() {
    @Override
    public void onSuccess(BleDevice device) {
        // 连接成功
    }
});
```

### 一键挪车

```java
OneKeyParkingManager parkingManager = BleSdk.getInstance().getOneKeyParkingManager();
parkingManager.setBleDevice(device);
parkingManager.forward();  // 前进
parkingManager.backward(); // 后退
parkingManager.stop();     // 停止
```

### PKE 设置

```java
// 生成PKE指令
byte[] command = PkeCommandApi.generatePKECommand(70, 80);
PkeCommandApi.sendPKECommand(device, command, callback);
```

### 配对功能

```java
PairingManager pairingManager = BleSdk.getInstance().getPairingManager();
pairingManager.setBleDevice(device);
pairingManager.startPairing(new PairingCallback() {
    @Override
    public void onPairingSuccess() {
        // 配对成功
    }
});
```

## 发布命令

```bash
# 构建并发布到 Gitee
./gradlew :bleSdk:publishReleasePublicationToMavenRepository
```

## 许可证

Apache-2.0 License