//package com.shenghao.blesdkdemo.ble;
//
//import android.Manifest;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothGatt;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.os.Handler;
//import android.os.ParcelUuid;
//import android.text.TextUtils;
//import android.util.Log;
//
//import androidx.core.app.ActivityCompat;
//
//import com.clj.fastble.BleManager;
//import com.clj.fastble.callback.BleGattCallback;
//import com.clj.fastble.data.BleDevice;
//import com.clj.fastble.exception.BleException;
//import com.shenghao.blesdk.BleConstant;
//import com.shenghao.blesdk.beacon.Beacon;
//import com.shenghao.blesdk.beacon.BeaconItem;
//import com.shenghao.blesdk.enums.BluetoothStatus;
//import com.shenghao.blesdk.manager.ScanManager;
//import com.shenghao.blesdk.utils.LogUtils;
//import com.shenghao.blesdkdemo.event.BleStatusEvent;
//import com.shenghao.blesdkdemo.utils.SPUtils;
//import com.shenghao.blesdkdemo.utils.StringUtils;
//import com.shenghao.blesdkdemo.utils.ToastUtils;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
//public class BluetoothUtils {
//    public final String TAG = this.getClass().getSimpleName();
//    private static final int RECONNECT_INTERVAL = 5000;
//    private final Handler handler;
//    private boolean isAutoConnectEnabled;
//    private Context context;
//    private Runnable autoRunnable;
//    private boolean isConnecting = false;
//    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//    public BluetoothUtils(Context context){
//        this.context = context;
//        this.handler = new Handler();
//        // 从SharedPreferences加载开关状态
//        this.isAutoConnectEnabled = SPUtils.getInstance().getBoolean(SPUtils.SP_AUTO_CONNECT_ENABLED, true);
//        initReconnectRunnable();
//    }
//
//    private void initReconnectRunnable() {
//        autoRunnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    // 原有逻辑
//                    // 检查开关状态
//                    if (!isAutoConnectEnabled) {
//                        Log.d(TAG, "自动连接已关闭，跳过检测");
//                        handler.postDelayed(this, RECONNECT_INTERVAL); // 继续检查但不执行连接
//                        return;
//                    }
//                    Log.e(TAG,"自动检测连接线程running:"+Thread.currentThread().getName());
//                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                        // TODO: Consider calling
//                        //    ActivityCompat#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for ActivityCompat#requestPermissions for more details.
//                        handler.postDelayed(this, RECONNECT_INTERVAL); // 继续检查但不执行连接
//                        return;
//                    }
//                    String mac = SPUtils.getInstance().getString(SPUtils.SP_BLE_MAC);
//                    if(!TextUtils.isEmpty(mac) ){
//                        BluetoothDevice deviceble = bluetoothAdapter.getRemoteDevice(mac);
//                        if(deviceble.getBondState() == BluetoothDevice.BOND_BONDED){
//                            connectBle(mac);
//                        }else {
//                            //扫描
////                            tryConnect2();
//                        }
//                    }
//                    handler.postDelayed(this, RECONNECT_INTERVAL);
//
//
//                } catch (Exception e) {
//                    Log.e(TAG, "Auto connect runnable error", e);
//                    handler.postDelayed(this, RECONNECT_INTERVAL); // 发生异常后继续调度
//                }
//            }
//        };
//    }
//
//    public static boolean isContains(List<BleDevice> allConnectedDevice, String mac) {
//        LogUtils.e("isContains","已连接蓝牙个数"+allConnectedDevice.size());
//        for (BleDevice item :allConnectedDevice) {
//            if(TextUtils.isEmpty(item.getName())) {
//                return false;
//            }
//
//            if(item.getMac().equals(mac)){
//                LogUtils.e("isContains","true");
//                return true;
//            }
//        }
//        LogUtils.e("isContains","false");
//        return false;
//    }
//    private void tryConnect2(){
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        ScanManager.getInstance().startScan(new com.shenghao.blesdk.callback.BleScanCallback() {
//            @Override
//            public void onScanStarted(boolean success) {
//            }
//
//            @Override
//            public void onLeScan(BleDevice bleDevice) {
//            }
//
//            @Override
//            public void onScanning(BleDevice bleDevice) {
//                // 你的过滤逻辑...
//                Beacon beacon = new Beacon(bleDevice.getScanRecord());
//                List<BeaconItem> mItems = beacon.mItems;
//                String uid = "";
//                for (int i = 0; i < mItems.size(); i++) {
//                    BeaconItem item = mItems.get(i);
//                    if (item.type == 255) {
//                        uid = StringUtils.bytesToHex(item.bytes);
//                    }
//                }
//                String name = bleDevice.getName();
//                if(TextUtils.isEmpty(name))
//                    name = "";
//                if (uid.startsWith(BleConstant.UID) || uid.toUpperCase().startsWith(BleConstant.UID_SH)) {
//                    String mac = SPUtils.getInstance().getString(SPUtils.SP_BLE_MAC);
//                    if(!TextUtils.isEmpty(mac) && !BleManager.getInstance().isConnected(mac) && mac.equals(bleDevice.getMac()) && !isConnecting) {
//                        LogUtils.e(TAG,"尝试连接2222："+mac+",");
//                        connectBle(mac);
//                    }
//                }
//            }
//
//            @Override
//            public void onScanFinished(List<BleDevice> scanResultList) {
//            }
//        });
//    }
//
//    private void connectBle(String mac) {
//        if(!TextUtils.isEmpty(mac) && !BleManager.getInstance().isConnected(mac) && !isConnecting){
//            BleManager.getInstance().destroy();
//            BleManager.getInstance().connect(mac, new BleGattCallback() {
//                @Override
//                public void onStartConnect() {
//                    EventBus.getDefault().post(new BleStatusEvent(BluetoothStatus.CONNECTING,mac));
//                    isConnecting = true;
//                }
//
//                @Override
//                public void onConnectFail(BleDevice bleDevice, BleException exception) {
//                    LogUtils.e(TAG,"连接失败："+bleDevice.getMac()+",");
//                    EventBus.getDefault().post(new BleStatusEvent(BluetoothStatus.DISCONNECTED,bleDevice.getMac()));
//                    isConnecting = false;
//                }
//
//                @Override
//                public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
//                    ToastUtils.showShort(context, "连接成功"+","+bleDevice.getName());
//                    LogUtils.e(TAG,"连接成功222222："+bleDevice.getMac()+","+bleDevice.getName());
//                    EventBus.getDefault().post(new BleStatusEvent(BluetoothStatus.CONNECTED,bleDevice.getMac(),bleDevice));
//                    isConnecting = false;
//                }
//
//                @Override
//                public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
//                    ToastUtils.showShort(context, "连接断开");
//                    LogUtils.e(TAG,"连接断开："+bleDevice.getMac()+",");
//                    EventBus.getDefault().post(new BleStatusEvent(BluetoothStatus.DISCONNECTED,bleDevice.getMac()));
//                    isConnecting = false;
//                }
//            });
//        }
//
//    }
//
//    private boolean isLoopRunning = false;
//
//    // 检查循环是否正在运行
//    public boolean isLoopRunning() {
//        return isLoopRunning;
//    }
//
//    // 修改启动方法，设置运行状态
//    public void start() {
//        handler.post(autoRunnable);
//        isLoopRunning = true;
//        Log.d(TAG, "5秒循环已启动");
//    }
//
//    // 修改停止方法，设置运行状态
//    public void stop() {
//        handler.removeCallbacks(autoRunnable);
//        isLoopRunning = false;
//        Log.d(TAG, "5秒循环已停止");
//    }
//
//    // 修改 setAutoConnectEnabled 方法
//    public void setAutoConnectEnabled(boolean enabled) {
//        this.isAutoConnectEnabled = enabled;
//        // 保存到SharedPreferences
//        SPUtils.getInstance().putBoolean(SPUtils.SP_AUTO_CONNECT_ENABLED, enabled);
//
//        // 如果启用自动连接且循环没有运行，则重新启动
//        if (enabled && !isLoopRunning) {
//            Log.d(TAG, "自动连接已启用，但循环未运行，重新启动");
//            start();
//        }
//
//        // 如果禁用自动连接，可以选择停止循环或让它空转
//        // 这里我们选择让它空转，这样重新启用时无需重启
//        // if (!enabled && isLoopRunning) {
//        //     Log.d(TAG, "自动连接已禁用，停止循环");
//        //     stop();
//        // }
//
//        Log.d(TAG, "自动连接状态: " + (enabled ? "开启" : "关闭"));
//    }
//
//
//    // 获取当前自动连接状态
//    public boolean isAutoConnectEnabled() {
//        return isAutoConnectEnabled;
//    }
//
//    // 停止所有正在进行的连接
//    private void stopAllConnections() {
//        Set<BluetoothDevice> pairedDevices = BluetoothUtils.getPairedDevices(context);
//        if (pairedDevices != null) {
//            for (BluetoothDevice device : pairedDevices) {
//                if (!isHidDevice(device)) continue;
//                String mac = device.getAddress();
//                if (BleManager.getInstance().isConnected(mac)) {
//                    // 可以保持已连接的设备不断开
//                    continue;
//                }
//                // 取消正在进行的连接
////                BleManager.getInstance().disconnect(get);
//            }
//        }
//        isConnecting = false;
//    }
//
//    //判断是不是我们这种设备
//    private boolean isHidDevice(BluetoothDevice device) {
//        // 获取设备的 UUID
//        ParcelUuid[] uuids = device.getUuids();
//        if (uuids == null || uuids.length == 0) {
//            Log.d(TAG, "设备未公开任何 UUID");
//            return false;
//        }
//
//        // 遍历 UUID 并转换为字符串
//        for (ParcelUuid parcelUuid : uuids) {
//            UUID uuid = parcelUuid.getUuid();
//            String uuidString = uuid.toString();
////            Log.d(TAG, "设备支持的 UUID: " + uuidString);
//            if(BleConstant.HID_DEVICE_UUID.equals(uuidString))
//                return true;
//        }
//        return false;
//    }
//
//    private boolean isBonded(String mac) {
//        Set<BluetoothDevice> pairedDevices = BluetoothUtils.getPairedDevices(context);
//        if(pairedDevices!=null)
//            for (BluetoothDevice device:pairedDevices) {
//                if(device.getAddress().equals(mac))
//                    return true;
//            }
//        return false;
//    }
//
//    public List<BluetoothDevice> getConnectedDevices() {
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        List<BluetoothDevice> list = new ArrayList<>();
//        if (bluetoothAdapter == null) {
//            Log.e(TAG, "设备不支持蓝牙");
//            return list;
//        }
//
//        // 检查蓝牙是否开启
//        if (!bluetoothAdapter.isEnabled()) {
//            Log.e(TAG, "蓝牙未开启");
//            return list;
//        }
//
//// 获取已绑定的设备
//        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
//        if (bondedDevices.isEmpty()) {
//            Log.d(TAG, "没有已绑定的设备");
//            return list;
//        }
//
//        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
//        try {//得到连接状态的方法
//            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
//            //打开权限
//            method.setAccessible(true);
//            int state = (int) method.invoke(adapter, (Object[]) null);
//
//            if(state == BluetoothAdapter.STATE_CONNECTED){
//                Log.i(TAG,"BluetoothAdapter.STATE_CONNECTED");
//                Log.i(TAG,"devices:"+bondedDevices.size());
//                for(BluetoothDevice device : bondedDevices){
//                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
//                    method.setAccessible(true);
//                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
//                    if(isConnected){
//                        Log.i(TAG,"connected:"+device.getName());
//                        list.add(device);
//                    }
//                }
//            }
//            return list;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//    public static Set<BluetoothDevice> getPairedDevices(Context context) {
//        // 获取蓝牙适配器
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        // 检查蓝牙是否可用
//        if (bluetoothAdapter == null) {
//            // 设备不支持蓝牙
//            return null;
//        }
//
//        try {
//            return bluetoothAdapter.getBondedDevices();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        // 获取已配对设备
//        return null;
//    }
//}