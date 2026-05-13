package com.shenghao.blesdkdemo.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.base.IPermission;
import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.XXPermissions.PermissionDescription;
import com.shenghao.blesdkdemo.XXPermissions.PermissionInterceptor;
import com.shenghao.blesdkdemo.adapter.BleAdapter;
import com.shenghao.blesdk.BleConstant;
import com.shenghao.blesdk.BleSdk;
import com.shenghao.blesdk.beacon.Beacon;
import com.shenghao.blesdk.beacon.BeaconItem;
import com.shenghao.blesdk.enums.BluetoothStatus;
import com.shenghao.blesdk.manager.ScanManager;
import com.shenghao.blesdk.utils.BluetoothUtils;
import com.shenghao.blesdkdemo.event.BleStatusEvent;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.GPSUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.SPUtils;
import com.shenghao.blesdkdemo.utils.StringUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;
import com.shenghao.blesdkdemo.widget.CommonDialog;
import com.shenghao.blesdkdemo.widget.EmptyRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class BleActivity extends BaseActivity {
    public final String TAG = this.getClass().getSimpleName();
    private static int bleType = 1;//0是旧版蓝牙，1是新版蓝牙
    private View mRippleView;
    private EmptyRecyclerView recyclerViewFind,recyclerViewBind;
    private Disposable countdownSubscription;
    private TextView tvScanning;
    private TextView tvScan;
    private RelativeLayout rlScan;
    private List<BleDevice> mScanList = new ArrayList<>();
    private List<BleDevice> mBindList = new ArrayList<>();
    private BleAdapter scanAdapter;
    private BleAdapter bindAdapter;
    private CommonDialog editdialog;
    private TextView tvTitle,tvTitle2;
    private LinearLayout llFind;
    private BlueToothDialog blueToothDialog;
    private BlueToothRssiPkeDialog blueToothRssiDialog;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final BroadcastReceiver pairingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (bondState == BluetoothDevice.BOND_BONDED) {
                    // 配对成功
                    connectBle(device.getAddress());
                } else if (bondState == BluetoothDevice.BOND_NONE) {
                    // 配对失败或取消
                    bindAdapter.updateStatus(device.getAddress(), BluetoothStatus.DISCONNECTED);
                    scanAdapter.updateStatus(device.getAddress(),BluetoothStatus.DISCONNECTED);
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        EventBus.getDefault().register(this);
        if(!BleManager.getInstance().isSupportBle()){
            ToastUtils.showShort(this,"当前设备不支持蓝牙");
            return;
        }
        String protocol = AppSingleton.getInstance().getCurrentTerminal().getProtocol();
        if("CAN".equals(protocol))
            bleType = 1;
        else {
            bleType = 0;
            registerBond();
        }

        initViews();
//        checkLocalPermission();
        startCountdown(0);
        hideScanAnim();

//        registerBond();
        updateBindList();//更新下边的绑定列表

    }

    @Override
    protected void onResume() {
        super.onResume();
        BleSdk.getInstance()
                .getBleConnectionManager()
                .setAutoConnectEnabled(true);
    }

    private void updateBindList() {
        mBindList.clear();
        Set<BluetoothDevice> pairedDevices = BluetoothUtils.getPairedDevices(BleActivity.this);
        if(pairedDevices != null)
            for (BluetoothDevice device:pairedDevices) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
                LogUtils.e("onBindViewHolder","已配对蓝牙个数："+pairedDevices.size()+",连接名称："+device.getName()+","+device.getType());
                if(device.getType() == BluetoothDevice.DEVICE_TYPE_LE)
                    mBindList.add(new BleDevice(device));
                if(BleManager.getInstance().isConnected(device.getAddress()))
                    bindAdapter.updateStatus(device.getAddress(),BluetoothStatus.CONNECTED);
                else bindAdapter.updateStatus(device.getAddress(),BluetoothStatus.DISCONNECTED);
            }
        notifyDataSetChanged();
    }

    private void registerBond() {
        // 注册广播接收器
        IntentFilter pairingFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(pairingReceiver, pairingFilter);
    }

    public void showSelectGearDialog(BleDevice bleDevice) {
        if (this.isFinishing() || this.isDestroyed()) {
            Log.e("BleActivity", "Activity is not alive, skip showing dialog.");
            return; // 直接返回，不再执行后续显示逻辑
        }
        try {
            if (bleType == 0) {//旧版蓝牙
                BlueToothDialog blueToothRssiDialog = new BlueToothDialog(this, R.style.Common_Dialog, R.layout.dialog_blue_tooth, Gravity.CENTER);
                blueToothRssiDialog.setBleDevice(bleDevice);
                blueToothRssiDialog.setCanceledOnTouchOutside(false);
                blueToothRssiDialog.show();
            } else {
                blueToothRssiDialog = new BlueToothRssiPkeDialog(this, R.style.Common_Dialog, R.layout.dialog_blue_tooth_rssi_pke, Gravity.CENTER);
                blueToothRssiDialog.setBleDevice(bleDevice);
                blueToothRssiDialog.setCanceledOnTouchOutside(false);
                blueToothRssiDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showScanAnim(){
        rlScan.setVisibility(View.VISIBLE);
        mRippleView.setVisibility(View.VISIBLE);
        llFind.setVisibility(View.GONE);
    }
    private void hideScanAnim(){
        rlScan.setVisibility(View.GONE);
        mRippleView.setVisibility(View.GONE);
        llFind.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initViews() {
        super.initViews();
//        SPUtils.getInstance().putString(SPUtils.SP_ONE_KEY_GEN_PWD,"48CDDA0751430A6C2B34E9D1968B0F92");
//        getGenPwd0();
        findViewById(R.id.tvDemo).setOnClickListener(view -> {
            startActivity(new Intent(this, BleSdkDemoActivity.class));
        });
        mRippleView = findViewById(R.id.ivBleBg);
        recyclerViewFind = findViewById(R.id.recyclerViewFind);
        recyclerViewBind = findViewById(R.id.recyclerViewBind);
        llFind = findViewById(R.id.llFind);
        rlScan = findViewById(R.id.rlScan);
        tvScanning = findViewById(R.id.tvScanning);
        tvScan = findViewById(R.id.tvScan);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle2 = findViewById(R.id.tvTitle2);
        tvScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocalPermission();
            }
        });
        // 创建ObjectAnimator实例，设置旋转属性
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(mRippleView, "rotation", 0f, 360f);
        // 设置动画持续时间（毫秒）
        rotationAnimator.setDuration(3000);
        // 设置重复次数（无限循环）
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        // 设置重复模式（重新开始）
        rotationAnimator.setRepeatMode(ObjectAnimator.RESTART);

// 启动动画
        rotationAnimator.start();
//        mRippleView.setVisibility(View.VISIBLE);
//        mRippleView.setAnimationProgressListener(new RippleView.AnimationListener() {
//            @Override
//            public void startAnimation() {
//                //开始动画了
//            }
//
//            @Override
//            public void EndAnimation() {
//                //结束动画了
//            }
//        });
        mBindList = new ArrayList<>();
        bindAdapter = new BleAdapter(this, mBindList);
        recyclerViewBind.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBind.setAdapter(bindAdapter);
        recyclerViewBind.setEmptyView(findViewById(R.id.tvEmptyBind));

        scanAdapter = new BleAdapter(this, mScanList);
        recyclerViewFind.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFind.setAdapter(scanAdapter);
        recyclerViewFind.setEmptyView(findViewById(R.id.tvEmptyFind));

        bindAdapter.setOnItemClickListener(new BleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View view) {
                connectBle(mBindList.get(pos).getMac());
            }
        });
        scanAdapter.setOnItemClickListener(new BleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View view) {
                connectBle(mScanList.get(pos).getMac());
            }
        });
        bindAdapter.setOnEditClickListener(new BleAdapter.OnEditClickListener() {
            @Override
            public void onItemClick(int pos, View view) {
//                editBle(mBindList.get(pos));
            }
        });
        scanAdapter.setOnEditClickListener(new BleAdapter.OnEditClickListener() {
            @Override
            public void onItemClick(int pos, View view) {
//                editBle(mScanList.get(pos));
            }
        });
    }

//    private void editBle(BleDevice bleDevice) {
//        BleManager.getInstance().notify(
//                bleDevice,
//                BleConstant.SERVICE_UUID,
//                BleConstant.NOTIFY_UUID,
//                new BleNotifyCallback() {
//                    @Override
//                    public void onNotifySuccess() {
//                        // 打开通知操作成功
////                        ToastUtils.showShort(BleActivity.this,"打开通知操作成功");
//                        BleManager.getInstance().setMtu(bleDevice, 100, new BleMtuChangedCallback() {
//                            @Override
//                            public void onSetMTUFailure(BleException exception) {
//                                // 设置MTU失败
//                                ToastUtils.showShort(BleActivity.this,exception.getDescription());
//                            }
//
//                            @Override
//                            public void onMtuChanged(int mtu) {
//                                // 设置MTU成功，并获得当前设备传输支持的MTU值
//                                showEditNameDialog(bleDevice);
//                            }
//                        });
//
//
//                    }
//
//                    @Override
//                    public void onNotifyFailure(BleException exception) {
//                        // 打开通知操作失败
//                        ToastUtils.showShort(BleActivity.this,"打开通知操作失败"+exception.getDescription());
//                    }
//
//                    @Override
//                    public void onCharacteristicChanged(byte[] data) {
//                        // 打开通知后，设备发过来的数据将在这里出现
//                        String msg = new String(data);
////                        LogUtils.e("onCharacteristicChanged",StringUtils.bytesToHex(data)+","+ msg);
//                        ToastUtils.showShort(BleActivity.this,"收到数据："+ msg.replace("\r\n",""));
//                        if(msg.toLowerCase().contains("ok")){
//                            //设置名字成功,重启
////                            writeData(bleDevice,"AT+REBOOT=1\r\n");
////                            SPUtils.getInstance().putString(SPUtils.SP_BLE_LAST_MAC,"");//不自动重连
////                            BleManager.getInstance().disconnect(bleDevice);
////                            BleManager.getInstance().disableBluetooth();
////                            checkLocalPermission();
//                        }
//                    }
//                });
//    }
    /**
     * 设备重命名弹窗
     */
//    protected void showEditNameDialog(BleDevice bleDevice) {
//        editdialog = new CommonDialog(this, new CommonDialog.OnCloseListener() {
//            @Override
//            public void onClick(Dialog dialog, boolean confirm) {
//                if (confirm) {  //确定
//                    String contentEtText = editdialog.getContentEtText().trim();
//                    if (!TextUtils.isEmpty(contentEtText)) {
//                        writeData(bleDevice,"AT+NAME="+contentEtText+"\r\n");
//
//                        dialog.dismiss();
//                    } else {
//                        ToastUtils.showShort(BleActivity.this, "请输入蓝牙名称");
//                    }
//
//                }
//            }
//        })
//                .setTitle("蓝牙名称")
//                .setHint("请输入蓝牙名称")
//                .setPositiveButton("保存")
//                .setContentEtVisibility(View.VISIBLE);
//        editdialog.setEditContent(bleDevice.getName());
//        editdialog.show();
//    }

//    private void writeData(BleDevice bleDevice,String content) {
//        BleManager.getInstance().write(
//                bleDevice,
//                BleConstant.SERVICE_UUID,
//                BleConstant.WRITE_UUID,
//                content.getBytes(),
//                false,
//                new BleWriteCallback() {
//                    @Override
//                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
//                        // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
////                        ToastUtils.showShort(BleActivity.this,"发送数据到设备成功");
//                        LogUtils.e("onWriteSuccess", StringUtils.bytesToHex(justWrite));
//                    }
//
//                    @Override
//                    public void onWriteFailure(BleException exception) {
//                        // 发送数据到设备失败
//                        ToastUtils.showShort(BleActivity.this,"发送数据到设备失败");
//                    }
//                });
//    }

    public void connectBle(String mac) {
        BleManager.getInstance().destroy();
        if(bleType == 0){
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mac);
            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                 //发起配对
                device.createBond();
                bindAdapter.updateStatus(mac,BluetoothStatus.CONNECTING);
                scanAdapter.updateStatus(mac,BluetoothStatus.CONNECTING);
                return;
            }
        }
//        TextView tvStatus = view.findViewById(R.id.tvStatus);
//        LinearLayout llStatus = view.findViewById(R.id.llStatus);
        BleManager.getInstance().connect(mac, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                bindAdapter.updateStatus(mac,BluetoothStatus.CONNECTING);
                scanAdapter.updateStatus(mac,BluetoothStatus.CONNECTING);
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                bindAdapter.updateStatus(mac,BluetoothStatus.DISCONNECTED);
                scanAdapter.updateStatus(mac,BluetoothStatus.DISCONNECTED);
                ToastUtils.showShort(BleActivity.this,"连接失败"+exception.getDescription());
                notifyDataSetChanged();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                bindAdapter.updateStatus(mac,BluetoothStatus.CONNECTED);
                scanAdapter.updateStatus(mac,BluetoothStatus.CONNECTED);
                SPUtils.getInstance().putString(SPUtils.SP_BLE_MAC,mac);
                notifyDataSetChanged();
//                SPUtils.getInstance().putString(SPUtils.SP_ONE_KEY_GEN_PWD,"48CDDA0751430A6C2B34E9D1968B0F92");
                //08断开后，重新连上需要重新notify
                if (blueToothRssiDialog != null && blueToothRssiDialog.isShowing()) {
                        blueToothRssiDialog.setBleDevice(bleDevice);
                }else {
                    showSelectGearDialog(bleDevice);
                }
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                bindAdapter.updateStatus(mac,BluetoothStatus.DISCONNECTED);
                scanAdapter.updateStatus(mac,BluetoothStatus.DISCONNECTED);
                ToastUtils.showShort(BleActivity.this,"连接断开!");
                notifyDataSetChanged();
                BleSdk.getInstance()
                        .getBleConnectionManager()
                        .setAutoConnectEnabled(true);
            }
        });
    }

    //获取蓝牙根秘钥
//    private void getGenPwd0() {
//        OkHttpPresent.getGenPwd0(AppSingleton.getInstance().getTerminalNo(), new OkHttpResultCallBack() {
//            @Override
//            protected void start() {
//                super.start();
////                showLoadingDialog();
//            }
//
//            @Override
//            protected void onResponse(Response response, String body) throws IOException {
//                LogUtils.e(TAG, "登录成功: " + body);
//                LoginResp loginResp = JsonUtils.parseT(body, LoginResp.class);
//                if (loginResp != null) {
//                    if (loginResp.isSuccess()) {
//                        getGenPwd();
//                    } else {    //登录失败
//                        ToastUtils.showShort(BleActivity.this, loginResp.getMsg());
////                        hideLoadingDialog();
//                    }
//                } else {
//                    ToastUtils.showShort(BleActivity.this, getString(R.string.request_retry));
////                    hideLoadingDialog();
//                }
//            }
//
//            @Override
//            protected void onFailed(Request request, Exception e) {
//                super.onFailed(request, e);
//                LogUtils.e(TAG, "登录失败: " + e);
//                ToastUtils.showShort(BleActivity.this, getString(R.string.request_retry));
////                hideLoadingDialog();
//            }
//
//        });
//    }
//    private void getGenPwd() {
//        OkHttpPresent.getGenPwd(AppSingleton.getInstance().getTerminalNo(), new OkHttpResultCallBack() {
//            @Override
//            protected void start() {
//                super.start();
////                showLoadingDialog();
//            }
//
//            @Override
//            protected void onResponse(Response response, String body) throws IOException {
//                LogUtils.e(TAG, "登录成功: " + body);
//                LoginResp loginResp = JsonUtils.parseT(body, LoginResp.class);
//                if (loginResp != null) {
//                    if (loginResp.isSuccess()) {
//                        SPUtils.getInstance().putString(SPUtils.SP_ONE_KEY_GEN_PWD,loginResp.getData());
//                    } else {    //登录失败
//                        ToastUtils.showShort(BleActivity.this, loginResp.getMsg());
////                        hideLoadingDialog();
//                    }
//                } else {
//                    ToastUtils.showShort(BleActivity.this, getString(R.string.request_retry));
////                    hideLoadingDialog();
//                }
//            }
//
//            @Override
//            protected void onFailed(Request request, Exception e) {
//                super.onFailed(request, e);
//                LogUtils.e(TAG, "登录失败: " + e);
//                ToastUtils.showShort(BleActivity.this, getString(R.string.request_retry));
////                hideLoadingDialog();
//            }
//
//        });
//    }


    private void startCountdown(int seconds) {
        if (countdownSubscription != null && !countdownSubscription.isDisposed()) {
            countdownSubscription.dispose(); // 如果已经有一个倒计时在运行，先停止它
        }
        countdownSubscription = Observable.intervalRange(1, seconds + 1, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        int remainingSeconds = (int) (seconds - aLong);
                        tvScanning.setText(String.format("正在搜索附近的设备(%ds)", remainingSeconds));
                        if (remainingSeconds <= 0) {
                            tvScan.setText("重新发现设备");
                            tvScan.setEnabled(true);
                            if (!countdownSubscription.isDisposed()) {
                                countdownSubscription.dispose();
                            }
                            try {
//                                BleManager.getInstance().cancelScan();
                                stopScan();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            hideScanAnim();
                        } else {
                            tvScan.setText("重新发现设备");
                            tvScan.setEnabled(false);
                        }
                    }
                });
    }

    private void checkLocalPermission() {
        XXPermissions.with(this)
                .permission(PermissionLists.getAccessCoarseLocationPermission())
                .permission(PermissionLists.getAccessFineLocationPermission())
                .permission(PermissionLists.getBluetoothScanPermission())
                .permission(PermissionLists.getBluetoothConnectPermission())
//                .permission(Permission.ACCESS_COARSE_LOCATION)
//                .permission(Permission.ACCESS_FINE_LOCATION)
//                .permission(Permission.BLUETOOTH_SCAN)
//                .permission(Permission.BLUETOOTH_CONNECT)
                // 设置权限请求拦截器（局部设置）
//                .interceptor(new PermissionInterceptor(getString(R.string.permissionLocation)))
                .interceptor(new PermissionInterceptor())
                .description(new PermissionDescription())
                //.unchecked()
                .request(new OnPermissionCallback() {
                    @Override
                    public void onResult(@NonNull List<IPermission> grantedList, @NonNull List<IPermission> deniedList) {
                        boolean allGranted = deniedList.isEmpty();
                        if (!allGranted) {
                            // 判断请求失败的权限是否被用户勾选了不再询问的选项
                            boolean doNotAskAgain = XXPermissions.isDoNotAskAgainPermissions(BleActivity.this, deniedList);
                            // 在这里处理权限请求失败的逻辑
                            if (doNotAskAgain) {
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(BleActivity.this, deniedList);
                            } else {
                                ToastUtils.showShort(BleActivity.this,"获取权限失败");
                            }
                            return;
                        }
                        // 在这里处理权限请求成功的逻辑
                        if (!BleManager.getInstance().isBlueEnable()) {
                            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                            if (ActivityCompat.checkSelfPermission(BleActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                                return;
//                            }
                            startActivityForResult(intent, 0x01);
                        } else {
                            checkGPS();
                        }
                    }

//                    @Override
//                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
//                        if (!allGranted) {
//                            ToastUtils.showShort(BleActivity.this,"获取部分权限成功，但部分权限未正常授予");
//                            return;
//                        }
//                        if (!BleManager.getInstance().isBlueEnable()) {
//                            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
////                            if (ActivityCompat.checkSelfPermission(BleActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
////                                return;
////                            }
//                            startActivityForResult(intent, 0x01);
//                        } else {
//                            checkGPS();
//                        }
//                    }
//                    @Override
//                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
//                        if (doNotAskAgain) {
//                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                            XXPermissions.startPermissionActivity(BleActivity.this, permissions);
//                        } else {
//                            ToastUtils.showShort(BleActivity.this,"获取权限失败");
//                        }
//                    }
                });
    }

    private void checkGPS() {
        if (!GPSUtils.isGpsOpen(this)) {
            //提示打开GPS
            new CommonDialog(BleActivity.this, new CommonDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {
                        dialog.dismiss();
                        GPSUtils.goGPSSetting1(BleActivity.this);
                    }
                }
            })
                    .setTitle(getString(R.string.locationPermission))
                    .setPositiveButton(getString(R.string.dialog_ok))
                    .setDialogCancelable(false)
                    .show();

        }else {
            startScan();
        }
    }

//    public void startDiscovering() {
//        BleManager.getInstance().scan(new BleScanCallback() {
//            @Override
//            public void onScanStarted(boolean success) {
//                showScanAnim();
//                startCountdown(15);
//                mScanList.clear();
//                mBindList.clear();
//                Set<BluetoothDevice> pairedDevices = BluetoothUtils.getPairedDevices(BleActivity.this);
//                if(pairedDevices != null)
//                    for (BluetoothDevice device:pairedDevices) {
//                        LogUtils.e("onBindViewHolder","已配对蓝牙个数："+pairedDevices.size()+",连接名称："+device.getName()+","+device.getType());
//                        if(device.getType() == BluetoothDevice.DEVICE_TYPE_LE)
//                            mBindList.add(new BleDevice(device));
//                        if(BleManager.getInstance().isConnected(device.getAddress()))
//                            bindAdapter.updateStatus(device.getAddress(),BluetoothStatus.CONNECTED);
//                        else bindAdapter.updateStatus(device.getAddress(),BluetoothStatus.DISCONNECTED);
//                    }
//                notifyDataSetChanged();
//            }
//
//            @Override
//            public void onLeScan(BleDevice bleDevice) {
////                LogUtils.e(TAG,"name:"+bleDevice.getName());
//            }
//
//            @Override
//            public void onScanning(BleDevice bleDevice) {
//                LogUtils.e(TAG,"name2:"+bleDevice.getName()+"scan:"+bleDevice.getScanRecord());
//                    Beacon beacon = new Beacon(bleDevice.getScanRecord());
//                    List<BeaconItem> mItems = beacon.mItems;
//                    String uid = "";
//                    for (int i = 0; i < mItems.size(); i++) {
//                        BeaconItem item = mItems.get(i);
//                        if (item.type == 255) {//过滤我们的设备才显示在列表页
//                            uid = StringUtils.bytesToHex(item.bytes);
//                        }
//                    }
//                    if (uid.startsWith(BleConstant.UID) || uid.toUpperCase().startsWith(BleConstant.UID_SH)) {
//                        LogUtils.e(TAG, "name2:" + bleDevice.getName() + "uid过滤条件:" + uid + ",scan:" + bleDevice.getScanRecord() + "," + new String(bleDevice.getScanRecord()));
//                        mScanList.add(bleDevice);
//                        llFind.setVisibility(View.VISIBLE);
//
//                        int index = isContains(bleDevice);
//                        if (index != -1) {
//                            devices.remove(index);
//                        }
//                        notifyDataSetChanged();
//                    }
//            }
//
//            @Override
//            public void onScanFinished(List<BleDevice> scanResultList) {
//                hideScanAnim();
//                notifyDataSetChanged();
//            }
//        });
//    }

    private int isContains(BleDevice bleDevice, List<BleDevice> devices) {
        for (int i = 0; i < devices.size(); i++) {
            BleDevice item = devices.get(i);
            if(item.getMac().equals(bleDevice.getMac())){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPSUtils.requestGPSCode) {//打开定位返回
            checkGPS();
        }else if(requestCode == 0x01){
//            ToastUtils.showShort(this,"蓝牙开启："+BleManager.getInstance().isBlueEnable());
            if(BleManager.getInstance().isBlueEnable()){
                checkLocalPermission();
            }else {
                ToastUtils.showShort(this,"蓝牙开关未开启！");
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleStatusEvent(BleStatusEvent event){
        bindAdapter.updateStatus(event.getMac(),event.getStatus());
        scanAdapter.updateStatus(event.getMac(),event.getStatus());
        notifyDataSetChanged();
        //08断开后，重新连上需要重新notify
        if (blueToothRssiDialog != null && blueToothRssiDialog.isShowing()) {
            if(event.getBleDevice()!=null)
                blueToothRssiDialog.setBleDevice(event.getBleDevice());
        }

    }

    // 蓝牙页面扫描
    public void startScan() {
        prepairStart();
        addConnectedDevice();

        ScanManager.getInstance().startScan(new com.shenghao.blesdk.callback.BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                // 更新UI
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {

            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                // 更新设备列表
                String name = bleDevice.getName();
                LogUtils.e(TAG,"name2:"+ name +"scan:"+bleDevice.getScanRecord());
                Beacon beacon = new Beacon(bleDevice.getScanRecord());
                List<BeaconItem> mItems = beacon.mItems;
                String uid = "";
                for (int i = 0; i < mItems.size(); i++) {
                    BeaconItem item = mItems.get(i);
                    if (item.type == 255) {//过滤我们的设备才显示在列表页
                        uid = StringUtils.bytesToHex(item.bytes);
                    }
                }
                if(TextUtils.isEmpty(name))
                    name = "";
                if (uid.startsWith(BleConstant.UID) || uid.toUpperCase().startsWith(BleConstant.UID_SH)) {
//                    LogUtils.e(TAG, "name2:" + name + "uid过滤条件:" + uid + ",scan:" + bleDevice.getScanRecord() + "," + new String(bleDevice.getScanRecord()));
                    if(isContains(bleDevice,mScanList) == -1)
                        mScanList.add(new BleDevice(bleDevice.getDevice()));
                    llFind.setVisibility(View.VISIBLE);

                    int index = isContains(bleDevice,mBindList);
                    if (index != -1) {
                        mBindList.remove(index);
                    }
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                // 扫描完成
//                hideScanAnim();
                notifyDataSetChanged();
            }
        });
    }

    private void addConnectedDevice() {
        List<BleDevice> connectedDevice = BleManager.getInstance().getAllConnectedDevice();
        for (int i = 0; i < connectedDevice.size(); i++) {
            BleDevice bleDevice = connectedDevice.get(i);
            BluetoothDevice deviceble = bleDevice.getDevice();
            if(deviceble.getBondState() != BluetoothDevice.BOND_BONDED){
                mScanList.add(new BleDevice(deviceble));
                scanAdapter.updateStatus(bleDevice.getMac(), BluetoothStatus.CONNECTED);
            }

        }
        notifyDataSetChanged();
    }

    private void prepairStart() {
        showScanAnim();
        startCountdown(15);
        mScanList.clear();
        mBindList.clear();
        Set<BluetoothDevice> pairedDevices = BluetoothUtils.getPairedDevices(BleActivity.this);
        if(pairedDevices != null)
            for (BluetoothDevice device:pairedDevices) {
                LogUtils.e("onBindViewHolder","已配对蓝牙个数："+pairedDevices.size()+",连接名称："+device.getName()+","+device.getType());
                if(device.getType() == BluetoothDevice.DEVICE_TYPE_LE)
                    mBindList.add(new BleDevice(device));
                if(BleManager.getInstance().isConnected(device.getAddress()))
                    bindAdapter.updateStatus(device.getAddress(),BluetoothStatus.CONNECTED);
                else bindAdapter.updateStatus(device.getAddress(),BluetoothStatus.DISCONNECTED);
            }
        notifyDataSetChanged();
    }

    public void stopScan() {
        ScanManager.getInstance().stopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
//            BleManager.getInstance().cancelScan();
            stopScan();
            unregisterReceiver(pairingReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (countdownSubscription != null && !countdownSubscription.isDisposed()) {
            countdownSubscription.dispose();
        }
        EventBus.getDefault().unregister(this);
    }

    public void notifyDataSetChanged() {
        bindAdapter.notifyDataSetChanged();
        scanAdapter.notifyDataSetChanged();
    }
}