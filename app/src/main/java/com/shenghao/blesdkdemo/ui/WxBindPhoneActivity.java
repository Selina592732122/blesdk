package com.shenghao.blesdkdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.LoginResp;
import com.shenghao.blesdkdemo.bean.TerminalListResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.Redirect;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.TerminalUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

public class WxBindPhoneActivity extends BaseActivity {

    private TextView tvGet;
    private EditText etPhone,etCode;
    private Disposable countdownSubscription;
    private String phone;
    private TextView btnLogin;
    private String openId,avatar,nickName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxbind_phone);
        StatusBarUtils.statusBarLightMode(this);
        StatusBarUtils.setStatusBarColor(this, R.color.white);
        openId = getIntent().getStringExtra("openId");
        nickName = getIntent().getStringExtra("nickname");
        avatar = getIntent().getStringExtra("avatar");
        initViews();
    }

    @Override
    protected void initViews() {
        super.initViews();
        etPhone = findViewById(R.id.etPhone);
        etCode = findViewById(R.id.etCode);
        tvGet = findViewById(R.id.tvGet);
        btnLogin = findViewById(R.id.tvLogin);

        tvGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = etPhone.getText().toString();
                if(TextUtils.isEmpty(phone))
                    return;
                if(phone.length() != 11)
                    return;
                getBindCode(phone);
                startCountdown(60);
            }
        });
        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    btnLogin.setEnabled(true);
                }else {
                    btnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnLogin.setOnClickListener(v -> {
            phone = etPhone.getText().toString();
            if(TextUtils.isEmpty(phone))
                return;
            wxLogin(phone,etCode.getText().toString(),openId,avatar,nickName);
        });
    }
    private void wxLogin(String phone,String code,String openId,String avatar,String nickName) {
        OkHttpPresent.wxLogin(phone,code,openId,avatar,nickName, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 绑定设备成功 = " + body);
                LoginResp baseResp = JsonUtils.parseT(body, LoginResp.class);
                if (baseResp != null) { //绑定成功
                    if (baseResp.isSuccess()) {
                        AppSingleton.getInstance().setToken(baseResp.getData());
                        AppSingleton.getInstance().setUserName(phone);
                        getDeviceList();
                    } else {
                        ToastUtils.showShort(WxBindPhoneActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(WxBindPhoneActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 绑定设备失败 = " + e);
                ToastUtils.showShort(WxBindPhoneActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }
    /**
     * 获取设备列表
     */
    private void getDeviceList() {
        //获取设备列表
        OkHttpPresent.getDeviceList(new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 获取设备列表成功 = " + body);
                TerminalListResp terminalListResp = JsonUtils.parseT(body, TerminalListResp.class);
                if (terminalListResp != null && terminalListResp.isSuccess()) {
                    if (terminalListResp.getData().size() > 0) {    //已绑定设备
                        TerminalUtils.setCurrentTerminal(terminalListResp.getData());
                        Intent intent = new Intent(WxBindPhoneActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
//                        Redirect.startMainActivity(WxBindPhoneActivity.this);
//                        finish();
                    } else {    //设备列表为空，跳转至绑定设备界面
                        Redirect.startBindTerminalActivity(WxBindPhoneActivity.this);
                        finish();
                    }
                } else {    //获取设备列表失败
                    ToastUtils.showShort(WxBindPhoneActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onResponse: 获取设备列表失败 = " + e);
                ToastUtils.showShort(WxBindPhoneActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }
    private void getBindCode(String phone){
        OkHttpPresent.getBindCode(phone, new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 短信验证码请求成功 = " + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) {
                        ToastUtils.showShort(WxBindPhoneActivity.this, "验证码发送成功");
                    } else {
                        ToastUtils.showShort(WxBindPhoneActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(WxBindPhoneActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 短信验证码请求失败 = " + e);
                ToastUtils.showShort(WxBindPhoneActivity.this, getString(R.string.request_retry));
            }
        });
    }

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
                        tvGet.setText(String.format("%ds后重新获取", remainingSeconds));
                        if (remainingSeconds <= 0) {
                            tvGet.setText("重新发送");
                            tvGet.setEnabled(true);
                            if (!countdownSubscription.isDisposed()) {
                                countdownSubscription.dispose();
                            }
                        } else {
                            tvGet.setEnabled(false);
                        }
                    }
                });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownSubscription != null && !countdownSubscription.isDisposed()) {
            countdownSubscription.dispose();
        }
    }
}
