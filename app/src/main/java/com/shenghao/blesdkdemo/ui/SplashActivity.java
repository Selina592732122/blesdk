package com.shenghao.blesdkdemo.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;


import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.ShengHaoApp;
import com.shenghao.blesdkdemo.bean.LoginResp;
import com.shenghao.blesdkdemo.bean.TerminalListResp;
import com.shenghao.blesdkdemo.event.BindTerminalEvent;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.Redirect;
import com.shenghao.blesdkdemo.utils.SPUtils;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.TerminalUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;
import com.shenghao.blesdkdemo.widget.CommonDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import okhttp3.Request;
import okhttp3.Response;

public class SplashActivity extends BaseActivity {
    private LinearLayout llBtns;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SplashActivity2", "onCreate");
        StatusBarUtils.transparencyBar(this);
        StatusBarUtils.statusBarLightMode(this);
        setContentView(R.layout.activity_splash);
        initViews();
        showPrivacyDialog();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void initViews() {
        super.initViews();
        llBtns = findViewById(R.id.llBtns);
//        llBtns.setVisibility(View.GONE);
        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                init();
                Redirect.startLoginActivity(SplashActivity.this);
            }
        });
        findViewById(R.id.btnUnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //虚拟体验
                startVirtualLogin();
            }
        });
//        Observable.timer(3, TimeUnit.SECONDS)
//        .subscribe(new Consumer<Long>() {
//            @Override
//            public void accept(Long aLong) throws Exception {
//                init();
//            }
//        });
//        // 播放GIF动画
//        ImageView splashGifIv = findViewById(R.id.splashGifIv);
//        RequestOptions options = new RequestOptions()
//                .centerCrop();
//
//        Glide.with(this)
//                .asGif()
//                .apply(options)
//                .load(R.drawable.gif_splash_opening)
//                .into(splashGifIv);
    }

    private void startVirtualLogin() {
        OkHttpPresent.loginSystem(AppSingleton.virtualTel, "0106", new OkHttpResultCallBack() {
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
                        AppSingleton.getInstance().setUserName(AppSingleton.virtualTel);
                        getDeviceList();
                    } else {    //登录失败
                        ToastUtils.showShort(SplashActivity.this, loginResp.getMsg());
                        hideLoadingDialog();
                    }
                } else {
                    ToastUtils.showShort(SplashActivity.this, getString(R.string.request_retry));
                    hideLoadingDialog();
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(SplashActivity.this, getString(R.string.request_retry));
                hideLoadingDialog();
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    @SuppressLint("CheckResult")
    private void init() {
        // 调整点二：隐私政策授权获取成功后调用
//        JCollectionAuth.setAuth(this, true); //如初始化被拦截过，将重试初始化过程
        if(AppSingleton.getInstance().getUserName().equals(AppSingleton.virtualTel)) {//虚拟账号,清空token
            AppSingleton.getInstance().setToken("");
            AppSingleton.getInstance().setUserName("");
        }
        String userName = SPUtils.getInstance().getString(SPUtils.SP_USER_NAME);
        String token = SPUtils.getInstance().getString(SPUtils.SP_TOKEN);

        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(token)) {
            // 账号token存在，请求有效token
            OkHttpPresent.getValidToken(new OkHttpResultCallBack() {
                @Override
                protected void onResponse(Response response, String body) throws IOException {
                    LogUtils.e(TAG, "获取token成功: " + body);
                    LoginResp loginResp = JsonUtils.parseT(body, LoginResp.class);
                    if (loginResp != null) {
                        if (loginResp.isSuccess() && !TextUtils.isEmpty(loginResp.getData())) {
                            AppSingleton.getInstance().setToken(loginResp.getData());
                            AppSingleton.getInstance().setUserName(userName);
                            getDeviceList();    //获取设备列表
                        } else {
                            Redirect.startLoginActivity(SplashActivity.this);
                            finish();
                        }
                    }
                }

                @Override
                protected void onFailed(Request request, Exception e) {
                    super.onFailed(request, e);
                    LogUtils.e(TAG, "获取token成功: " + e);
                    Redirect.startLoginActivity(SplashActivity.this);
                    finish();
                }
            });
        } else {
            llBtns.setVisibility(View.VISIBLE);
            // 账号token不存在，延迟1s跳转到登录页TODO
//            Observable.timer(3, TimeUnit.SECONDS)
//                    .subscribe(new Consumer<Long>() {
//                        @Override
//                        public void accept(Long aLong) throws Exception {
//                            Redirect.startLoginActivity(SplashActivity.this);
//                            finish();
//                        }
//                    });
        }
    }

    /**
     * 弹出隐私政策弹窗
     */
    private void showPrivacyDialog() {
        boolean hasShow = SPUtils.getInstance().getBoolean(SPUtils.SP_HAS_SHOW_PRIVACY, false);
        if (!hasShow) {
            // 创建文本
//            String text = "欢迎使用" + getString(R.string.app_name) + "！\n进入应用前，请您认真阅读《隐私政策》了解详细信息。\n点击“同意”按钮开始接受我们的服务。";
            String text = "尊敬的客户，为了向您提供更优质的服务，在您使用盛昊智行APP前，请你务必审慎阅读、充分理解《隐私政策》各条款，如你同意，请点击下方按钮开始接受我们的服务。";

            // 创建SpannableString
            SpannableString spannableString = new SpannableString(text);

            // 定义可点击区域的开始和结束位置
            int start = text.indexOf("《");
            int end = text.indexOf("》") + 1;

            // 设置文本颜色
            ForegroundColorSpan blueColorSpan = new ForegroundColorSpan(getColor(R.color.theme_blue));
            spannableString.setSpan(blueColorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // 定义Click事件
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // 跳转到隐私协议页面
                    Redirect.startPrivacyUrl(SplashActivity.this);
                }

                // 移除默认下划线
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getColor(R.color.theme_blue));
                    ds.setUnderlineText(false);
                }
            };

            // 添加ClickableSpan
            spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            CommonDialog commonDialog = new CommonDialog(SplashActivity.this, new CommonDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {  //确定
                        dialog.dismiss();
                        SPUtils.getInstance().putBoolean(SPUtils.SP_HAS_SHOW_PRIVACY, true);
                        ((ShengHaoApp)getApplication()).initBugly();
                    } else {
                        finish();
                    }
                }
            })
                    .setMessageType(CommonDialog.MessageType.INFOREAD)
                    .setPositiveButton("同意")
                    .setNegativeButton("不同意")
                    .setTitle("请阅读并同意以下条款")
                    .setContent(spannableString)
                    .setContentVisibility(View.VISIBLE)
                    .setDialogCancelable(false);
            commonDialog.show();
            llBtns.setVisibility(View.VISIBLE);
        } else {
            init();
        }
    }

    /**
     * 获取设备列表
     */
    private void getDeviceList() {
        //获取设备列表
        OkHttpPresent.getDeviceList(new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 获取设备列表成功 = " + body);
                hideLoadingDialog();
                TerminalListResp terminalListResp = JsonUtils.parseT(body, TerminalListResp.class);
                if (terminalListResp != null && terminalListResp.isSuccess()) {
                    if (terminalListResp.getData().size() > 0) {    //已绑定设备
                        TerminalUtils.setCurrentTerminal(terminalListResp.getData());
                        startMainPage();
                    } else {    //设备列表为空，跳转至绑定设备界面
                        Redirect.startBindTerminalActivity(SplashActivity.this);
                    }
                } else {    //获取设备列表失败
                    Redirect.startLoginActivity(SplashActivity.this);
                    finish();
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                hideLoadingDialog();
                LogUtils.e(TAG, "onResponse: 获取设备列表失败 = " + e);
                Redirect.startLoginActivity(SplashActivity.this);
                finish();
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    /**
     * 打开主界面
     */
    @SuppressLint("CheckResult")
    private void startMainPage() {
        //TODO superLT 延迟500ms打开主界面
        Observable.timer(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Redirect.startMainActivity(SplashActivity.this);
                        finish();
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BindTerminalEvent bindTerminalEvent) {  //设备绑定成功
        Redirect.startMainActivity(SplashActivity.this);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SplashActivity2", "onDestroy");
        EventBus.getDefault().unregister(this);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
