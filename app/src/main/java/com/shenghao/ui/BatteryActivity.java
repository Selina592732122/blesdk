package com.shenghao.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.shenghao.R;
import com.shenghao.utility.AppSingleton;
import com.shenghao.utils.DensityUtil;
import com.shenghao.utils.StatusBarUtils;
import com.shenghao.utils.StringUtils;
import com.shenghao.widget.VerticalProgressBar;

public class BatteryActivity extends BaseActivity {
    private double powerPercent;
    private TextView tvPercent;
    private ImageView ivCar;
    private VerticalProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        StatusBarUtils.setStatusBarColor(this, R.color.transparent);
        StatusBarUtils.statusBarLightMode(this);
        powerPercent = getIntent().getDoubleExtra("progress",0);
        initViews();
    }

    @Override
    protected void initViews() {
        super.initViews();
        tvPercent = findViewById(R.id.tvPercent);
        ivCar = findViewById(R.id.ivCar);
        progressBar = findViewById(R.id.progressBar);

        View statusBarView = findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(this, statusBarView);
        tvPercent.setText(StringUtils.getFormatNumber(powerPercent)+"%");
        //根据车型显示
        Glide.with(this)
                .load(AppSingleton.getInstance().getCurrentTerminal().getCarPicture())
                .placeholder(R.drawable.ic_item_vehicle) // 加载中的占位图
                .error(R.drawable.ic_item_vehicle)
                .into(ivCar);
        initProgress();
    }

    private void startVirtualLogin() {
//        OkHttpPresent.loginSystem("jifang123456789", "0106", new OkHttpResultCallBack() {
//            @Override
//            protected void start() {
//                super.start();
//                showLoadingDialog();
//            }
//
//            @Override
//            protected void onResponse(Response response, String body) throws IOException {
//                LogUtils.e(TAG, "登录成功: " + body);
//                LoginResp loginResp = JsonUtils.parseT(body, LoginResp.class);
//                if (loginResp != null) {
//                    if (loginResp.isSuccess() && !TextUtils.isEmpty(loginResp.getData())) {
//                        AppSingleton.getInstance().setToken(loginResp.getData());
//                        AppSingleton.getInstance().setUserName("jifang123456789");
//                        getDeviceList();
//                    } else {    //登录失败
//                        ToastUtils.showShort(SplashActivity.this, loginResp.getMsg());
//                        hideLoadingDialog();
//                    }
//                } else {
//                    ToastUtils.showShort(SplashActivity.this, getString(R.string.request_retry));
//                    hideLoadingDialog();
//                }
//            }
//
//            @Override
//            protected void onFailed(Request request, Exception e) {
//                super.onFailed(request, e);
//                LogUtils.e(TAG, "登录失败: " + e);
//                ToastUtils.showShort(SplashActivity.this, getString(R.string.request_retry));
//                hideLoadingDialog();
//            }
//
//        });
    }

    private void initProgress() {
        progressBar.setCornerRadius(DensityUtil.dip2px(this,4));
        progressBar.setMax(100);
        progressBar.setProgress((int) powerPercent);
    }
}
