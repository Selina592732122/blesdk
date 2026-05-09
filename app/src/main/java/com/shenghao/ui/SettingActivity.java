package com.shenghao.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.TypeReference;
import com.shenghao.R;
import com.shenghao.bean.BaseHttpResp;
import com.shenghao.bean.UpgradeVersionResp;
import com.shenghao.constant.Const;
import com.shenghao.okhttp.OkHttpBaseResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.utility.AppSingleton;
import com.shenghao.utils.ActivityManager;
import com.shenghao.utils.AppUtils;
import com.shenghao.utils.CacheManager;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.Redirect;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.CommonDialog;
import com.shenghao.widget.IosBottomSheetDialog;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class SettingActivity extends BaseActivity {
    private TextView versionNameTv;
    private TextView tvSize;
    private TextView logoutBtn;
    private LinearLayout needUpgradeLayout;
    private LinearLayout uploadTerminalInfoLayout;
    private LinearLayout cancellationLayout;
    private LinearLayout feedbackLayout;
    private LinearLayout privacyLayout;
    private LinearLayout llCash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initViews();
        getUserRole();
    }

    @Override
    protected void initViews() {
        super.initViews();
        versionNameTv = findViewById(R.id.versionNameTv);
        tvSize = findViewById(R.id.tvSize);
        logoutBtn = findViewById(R.id.logoutBtn);
        needUpgradeLayout = findViewById(R.id.needUpgradeLayout);
        uploadTerminalInfoLayout = findViewById(R.id.uploadTerminalInfoLayout);
        cancellationLayout = findViewById(R.id.cancellationLayout);
        feedbackLayout = findViewById(R.id.feedbackLayout);
        privacyLayout = findViewById(R.id.privacyLayout);
        findViewById(R.id.llCash).setOnClickListener(view -> {
//            showClearOptions();
            CacheManager.clearAllCache(this);
            Toast.makeText(this, "缓存清除成功", Toast.LENGTH_SHORT).show();
        });

        versionNameTv.setText(AppUtils.getAppVersionName(this));
        try {
            tvSize.setText(CacheManager.getTotalCacheSize(this));
        }catch (Exception e){
            e.printStackTrace();
        }
        findViewById(R.id.llAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AccountManageActivity.class);
                startActivity(intent);
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogout();
            }
        });

        needUpgradeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO superLT 下载apk文件
                checkUpgradeVersion();
            }
        });

        uploadTerminalInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Redirect.startUploadTerminalInfoActivity(SettingActivity.this);
            }
        });

        // 隐私政策
        privacyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Redirect.startPrivacyUrl(SettingActivity.this);
            }
        });

        feedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFeedbackTel();
            }
        });

        cancellationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CommonDialog(SettingActivity.this, new CommonDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {  //确定
                            dialog.dismiss();
                            doCancellation();
                        }
                    }
                })
                        .setTitle("确定注销账号吗？")
                        .show();
            }
        });
    }

    // 提供两种选择给用户
    public void showClearOptions() {
        String[] options = {"仅清除缓存（安全）", "清除所有数据（包括账号设置）"};

        new AlertDialog.Builder(this)
                .setTitle("选择清除方式")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // 仅清除缓存
                            CacheManager.clearAllCache(this);
                            Toast.makeText(this, "缓存已清除", Toast.LENGTH_SHORT).show();
                            try {
                                tvSize.setText(CacheManager.getTotalCacheSize(this));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            break;
                        case 1: // 清除所有数据
                            CacheManager.clearAllData(this);
                            Toast.makeText(this, "所有数据已清除，请重新登录", Toast.LENGTH_SHORT).show();
                            ActivityManager.finishAllActivity();
                            Redirect.startSplashActivity(this);
                            break;
                    }
                })
                .show();
    }

    /**
     * 获取用户角色
     */
    private void getUserRole() {
        OkHttpPresent.getUserRole(new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 用户角色获取成功 = " + body);
                BaseHttpResp<String> resp = JsonUtils.parseObject(body, new TypeReference<BaseHttpResp<String>>() {
                });
                if (resp!=null&&resp.isSuccess()){
                    String userRole = resp.getData();
                    if (TextUtils.equals(Const.USER_ROLE_MANUFACTURER, userRole)) { //车辆生产厂家
                        uploadTerminalInfoLayout.setVisibility(View.VISIBLE);
                    } else if (TextUtils.equals(Const.USER_ROLE_GPS_DEVICE_MANAGER, userRole)) {    //GPS设备管理员
                        uploadTerminalInfoLayout.setVisibility(View.GONE);
                    } else {    //普通用户
                        uploadTerminalInfoLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 用户角色获取失败 = " + e);
            }
        });
    }

    /**
     * 检查版本更新
     */
    private void checkUpgradeVersion() {
        OkHttpPresent.getUpgradeVersion(new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 版本更新成功 = " + body);
                UpgradeVersionResp versionResp = JsonUtils.parseT(body, UpgradeVersionResp.class);
                if (versionResp != null && versionResp.isSuccess()) {
                    long localVersion = AppUtils.getAppVersionCode(SettingActivity.this);
                    if (localVersion != 0 && versionResp.getData().getVersionCode() > localVersion) {   //有新版本
                        new CommonDialog(SettingActivity.this, new CommonDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {  //确定
                                    openUrlInBrowser(versionResp.getData().getUrl());
                                    dialog.dismiss();
                                }
                            }
                        })
                                .setTitle("发现新版本 v" + versionResp.getData().getVersion())
                                .setPositiveButton("立即升级")
                                .show();
                    } else {
                        ToastUtils.showShort(SettingActivity.this, "已是最新版本");
                    }
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 获取新版本失败 = " + e);
            }
        });
    }

    public void openUrlInBrowser(String url) {
        Intent intent = new Intent();
        Uri uri = Uri.parse(url);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);
    }

    /**
     * 获取用户反馈客服电话
     */
    private void getFeedbackTel() {
        OkHttpPresent.getFeedbackTel(new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                BaseHttpResp<String> baseResp = JsonUtils.parseObject(body, new TypeReference<BaseHttpResp<String>>() {
                });
                if (baseResp != null) { //成功
                    if (baseResp.isSuccess()) {
                        showFeedbackDialog(baseResp.getData());
                    } else {
                        ToastUtils.showShort(SettingActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(SettingActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                ToastUtils.showShort(SettingActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    /**
     * 显示用户反馈弹窗
     */
    private void showFeedbackDialog(String telNo) {
        IosBottomSheetDialog.Builder builder = new IosBottomSheetDialog.Builder(this);
        builder.addTitleView("平台客服", 0);
        builder.addItemView(telNo, 0, false, new IosBottomSheetDialog.OnItemClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + telNo));
                startActivity(intent);
            }
        });
        builder.build().show();
    }

    /**
     * 账号注销
     */
    private void doCancellation() {
        OkHttpPresent.cancellation(new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) { //注销成功
                    if (baseResp.isSuccess()) {
                        doLogout();
                    } else {
                        ToastUtils.showShort(SettingActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(SettingActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                ToastUtils.showShort(SettingActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    private void doLogout() {
        AppSingleton.getInstance().clearAllData();
        ActivityManager.finishAllActivity();
        startActivity(new Intent(this,SplashActivity.class));
        finish();
    }
}
