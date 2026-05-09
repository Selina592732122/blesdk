package com.shenghao.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.base.IPermission;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.shenghao.R;
import com.shenghao.XXPermissions.PermissionDescription;
import com.shenghao.XXPermissions.PermissionInterceptor;
import com.shenghao.okhttp.OkHttpBaseResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.StatusBarUtils;
import com.shenghao.utils.ToastUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class UploadTerminalInfoActivity extends BaseActivity{
    private static final int REQUEST_CODE_TERMINAL_NO_SCAN = 1; //设备号扫码
    private static final int REQUEST_CODE_VIN_SCAN = 2; //车架号扫码
    private static final int REQUEST_CODE_CONTROLLER_SCAN = 3; //控制器扫码

    private EditText terminalNoEt;
    private ImageView terminalNoScanBtn;
    private EditText vinEt;
    private ImageView vinScanBtn;
    private EditText controllerNoEt;
    private ImageView controllerScanBtn;
    private View uploadBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.transparencyBar(this);
        StatusBarUtils.statusBarLightMode(this);
        setContentView(R.layout.activity_upload_terminal_info);
        initViews();
    }

    @Override
    protected void initViews() {
        super.initViews();
        terminalNoEt = findViewById(R.id.terminalNoEt);
        terminalNoScanBtn = findViewById(R.id.terminalNoScanBtn);
        vinEt = findViewById(R.id.vinEt);
        vinScanBtn = findViewById(R.id.vinScanBtn);
        controllerNoEt = findViewById(R.id.controllerNoEt);
        controllerScanBtn = findViewById(R.id.controllerScanBtn);
        uploadBtn = findViewById(R.id.uploadBtn);

        //设置状态栏高度
        View statusBarView = findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(this, statusBarView);

        terminalNoScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTerminalNoScan(REQUEST_CODE_TERMINAL_NO_SCAN);
            }
        });

        vinScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTerminalNoScan(REQUEST_CODE_VIN_SCAN);
            }
        });

        controllerScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTerminalNoScan(REQUEST_CODE_CONTROLLER_SCAN);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTerminalInfo();
            }
        });
    }

    /**
     * 上传信息
     */
    private void uploadTerminalInfo() {
        String terminalNo = terminalNoEt.getText().toString();
        String vin = vinEt.getText().toString();
        String controller = controllerNoEt.getText().toString();

        if (TextUtils.isEmpty(terminalNo)) {
            ToastUtils.showShort(UploadTerminalInfoActivity.this, "请输入设备号");
            return;
        }
        if (TextUtils.isEmpty(vin)) {
            ToastUtils.showShort(UploadTerminalInfoActivity.this, "请输入车架号");
            return;
        }
        if (TextUtils.isEmpty(controller)) {
            ToastUtils.showShort(UploadTerminalInfoActivity.this, "请输入控制器信息");
            return;
        }

        OkHttpPresent.uploadTerminalInfo(terminalNo, vin, controller, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 设备信息上传成功 = " + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) { //上传成功
                    if (baseResp.isSuccess()) {
                        ToastUtils.showShort(UploadTerminalInfoActivity.this, "上传成功");
                        terminalNoEt.setText("");
                        vinEt.setText("");
                        controllerNoEt.setText("");
                        terminalNoEt.requestFocus();
                    } else {
                        ToastUtils.showShort(UploadTerminalInfoActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(UploadTerminalInfoActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 设备信息上传失败 = " + e);
                ToastUtils.showShort(UploadTerminalInfoActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    /**
     * 启动设备号扫描
     */
    private void startTerminalNoScan(int scanRequestCode) {
//        PermissionX.init(UploadTerminalInfoActivity.this)
//                .permissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .request(new RequestCallback() {
//                    @Override
//                    public void onResult(boolean allGranted,
//                                         @NonNull List<String> grantedList,
//                                         @NonNull List<String> deniedList) {
//                        if (allGranted) {
//                            ScanUtil.startScan(UploadTerminalInfoActivity.this, scanRequestCode, null);
//                        }
//                    }
//                });
        XXPermissions.with(this)
                // 申请多个权限
                .permission(PermissionLists.getCameraPermission())
                .permission(PermissionLists.getReadMediaImagesPermission())
//                .permission(Permission.CAMERA)
//                .permission(Permission.READ_MEDIA_IMAGES)
                // 设置权限请求拦截器（局部设置）
//                .interceptor(new PermissionInterceptor(getString(R.string.permissionCamera)))
                .interceptor(new PermissionInterceptor())
                .description(new PermissionDescription())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onResult(@NonNull List<IPermission> grantedList, @NonNull List<IPermission> deniedList) {
                        boolean allGranted = deniedList.isEmpty();
                        if (!allGranted) {
                            // 判断请求失败的权限是否被用户勾选了不再询问的选项
                            boolean doNotAskAgain = XXPermissions.isDoNotAskAgainPermissions(UploadTerminalInfoActivity.this, deniedList);
                            // 在这里处理权限请求失败的逻辑
                            if (doNotAskAgain) {
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(UploadTerminalInfoActivity.this, deniedList);
                            } else {
                                ToastUtils.showShort(UploadTerminalInfoActivity.this,"获取权限失败");
                            }
                            return;
                        }
                        // 在这里处理权限请求成功的逻辑
                        ScanUtil.startScan(UploadTerminalInfoActivity.this, scanRequestCode, null);
                    }

//                    @Override
//                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
//                        if (!allGranted) {
//                            ToastUtils.showShort(UploadTerminalInfoActivity.this,"获取部分权限成功，但部分权限未正常授予");
//                            return;
//                        }
//                        ScanUtil.startScan(UploadTerminalInfoActivity.this, scanRequestCode, null);
//                    }
//
//                    @Override
//                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
//                        if (doNotAskAgain) {
//                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                            XXPermissions.startPermissionActivity(UploadTerminalInfoActivity.this, permissions);
//                        } else {
//                            ToastUtils.showShort(UploadTerminalInfoActivity.this,"获取权限失败");
//                        }
//                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

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
                if (!TextUtils.isEmpty(qrResult)) {
                    if (requestCode == REQUEST_CODE_TERMINAL_NO_SCAN) {
                        terminalNoEt.setText(qrResult);
                        terminalNoEt.setSelection(terminalNoEt.getText().length());
                    } else if (requestCode == REQUEST_CODE_VIN_SCAN) {
                        vinEt.setText(qrResult);
                        vinEt.setSelection(vinEt.getText().length());
                    } else if (requestCode == REQUEST_CODE_CONTROLLER_SCAN) {
                        controllerNoEt.setText(qrResult);
                        controllerNoEt.setSelection(controllerNoEt.getText().length());
                    }
                }
            }
        }
            /*if (errorCode == ScanUtil.ERROR_NO_READ_PERMISSION) {
                // 无文件权限，请求文件权限
            }*/
    }

}
