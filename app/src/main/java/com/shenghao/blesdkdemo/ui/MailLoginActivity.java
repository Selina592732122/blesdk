package com.shenghao.blesdkdemo.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.BindWxBean;
import com.shenghao.blesdkdemo.bean.MailLogin;
import com.shenghao.blesdkdemo.bean.MailLoginResp;
import com.shenghao.blesdkdemo.bean.TerminalListResp;
import com.shenghao.blesdkdemo.bean.UserInfo;
import com.shenghao.blesdkdemo.event.BindTerminalEvent;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.MD5Utils;
import com.shenghao.blesdkdemo.utils.Redirect;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.TerminalUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;
import com.shenghao.blesdkdemo.widget.PasswordEditText;
import com.shenghao.blesdkdemo.wxapi.WeChatLoginHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import io.reactivex.disposables.Disposable;
import okhttp3.Request;
import okhttp3.Response;

public class MailLoginActivity extends BaseActivity implements WeChatLoginHelper.LoginCallback {

    private TextView privacyTv;
    private View loginBtn;
    private EditText userNameEt;
    private PasswordEditText etPwd;
    private ImageView agreementCb;
    private TextView tvWelcome;
    private boolean isAgreementChecked;
    private boolean isShowPwd;  //是否显示密码
    private Disposable countdownSubscription;
    private WeChatLoginHelper weChatLoginHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.transparencyBar(this);
        StatusBarUtils.statusBarDarkMode(this);
        setContentView(R.layout.activity_mail_login);
        initViews();
        initData();
        EventBus.getDefault().register(this);
        weChatLoginHelper = new WeChatLoginHelper(this, this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        privacyTv = findViewById(R.id.privacyTv);
        loginBtn = findViewById(R.id.loginBtn);
        userNameEt = findViewById(R.id.userNameEt);
        etPwd = findViewById(R.id.etPwd);
        agreementCb = findViewById(R.id.agreementCb);
        tvWelcome = findViewById(R.id.tvWelcome);
//        tvWelcome.setText("欢迎登录"+getString(R.string.app_name));
        findViewById(R.id.ivChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAgreementChecked) {
                    ToastUtils.showShort(MailLoginActivity.this, getString(R.string.to_agree_privacy));
                    return;
                }
                weChatLoginHelper.performLogin();
            }
        });
        findViewById(R.id.ivCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MailLoginActivity.this,LoginActivity.class));
                finish();
            }
        });
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        userNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches(emailPattern)) {
                    userNameEt.setError("请输入有效的邮箱地址");
                }
            }
        });
        findViewById(R.id.tvForget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MailLoginActivity.this, FindPasswordActivity.class);
                intent.putExtra("email",userNameEt.getText().toString());
                startActivity(intent);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
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
        String text = getString(R.string.agree_privacy2);

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
                Redirect.startPrivacyUrl(MailLoginActivity.this);
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
        UserInfo userInfo = AppSingleton.getInstance().getUserInfo();
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getEmail())) {
            userNameEt.setText(userInfo.getEmail());
        }
    }

    private void startLogin() {
        String userName = userNameEt.getText().toString();
        String smsCode = etPwd.getText();
        if (TextUtils.isEmpty(userName)) {
            ToastUtils.showShort(MailLoginActivity.this, "请输入邮箱");
            return;
        }

        if (TextUtils.isEmpty(smsCode)) {
            ToastUtils.showShort(MailLoginActivity.this, "请输入密码");
            return;
        }

        if (!isAgreementChecked) {
            ToastUtils.showShort(MailLoginActivity.this, getString(R.string.to_agree_privacy));
            return;
        }

        OkHttpPresent.emailLogin(userName, MD5Utils.md5(smsCode), new OkHttpResultCallBack() {
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
                        AppSingleton.getInstance().setUserName(data.getPhone());
                        getDeviceList();
                    } else {    //登录失败
                        ToastUtils.showShort(MailLoginActivity.this, loginResp.getMsg());
                        hideLoadingDialog();
                    }
                } else {
                    ToastUtils.showShort(MailLoginActivity.this, getString(R.string.request_retry));
                    hideLoadingDialog();
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(MailLoginActivity.this, getString(R.string.request_retry));
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
//                        Redirect.startMainActivity(MailLoginActivity.this);
//                        finish();
                        Intent intent = new Intent(MailLoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
//                        finish();
                    } else {    //设备列表为空，跳转至绑定设备界面
                        Redirect.startBindTerminalActivity(MailLoginActivity.this);
                        finish();
                    }
                } else {    //获取设备列表失败
                    ToastUtils.showShort(MailLoginActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onResponse: 获取设备列表失败 = " + e);
                ToastUtils.showShort(MailLoginActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BindTerminalEvent bindTerminalEvent) {  //设备绑定成功
        Redirect.startMainActivity(MailLoginActivity.this);
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
        ToastUtils.showShort(MailLoginActivity.this, getString(R.string.request_retry));
    }

    @Override
    public void onUnboundUser(String openId, String nickname, String avatar) {
        //未绑定，跳绑定手机号页面
        startActivity(new Intent(MailLoginActivity.this,WxBindPhoneActivity.class)
                .putExtra("openId",openId)
                .putExtra("nickname",nickname)
                .putExtra("avatar",avatar)
        );
    }
    //微信登录相关-----------------------------------------------------------------------------------------------------------------------------------

}
