package com.shenghao.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;


import com.shenghao.R;
import com.shenghao.bean.BindWxBean;
import com.shenghao.bean.LoginResp;
import com.shenghao.bean.LoginWxResp;
import com.shenghao.bean.MailLogin;
import com.shenghao.bean.MailLoginResp;
import com.shenghao.bean.TerminalListResp;
import com.shenghao.event.BindTerminalEvent;
import com.shenghao.okhttp.OkHttpBaseResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.utility.AppSingleton;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.MD5Utils;
import com.shenghao.utils.Redirect;
import com.shenghao.utils.StatusBarUtils;
import com.shenghao.utils.TerminalUtils;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.PasswordEditText;
import com.shenghao.wxapi.Constants;
import com.shenghao.wxapi.WeChatLoginHelper;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends BaseActivity implements WeChatLoginHelper.LoginCallback {

    private TextView getSmsCodeBtn;
    private TextView privacyTv;
    private View loginBtn;
    private EditText userNameEt;
    private EditText smsCodeEt;
    private ImageView agreementCb;

    private boolean isAgreementChecked;
    private boolean isShowPwd;  //是否显示密码
    private Disposable countdownSubscription;
    private WeChatLoginHelper weChatLoginHelper;
    private LinearLayout llPwd,llCode;
    private RadioGroup rgWay;
    private PasswordEditText etPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.transparencyBar(this);
        StatusBarUtils.statusBarDarkMode(this);
        setContentView(R.layout.activity_login);
        initViews();
        initData();
        EventBus.getDefault().register(this);
        weChatLoginHelper = new WeChatLoginHelper(this, this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        getSmsCodeBtn = findViewById(R.id.getSmsCodeBtn);
        privacyTv = findViewById(R.id.privacyTv);
        loginBtn = findViewById(R.id.loginBtn);
        userNameEt = findViewById(R.id.userNameEt);
        etPwd = findViewById(R.id.etPwd);
        smsCodeEt = findViewById(R.id.smsCodeEt);
        agreementCb = findViewById(R.id.agreementCb);
        llPwd = findViewById(R.id.llPwd);
        llCode = findViewById(R.id.llCode);
        rgWay = findViewById(R.id.rgWay);
        rgWay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(R.id.rbCode == i){
                    llCode.setVisibility(View.VISIBLE);
                    llPwd.setVisibility(View.GONE);
                }else {
                    llCode.setVisibility(View.GONE);
                    llPwd.setVisibility(View.VISIBLE);
                }
            }
        });
        findViewById(R.id.tvForget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
//                intent.putExtra("email",userNameEt.getText().toString());
                startActivity(intent);
            }
        });
        findViewById(R.id.ivMail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,MailLoginActivity.class));
                finish();
            }
        });
        findViewById(R.id.ivChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAgreementChecked) {
                    ToastUtils.showShort(LoginActivity.this, getString(R.string.to_agree_privacy));
                    return;
                }
                weChatLoginHelper.performLogin();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rgWay.getCheckedRadioButtonId() == R.id.rbCode)
                    startLogin();
                else startLoginByPwd();
            }
        });

        //获取短信验证码
        getSmsCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEt.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    ToastUtils.showShort(LoginActivity.this, "请输入手机号");
                    return;
                }
                startCountdown(60);
                OkHttpPresent.getSmsAuthCode(userName, new OkHttpResultCallBack() {
                    @Override
                    protected void onResponse(Response response, String body) throws IOException {
                        LogUtils.e(TAG, "onResponse: 短信验证码请求成功 = " + body);
                        OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                        if (baseResp != null) {
                            if (baseResp.isSuccess()) {
                                ToastUtils.showShort(LoginActivity.this, "验证码发送成功");
                            } else {
                                ToastUtils.showShort(LoginActivity.this, baseResp.getMsg());
                            }
                        } else {
                            ToastUtils.showShort(LoginActivity.this, getString(R.string.request_retry));
                        }
                    }

                    @Override
                    protected void onFailed(Request request, Exception e) {
                        super.onFailed(request, e);
                        LogUtils.e(TAG, "onFailed: 短信验证码请求失败 = " + e);
                        ToastUtils.showShort(LoginActivity.this, getString(R.string.request_retry));
                    }
                });
            }
        });

        agreementCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAgreementChecked) {
                    isAgreementChecked = false;
                    agreementCb.setImageDrawable(getDrawable(R.drawable.ic_check_off));
                } else {
                    isAgreementChecked = true;
                    agreementCb.setImageDrawable(getDrawable(R.drawable.ic_check_on));
                }
            }
        });

        initPrivacy();
    }



    /**
     * 初始化隐私协议
     */
    private void initPrivacy() {
        // 创建文本
        String text = getString(R.string.agree_privacy);

        // 创建SpannableString
        SpannableString spannableString = new SpannableString(text);

        // 定义可点击区域的开始和结束位置
        int start = text.indexOf("《");
        int end = text.indexOf("》") + 1;

        // 设置文本颜色
        ForegroundColorSpan blueColorSpan = new ForegroundColorSpan(Color.parseColor("#99FFFFFF"));
        spannableString.setSpan(blueColorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 定义Click事件
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // 跳转到隐私协议页面
                Redirect.startPrivacyUrl(LoginActivity.this);
            }

            // 移除默认下划线
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#FFffff"));
                ds.setUnderlineText(false);
            }
        };

        // 添加ClickableSpan
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 设置到TextView
        privacyTv.setText(spannableString);
        // 设置TextView可点击并启用链接
        privacyTv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initData() {
        String userName = AppSingleton.getInstance().getUserName();
        if (!TextUtils.isEmpty(userName) && userName.startsWith("1")) {
            userNameEt.setText(userName);
        }
    }

    private void startLogin() {
        String userName = userNameEt.getText().toString();
        String smsCode = smsCodeEt.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            ToastUtils.showShort(LoginActivity.this, "请输入手机号");
            return;
        }

        if (TextUtils.isEmpty(smsCode)) {
            ToastUtils.showShort(LoginActivity.this, "请输入验证码");
            return;
        }

        if (!isAgreementChecked) {
            ToastUtils.showShort(LoginActivity.this, getString(R.string.to_agree_privacy));
            return;
        }

        OkHttpPresent.loginSystem(userName, smsCode, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "登录成功: " + body);
                LoginResp loginResp = JsonUtils.parseT(body, LoginResp.class);
                if (loginResp != null) {
                    if (loginResp.isSuccess() && !TextUtils.isEmpty(loginResp.getData())) {
                        AppSingleton.getInstance().setToken(loginResp.getData());
                        AppSingleton.getInstance().setUserName(userName);
                        getDeviceList();
                    } else {    //登录失败
                        ToastUtils.showShort(LoginActivity.this, loginResp.getMsg());
                        hideLoadingDialog();
                    }
                } else {
                    ToastUtils.showShort(LoginActivity.this, getString(R.string.request_retry));
                    hideLoadingDialog();
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(LoginActivity.this, getString(R.string.request_retry));
                hideLoadingDialog();
            }

        });
    }
    private void startLoginByPwd() {
        String userName = userNameEt.getText().toString();
        String pwd = etPwd.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            ToastUtils.showShort(LoginActivity.this, "请输入手机号");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showShort(LoginActivity.this, "请输入密码");
            return;
        }

        if (!isAgreementChecked) {
            ToastUtils.showShort(LoginActivity.this, getString(R.string.to_agree_privacy));
            return;
        }

        OkHttpPresent.phoneLogin(userName, MD5Utils.md5(pwd), new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "登录成功: " + body);
                MailLoginResp loginResp = JsonUtils.parseT(body, MailLoginResp.class);
                if (loginResp != null) {
                    if (loginResp.isSuccess() && loginResp.getData() !=null) {
                        MailLogin data = loginResp.getData();
                        AppSingleton.getInstance().setToken(data.getToken());
                        AppSingleton.getInstance().setUserName(userName);
                        getDeviceList();
                    } else {    //登录失败
                        ToastUtils.showShort(LoginActivity.this, loginResp.getMsg());
                        hideLoadingDialog();
                    }
                } else {
                    ToastUtils.showShort(LoginActivity.this, getString(R.string.request_retry));
                    hideLoadingDialog();
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(LoginActivity.this, getString(R.string.request_retry));
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
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
//                        Redirect.startMainActivity(LoginActivity.this);
//                        finish();
                    } else {    //设备列表为空，跳转至绑定设备界面
//                        Redirect.startBindTerminalActivity(LoginActivity.this);
//                        finish();
                        Intent intent = new Intent(LoginActivity.this, BindTerminalActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } else {    //获取设备列表失败
                    ToastUtils.showShort(LoginActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onResponse: 获取设备列表失败 = " + e);
                ToastUtils.showShort(LoginActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
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
                        getSmsCodeBtn.setText(String.format("%ds", remainingSeconds));
                        getSmsCodeBtn.setTextColor(Color.parseColor("#EDC02B"));
                        if (remainingSeconds <= 0) {
                            getSmsCodeBtn.setText("重新发送");
                            getSmsCodeBtn.setEnabled(true);
                            getSmsCodeBtn.setTextColor(Color.parseColor("#EDC02B"));
                            if (!countdownSubscription.isDisposed()) {
                                countdownSubscription.dispose();
                            }
                        } else {
                            getSmsCodeBtn.setEnabled(false);
                        }
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BindTerminalEvent bindTerminalEvent) {  //设备绑定成功
        Redirect.startMainActivity(LoginActivity.this);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        weChatLoginHelper.release();
        EventBus.getDefault().unregister(this);
        if (countdownSubscription != null && !countdownSubscription.isDisposed()) {
            countdownSubscription.dispose();
        }
    }
    //微信登录相关-----------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onLoginSuccess(BindWxBean userInfo) {
        //已经绑定过微信
        getDeviceList();
    }

    @Override
    public void onLoginFailed(String error) {
        //网络请求错误
        ToastUtils.showShort(LoginActivity.this, getString(R.string.request_retry));
    }

    @Override
    public void onUnboundUser(String openId, String nickname, String avatar) {
        //未绑定，跳绑定手机号页面
        startActivity(new Intent(LoginActivity.this,WxBindPhoneActivity.class)
                .putExtra("openId",openId)
                .putExtra("nickname",nickname)
                .putExtra("avatar",avatar)
        );
    }
    //微信登录相关-----------------------------------------------------------------------------------------------------------------------------------

}
