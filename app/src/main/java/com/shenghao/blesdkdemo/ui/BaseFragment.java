package com.shenghao.blesdkdemo.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.shenghao.blesdkdemo.R;


public class BaseFragment extends Fragment {
    private ProgressDialog loadingDialog;
    private Dialog loadingDialogV1;

    protected void showLoadingDialog(Context context) {
        showLoadingDialog(context, "加载中...");
    }

    protected void showLoadingDialog(Context context, String message) {
        loadingDialogV1 = new Dialog(context, R.style.TransparentProgressDialogTheme);
        loadingDialogV1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialogV1.setContentView(R.layout.dialog_custom_progress);
        loadingDialogV1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialogV1.setCancelable(true);
        loadingDialogV1.setCanceledOnTouchOutside(true);
        TextView messageTv = loadingDialogV1.findViewById(R.id.message_tv);
        messageTv.setText(message);
        loadingDialogV1.show();
    }

    protected void hideLoadingDialog() {
        if (loadingDialogV1 != null && loadingDialogV1.isShowing()) {
            loadingDialogV1.dismiss();
        }
    }

//    protected void showLoadingDialog(Context context, String message) {
//        loadingDialog = ProgressDialog.show(context, "", message);
//        loadingDialog.setCanceledOnTouchOutside(true);
//        loadingDialog.show();
//    }
//
//    protected void showLoadingProcessDialog(Context context, String message) {
//        loadingDialog = new ProgressDialog(context);
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
