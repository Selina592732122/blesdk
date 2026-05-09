package com.shenghao.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shenghao.R;
import com.shenghao.bean.UserInfo;
import com.shenghao.bean.UserInfoResp;
import com.shenghao.okhttp.OkHttpBaseResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.utility.AppSingleton;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.MD5Utils;
import com.shenghao.utils.StatusBarUtils;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.PasswordEditText;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class SettingPasswordActivity extends BaseActivity {
    public final String TAG = this.getClass().getSimpleName();
    private PasswordEditText etPwd,etPwd2;
    private TextView tvConfirm;
    private String email;
    private int type;//type 1说明是绑邮箱那里的设置密码，2说明是忘记密码那里的设置密码
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.transparencyBar(this);
        StatusBarUtils.statusBarLightMode(this);
        setContentView(R.layout.activity_setting_password);
        email = getIntent().getStringExtra("email");
        type = getIntent().getIntExtra("type",1);
        initViews();
//        String pwd = "123456";
//        LogUtils.e(TAG,"加密前:"+pwd+"\n加密后："+MD5Utils.md5(pwd));
    }

    @Override
    protected void initViews() {
        super.initViews();
        etPwd = findViewById(R.id.etPwd);
        etPwd2 = findViewById(R.id.etPwd2);
        tvConfirm = findViewById(R.id.tvConfirm);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type == 1)
                    bindEmailAndPassword(true);
                else bindEmailAndPassword(false);
            }
        });
        etPwd.setTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(etPwd2.getText()) && !TextUtils.isEmpty(etPwd2.getText())){
                    tvConfirm.setEnabled(true);
                }else {
                    tvConfirm.setEnabled(false);
                }
            }
        });
        etPwd2.setTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(etPwd.getText()) && !TextUtils.isEmpty(etPwd.getText())){
                    tvConfirm.setEnabled(true);
                }else {
                    tvConfirm.setEnabled(false);
                }
            }
        });
    }
    private void bindEmailAndPassword(boolean needPhone){
        if(!etPwd.getText().toString().equals(etPwd2.getText().toString())){
            ToastUtils.showShort(this,"密码不一致,请重新输入");
            return;
        }
        String pwd = MD5Utils.md5(etPwd.getText().toString());
        String phone = null;
        if(needPhone)
            phone = AppSingleton.getInstance().getUserName();
        OkHttpPresent.bindEmailAndPassword(email, pwd, phone,new OkHttpResultCallBack() {
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
                        ToastUtils.showShort(SettingPasswordActivity.this,"设置成功");
                        if(type == 2) {//忘记密码，直接finish();
                            Intent intent = new Intent(SettingPasswordActivity.this, MailLoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();
                        }else {
                            //设置密码成功,更新个人信息
                            getUserInfo();
                        }
                    } else {
                        ToastUtils.showShort(SettingPasswordActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(SettingPasswordActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 绑定设备失败 = " + e);
                ToastUtils.showShort(SettingPasswordActivity.this, getString(R.string.request_retry));
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
                    Intent intent = new Intent(SettingPasswordActivity.this, AccountManageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }else {
                    ToastUtils.showShort(SettingPasswordActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 失败 = " + e);
                ToastUtils.showShort(SettingPasswordActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }
}
