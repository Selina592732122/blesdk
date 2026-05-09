package com.shenghao.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shenghao.R;
import com.shenghao.okhttp.OkHttpBaseResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.StatusBarUtils;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.PasswordEditText;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

public class FindPasswordActivity extends BaseActivity {

    private PasswordEditText etPwd,etPwd2;
    private TextView tvConfirm;
    private TextView tvCode;
    private Disposable countdownSubscription;
    private EditText etMail,etCode;
    private String email;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.transparencyBar(this);
        StatusBarUtils.statusBarLightMode(this);
        setContentView(R.layout.activity_find_password);
        email = getIntent().getStringExtra("email");
        initViews();
    }

    @Override
    protected void initViews() {
        super.initViews();
        tvConfirm = findViewById(R.id.tvConfirm);
        tvCode = findViewById(R.id.tvCode);
        etMail = findViewById(R.id.etMail);
        etCode = findViewById(R.id.etCode);
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        etMail.setText(email);
        etMail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches(emailPattern)) {
                    etMail.setError("请输入有效的邮箱地址");
                }
            }
        });
        tvCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
                if (!etMail.getText().toString().matches(emailPattern)){
                    ToastUtils.showShort(FindPasswordActivity.this, "请输入正确的邮箱");
                    return;
                }
                getEmailCode(etMail.getText().toString());
                startCountdown(60);

            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etMail.getText().toString())){
                    ToastUtils.showShort(FindPasswordActivity.this,"请输入邮箱");
                    return;
                }
                if(TextUtils.isEmpty(etCode.getText().toString())){
                    ToastUtils.showShort(FindPasswordActivity.this,"请输入验证码");
                    return;
                }
                bindEmail(etCode.getText().toString());


            }
        });

    }
    private void bindEmail(String code) {
        OkHttpPresent.bindEmail(etMail.getText().toString(), code, new OkHttpResultCallBack() {
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
                        //邮箱验证成功，跳转设置密码
                        //下一步，设置密码
                        Intent intent = new Intent(FindPasswordActivity.this,SettingPasswordActivity.class);
                        intent.putExtra("email",etMail.getText().toString());
                        intent.putExtra("type",2);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtils.showShort(FindPasswordActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(FindPasswordActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 绑定设备失败 = " + e);
                ToastUtils.showShort(FindPasswordActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    private void getEmailCode(String email){
        OkHttpPresent.getEmailCode(email, "2",new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 短信验证码请求成功 = " + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) {
                        ToastUtils.showShort(FindPasswordActivity.this, "验证码发送成功");
                    } else {
                        ToastUtils.showShort(FindPasswordActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(FindPasswordActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 短信验证码请求失败 = " + e);
                ToastUtils.showShort(FindPasswordActivity.this, getString(R.string.request_retry));
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
                        tvCode.setText(String.format("%ds", remainingSeconds));
                        if (remainingSeconds <= 0) {
                            tvCode.setText("重新发送");
                            tvCode.setEnabled(true);
                            if (!countdownSubscription.isDisposed()) {
                                countdownSubscription.dispose();
                            }
                        } else {
                            tvCode.setEnabled(false);
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