package com.shenghao.blesdkdemo.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothDevice;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdk.BleSdk;
import com.shenghao.blesdk.api.CommandApi;
import com.shenghao.blesdk.callback.BleConnectCallback;
import com.shenghao.blesdk.callback.BleNotifyCallback;
import com.shenghao.blesdk.callback.BleScanCallback;
import com.shenghao.blesdk.callback.BleStateListener;
import com.shenghao.blesdk.entity.BleSdkDevice;
import com.shenghao.blesdk.enums.BlueRssiPke;
import com.shenghao.blesdk.exception.BleSdkException;
import com.shenghao.blesdk.listener.BluetoothStateChangeListener;
import com.shenghao.blesdk.manager.BleConnectionManager;
import com.shenghao.blesdk.manager.OneKeyParkingManager;
import com.shenghao.blesdk.manager.PairingManager;
import com.shenghao.blesdk.manager.ScanManager;
import com.shenghao.blesdk.receiver.BluetoothReceiver;
import com.shenghao.blesdk.service.BluetoothService;
import com.shenghao.blesdk.utils.BleConfigManager;
import com.shenghao.blesdk.utils.BlePermissionManager;
import com.shenghao.blesdk.utils.ByteUtils;

import java.util.ArrayList;
import java.util.List;

public class BleSdkDemoActivity extends BaseActivity {

    private static final String TAG = "BleSdkDemo";

    private RecyclerView rvDevices;
    private TextView tvStatus;
    private TextView tvConnectedDevice;
    private Button btnScan;
    private Button btnConnect;
    private Button btnStartService;
    private Button btnStopService;

    private Button btnForward;
    private Button btnBackward;
    private Button btnStop;

    private Button btnPkeWeak;
    private Button btnPkeMiddle;
    private Button btnPkeStrong;
    private Button btnReadPke;
    private Button btnPair;
    private Button btnNotify;
    private Switch switchPKE;
    private ProgressBar progressBar;

    private DeviceAdapter deviceAdapter;
    private List<BleSdkDevice> deviceList = new ArrayList<>();
    private BleSdkDevice selectedDevice;
    private BluetoothReceiver bluetoothReceiver;
    private OneKeyParkingManager parkingManager;
    private PairingManager pairingManager;
    private boolean isBonded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_sdk_demo);
        BleSdk.getInstance()
                .getBleConnectionManager()
                .setAutoConnectEnabled(true);
        initViews();
        initAdapter();
        initListeners();
        initBluetoothReceiver();

        initializeBleSdk();
    }

    @Override
    protected void initViews() {
        rvDevices = findViewById(R.id.rv_devices);
        tvStatus = findViewById(R.id.tv_status);
        tvConnectedDevice = findViewById(R.id.tv_connected_device);
        btnScan = findViewById(R.id.btn_scan);
        btnConnect = findViewById(R.id.btn_connect);
        btnStartService = findViewById(R.id.btn_start_service);
        btnStopService = findViewById(R.id.btn_stop_service);

        btnForward = findViewById(R.id.btn_forward);
        btnBackward = findViewById(R.id.btn_backward);
        btnStop = findViewById(R.id.btn_stop);

        btnPkeWeak = findViewById(R.id.btn_pke_weak);
        btnPkeMiddle = findViewById(R.id.btn_pke_middle);
        btnPkeStrong = findViewById(R.id.btn_pke_strong);
        btnReadPke = findViewById(R.id.btn_read_pke);
        btnPair = findViewById(R.id.btn_pair);
        btnNotify = findViewById(R.id.btn_notify);
        switchPKE = findViewById(R.id.switch_pke);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void initAdapter() {
        deviceAdapter = new DeviceAdapter(deviceList);
        rvDevices.setLayoutManager(new LinearLayoutManager(this));
        rvDevices.setAdapter(deviceAdapter);

        deviceAdapter.setOnItemClickListener(device -> {
            selectedDevice = device;
            tvStatus.setText("已选择设备: " + device.getName() + " (" + device.getMac() + ")");
            btnConnect.setEnabled(true);
            updateBondState(device);
        });
    }

    private void updateBondState(BleSdkDevice device) {
        if (pairingManager != null && device != null) {
            pairingManager.setBleDevice(device);
            isBonded = pairingManager.isBonded();
            btnPair.setText(isBonded ? "已配对" : "去配对");
            btnPair.setEnabled(!isBonded && selectedDevice != null);
        }
    }

    private void initListeners() {
        btnScan.setOnClickListener(v -> {
            if (!BlePermissionManager.checkAndRequestPermissions(this)) {
                Toast.makeText(this, "请先授予蓝牙权限", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!BlePermissionManager.isBluetoothEnabled()) {
                BlePermissionManager.requestEnableBluetooth(this);
                return;
            }

            startScan();
        });

        btnConnect.setOnClickListener(v -> {
            if (selectedDevice == null) {
                Toast.makeText(this, "请先选择设备", Toast.LENGTH_SHORT).show();
                return;
            }
            connectToDevice(selectedDevice);
        });

        btnStartService.setOnClickListener(v -> {
            startService(new Intent(this, BluetoothService.class));
            Toast.makeText(this, "蓝牙服务已启动", Toast.LENGTH_SHORT).show();
        });

        btnStopService.setOnClickListener(v -> {
            stopService(new Intent(this, BluetoothService.class));
            Toast.makeText(this, "蓝牙服务已停止", Toast.LENGTH_SHORT).show();
        });

        btnForward.setOnClickListener(v -> {
            if (!checkDeviceConnected()) return;
            tvStatus.setText("发送前进指令");
            parkingManager.forward();
        });

        btnBackward.setOnClickListener(v -> {
            if (!checkDeviceConnected()) return;
            tvStatus.setText("发送后退指令");
            parkingManager.backward();
        });

        btnStop.setOnClickListener(v -> {
            if (!checkDeviceConnected()) return;
            tvStatus.setText("发送停止指令");
            parkingManager.stop();
        });

        btnPkeWeak.setOnClickListener(v -> {
            if (!checkDeviceConnected()) return;
            setPkeRssiLevel(BlueRssiPke.WEAK);
        });

        btnPkeMiddle.setOnClickListener(v -> {
            if (!checkDeviceConnected()) return;
            setPkeRssiLevel(BlueRssiPke.MIDDLE);
        });

        btnPkeStrong.setOnClickListener(v -> {
            if (!checkDeviceConnected()) return;
            setPkeRssiLevel(BlueRssiPke.STRONG);
        });

        btnReadPke.setOnClickListener(v -> {
            if (!checkDeviceConnected()) return;
            readPkeConfig();
        });

        btnPair.setOnClickListener(v -> {
            if (!checkDeviceConnected()) return;
            if (!isBonded) {
                startPairing();
            }
        });

        btnNotify.setOnClickListener(v -> {
//            if (!checkDeviceConnected()) return;
            enableNotification();
        });

        switchPKE.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!checkDeviceConnected()) {
                buttonView.setChecked(false);
                return;
            }
            togglePKE(isChecked);
        });
    }

    private boolean checkDeviceConnected() {
        if (selectedDevice == null) {
            Toast.makeText(this, "请先选择设备", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!BleSdk.getInstance().getBleConnectionManager().isConnected(selectedDevice.getMac())) {
            Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initBluetoothReceiver() {
        bluetoothReceiver = new BluetoothReceiver(new BluetoothStateChangeListener() {
            @Override
            public void onBluetoothOff() {
                tvStatus.setText("蓝牙已关闭");
                btnScan.setEnabled(false);
                btnConnect.setEnabled(false);
            }

            @Override
            public void onBluetoothTurningOff() {
                tvStatus.setText("蓝牙正在关闭...");
            }

            @Override
            public void onBluetoothOn() {
                tvStatus.setText("蓝牙已开启");
                btnScan.setEnabled(true);
                btnConnect.setEnabled(true);
            }

            @Override
            public void onBluetoothTurningOn() {
                tvStatus.setText("蓝牙正在开启...");
            }
        });
    }

    private void initializeBleSdk() {
        try {
            BleSdk.getInstance().initialize(getApplication());
            BleConfigManager.getInstance(getApplication());
            parkingManager = BleSdk.getInstance().getOneKeyParkingManager();
            pairingManager = BleSdk.getInstance().getPairingManager();

            BleConnectionManager connectionManager = BleSdk.getInstance().getBleConnectionManager();
            connectionManager.setStateListener(new BleStateListener() {
                @Override
                public void onConnecting(String mac) {
                    tvStatus.setText("正在连接: " + mac);
                }

                @Override
                public void onConnected(String mac, BleSdkDevice device) {
                    tvStatus.setText("已连接: " + device.getName() + " (" + mac + ")");
                    tvConnectedDevice.setText("当前已连接: " + (TextUtils.isEmpty(device.getName()) ? "未知设备" : device.getName()) + " (" + mac + ")");
                    parkingManager.setBleDevice(device);
                    pairingManager.setBleDevice(device);
                    enableParkingButtons(true);
                    enablePkeButtons(true);
                    updateBondState(device);
                }

                @Override
                public void onDisconnected(String mac) {
                    tvStatus.setText("已断开连接: " + mac);
                    tvConnectedDevice.setText("当前已连接: 无");
                    enableParkingButtons(false);
                    enablePkeButtons(false);
                    switchPKE.setChecked(false);
                }

                @Override
                public void onConnectFailed(String mac, String errorMessage) {
                    tvStatus.setText("连接失败: " + mac + ", 错误: " + errorMessage);
                }
            });

            tvStatus.setText("BLE SDK 初始化完成");
            btnScan.setEnabled(true);
            checkConnectedDevice();
        } catch (Exception e) {
            tvStatus.setText("SDK初始化失败: " + e.getMessage());
            Log.e(TAG, "SDK initialization failed", e);
        }
    }

    private void checkConnectedDevice() {
        String savedMac = BleConfigManager.getInstance().getBleMac();
        BleConnectionManager connectionManager = BleSdk.getInstance().getBleConnectionManager();
        if (!TextUtils.isEmpty(savedMac) && connectionManager.isConnected(savedMac)) {
            BleSdkDevice device = connectionManager.getConnectedDevice(savedMac);
            if (device != null) {
                tvConnectedDevice.setText("当前已连接: " + (TextUtils.isEmpty(device.getName()) ? "未知设备" : device.getName()) + " (" + savedMac + ")");
                selectedDevice = device;
                parkingManager.setBleDevice(device);
                pairingManager.setBleDevice(device);
                enableParkingButtons(true);
                updateBondState(device);
                return;
            }
        }
        tvConnectedDevice.setText("当前已连接: 无");
    }

    private void enableParkingButtons(boolean enabled) {
        btnForward.setEnabled(enabled);
        btnBackward.setEnabled(enabled);
        btnStop.setEnabled(enabled);
    }

    private void enablePkeButtons(boolean enabled) {
        btnPkeWeak.setEnabled(enabled && isBonded);
        btnPkeMiddle.setEnabled(enabled && isBonded);
        btnPkeStrong.setEnabled(enabled && isBonded);
        btnReadPke.setEnabled(enabled);
        btnPair.setEnabled(enabled && !isBonded);
        btnNotify.setEnabled(enabled);
        switchPKE.setEnabled(enabled);
    }

    private void enableNotification() {
        tvStatus.setText("正在打开通知...");
        btnNotify.setEnabled(false);

        BleSdkDevice device = selectedDevice;
        if (device == null) {
            List<BleSdkDevice> connectedDevices = BleSdk.getInstance().getBleConnectionManager().getSdkConnectedDevices();
            if (connectedDevices != null && !connectedDevices.isEmpty()) {
                device = connectedDevices.get(0);
                selectedDevice = device;
            }
        }

        CommandApi.setNotifyListener(device, new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {
                runOnUiThread(() -> {
                    tvStatus.setText("通知已打开，正在监听设备数据...");
                    btnNotify.setText("通知已打开");
                    Toast.makeText(BleSdkDemoActivity.this, "通知已打开", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onNotifyFailed(com.shenghao.blesdk.exception.BleSdkException exception) {
                runOnUiThread(() -> {
                    tvStatus.setText("通知打开失败: " + exception.getDescription());
                    btnNotify.setText("打开通知");
                    btnNotify.setEnabled(true);
                    Toast.makeText(BleSdkDemoActivity.this, "通知打开失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                String hex = ByteUtils.bytes2HexStr(data);
                Log.d(TAG, "收到数据: " + hex);
            }
        });
    }

    private void startScan() {
        deviceList.clear();
        deviceAdapter.notifyDataSetChanged();
        tvStatus.setText("正在扫描设备...");

        ScanManager scanManager = BleSdk.getInstance().getScanManager();
        scanManager.startScan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                if (!success) {
                    tvStatus.setText("扫描启动失败");
                }
            }

            @Override
            public void onLeScan(BleSdkDevice device) {
                if (!deviceList.contains(device)) {
                    deviceList.add(device);
                    deviceAdapter.notifyItemInserted(deviceList.size() - 1);
                }
            }

            @Override
            public void onScanning(BleSdkDevice device) {
            }

            @Override
            public void onScanFinished(List<BleSdkDevice> devices) {
                tvStatus.setText("扫描完成，共发现 " + devices.size() + " 个设备");
            }
        });
    }

    private void connectToDevice(BleSdkDevice device) {
        BleConnectionManager connectionManager = BleSdk.getInstance().getBleConnectionManager();
        connectionManager.connect(device.getMac(), new BleConnectCallback() {
            @Override
            public void onSuccess(BleSdkDevice bleDevice) {
                selectedDevice = bleDevice;
                BleConfigManager.getInstance().setBleMac(bleDevice.getMac());
            }

            @Override
            public void onFailed(BleSdkException exception) {

            }

            @Override
            public void onDisconnected() {

            }
        });
    }

    private void startPairing() {
        tvStatus.setText("配对初始化...");
        btnPair.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        pairingManager.startPairing(new PairingManager.SimplePairingCallback() {
            @Override
            public void onPairingRequest(BluetoothDevice device, int variant) {
                runOnUiThread(() -> {
                    tvStatus.setText("配对请求已发送，等待设备响应...");
                    Toast.makeText(BleSdkDemoActivity.this, "配对请求已发送", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onPairingInProgress() {
                runOnUiThread(() -> {
                    tvStatus.setText("配对中，请注意设备上的配对确认...");
                    btnPair.setText("配对中");
                });
            }

            @Override
            public void onPairingSuccess() {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvStatus.setText("配对成功！设备已绑定");
                    isBonded = true;
                    btnPair.setText("已配对");
                    btnPair.setEnabled(false);
                    enablePkeButtons(true);
                    Toast.makeText(BleSdkDemoActivity.this, "配对成功", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onPairingFailed(String errorMessage) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvStatus.setText("配对失败: " + errorMessage);
                    btnPair.setText("去配对");
                    btnPair.setEnabled(selectedDevice != null);
                    Toast.makeText(BleSdkDemoActivity.this, "配对失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onPairingCancelled() {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvStatus.setText("配对已取消");
                    btnPair.setText("去配对");
                    btnPair.setEnabled(selectedDevice != null);
                });
            }
        });
    }

    private void togglePKE(boolean enable) {
        tvStatus.setText(enable ? "开启PKE" : "关闭PKE");
        com.shenghao.blesdk.api.PkeCommandApi.sendPKECommand(selectedDevice,
                com.shenghao.blesdk.api.PkeCommandApi.generatePKECommand(enable, false, false, false,
                        BleConfigManager.getInstance().getBleUnlockRssi(),
                        BleConfigManager.getInstance().getBleLockRssi(), 1, "0000"),
                new com.shenghao.blesdk.callback.BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        tvStatus.setText("PKE" + (enable ? "开启" : "关闭") + "成功");
                    }

                    @Override
                    public void onWriteFailed(com.shenghao.blesdk.exception.BleSdkException exception) {
                        tvStatus.setText("PKE" + (enable ? "开启" : "关闭") + "失败");
                        switchPKE.setChecked(!enable);
                    }
                });
    }

    private void setPkeRssiLevel(BlueRssiPke level) {
        int unlockDb = level.getLowRssi();
        int lockDb = level.getHighRssi();

        tvStatus.setText("设置PKE档位: " + level.getValue() + " (解锁:" + unlockDb + ", 关锁:" + lockDb + ")");

        com.shenghao.blesdk.api.PkeCommandApi.sendPKECommand(selectedDevice,
                com.shenghao.blesdk.api.PkeCommandApi.generatePKECommand(unlockDb, lockDb),
                new com.shenghao.blesdk.callback.BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        tvStatus.setText("PKE档位设置成功");
                        BleConfigManager.getInstance().setBleUnlockRssi(unlockDb);
                        BleConfigManager.getInstance().setBleLockRssi(lockDb);
                    }

                    @Override
                    public void onWriteFailed(com.shenghao.blesdk.exception.BleSdkException exception) {
                        tvStatus.setText("PKE档位设置失败");
                    }
                });
    }

    private void readPkeConfig() {
        tvStatus.setText("读取PKE配置...");
        com.shenghao.blesdk.api.PkeCommandApi.sendPKECommand(selectedDevice,
                com.shenghao.blesdk.api.PkeCommandApi.generateReadPKECommand(),
                new com.shenghao.blesdk.callback.BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        tvStatus.setText("PKE配置读取命令已发送");
                    }

                    @Override
                    public void onWriteFailed(com.shenghao.blesdk.exception.BleSdkException exception) {
                        tvStatus.setText("PKE配置读取失败");
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bluetoothReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScanManager.getInstance().stopScan();
        if (parkingManager != null) {
            parkingManager.release();
        }
        if (pairingManager != null) {
            pairingManager.unregisterReceiver();
        }
    }

    public static class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

        private List<BleSdkDevice> devices;
        private OnItemClickListener listener;

        public DeviceAdapter(List<BleSdkDevice> devices) {
            this.devices = devices;
        }

        public interface OnItemClickListener {
            void onItemClick(BleSdkDevice device);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            BleSdkDevice device = devices.get(position);
            holder.tvName.setText(TextUtils.isEmpty(device.getName()) ? "未知设备" : device.getName());
            holder.tvMac.setText(device.getMac() + " | RSSI: " + device.getRssi());

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(device);
                }
            });
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            TextView tvMac;

            public ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(android.R.id.text1);
                tvMac = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}