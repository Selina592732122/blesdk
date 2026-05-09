package com.shenghao.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.shenghao.R;
import com.shenghao.utils.StatusBarUtils;
import com.shenghao.widget.TimeDialog;

public class ChargeActivity extends BaseActivity {
    private Switch switchChong;
    private LinearLayout llStart,llEnd;
    private TextView tvStart,tvEnd,tvTimeStart,tvTimeEnd;
    private TimeDialog timeDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);
        StatusBarUtils.setStatusBarColor(this, R.color.transparent);
        StatusBarUtils.statusBarLightMode(this);
        initViews();
    }

    @Override
    protected void initViews() {
        super.initViews();
        switchChong = findViewById(R.id.switchChong);
        llStart = findViewById(R.id.llStart);
        llEnd = findViewById(R.id.llEnd);
        tvStart = findViewById(R.id.tvStart);
        tvEnd = findViewById(R.id.tvEnd);
        tvTimeStart = findViewById(R.id.tvTimeStart);
        tvTimeEnd = findViewById(R.id.tvTimeEnd);
        View statusBarView = findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(this, statusBarView);

        llStart.setOnClickListener(v -> {
            showTimeDialog(new TimeDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm, String item) {
                    dialog.dismiss();
                    if(confirm)
                        tvTimeStart.setText("每天 "+item);
                }
            });
        });
        llEnd.setOnClickListener(v -> {
            showTimeDialog((dialog,confirm,item) -> {
                dialog.dismiss();
                if(confirm)
                    tvTimeEnd.setText("次日 "+item);
            });
        });

        switchChong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    llStart.setClickable(true);
                    llEnd.setClickable(true);
                    tvStart.setTextColor(Color.parseColor("#99000000"));
                    tvEnd.setTextColor(Color.parseColor("#99000000"));
                    tvTimeStart.setTextColor(Color.parseColor("#CC000000"));
                    tvTimeEnd.setTextColor(Color.parseColor("#CC000000"));
                    Drawable drawable = AppCompatResources.getDrawable(ChargeActivity.this, R.drawable.ic_enter_arrow);
                    tvTimeStart.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null); // 使用
                    tvTimeEnd.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null); // 使用
                }else {
                    llStart.setClickable(false);
                    llEnd.setClickable(false);
                    tvStart.setTextColor(Color.parseColor("#FFC1C1C1"));
                    tvEnd.setTextColor(Color.parseColor("#FFC1C1C1"));
                    tvTimeStart.setTextColor(Color.parseColor("#FFC1C1C1"));
                    tvTimeEnd.setTextColor(Color.parseColor("#FFC1C1C1"));
                    Drawable drawable = AppCompatResources.getDrawable(ChargeActivity.this, R.drawable.ic_enter_arrow_disable);
                    tvTimeStart.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null); // 使用
                    tvTimeEnd.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null); // 使用
                }
            }
        });
    }

    private interface TimeCallBack{
        void onSelected(String item);
    }
    private void showTimeDialog(TimeDialog.OnCloseListener callBack) {
        if(timeDialog == null){
            timeDialog = new TimeDialog(this, null)
                    .setTimeType(TimeDialog.TimeType.hoursAndMinutes)
                    .setDialogCancelable(true);
        }
        timeDialog.setOnCloseListener(callBack);
        timeDialog.show();
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
