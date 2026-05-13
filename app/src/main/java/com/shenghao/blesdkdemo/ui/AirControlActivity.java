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

import androidx.annotation.Nullable;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;
import com.shenghao.blesdkdemo.widget.SixGearSeekBar;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;


public class AirControlActivity extends BaseActivity {
    private SixGearSeekBar mGearSeekBar;
    private RadioGroup rgFan;
    private TextView tvBtn,tvMode;
    private View llBtn;
    private boolean airEnable = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_control);
        StatusBarUtils.setStatusBarColor(this, R.color.transparent);
        StatusBarUtils.statusBarLightMode(this);
        initViews();
        setupGearSeekBar();
        setEnable(airEnable);
        mGearSeekBar.setCurrentGear(4);
    }



    @Override
    protected void initViews() {
        super.initViews();
//        tvPercent = findViewById(R.id.tvPercent);
        View statusBarView = findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(this, statusBarView);
        mGearSeekBar = findViewById(R.id.gearSeekBar);
//        mGearSeekBar.setThumbSizeDp(32, 32); // 设置为32dp x 32dp
        tvMode = findViewById(R.id.tvMode);
        tvBtn = findViewById(R.id.tvBtn);
        rgFan = findViewById(R.id.rgFan);
        llBtn = findViewById(R.id.llBtn);
        llBtn.setOnClickListener(view -> {
//            airEnable = !airEnable;
//            setEnable(airEnable);
            mGearSeekBar.setCurrentGear(4);
            handleGearChange(4);
        });
    }

    private void setupGearSeekBar() {
        mGearSeekBar.setOnGearChangeListener(new SixGearSeekBar.OnGearChangeListener() {
            @Override
            public void onGearChanged(int gear) {
                // 这里可以添加你的业务逻辑，比如控制设备
            }

            @Override
            public void onStartTrackingTouch() {
                // 开始拖动时的处理
            }

            @Override
            public void onStopTrackingTouch() {
                // 结束拖动时的处理
//                ToastUtils.showShort(AirControlActivity.this,""+mGearSeekBar.getCurrentGear());
                handleGearChange(mGearSeekBar.getCurrentGear());
            }
        });
    }

    private void handleGearChange(int gear) {
//        showLoadingDialog("请稍后");
        showAndAutoHideMsg();
        setEnable(true);
        airControl(gear-4);
        if (gear <= 3) {
            tvMode.setText("制冷");
            // 先执行制冷开
//            airControlWithDelay(7, 1, () -> {
//                // 再执行档位设置
//                airControlWithDelay(13, Math.abs(gear - 4), this::hideLoadingDialog);
//            });
        } else if (gear == 4) {
            tvMode.setText("-");
            setEnable(false);
            // 先执行制冷关
//            airControlWithDelay(7, 0, () -> {
//                // 再执行制热关
//                airControlWithDelay(26, 0, this::hideLoadingDialog);
//            });
        } else {
            tvMode.setText("制热");
            // 先执行制热开
//            airControlWithDelay(26, 1, () -> {
//                // 再执行档位设置
//                airControlWithDelay(13, Math.abs(gear - 4), this::hideLoadingDialog);
//            });
        }
    }

    private void setEnable(boolean b) {
        mGearSeekBar.setEnabled(b);
        mGearSeekBar.setThumbResource(this, b?R.drawable.ic_air_thumb:R.drawable.ic_air_thumb_disable);
        rgFan.setEnabled(b);

        // 递归设置所有子 RadioButton
        for (int i = 0; i < rgFan.getChildCount(); i++) {
            RadioButton child = (RadioButton) rgFan.getChildAt(i);
            child.setEnabled(b);
            if(!b)
                child.setChecked(b);
        }
        if(b)
            rgFan.check(R.id.rbFan1);

        llBtn.setBackgroundTintList(b?ColorStateList.valueOf(Color.parseColor("#FFE12121")):ColorStateList.valueOf(Color.parseColor("#FFD1D1D1")));
//        tvBtn.setText(b?"关闭":"开启");
    }
    public void showAndAutoHideMsg() {
        showLoadingDialog("请稍后");
        new Handler(Looper.getMainLooper()).postDelayed(this::hideLoadingDialog, 2000);
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
                        ToastUtils.showShort(AirControlActivity.this, loginResp.getMsg());
//                        hideLoadingDialog();
                    }
                } else {
                    ToastUtils.showShort(AirControlActivity.this, getString(R.string.request_retry));
//                    hideLoadingDialog();
                }
            }


            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(AirControlActivity.this, getString(R.string.request_retry));
//                hideLoadingDialog();
            }

        });
    }

}
