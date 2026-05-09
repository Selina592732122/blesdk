package com.shenghao.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shenghao.R;
import com.shenghao.okhttp.OkHttpBaseResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.utility.AppSingleton;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.StatusBarUtils;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.IosBottomSheetDialog;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;


public class WindowControlActivity extends BaseActivity {
    private Handler handler = new Handler(Looper.getMainLooper());
    private LinearLayout llLeftClose,llLeftOpen,llRightClose,llRightOpen,tvBtnClose,tvBtnOpen;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_control);
        StatusBarUtils.setStatusBarColor(this, R.color.transparent);
        StatusBarUtils.statusBarLightMode(this);
        initViews();
    }



    @Override
    protected void initViews() {
        super.initViews();
        View statusBarView = findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(this, statusBarView);
        llLeftClose = findViewById(R.id.llLeftClose);
        llLeftOpen = findViewById(R.id.llLeftOpen);
        llRightClose = findViewById(R.id.llRightClose);
        llRightOpen = findViewById(R.id.llRightOpen);
        tvBtnOpen = findViewById(R.id.tvBtnOpen);
        tvBtnClose = findViewById(R.id.tvBtnClose);

        initListeners(11);
    }

    private void initListeners(int type) {
        llLeftClose.setOnClickListener(view -> {
            airControl2(type,1,16);//左前窗关
        });
        llLeftOpen.setOnClickListener(view -> {
            airControl2(type,1,32);//左前窗开
        });
        llRightClose.setOnClickListener(view -> {
            airControl2(type,1,1);//右前窗关
        });
        llRightOpen.setOnClickListener(view -> {
            airControl2(type,1,2);//右前窗开
        });
        tvBtnClose.setOnClickListener(view -> {
            airControl2(type,1,17);//全车关
        });
        tvBtnOpen.setOnClickListener(view -> {
            airControl2(type,1,34);//全车开
        });
    }


    private void airControl2(int type,int param,int angle) {
        OkHttpPresent.airControl2(AppSingleton.getInstance().getTerminalNo(),type,param,angle, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }
            
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "登录成功: " + body);
                OkHttpBaseResp loginResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (loginResp != null) {
                    if (loginResp.isSuccess()) {
                        ToastUtils.showShort(WindowControlActivity.this, "发送成功");
                        hideLoadingDialog();
                    } else {    //登录失败
                        ToastUtils.showShort(WindowControlActivity.this, loginResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(WindowControlActivity.this, getString(R.string.request_retry));
                }
            }
            
            
            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(WindowControlActivity.this, getString(R.string.request_retry));
            }
            
            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }
    
    /**
     * 带延迟和控制完成的回调
     */
    private void airControlWithDelay(int type, int param, Runnable onComplete) {
        OkHttpPresent.airControl(AppSingleton.getInstance().getTerminalNo(), type, param, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "响应: " + body + " type=" + type + " param=" + param);
                handler.postDelayed(() -> {
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }, 300); // 延迟300ms执行下一个
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "失败: " + e);
                // 即使失败也继续执行下一个
                handler.postDelayed(() -> {
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }, 300);
            }
        });
    }

    private void airControl(int status) {
        OkHttpPresent.adjust(AppSingleton.getInstance().getTerminalNo(),status, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
//                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "登录成功: " + body);
                OkHttpBaseResp loginResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (loginResp != null) {
                    if (loginResp.isSuccess()) {
                    } else {    //登录失败
                        ToastUtils.showShort(WindowControlActivity.this, loginResp.getMsg());
//                        hideLoadingDialog();
                    }
                } else {
                    ToastUtils.showShort(WindowControlActivity.this, getString(R.string.request_retry));
//                    hideLoadingDialog();
                }
            }


            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(WindowControlActivity.this, getString(R.string.request_retry));
//                hideLoadingDialog();
            }

        });
    }

}
