package com.shenghao.blesdkdemo.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.UserInfo;
import com.shenghao.blesdkdemo.bean.UserInfoResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class SosActivity extends BaseActivity {
    public final String TAG = this.getClass().getSimpleName();
    private EditText etName,etPhone,etRelationship;
    private TextView tvHint;
    private Button btnOk;
    private boolean isEditable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        StatusBarUtils.statusBarLightMode(this);
        StatusBarUtils.setStatusBarColor(this,R.color.white);
        initViews();
        getUserInfo();
    }

    @Override
    protected void initViews() {
        super.initViews();
        tvHint = findViewById(R.id.tvHint);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etTel);
        etRelationship = findViewById(R.id.etRelationship);
        btnOk = findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditable)
                    updateUser(etName.getText().toString(),etPhone.getText().toString(),etRelationship.getText().toString());
                else {
                    updateEnable(true);
                    btnOk.setText("保存");
                }
            }
        });
        UserInfo userInfo = AppSingleton.getInstance().getUserInfo();
        if(TextUtils.isEmpty(userInfo.getEmergencyContactPhone())){
            tvHint.setText("暂无紧急联系人，请填写");
            btnOk.setText("保存");
            updateEnable(true);
        }else {
            tvHint.setText("当前紧急联系人");
            btnOk.setText("编辑");
            updateEnable(false);
            etName.setText(userInfo.getEmergencyContact());
            etPhone.setText(userInfo.getEmergencyContactPhone());
            etRelationship.setText(userInfo.getEmergencyContactRelation());
        }
    }

    private void updateUser(String name,String phone,String relationship) {
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShort(this, "请填写姓名");
            return;
        }
        if (TextUtils.isEmpty(relationship)) {
            ToastUtils.showShort(this, "请填写关系");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showShort(this, "请填写手机号");
            return;
        }
        OkHttpPresent.updateUser(name, phone,relationship, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 上传成功 = " + body);
                try {
                    OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                    if (baseResp != null) { //绑定成功
                        if (baseResp.isSuccess()) {
                            ToastUtils.showShort(SosActivity.this,"保存成功");
                            getUserInfo();
                            finish();
                        } else {
                            // 显示服务器返回的具体错误信息
                            String errorMsg = TextUtils.isEmpty(baseResp.getMsg()) ?
                                    getString(R.string.request_retry) : baseResp.getMsg();
                            ToastUtils.showShort(SosActivity.this, errorMsg);
                        }
                    } else {
                        ToastUtils.showShort(SosActivity.this, getString(R.string.request_retry));
                    }
                } catch (Exception e) {
                    ToastUtils.showShort(SosActivity.this, getString(R.string.request_retry));
                }

            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 上传失败 = " + e);
                ToastUtils.showShort(SosActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }
    private void getUserInfo(){
        OkHttpPresent.getUserInfo(new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 成功 = " + body);
                UserInfoResp userInfoResp = JsonUtils.parseT(body, UserInfoResp.class);
                if (userInfoResp != null && userInfoResp.isSuccess()) {
                    UserInfo userInfo = userInfoResp.getData();
                    AppSingleton.getInstance().setUserInfo(userInfo);
                }else {
                    ToastUtils.showShort(SosActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 失败 = " + e);
                ToastUtils.showShort(SosActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    private void updateEnable(boolean b) {
        isEditable = b;
        etName.setEnabled(b);
        etPhone.setEnabled(b);
        etRelationship.setEnabled(b);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
