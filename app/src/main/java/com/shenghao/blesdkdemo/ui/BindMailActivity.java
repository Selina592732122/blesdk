package com.shenghao.blesdkdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;
import com.shenghao.blesdkdemo.widget.VerificationCodeView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

public class BindMailActivity extends BaseActivity {

    private VerificationCodeView verifyView;
    private TextView tvCode;
    private View llStep1,llStep2;
    private EditText etMail;
    private Disposable countdownSubscription;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_mail);
        StatusBarUtils.statusBarLightMode(this);
        StatusBarUtils.setStatusBarColor(this, R.color.white);
        initViews();
    }

    @Override
    protected void initViews() {
//        super.initViews();
        llStep1 = findViewById(R.id.llStep1);
        llStep2 = findViewById(R.id.llStep2);
        verifyView = findViewById(R.id.verifyView);
        etMail = findViewById(R.id.etMail);
        tvCode = findViewById(R.id.tvCode);
        llStep1.setVisibility(View.VISIBLE);
        llStep2.setVisibility(View.GONE);
        verifyView.setOnCompleteListener(new VerificationCodeView.OnCompleteListener() {
            @Override
            public void onFinish(String code) {
                //输完验证码
//                ToastUtils.showShort(BindMailActivity.this,"验证码输入完毕"+code);
                bindEmail(code);
            }
        });
        findViewById(R.id.titleBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        tvCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEmailCode(etMail.getText().toString());
                startCountdown(60);
            }
        });
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
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
                    tvCode.setEnabled(false);
                }else {
                    tvCode.setEnabled(true);
                }
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
                        Intent intent = new Intent(BindMailActivity.this, SettingPasswordActivity.class);
                        intent.putExtra("email",etMail.getText().toString());
                        startActivity(intent);
                    } else {
                        ToastUtils.showShort(BindMailActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(BindMailActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 绑定设备失败 = " + e);
                ToastUtils.showShort(BindMailActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }
    private void getEmailCode(String email){
        OkHttpPresent.getEmailCode(email, new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 短信验证码请求成功 = " + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) {
                        ToastUtils.showShort(BindMailActivity.this, "验证码发送成功");
                        llStep1.setVisibility(View.GONE);
                        llStep2.setVisibility(View.VISIBLE);
                        verifyView.requestFocus();
                        etMail.clearFocus();
                    } else {
                        ToastUtils.showShort(BindMailActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(BindMailActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 短信验证码请求失败 = " + e);
                ToastUtils.showShort(BindMailActivity.this, getString(R.string.request_retry));
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
                        tvCode.setText(String.format("%ds后重新获取", remainingSeconds));
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
    private void goBack(){
        if(llStep2.isShown()){
            llStep1.setVisibility(View.VISIBLE);
            llStep2.setVisibility(View.GONE);
            etMail.requestFocus();
            verifyView.clearFocus();
            verifyView.clear();
        }else {
            finish();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 拦截返回键
            goBack();
            return true; // 返回 true 表示事件已处理，不会继续传递
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownSubscription != null && !countdownSubscription.isDisposed()) {
            countdownSubscription.dispose();
        }
    }
}