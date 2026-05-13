package com.shenghao.blesdkdemo.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.UserInfo;
import com.shenghao.blesdkdemo.bean.UserInfoResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.ui.BaseActivity;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.Redirect;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class AcountShareDialog extends Dialog implements View.OnClickListener {
    public final String TAG = this.getClass().getSimpleName();
    private final Context mContext;
    private OnCloseListener listener;
    private boolean cancelable;
    private String title;
    private TextView titleTxt;
    private EditText etPhone;
    private View llAcount,llSuccess,llUser;
    private TextView tvMain,tvPhone,tvInvite;
    private ImageView ivUser;

    public AcountShareDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public AcountShareDialog(Context context, OnCloseListener listener) {
        super(context, R.style.Common_Dialog2);
        this.mContext = context;
        this.listener = listener;
    }

    protected AcountShareDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public AcountShareDialog setDialogCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_account_share);
        setCanceledOnTouchOutside(cancelable);
        setCancelable(cancelable);

//        WindowManager m = getWindow().getWindowManager();
//        Display d = m.getDefaultDisplay();
//        WindowManager.LayoutParams p = getWindow().getAttributes();
//        p.height = ViewGroup.LayoutParams.MATCH_PARENT;
////        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        p.width = (int) (ScreenUtils.getScreenWidth(mContext) * 1.0f);
//        getWindow().setAttributes(p);
        Window window = getWindow();
        if (window != null) {
            // 1. 设置全屏布局
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            // 2. 清除浮动窗口标志
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            // 3. 设置状态栏透明
            // 现代API (21+)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

            // 设置系统UI可见性
            View decorView = window.getDecorView();
            int systemUiVisibility = decorView.getSystemUiVisibility();
            // 添加全屏布局标志
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(systemUiVisibility);

            // 4. 关键：确保内容延伸到状态栏区域
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }
        View statusBarView = findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(mContext, statusBarView);
        handleNavigationBar();//底部导航栏计算
        initView();
    }

    private void handleNavigationBar() {
        View rootView = findViewById(R.id.rootLayout);
        if (rootView == null) return;

//        int padding = DensityUtil.dip2px(mContext, 16);
        int padding = 0;
        // 方法1：使用WindowInsets API（推荐）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            rootView.setOnApplyWindowInsetsListener((v, insets) -> {
                int navHeight = insets.getInsets(WindowInsets.Type.navigationBars()).bottom;
                v.setPadding(padding, padding, padding, padding+navHeight);
                return insets;
            });
        }
        // 方法2：兼容旧版本
        else {
            // 获取资源中定义的导航栏高度
            int resourceNavHeight = 0;
            int resourceId = mContext.getResources().getIdentifier(
                    "navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                resourceNavHeight = mContext.getResources().getDimensionPixelSize(resourceId);
            }

            int finalResourceNavHeight = resourceNavHeight;
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            Rect rect = new Rect();
                            rootView.getWindowVisibleDisplayFrame(rect);

                            int screenHeight = rootView.getRootView().getHeight();
                            int calculatedNavHeight = screenHeight - rect.bottom;

                            // 选择最大的高度值（动态计算值或资源值）
                            int finalNavHeight = Math.max(calculatedNavHeight, finalResourceNavHeight);

                            // 仅在检测到有效高度时应用
                            if (finalNavHeight > 0) {
                                rootView.setPadding(
                                        padding,
                                        padding,
                                        padding,
                                        padding + finalNavHeight
                                );

                                // 移除监听避免重复设置
                                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        }
                    }
            );
        }
    }

    private void initView() {
        titleTxt = (TextView) findViewById(R.id.title);
        llAcount = findViewById(R.id.llAccount);
        llSuccess = findViewById(R.id.llSuccess);
        llUser = findViewById(R.id.llUser);
        tvMain = findViewById(R.id.tvMain);
        tvPhone = findViewById(R.id.tvPhone);
        tvInvite = findViewById(R.id.tvInvite);
        ivUser = findViewById(R.id.ivUser);
        tvMain.setOnClickListener(view -> {
            dismiss();
            Redirect.startMainActivity(mContext);
            ((BaseActivity)mContext).finish();
        });
        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        if (!TextUtils.isEmpty(title)) {
            titleTxt.setText(title);
        }
        etPhone = findViewById(R.id.etPhone);
        etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etPhone.setBackgroundResource(R.drawable.bg_blue_radius12_shape);
                } else {
                    etPhone.setBackgroundResource(R.drawable.bg_white_radius12_shape);
                }
            }
        });
        findViewById(R.id.tvSearch).setOnClickListener(view -> {
            searchAccount();
        });
    }

    private void searchAccount() {
        OkHttpPresent.searchAccount(etPhone.getText().toString(), new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                ((BaseActivity)mContext).showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "登录成功: " + body);
                UserInfoResp loginResp = JsonUtils.parseT(body, UserInfoResp.class);
                if (loginResp != null) {
                    UserInfo data = loginResp.getData();
                    if (loginResp.isSuccess() && data !=null) {
                        llAcount.setVisibility(View.GONE);
                        llSuccess.setVisibility(View.GONE);
                        Glide.with(mContext)
                                .load(data.getAvatar())
                                .placeholder(R.drawable.ic_user_default_portrait) // 加载中的占位图
                                .error(R.drawable.ic_user_default_portrait)
                                .into(ivUser);
                        tvPhone.setText(data.getPhone());
                        llUser.setVisibility(View.VISIBLE);
                        tvInvite.setOnClickListener(view -> {
                            shareInvite(data.getPhone());
                        });
                    } else {    //登录失败
                        ToastUtils.showShort(mContext, loginResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(mContext, mContext.getString(R.string.request_retry));
                }
                ((BaseActivity)mContext).hideLoadingDialog();
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(mContext, mContext.getString(R.string.request_retry));
                ((BaseActivity)mContext).hideLoadingDialog();
            }

        });
    }


    private void shareInvite(String phone) {
        OkHttpPresent.shareInvite(phone, AppSingleton.getInstance().getTerminalNo(), new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                ((BaseActivity)mContext).showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "登录成功: " + body);
                OkHttpBaseResp loginResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (loginResp != null) {
                    if (loginResp.isSuccess()) {
                        llAcount.setVisibility(View.GONE);
                        llSuccess.setVisibility(View.VISIBLE);
                        llUser.setVisibility(View.GONE);
                    } else {    //登录失败
                        ToastUtils.showShort(mContext, loginResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(mContext, mContext.getString(R.string.request_retry));
                }
                ((BaseActivity)mContext).hideLoadingDialog();
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(mContext, mContext.getString(R.string.request_retry));
                ((BaseActivity)mContext). hideLoadingDialog();
            }

        });
    }

    public AcountShareDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancelTv) {
            if (listener != null) {
                listener.onClick(this, false);
            }
            this.dismiss();
        } else if (v.getId() == R.id.submitTv) {
            if (listener != null) {
                listener.onClick(this, true);
            }
        }
    }

    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean confirm);
    }
}
