package com.shenghao.blesdkdemo.ui;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.utils.ActivityManager;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;


public class BaseActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getSimpleName();
    private Dialog loadingDialogV1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if(!allowActivityOrientation())
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StatusBarUtils.setStatusBarColor(this, R.color.theme_background);
        StatusBarUtils.statusBarLightMode(this);
        ActivityManager.pushActivity(this);
    }

    // 默认不允许子类自定义，子类可重写
    protected boolean allowActivityOrientation() {
        return false;
    }

    protected void initViews() {
        View titleBackBtn = findViewById(R.id.titleBackBtn);

        if (titleBackBtn != null) {
            titleBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
        hideLoadingDialog();
    }

    public void showLoadingDialog() {
        showLoadingDialog("加载中...");
    }

    protected void showLoadingDialog(String message) {
        loadingDialogV1 = new Dialog(this, R.style.TransparentProgressDialogTheme);
        loadingDialogV1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialogV1.setContentView(R.layout.dialog_custom_progress);
        loadingDialogV1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialogV1.setCancelable(true);
        loadingDialogV1.setCanceledOnTouchOutside(true);
        TextView messageTv = loadingDialogV1.findViewById(R.id.message_tv);
        messageTv.setText(message);
        loadingDialogV1.show();
    }

    public void hideLoadingDialog() {
        if (loadingDialogV1 != null && loadingDialogV1.isShowing()) {
            loadingDialogV1.dismiss();
        }
    }

//    private ProgressDialog loadingDialog;
//    protected void showLoadingDialog(String message) {
//        loadingDialog = ProgressDialog.show(this, "", message);
//        loadingDialog.setCanceledOnTouchOutside(true);
//        loadingDialog.show();
//    }
//
//    protected void showLoadingProcessDialog(String message) {
//        loadingDialog = new ProgressDialog(this);
//        loadingDialog.setMessage(message);
////        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        loadingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        loadingDialog.setMax(100);
//        loadingDialog.setCanceledOnTouchOutside(false);
//        loadingDialog.setCancelable(false);
//        loadingDialog.show();
//    }
//
//    protected void setLoadingDialogProcess(int value) {
//        if (loadingDialog != null) {
//            loadingDialog.setProgress(Math.max(value, 0));
//        }
//    }
//
//    protected int getLoadingDialogProcess() {
//        if (loadingDialog != null) {
//            return loadingDialog.getProgress();
//        }
//        return 0;
//    }
//
//    protected void hideLoadingDialog() {
//        if (loadingDialog != null && loadingDialog.isShowing()) {
//            loadingDialog.dismiss();
//        }
//    }
}
