package com.shenghao.blesdkdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.TerminalBean;
import com.shenghao.blesdkdemo.constant.Const;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;

public class Control2Activity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control2);
        StatusBarUtils.setStatusBarColor(this, R.color.transparent);
        StatusBarUtils.statusBarLightMode(this);
        initViews();
    }

    @Override
    protected void initViews() {
        super.initViews();
//        tvPercent = findViewById(R.id.tvPercent);
        View llWindow = findViewById(R.id.llWindow);
        View llSeat = findViewById(R.id.llSeat);
        View statusBarView = findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(this, statusBarView);
        TerminalBean currentTerminal = AppSingleton.getInstance().getCurrentTerminal();

        View llCharge = findViewById(R.id.llCharge);
        llCharge.setOnClickListener(v -> {
            startActivity(new Intent(Control2Activity.this,ChargeActivity.class));
        });
        View llAir = findViewById(R.id.llAir);
        llAir.setOnClickListener(v -> {
            startActivity(new Intent(Control2Activity.this,AirControlActivity.class));
        });

        llSeat.setOnClickListener(v -> {
            startActivity(new Intent(Control2Activity.this,SeatControlActivity.class));
        });

        llWindow.setOnClickListener(v -> {
            startActivity(new Intent(Control2Activity.this,WindowControlActivity.class));
        });
        if(currentTerminal.getVehicleModel() == Const.VEHICLE_MODEL_T30){
            llSeat.setVisibility(View.GONE);
            llWindow.setVisibility(View.GONE);
            llCharge.setVisibility(View.GONE);
        }
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

}
