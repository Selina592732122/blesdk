# BleSdk

蓝牙SDK封装库，提供蓝牙连接、扫描、一键挪车、PKE等功能。

## 功能特性

- ✅ 蓝牙设备扫描与连接
- ✅ **自动重连机制**（应用重启后自动连接上次连接的设备）
- ✅ 一键挪车（前进/后退/停止）
- ✅ PKE蓝牙钥匙功能
- ✅ 配对流程封装
- ✅ 连接状态监听（支持注册时检测已连接设备）

## 集成方式

### JitPack 依赖

在项目根目录的 `build.gradle` 中添加 JitPack 仓库：

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

在 `app/build.gradle` 中添加依赖：

```gradle
dependencies {
    implementation 'com.github.yourusername:blesdk:1.0.0'
}
```

## 使用示例

### 初始化 SDK

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化后自动启动自动连接循环
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

### 连接状态监听

```java
BleConnectionManager connectionManager = BleSdk.getInstance().getBleConnectionManager();
connectionManager.setStateListener(new BleStateListener() {
    @Override
    public void onConnecting(String mac) {
        // 正在连接
    }

    @Override
    public void onConnected(String mac, BleDevice device) {
        // 连接成功
        // 如果注册时设备已连接，此回调会立即触发
    }

    @Override
    public void onDisconnected(String mac) {
        // 断开连接
    }

    @Override
    public void onConnectFailed(String mac, String errorMessage) {
        // 连接失败
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

## 自动连接说明

SDK 初始化后会自动启动自动连接循环，每隔 5 秒检测一次是否需要连接设备：

1. 从配置中读取上次连接的设备 MAC 地址
2. 如果设备已绑定（BOND_BONDED），直接尝试连接
3. 如果未绑定，进行扫描并尝试连接

## 生成 AAR 文件

### 方式一：使用 Android Studio

1. 打开项目后，在右侧 Gradle 面板中找到 `bleSdk` 模块
2. 展开 `Tasks` -> `build`
3. 双击 `assembleRelease` 或 `assembleDebug`

### 方式二：使用命令行

```bash
# 生成 Release 版本 AAR
./gradlew :bleSdk:assembleRelease

# 生成 Debug 版本 AAR
./gradlew :bleSdk:assembleDebug

# 或同时生成两个版本
./gradlew :bleSdk:build
```

### AAR 文件位置

生成的 AAR 文件位于：

```
bleSdk/build/outputs/aar/
├── blesdk-debug.aar
└── blesdk-release.aar
```

## 发布命令

```bash
# 构建并发布到 Gitee
./gradlew :bleSdk:publishReleasePublicationToMavenRepository
```

## 许可证

Apache-2.0 License