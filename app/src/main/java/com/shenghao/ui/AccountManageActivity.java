package com.shenghao.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.shenghao.R;
import com.shenghao.bean.UserInfo;
import com.shenghao.utility.AppSingleton;
import com.shenghao.utils.StringUtils;
import com.shenghao.utils.ToastUtils;

public class AccountManageActivity extends BaseActivity {
    private TextView tvTel,tvMail,tvMailBind;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manage);
        initViews();
    }

    @Override
    protected void initViews() {
        super.initViews();
        tvTel = findViewById(R.id.tvTel);//电话
        tvMail = findViewById(R.id.tvMail);//邮箱
        tvMailBind = findViewById(R.id.tvMailBind);
        tvTel.setText(StringUtils.maskPhoneNumber(AppSingleton.getInstance().getUserName()));
        findViewById(R.id.tvPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountManageActivity.this, BindPhoneActivity.class);
                startActivity(intent);
            }
        });
        tvMailBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountManageActivity.this, BindMailActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.tvPwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(AppSingleton.getInstance().getUserInfo().getEmail())){
                    ToastUtils.showShort(AccountManageActivity.this,getString(R.string.please_bind_email_first));
                    return;
                }
                Intent intent = new Intent(AccountManageActivity.this, SettingPasswordActivity.class);
                intent.putExtra("email",AppSingleton.getInstance().getUserInfo().getEmail());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserInfo userInfo = AppSingleton.getInstance().getUserInfo();
        if(userInfo != null){
            if(TextUtils.isEmpty(userInfo.getEmail())){
                tvMailBind.setText(getString(R.string.bind));
                tvMailBind.setBackground(AppCompatResources.getDrawable(this,R.drawable.bg_blue_radius_shape));
                ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#FFF57F2A"));//红色
                tvMailBind.setBackgroundTintList(colorStateList);
                tvMail.setText(getString(R.string.unbound));
            }else {
                tvMailBind.setText(getString(R.string.change));
                tvMailBind.setBackground(AppCompatResources.getDrawable(this,R.drawable.bg_blue_radius_shape));
                ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#ff1d99ff"));//蓝色
                tvMailBind.setBackgroundTintList(colorStateList);
                tvMail.setText(userInfo.getEmail());
            }
            tvTel.setText(StringUtils.maskPhoneNumber(userInfo.getPhone()));
        }
    }
}
