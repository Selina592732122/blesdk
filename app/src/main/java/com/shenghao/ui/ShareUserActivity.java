package com.shenghao.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.shenghao.R;
import com.shenghao.bean.ShareUser;
import com.shenghao.databinding.ActivityShareUserBinding;
import com.shenghao.okhttp.OkHttpBaseResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.utility.AppSingleton;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.StatusBarUtils;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.CommonDialog;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;


public class ShareUserActivity extends BaseActivity {
    private @NonNull ActivityShareUserBinding binding;
    private TextView tvCancel,tvName,tvTime,tvPhone,tvNote;
    private ShareUser shareUser;
    private ImageView ivUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShareUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        shareUser = (ShareUser) getIntent().getSerializableExtra("ShareUser");
        StatusBarUtils.setStatusBarColor(this, R.color.transparent);
        StatusBarUtils.statusBarLightMode(this);
        initViews();
    }

    @Override
    protected void initViews() {
        super.initViews();
        tvCancel = findViewById(R.id.tvCancel);
        tvName = findViewById(R.id.tvName);
        tvTime = findViewById(R.id.tvTime);
        tvPhone = findViewById(R.id.tvPhone);
        tvNote = findViewById(R.id.tvNote);
        ivUser = findViewById(R.id.ivUser);
        StatusBarUtils.setStatusBarHeight(this, binding.statusBarView);
        tvName.setText(TextUtils.isEmpty(shareUser.getRemark())?shareUser.getPhone():shareUser.getRemark());
        tvTime.setText(shareUser.getSysCreated()+getString(R.string.join));
        tvPhone.setText(shareUser.getPhone());
        Glide.with(this)
                .load(shareUser.getAvatar())
                .placeholder(R.drawable.ic_user_default_portrait) // 加载中的占位图
                .error(R.drawable.ic_user_default_portrait)
                .into(ivUser);
        tvNote.setText(shareUser.getRemark() == null ? getString(R.string.no_set):shareUser.getRemark());

        binding.llNote.setOnClickListener(v -> {
            showEditDialog(binding.tvNote.getText().toString());
        });
        tvCancel.setOnClickListener(view -> {
            cancelShare(shareUser.getPhone());
        });
    }
    private void cancelShare(String phone) {
        OkHttpPresent.cancelShare(AppSingleton.getInstance().getTerminalNo(),phone, new OkHttpResultCallBack() {
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
                        finish();
                    } else {    //登录失败
                        ToastUtils.showShort(ShareUserActivity.this, loginResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(ShareUserActivity.this, getString(R.string.request_retry));
                }


                hideLoadingDialog();
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(ShareUserActivity.this, getString(R.string.request_retry));
                hideLoadingDialog();
            }

        });
    }

    protected void showEditDialog(String content) {
        CommonDialog editTerminalNameDialog = new CommonDialog(this, new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {  //确定
                    String contentEtText = ((CommonDialog)dialog).getContentEtText().trim();
                    if (!TextUtils.isEmpty(contentEtText)) {
                        dialog.dismiss();
                        updateMemberRemark(contentEtText);//调用接口
                    } else {
                        ToastUtils.showShort(ShareUserActivity.this, getString(R.string.note_hint));
                    }

                }
            }
        })
                .setTitle(getString(R.string.note))
                .setHint(getString(R.string.note_hint))
                .setPositiveButton(getString(R.string.save))
                .setContentEtVisibility(View.VISIBLE);
//        editTerminalNameDialog.setEditContent(content);
        editTerminalNameDialog.show();
    }

    private void updateMemberRemark(String remark) {
        OkHttpPresent.updateMemberRemark(AppSingleton.getInstance().getTerminalNo(),shareUser.getPhone(),remark, new OkHttpResultCallBack() {
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
                        tvNote.setText(remark);
                        if(!TextUtils.isEmpty(remark))
                            tvName.setText(remark);
                    } else {    //登录失败
                        ToastUtils.showShort(ShareUserActivity.this, loginResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(ShareUserActivity.this, getString(R.string.request_retry));
                }


                hideLoadingDialog();
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(ShareUserActivity.this, getString(R.string.request_retry));
                hideLoadingDialog();
            }

        });
    }

}
