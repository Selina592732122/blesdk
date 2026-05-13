package com.shenghao.blesdkdemo.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.databinding.ActivityKeyBinding;
import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class KeyActivity extends BaseActivity {
    public final String TAG = this.getClass().getSimpleName();
    private @NonNull ActivityKeyBinding binding;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pairingRunnable;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setStatusBarColor(this, R.color.theme_background);
        binding = ActivityKeyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
        pairingRunnable = new Runnable() {
            @Override
            public void run() {
            }
        };
    }

    @Override
    protected void initViews() {
        super.initViews();
        keyPair(AppSingleton.getInstance().getTerminalNo());
    }

    private void keyPair(String terminalNo) {
        OkHttpPresent.keyPair(terminalNo, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                OkHttpBaseResp loginResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (loginResp.isSuccess()) {
                    ToastUtils.showShort(KeyActivity.this,"指令已发送");
                } else {    // 未绑定
                    ToastUtils.showShort(KeyActivity.this, TextUtils.isEmpty(loginResp.getMsg())? getString(R.string.request_retry) :loginResp.getMsg());
                }
            }
            @Override
            protected void onFailed(Request request, Exception e) {
                ToastUtils.showShort(KeyActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                // 可在此处处理结束逻辑
                hideLoadingDialog();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关键：移除所有回调并释放引用
        handler.removeCallbacks(pairingRunnable);
        handler = null; // 避免内存泄漏
    }
}
