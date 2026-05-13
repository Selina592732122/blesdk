package com.shenghao.blesdkdemo.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.adapter.ExamplePagerAdapter;
import com.shenghao.blesdkdemo.adapter.VehicleModeAdapter;
import com.shenghao.blesdkdemo.bean.VehicleModel;
import com.shenghao.blesdkdemo.bean.VehicleModelBean;
import com.shenghao.blesdkdemo.bean.VehicleModelResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class CarTypeDialog extends Dialog implements View.OnClickListener {
    public final String TAG = this.getClass().getSimpleName();
    private final Context mContext;
    private OnCloseListener listener;
    private boolean cancelable;
    private TextView submitTxt;
    private int selectType = -1;
    private String title;
    private TextView titleTxt;
    private ViewPager mViewPager;
    private final List<String> mDataList = new ArrayList<>();
    private List<VehicleModelBean> datas = new ArrayList<>();//列表
    private VehicleModeAdapter vehicleModeAdapter;
    private final ExamplePagerAdapter mExamplePagerAdapter = new ExamplePagerAdapter(mDataList);
    private List<VehicleModel> data;

    public CarTypeDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public CarTypeDialog(Context context, OnCloseListener listener) {
        super(context, R.style.Common_Dialog2);
        this.mContext = context;
        this.listener = listener;
    }

    protected CarTypeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public CarTypeDialog setDialogCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_cartype);
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
        handleNavigationBar();//底部导航栏适配
        getVehicleModelMenu();

//        initView();
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
    private void getVehicleModelMenu(){
        OkHttpPresent.getVehicleModelMenu(new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 成功 = " + body);
                VehicleModelResp vehicleModelResp = JsonUtils.parseT(body, VehicleModelResp.class);
                if (vehicleModelResp != null && vehicleModelResp.isSuccess()) {
                    data = vehicleModelResp.getData();
                    mDataList.clear();
                    datas.clear();
                    for (int i = 0; i < data.size(); i++) {
                        mDataList.add(data.get(i).getName());
                        mExamplePagerAdapter.notifyDataSetChanged();
                    }
                    datas.addAll(data.get(0).getChildren());
                    initView();
                }else {
                    ToastUtils.showShort(mContext, mContext.getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 失败 = " + e);
                ToastUtils.showShort(mContext, mContext.getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
            }
        });
    }

    private void initMagicIndicator() {
        MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
//        magicIndicator.setBackgroundColor(Color.parseColor("#00c853"));
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setScrollPivotX(0.25f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(Color.parseColor("#99000000"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#E6000000"));
                simplePagerTitleView.setTextSize(16);
                simplePagerTitleView.setTypeface(null, Typeface.BOLD);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                        if(data != null){
                            datas.clear();
                            if(data.get(index).getChildren() != null)
                                datas.addAll(data.get(index).getChildren());
                            vehicleModeAdapter.setSelectedPosition(RecyclerView.NO_POSITION);
                            selectType = -1;
                            vehicleModeAdapter.notifyDataSetChanged();
                        }
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setYOffset(UIUtil.dip2px(context, 3));
                indicator.setColors(Color.parseColor("#E6000000"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }
    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mExamplePagerAdapter);
        initMagicIndicator();
        initRecyclerView();
        submitTxt = (TextView) findViewById(R.id.submitTv);
        submitTxt.setOnClickListener(this);
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

    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        vehicleModeAdapter = new VehicleModeAdapter(mContext, datas, new VehicleModeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int id = datas.get(position).getId();
                selectType = id;
//                ToastUtils.showShort(getContext(),id+"");
            }
        });
        recyclerView.setAdapter(vehicleModeAdapter);
    }

    public CarTypeDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancelTv) {
            if (listener != null) {
                listener.onClick(this, false,selectType);
            }
            this.dismiss();
        } else if (v.getId() == R.id.submitTv) {
            if(selectType == -1){
                ToastUtils.showShort(getContext(),"请选择车型！");
                return;
            }
            if (listener != null) {
                listener.onClick(this, true,selectType);
            }
        }
    }

    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean confirm, int type);
    }
}
