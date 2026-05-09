package com.shenghao.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.shenghao.R;
import com.shenghao.bean.ThemeBean;
import com.shenghao.event.BindTerminalEvent;
import com.shenghao.event.ThemeChangeEvent;
import com.shenghao.okhttp.OkHttpBaseResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.ui.ThemeActivity;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;


public class ThemeDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private CharSequence content;
    private OnCloseListener listener;
    private String title;

    private boolean cancelable = true;

    private ImageView iv;
    private ThemeBean themeBean;
    private TextView tvUse;

    public ThemeDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public ThemeDialog(Context context, OnCloseListener listener) {
        super(context, R.style.Common_Dialog);
        this.mContext = context;
        this.listener = listener;
    }

    protected ThemeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public ThemeDialog setDialogCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public ThemeDialog setTitle(String title) {
        this.title = title;
        return this;
    }


    public ThemeDialog setContent(CharSequence content) {
        this.content = content;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_theme);
        setCanceledOnTouchOutside(cancelable);
        setCancelable(cancelable);
        initView();
    }

    private void initView() {
        tvUse = (TextView) findViewById(R.id.tvUse);
        iv = (ImageView) findViewById(R.id.iv);
        Glide.with(mContext).load(themeBean.getThemeImage()).into(iv);
        tvUse.setOnClickListener(view -> {
            setTheme(themeBean.getId()+"");
        });
    }

    public void setTheme(String id) {
        OkHttpPresent.setTheme(id,new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
            }

            @Override
            protected void onResponse(Response response, String body) {
                OkHttpBaseResp loginResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (loginResp != null && loginResp.isSuccess()) {
                    EventBus.getDefault().post(new ThemeChangeEvent(themeBean.getThemeImage()));
                    if(listener != null) listener.onClick(ThemeDialog.this,true);
                    dismiss();
                } else {
                    ToastUtils.showShort(mContext, mContext.getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                ToastUtils.showShort(mContext, mContext.getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
            }
        });
    }

    @Override
    public void onClick(View v) {
    }

    public Dialog setThemeBean(ThemeBean img) {
        this.themeBean = img;
        return this;
    }

    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean refresh);
    }
}
