package com.shenghao.blesdkdemo.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.TerminalListResp;
import com.shenghao.blesdkdemo.event.BindTerminalEvent;
import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.ui.helper.ScanPermissionHelper;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.ActivityManager;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.Redirect;
import com.shenghao.blesdkdemo.utils.SPUtils;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.TerminalUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;
import com.shenghao.blesdkdemo.widget.CarTypeDialog;
import com.shenghao.blesdkdemo.widget.CommonDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 绑定设备
 */
public class BindTerminalActivity extends BaseActivity {
    private static final int REQUEST_CODE_SCAN = 1; //扫描
    private static final int REQUEST_CODE_SCAN2 = 2; //扫描

    private ImageView scanBtn;
    private EditText terminalNoEt,etVehicle;
    private EditText batteryCountEt;
    private View bindBtn;
    private ImageView scanBtn2;
    private String terminalNo;
    private TextView tvExit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.transparencyBar(this);
        StatusBarUtils.statusBarLightMode(this);
        setContentView(R.layout.activity_bind_terminal);
        // 在这里处理权限请求成功的逻辑
        SPUtils.getInstance().putBoolean(SPUtils.SP_NEED_SHOW_SCAN_PERMISSION_DIALOG, false);
        terminalNo = getIntent().getStringExtra("terminalNo");
        initViews();
    }

    private void showDialog(){
        CommonDialog commonDialog = new CommonDialog(this, new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {  //确定
                    dialog.dismiss();
                    SPUtils.getInstance().putBoolean(SPUtils.SP_HAS_SHOW_PRIVACY, true);
//                        init();//TODO
                } else {
                    finish();
                }
            }
        })
                .setMessageType(CommonDialog.MessageType.INFO)
                .setIKnowButton("我知道了")
                .setTitle("温馨提示")
                .setContent("请您在扫码绑定后拍照保存绑定二维码，防止二维码绑定后再次绑定困难")
                .setContentVisibility(View.VISIBLE)
                .setDialogCancelable(false);
        commonDialog.show();
    }

    @Override
    protected void initViews() {
        super.initViews();
        tvExit = findViewById(R.id.tvExit);
        scanBtn = findViewById(R.id.scanBtn);
        scanBtn2 = findViewById(R.id.scanBtn2);
        terminalNoEt = findViewById(R.id.terminalNoEt);
        etVehicle = findViewById(R.id.etVehicle);
        batteryCountEt = findViewById(R.id.batteryCountEt);
        bindBtn = findViewById(R.id.bindBtn);

        if(!TextUtils.isEmpty(terminalNo)){
            terminalNoEt.setText(terminalNo);
            terminalNoEt.setEnabled(false);
            scanBtn.setEnabled(false);
            batteryCountEt.setText(AppSingleton.getInstance().getBatteryCount()+"");
            batteryCountEt.setEnabled(false);
        }

        //设置状态栏高度
        View statusBarView = findViewById(R.id.statusBarView);
//        StatusBarUtils.setStatusBarHeight(this, statusBarView);

        if(AppSingleton.getInstance().getCurrentTerminal() == null){
//            tvExit.setVisibility(View.VISIBLE);
        }
        tvExit.setOnClickListener(view -> {
            doLogout();
        });
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanPermissionHelper.checkAndRequestScanPermission(BindTerminalActivity.this,REQUEST_CODE_SCAN);
            }
        });
        scanBtn2.setOnClickListener(view -> {
            ScanPermissionHelper.checkAndRequestScanPermission(BindTerminalActivity.this,REQUEST_CODE_SCAN2);
        });

        bindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBind();
            }
        });
    }

    /**
     * 绑定
     */
    private void startBind() {
        String terminalNo = terminalNoEt.getText().toString();
        String vehicleNo = etVehicle.getText().toString();
        int batteryCount = 0;
        if (!TextUtils.isEmpty(batteryCountEt.getText().toString())) {
            batteryCount = Integer.parseInt(batteryCountEt.getText().toString());
        }

        if (TextUtils.isEmpty(terminalNo)) {
            ToastUtils.showShort(BindTerminalActivity.this, "请输入设备号");
            return;
        }

        if (TextUtils.isEmpty(vehicleNo)) {
            ToastUtils.showShort(BindTerminalActivity.this, getString(R.string.enter_vehicle_number));
            return;
        }

        if (batteryCount == 0) {
            ToastUtils.showShort(BindTerminalActivity.this, "请输入电池数量");
            return;
        }
        if(TextUtils.isEmpty(BindTerminalActivity.this.terminalNo))
            showCarTypeDialog(terminalNo,vehicleNo, batteryCount);
        else //更新车架号
            OkHttpPresent.updateVin(terminalNo, vehicleNo, new OkHttpResultCallBack() {
                @Override
                protected void start() {
                    super.start();
                    showLoadingDialog();
                }

                @Override
                protected void onResponse(Response response, String body) throws IOException {
                    LogUtils.e(TAG, "onResponse: 绑定设备成功 = " + body);
                    OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                    if (baseResp != null) { //绑定成功
                        if (baseResp.isSuccess()) {
                            getDeviceList(terminalNo);
                        } else {
                            ToastUtils.showShort(BindTerminalActivity.this, baseResp.getMsg());
                        }
                    } else {
                        ToastUtils.showShort(BindTerminalActivity.this, getString(R.string.request_retry));
                    }
                }

                @Override
                protected void onFailed(Request request, Exception e) {
                    super.onFailed(request, e);
                    LogUtils.e(TAG, "onFailed: 绑定设备失败 = " + e);
                    ToastUtils.showShort(BindTerminalActivity.this, getString(R.string.request_retry));
                }

                @Override
                protected void end() {
                    super.end();
                    hideLoadingDialog();
                }
            });
    }
    private void showCarTypeDialog(String terminalNo,String vehicleNo,  int batteryCount) {
        new CarTypeDialog(this, new CarTypeDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm,int type) {
                dialog.dismiss();
                OkHttpPresent.bindTerminal(terminalNo,vehicleNo, batteryCount, type, new OkHttpResultCallBack() {
                    @Override
                    protected void start() {
                        super.start();
                        showLoadingDialog();
                    }

                    @Override
                    protected void onResponse(Response response, String body) throws IOException {
                        LogUtils.e(TAG, "onResponse: 绑定设备成功 = " + body);
                        OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                        if (baseResp != null) { //绑定成功
                            if (baseResp.isSuccess()) {
                                getDeviceList(terminalNo);
                            } else {
                                ToastUtils.showShort(BindTerminalActivity.this, baseResp.getMsg());
                            }
                        } else {
                            ToastUtils.showShort(BindTerminalActivity.this, getString(R.string.request_retry));
                        }
                    }

                    @Override
                    protected void onFailed(Request request, Exception e) {
                        super.onFailed(request, e);
                        LogUtils.e(TAG, "onFailed: 绑定设备失败 = " + e);
                        ToastUtils.showShort(BindTerminalActivity.this, getString(R.string.request_retry));
                    }

                    @Override
                    protected void end() {
                        super.end();
                        hideLoadingDialog();
                    }
                });
            }
        }).setTitle("车型选择")
                .setDialogCancelable(true)
                .show();
    }
    /**
     * 获取设备列表
     */
    private void getDeviceList(String terminalNo) {
        //获取设备列表
        OkHttpPresent.getDeviceList(new OkHttpResultCallBack() {

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 获取设备列表成功 = " + body);
                TerminalListResp terminalListResp = JsonUtils.parseT(body, TerminalListResp.class);
                if (terminalListResp != null && terminalListResp.isSuccess()) {
                    if (terminalListResp.getData().size() > 0) {    //已绑定设备
                        TerminalUtils.setCurrentTerminal(terminalListResp.getData());
                        EventBus.getDefault().post(new BindTerminalEvent(terminalNo));
                        Redirect.startMainActivity(BindTerminalActivity.this);
                        finish();
                    }
                } else {    //获取设备列表失败
                    ToastUtils.showShort(BindTerminalActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onResponse: 获取设备列表失败 = " + e);
                ToastUtils.showShort(BindTerminalActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

//    /**
//     * 启动扫描
//     */
//    private void startScan() {
////        Manifest.permission.READ_EXTERNAL_STORAGE,
////                Manifest.permission.WRITE_EXTERNAL_STORAGE
//        List<String> permissions = new ArrayList<>();
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
//            permissions.add(Manifest.permission.CAMERA);
//            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
//        }else {
//            permissions.add(Manifest.permission.CAMERA);
//            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
////            permissions.add(Manifest.permission.CAMERA);
////            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
////            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
//
//        XXPermissions.with(BindTerminalActivity.this)
////                .permission(permissions)
//                .permission(PermissionLists.getCameraPermission())
//                .permission(PermissionLists.getReadMediaImagesPermission())
//                // 设置权限请求拦截器（局部设置）
////                .interceptor(new PermissionInterceptor(getString(R.string.permissionCamera)))
//                .interceptor(new PermissionInterceptor())
//                .description(new PermissionDescription())
//                // 设置不触发错误检测机制（局部设置）
//                //.unchecked()
//                .request(new OnPermissionCallback() {
//
//                    @Override
//                    public void onResult(@NonNull List<IPermission> grantedList, @NonNull List<IPermission> deniedList) {
//                        boolean allGranted = deniedList.isEmpty();
//                        if (!allGranted) {
//                            // 判断请求失败的权限是否被用户勾选了不再询问的选项
//                            boolean doNotAskAgain = XXPermissions.isDoNotAskAgainPermissions(BindTerminalActivity.this, deniedList);
//                            // 在这里处理权限请求失败的逻辑
//                            if (doNotAskAgain) {
//                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
////                                XXPermissions.startPermissionActivity(BindTerminalActivity.this, deniedList);
//                            } else {
//                                ToastUtils.showShort(BindTerminalActivity.this,"获取权限失败");
//                            }
//                            return;
//                        }
//                        // 在这里处理权限请求成功的逻辑
//                        SPUtils.getInstance().putBoolean(SPUtils.SP_NEED_SHOW_SCAN_PERMISSION_DIALOG, false);
//                        ScanUtil.startScan(BindTerminalActivity.this, REQUEST_CODE_SCAN, null);
//                    }
//
////                    @Override
////                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
////                        if (!allGranted) {
////                            ToastUtils.showShort(BindTerminalActivity.this,"获取部分权限成功，但部分权限未正常授予");
////                            return;
////                        }
////                        SPUtils.getInstance().putBoolean(SPUtils.SP_NEED_SHOW_SCAN_PERMISSION_DIALOG, false);
////                        ScanUtil.startScan(BindTerminalActivity.this, REQUEST_CODE_SCAN, null);
////                    }
////
////                    @Override
////                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
////                        if (doNotAskAgain) {
////                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
////                            XXPermissions.startPermissionActivity(BindTerminalActivity.this, permissions);
////                        } else {
////                            ToastUtils.showShort(BindTerminalActivity.this,"获取权限失败");
////                        }
////                    }
//                });
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_SCAN || requestCode == REQUEST_CODE_SCAN2) {
            // 导入图片扫描返回结果
            int errorCode = data.getIntExtra(ScanUtil.RESULT_CODE, ScanUtil.SUCCESS);
            if (errorCode == ScanUtil.SUCCESS) {
                HmsScan hmsScan = data.getParcelableExtra(ScanUtil.RESULT);
                if (hmsScan != null) {
                    // 展示扫码结果
                    String qrResult = hmsScan.getOriginalValue();
                    if (TextUtils.isEmpty(qrResult)) {
                        qrResult = hmsScan.getShowResult();
                    }
                    if (!TextUtils.isEmpty(qrResult))
                        if(qrResult.contains("&")){
                            //分享接口
                            qrCodeInvite(qrResult);
                        }else {
                            if(requestCode == REQUEST_CODE_SCAN){
                                terminalNoEt.setText(qrResult);
                                terminalNoEt.setSelection(terminalNoEt.getText().length());
                                showDialog();
                            }else {
                                etVehicle.setText(qrResult);
                                etVehicle.setSelection(etVehicle.getText().length());
                            }

                        }
                }
            }
            /*if (errorCode == ScanUtil.ERROR_NO_READ_PERMISSION) {
                // 无文件权限，请求文件权限
            }*/
        }
    }

    private void qrCodeInvite(String qrResult) {
        String[] split = qrResult.split("&");
        if(split.length < 3)
            return;
        OkHttpPresent.qrCodeInvite(split[0],split[1],split[2],new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "登录成功: " + body);
                OkHttpBaseResp loginResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (loginResp != null) {
                    if (loginResp.isSuccess()) {
                        getDeviceList(qrResult);
                    } else {    //登录失败
                        ToastUtils.showShort(BindTerminalActivity.this, loginResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(BindTerminalActivity.this, getString(R.string.request_retry));
                }


                hideLoadingDialog();
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(BindTerminalActivity.this, getString(R.string.request_retry));
                hideLoadingDialog();
            }

        });
    }
    private void doLogout() {
        AppSingleton.getInstance().clearAllData();
        ActivityManager.finishAllActivity();
        Redirect.startLoginActivity(BindTerminalActivity.this);
    }
}
