package com.shenghao.blesdkdemo.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.adapter.MyTerminalAdapter;
import com.shenghao.blesdkdemo.bean.TerminalBean;
import com.shenghao.blesdkdemo.bean.TerminalListResp;
import com.shenghao.blesdkdemo.event.BindTerminalEvent;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.ui.BaseActivity;
import com.shenghao.blesdkdemo.ui.helper.TerminalOperationHelper;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.Redirect;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.TerminalUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class TerminalDialog extends Dialog implements View.OnClickListener {
    public final String TAG = this.getClass().getSimpleName();
    private final Context mContext;
    private OnCloseListener listener;
    private boolean cancelable;
    private int selectType = -1;
    private String title;
    private TextView titleTxt;
    private List<TerminalBean> terminalList = new ArrayList<>();
    private MyTerminalAdapter terminalAdapter;
    private EditText etNo;
    private TerminalOperationHelper mTerminalOperationHelper;

    public TerminalDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public TerminalDialog(Context context, OnCloseListener listener) {
        super(context, R.style.Common_Dialog2);
        this.mContext = context;
        this.listener = listener;
    }

    protected TerminalDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public TerminalDialog setDialogCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_terminal);
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
            }

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
        initRecyclerView();
        titleTxt = (TextView) findViewById(R.id.title);
        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        if (!TextUtils.isEmpty(title)) {
            titleTxt.setText(title);
        }
        findViewById(R.id.llAdd).setOnClickListener(v -> {
            Redirect.startBindTerminalActivity(mContext);
            dismiss();
        });
        getDeviceList();
        etNo = findViewById(R.id.etNo);
        etNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                terminalAdapter.filter(s.toString());
            }
        });

        mTerminalOperationHelper = new TerminalOperationHelper(mContext, new TerminalOperationHelper.TerminalOperationCallback() {
            @Override
            public void showLoading() {
                ((BaseActivity)mContext).showLoadingDialog();
            }

            @Override
            public void hideLoading() {
                ((BaseActivity)mContext).hideLoadingDialog();
            }

            @Override
            public void refreshDeviceList() {
                getDeviceList();
            }
        });


    }

    private void getDeviceList() {
        //获取设备列表
        OkHttpPresent.getDeviceList(new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                ((BaseActivity)mContext).showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 获取设备列表成功 = " + body);
                TerminalListResp terminalListResp = JsonUtils.parseT(body, TerminalListResp.class);
                if (terminalListResp != null && terminalListResp.isSuccess()) {
                    if (terminalListResp.getData().size() > 0) {    //已绑定设备
                        TerminalUtils.setCurrentTerminal(terminalListResp.getData());
                        EventBus.getDefault().post(new BindTerminalEvent(""));
                        notifyTerminalList(true);
                    }
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onResponse: 获取设备列表失败 = " + e);
            }

            @Override
            protected void end() {
                super.end();
                ((BaseActivity)mContext).hideLoadingDialog();
            }
        });
    }
    /**
     * 刷新设备列表数据
     */
    private void notifyTerminalList(boolean notifyData) {
        terminalList.clear();
        for (TerminalBean terminalBean : AppSingleton.getInstance().getTerminalList()) {
            if (terminalBean.isSelected()) {    //将当前设备排第一位
                terminalList.add(0, terminalBean);
            } else {
                terminalList.add(terminalBean);
            }
        }
//        circleNavigator.setCircleCount(terminalList.size());
//        indicator.setTotalItems(terminalList.size());
        if (notifyData) {
            if(terminalAdapter != null){
                terminalAdapter.notifyDataSetChanged();
                terminalAdapter.updateAllTerminalList(terminalList,etNo.getText().toString());
            }
        }
    }
    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
// 创建分割线
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
// 设置分割线的Drawable
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
        terminalAdapter = new MyTerminalAdapter(mContext, terminalList, new MyTerminalAdapter.OnTerminalItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                if(listener != null){
//                    listener.onClick(TerminalDialog.this,false,position);
//                }
                TerminalBean terminalBean = terminalList.get(Math.min(position, terminalList.size()));
                if (terminalBean == null) {
                    return;
                }
                showBottomSheetDialog(terminalBean,position);
            }
        });
        recyclerView.setAdapter(terminalAdapter);
    }

    public TerminalDialog setTitle(String title) {
        this.title = title;
        return this;
    }
    // 替换原有的showBottomSheetDialog方法调用
    private void showBottomSheetDialog(TerminalBean terminalBean, int pos) {
        mTerminalOperationHelper.showBottomSheetDialog(terminalBean, pos);
    }


    @Override
    public void onClick(View v) {
    }

    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean confirm, int pos);
    }
}
