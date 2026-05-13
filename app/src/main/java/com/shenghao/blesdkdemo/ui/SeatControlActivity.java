package com.shenghao.blesdkdemo.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;

import java.io.IOException;

import androidx.annotation.Nullable;
import okhttp3.Request;
import okhttp3.Response;


public class SeatControlActivity extends BaseActivity {
    private RadioGroup rgFan, rgFanBackground;
    private TextView tvBtn,tvMode,tvModeBackground;
    private View llBtn, llBtnBackground;
    private boolean airEnable = false;
    private boolean airEnableBackground = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private RadioButton rbFan1,rbFan2,rbFan3;
    private RadioButton rbFan1Background,rbFan2Background,rbFan3Background;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_control);
        StatusBarUtils.setStatusBarColor(this, R.color.transparent);
        StatusBarUtils.statusBarLightMode(this);
        initViews();
        setEnable(airEnable);
        setEnableBackground(airEnableBackground);
    }



    @Override
    protected void initViews() {
        super.initViews();
//        tvPercent = findViewById(R.id.tvPercent);
        View statusBarView = findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(this, statusBarView);
//        mGearSeekBar.setThumbSizeDp(32, 32); // 设置为32dp x 32dp
        tvMode = findViewById(R.id.tvMode);
        tvModeBackground = findViewById(R.id.tvModeBackground);
        tvBtn = findViewById(R.id.tvBtn);
        rgFan = findViewById(R.id.rgFan);
        rgFanBackground = findViewById(R.id.rgFanBackground);
        llBtn = findViewById(R.id.llBtn);
        llBtnBackground = findViewById(R.id.llBtnBackground);
        rgFan = findViewById(R.id.rgFan);
        rbFan1 = findViewById(R.id.rbFan1);
        rbFan2 = findViewById(R.id.rbFan2);
        rbFan3 = findViewById(R.id.rbFan3);
        rbFan1Background = findViewById(R.id.rbFan1Background);
        rbFan2Background = findViewById(R.id.rbFan2Background);
        rbFan3Background = findViewById(R.id.rbFan3Background);
        rgFan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                airEnable = true;
                setEnable(true);
                if(id == R.id.rbFan1){
                    tvMode.setText("温暖");
                    if(rbFan1.isChecked())
                        airControl2(17,1,1);
                } else if(id == R.id.rbFan2){
                    tvMode.setText("炙热");
                    if(rbFan2.isChecked())
                        airControl2(17,1,2);
                } else if(id == R.id.rbFan3){
                    tvMode.setText("自动");
                    if(rbFan3.isChecked())
                        airControl2(17,1,4);
                }
            }
        });
        rgFanBackground.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                airEnableBackground = true;
                setEnableBackground(true);
                if(id == R.id.rbFan1Background){
                    tvModeBackground.setText("温暖");
                    if(rbFan1Background.isChecked())
                        airControl2(19,1,1);
                } else if(id == R.id.rbFan2Background){
                    tvModeBackground.setText("炙热");
                    if(rbFan2Background.isChecked())
                        airControl2(19,1,2);
                } else if(id == R.id.rbFan3Background){
                    tvModeBackground.setText("自动");
                    if(rbFan3Background.isChecked())
                        airControl2(19,1,4);
                }
            }
        });
        llBtn.setOnClickListener(view -> {
            airEnable = false;
            setEnable(airEnable);
            airControl2(17,0,0);
        });
        llBtnBackground.setOnClickListener(view -> {
            airEnableBackground = false;
            setEnableBackground(airEnableBackground);
            airControl2(19,0,0);
        });
    }

    private void setEnable(boolean b) {
        rgFan.setEnabled(b);
        if(!b){
            rgFan.clearCheck();
            tvMode.setText("-");
        }

        llBtn.setBackgroundTintList(b?ColorStateList.valueOf(Color.parseColor("#FFE12121")):ColorStateList.valueOf(Color.parseColor("#FFD1D1D1")));
    }

    private void setEnableBackground(boolean b) {
        rgFanBackground.setEnabled(b);
        if(!b){
            rgFanBackground.clearCheck();
            tvModeBackground.setText("-");
        }

        llBtnBackground.setBackgroundTintList(b?ColorStateList.valueOf(Color.parseColor("#FFE12121")):ColorStateList.valueOf(Color.parseColor("#FFD1D1D1")));
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
                        ToastUtils.showShort(SeatControlActivity.this, "发送成功");
                        hideLoadingDialog();
                    } else {    //登录失败
                        ToastUtils.showShort(SeatControlActivity.this, loginResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(SeatControlActivity.this, getString(R.string.request_retry));
                }
            }
            
            
            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(SeatControlActivity.this, getString(R.string.request_retry));
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
                        ToastUtils.showShort(SeatControlActivity.this, loginResp.getMsg());
//                        hideLoadingDialog();
                    }
                } else {
                    ToastUtils.showShort(SeatControlActivity.this, getString(R.string.request_retry));
//                    hideLoadingDialog();
                }
            }


            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(SeatControlActivity.this, getString(R.string.request_retry));
//                hideLoadingDialog();
            }

        });
    }

}
