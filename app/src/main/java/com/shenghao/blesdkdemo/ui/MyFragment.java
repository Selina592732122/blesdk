package com.shenghao.blesdkdemo.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EdgeEffect;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.adapter.AvatarAdapter;
import com.shenghao.blesdkdemo.adapter.MyTerminalAdapter;
import com.shenghao.blesdkdemo.bean.ShareUser;
import com.shenghao.blesdkdemo.bean.ShareUserResp;
import com.shenghao.blesdkdemo.bean.TerminalBean;
import com.shenghao.blesdkdemo.bean.TerminalListResp;
import com.shenghao.blesdkdemo.event.BindTerminalEvent;
import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.ActivityManager;
import com.shenghao.blesdkdemo.utils.DensityUtil;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.Redirect;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.TerminalUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;
import com.shenghao.blesdkdemo.widget.CommonDialog;
import com.shenghao.blesdkdemo.widget.CustomDotIndicator;
import com.shenghao.blesdkdemo.widget.IosBottomSheetDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class MyFragment extends BaseFragment {
    public static final String TAG = "MyFragment";
    private Context mContext;
    private View geoFenceLayout;
    private View pointSearchLayout;
    private View serviceLayout;
    private View settingLayout;
    private TextView addTerminalBtn;
    private TextView userNameTv;
    private RecyclerView terminalRv;
    private CommonDialog editTerminalNameDialog;
    private CommonDialog editBatteryCountDialog;

    private MyTerminalAdapter myTerminalAdapter;
    private List<TerminalBean> terminalList = new ArrayList<>();
    private ImageView ivUser;
    private CustomDotIndicator indicator;
    private RecyclerView recyclerView;
    private TextView tvShare;
    private View llShare;
    private TextView tvNum;

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        return fragment;
    }

    public MyFragment() {
        super();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context; // ✅ 在这里获取 Context
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null; // ✅ 避免内存泄漏
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_my, null);
        initViews(view);
        initTerminalList();
        getSharedList();
        EventBus.getDefault().register(this);
        return view;
    }


    private void getSharedList() {
        OkHttpPresent.getSharedList(AppSingleton.getInstance().getTerminalNo(), new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog(mContext);
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "登录成功: " + body);
                ShareUserResp loginResp = JsonUtils.parseT(body, ShareUserResp.class);
                if (loginResp != null) {
                    List<ShareUser> data = loginResp.getData();
                    if (loginResp.isSuccess()&& data !=null) {
                        setupAvatarRecyclerView(data);//共享
                    } else {    //登录失败
                        ToastUtils.showShort(mContext, loginResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(mContext, getString(R.string.request_retry));
                }
                hideLoadingDialog();
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(mContext, getString(R.string.request_retry));
                hideLoadingDialog();
            }

        });
    }

    private void setupAvatarRecyclerView(List<ShareUser> data) {
        int shareStatus = AppSingleton.getInstance().getCurrentTerminal().getShareStatus();
        if(shareStatus == 1){
            llShare.setVisibility(View.VISIBLE);
        }else {
            llShare.setVisibility(View.GONE);
        }

        // 创建模拟数据
        List<String> avatarUrls = new ArrayList<>();
//        avatarUrls.add("https://example.com/avatar1.jpg");
        for (int i = 0; i < data.size(); i++) {
            avatarUrls.add(data.get(i).getAvatar());
        }
        tvNum.setText(String.format(getString(R.string.nums_of_user),data.size()));
        // 设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // 关键设置：允许超出边界
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);

        // 添加右边距显示完整头像
        int paddingRight = DensityUtil.dip2px(mContext, 12);
        recyclerView.setPadding(0, 0, paddingRight, 0);

        // 设置适配器
        AvatarAdapter adapter = new AvatarAdapter(mContext, avatarUrls, 3);
        recyclerView.setAdapter(adapter);

        // 确保RecyclerView父容器也允许超出边界
        if (recyclerView.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) recyclerView.getParent();
            parent.setClipChildren(false);
            parent.setClipToPadding(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        getNoticeUnreadNum();
        //显示昵称和头像
        if(TextUtils.isEmpty(AppSingleton.getInstance().getUserInfo().getNickName()))
            userNameTv.setText(AppSingleton.getInstance().getUserName());
        else userNameTv.setText(AppSingleton.getInstance().getUserInfo().getNickName());

        Glide.with(requireActivity())
                .load(AppSingleton.getInstance().getUserInfo().getAvatar())
                .placeholder(R.drawable.ic_user_default_portrait) // 加载中的占位图
                .error(R.drawable.ic_user_default_portrait)
                .into(ivUser);
    }

    private void initViews(View view) {
        geoFenceLayout = view.findViewById(R.id.geoFenceLayout);
        pointSearchLayout = view.findViewById(R.id.pointSearchLayout);
        serviceLayout = view.findViewById(R.id.serviceLayout);
        settingLayout = view.findViewById(R.id.settingLayout);
        addTerminalBtn = view.findViewById(R.id.addTerminalBtn);
        userNameTv = view.findViewById(R.id.userNameTv);
        terminalRv = view.findViewById(R.id.terminalRv);
        indicator = view.findViewById(R.id.indicator);
        ivUser = view.findViewById(R.id.ivUser);
        tvShare = view.findViewById(R.id.tvShare);
        tvShare.setText(AppSingleton.getInstance().getTerminalNo()+getString(R.string.someone_is_share));
        recyclerView = view.findViewById(R.id.recyclerView);
        llShare = view.findViewById(R.id.llShare);
        tvNum = view.findViewById(R.id.tvNum);
        llShare.setOnClickListener(v -> {
            startActivity(new Intent(mContext,ShareActivity.class));
        });
        view.findViewById(R.id.tvGuide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(),PdfActivity.class));
            }
        });
        view.findViewById(R.id.tvSos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(),SosActivity.class));
            }
        });
        view.findViewById(R.id.tvOta).setOnClickListener(view1 -> {
            new CommonDialog(requireActivity(), new CommonDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {  //确定
                        updateOta();
                        dialog.dismiss();
                    }
                }
            })
                    .setTitle("升级提示")
                    .setContent("升级时车辆禁用远程控制，不可断电或使用车辆，确定升级？")
                    .setContentVisibility(View.VISIBLE)
                    .setPositiveButton("确定")
                    .setPositiveButtonColor(ContextCompat.getColor(requireActivity(), R.color.white))
                    .show();
        });
        view.findViewById(R.id.tvPair).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), KeyActivity.class));
            }
        });
        view.findViewById(R.id.tvTheme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), ThemeActivity.class));
            }
        });
        view.findViewById(R.id.tvUnbind).setOnClickListener(view1 -> {
            new CommonDialog(requireActivity(), new CommonDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {  //确定
                        dialog.dismiss();
                        //解绑设备
                        unbindAllTerminal();
                    }
                }
            })
                    .setTitle("确定删除所有车辆吗？")
                    .show();


        });
        //渐变色文本
        TextView textView = view.findViewById(R.id.tvGradient);
        // 在视图布局完成后设置渐变
        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                Shader shader = new LinearGradient(
                        0, 0, 0, textView.getHeight(),
                        new int[]{Color.parseColor("#FFFFFFFF"), Color.parseColor("#FFC9CFE5")},
                        null,
                        Shader.TileMode.CLAMP
                );

                textView.getPaint().setShader(shader);
                textView.invalidate();
            }
        });


        view.findViewById(R.id.ivEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppSingleton.getInstance().getUserName().equals(AppSingleton.virtualTel)){//虚拟账号
                    ToastUtils.showShort(requireActivity(),getString(R.string.virtual_mode));
                    return;
                }
                startActivity(new Intent(requireActivity(),PersonalActivity.class));
            }
        });
        //设置状态栏高度
        View statusBarView = view.findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(mContext, statusBarView);
        userNameTv.setText(AppSingleton.getInstance().getUserName());

        settingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppSingleton.getInstance().getUserName().equals(AppSingleton.virtualTel)){//虚拟账号
                    ToastUtils.showShort(requireActivity(),getString(R.string.virtual_mode));
                    return;
                }
                Redirect.startSettingActivity(mContext);
            }
        });

        geoFenceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Redirect.startGeoFenceListActivity(mContext);
            }
        });

        view.findViewById(R.id.llBle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(requireActivity(),BleActivity.class));
            }
        });

        pointSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Redirect.startComingSoonActivity(mContext, "网点查询");
//                startActivity(new Intent(requireActivity(), AddressActivity.class));
            }
        });

        serviceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Redirect.startComingSoonActivity(mContext, "智能服务");
//                if(AppSingleton.getInstance().getUserName().equals("jifang123456789")){//虚拟账号
//                    ToastUtils.showShort(requireActivity(),getString(R.string.virtual_mode));
//                    return;
//                }
                //TODO superLT 暂时隐藏支付功能，上架审核
                Redirect.startPayServiceActivity(mContext);
            }
        });

        addTerminalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppSingleton.getInstance().getUserName().equals(AppSingleton.virtualTel)){//虚拟账号
                    ToastUtils.showShort(requireActivity(),getString(R.string.virtual_mode));
                    return;
                }
                Redirect.startBindTerminalActivity(mContext);
            }
        });
    }
    // 自定义 LinearLayoutManager 解决边界滚动问题
    public static class NonScrollBlockingLinearLayoutManager extends LinearLayoutManager {
        public NonScrollBlockingLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public boolean canScrollHorizontally() {
            return true; // 始终允许水平滚动
        }
    }
    private void updateOta() {
        OkHttpPresent.otaUpdate(AppSingleton.getInstance().getTerminalNo(), new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog(mContext);
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 解绑成功 = " + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null && baseResp.isSuccess()) {
                    ToastUtils.showShort(mContext, "硬件升级请求已下发");
                } else {
                    ToastUtils.showShort(mContext, TextUtils.isEmpty(baseResp.getMsg())?getString(R.string.request_retry):baseResp.getMsg());
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 解绑失败 = " + e);
                ToastUtils.showShort(mContext, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }
    private void initTerminalList() {
        notifyTerminalList(false);
        myTerminalAdapter = new MyTerminalAdapter(mContext, terminalList, new MyTerminalAdapter.OnTerminalItemClickListener() {
            @Override
            public void onItemClick(int position) { //切换设备
                if(AppSingleton.getInstance().getUserName().equals(AppSingleton.virtualTel)){//虚拟账号
                    ToastUtils.showShort(requireActivity(),getString(R.string.virtual_mode));
                    return;
                }
                TerminalBean terminalBean = terminalList.get(Math.min(position, terminalList.size()));
                if (terminalBean == null) {
                    return;
                }
                showBottomSheetDialog(terminalBean);
            }
        });
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(terminalRv);
        terminalRv.setLayoutManager(new NonScrollBlockingLinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        terminalRv.setAdapter(myTerminalAdapter);

//        int paddingRight = DensityUtil.dip2px(mContext,12); // 16dp
//        terminalRv.setPadding(0, 0, paddingRight, 0);
//        terminalRv.setClipToPadding(false);

        // 设置指示器
        indicator.setTotalItems(terminalList.size());
        // 添加滚动监听器
        terminalRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    indicator.updateCurrentItem(firstVisibleItemPosition);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 当滚动停止时，确保位置正确对齐
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (lm != null) {
                        View snapView = snapHelper.findSnapView(lm);
                        if (snapView != null) {
                            int position = lm.getPosition(snapView);
                            recyclerView.smoothScrollToPosition(position);
                        }
                    }
                }
            }
        });

        // 添加边缘效果处理（可选但推荐）
        terminalRv.setEdgeEffectFactory(new RecyclerView.EdgeEffectFactory() {
            @Override
            protected EdgeEffect createEdgeEffect(RecyclerView view, int direction) {
                // 禁用默认的边缘效果
                return new EdgeEffect(view.getContext()) {
                    @Override
                    public void onPull(float deltaDistance) {
                        // 空实现，禁用拉动效果
                    }

                    @Override
                    public void onPull(float deltaDistance, float displacement) {
                        // 空实现，禁用拉动效果
                    }
                };
            }
        });
// 设置水平滚动条（帮助调试）
        terminalRv.setHorizontalScrollBarEnabled(true);
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

        if (notifyData) {
            myTerminalAdapter.notifyDataSetChanged();
            indicator.setTotalItems(terminalList.size());
            LinearLayoutManager layoutManager = (LinearLayoutManager) terminalRv.getLayoutManager();
            int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
            indicator.updateCurrentItem(firstVisiblePosition);
        }

    }

    private void showBottomSheetDialog(TerminalBean terminalBean) {
        String terminalNo = terminalBean.getTerminalNo();
        String terminalName = terminalBean.getName();
        int batteryCount = terminalBean.getBatteries();
        boolean isCurrentTerminal = terminalBean.isSelected();
        IosBottomSheetDialog.Builder builder = new IosBottomSheetDialog.Builder(mContext);
        builder.addTitleView("设备号：" + terminalNo, 0);
        if (!isCurrentTerminal) {   //非当前设备
            builder.addItemView("切换到该设备", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
                @Override
                public void onClick(View view) {
                    new CommonDialog(mContext, new CommonDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {  //确定
                                dialog.dismiss();
                                updateDefaultTerminal(terminalNo, 1);
                            }
                        }
                    })
                            .setTitle("确定切换到该设备吗？")
                            .show();
                }
            });
        }
        builder.addItemView("重命名", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
            @Override
            public void onClick(View view) {
                showEditTerminalNameDialog(terminalNo, terminalName);
            }
        });
        builder.addItemView("修改电池数量", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
            @Override
            public void onClick(View view) {
                showEditBatteryCountDialog(terminalNo, batteryCount);
            }
        });
        if (!isCurrentTerminal) {   //非当前设备
            builder.addItemView("解除绑定", Color.parseColor("#de001f"), false, new IosBottomSheetDialog.OnItemClickListener() {
                @Override
                public void onClick(View view) {
                    new CommonDialog(mContext, new CommonDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                dialog.dismiss();
                                unbindTerminal(terminalNo);
                            }
                        }
                    })
                            .setTitle("确定解除绑定该设备吗？")
                            .show();
                }
            });
        }
        builder.build().show();
    }
    private void doLogout() {
//        AppSingleton.getInstance().clearAllData();
        ActivityManager.finishAllActivity();
        startActivity(new Intent(requireActivity(),SplashActivity.class));
        requireActivity().finish();
    }
    /**
     * 解绑全部设备
     */
    private void unbindAllTerminal() {
        OkHttpPresent.allUnbind(new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog(mContext);
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 解绑成功 = " + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) { //注销成功
                    if (baseResp.isSuccess()) {
                        doLogout();
                    } else {
                        ToastUtils.showShort(requireActivity(), baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(requireActivity(), getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 解绑失败 = " + e);
                ToastUtils.showShort(mContext, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }
    /**
     * 解绑设备
     */
    private void unbindTerminal(String terminalNo) {
        OkHttpPresent.unbindTerminal(terminalNo, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog(mContext);
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 解绑成功 = " + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null && baseResp.isSuccess()) {
                    getDeviceList(false);
                } else {
                    ToastUtils.showShort(mContext, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 解绑失败 = " + e);
                ToastUtils.showShort(mContext, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    /**
     * 设备重命名弹窗
     */
    protected void showEditTerminalNameDialog(String terminalNo, String terminalName) {
        editTerminalNameDialog = new CommonDialog(mContext, new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {  //确定
                    String contentEtText = editTerminalNameDialog.getContentEtText().trim();
                    if (!TextUtils.isEmpty(contentEtText)) {
                        reNameTerminal(terminalNo, contentEtText);
                        dialog.dismiss();
                    } else {
                        ToastUtils.showShort(mContext, "请输入设备名称");
                    }

                }
            }
        })
                .setTitle("设备名称")
                .setHint("请输入设备名称")
                .setPositiveButton("保存")
                .setContentEtVisibility(View.VISIBLE);
        editTerminalNameDialog.setEditContent(terminalName);
        editTerminalNameDialog.show();
    }

    /**
     * 重命名设备
     */
    private void reNameTerminal(String terminalNo, String contentEtText) {
        OkHttpPresent.reNameTerminal(terminalNo, contentEtText, new OkHttpResultCallBack() {

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 重命名成功 = " + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) {
                        getDeviceList(false);
                    } else {
                        ToastUtils.showShort(mContext, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(mContext, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 重命名失败 = " + e);
            }
        });
    }

    /**
     * 修改电池数量弹窗
     */
    protected void showEditBatteryCountDialog(String terminalNo, int batteryCount) {
        editBatteryCountDialog = new CommonDialog(mContext, new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {  //确定
                    String contentEtText = editBatteryCountDialog.getContentEtText().trim();
                    if (!TextUtils.isEmpty(contentEtText)) {
                        changeBatteryCount(terminalNo, contentEtText);
                        dialog.dismiss();
                    } else {
                        ToastUtils.showShort(mContext, "请输入电池数量");
                    }

                }
            }
        })
                .setTitle("电池数量")
                .setHint("请输入电池数量")
                .setPositiveButton("保存")
                .setContentEtVisibility(View.VISIBLE);
        editBatteryCountDialog.setEditContent(batteryCount + "");
        editBatteryCountDialog.setEditContentInputType(EditorInfo.TYPE_CLASS_NUMBER);
        editBatteryCountDialog.show();
    }

    /**
     * 修改电池数量
     */
    private void changeBatteryCount(String terminalNo, String contentEtText) {
        OkHttpPresent.changeBatteryCount(terminalNo, Integer.parseInt(contentEtText), new OkHttpResultCallBack() {

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 修改电池数量成功 = " + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) {
                        getDeviceList(false);
                    } else {
                        ToastUtils.showShort(mContext, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(mContext, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 修改电池数量失败 = " + e);
            }
        });
    }

    /**
     * 设置当前默认设备
     */
    private void updateDefaultTerminal(String terminalNo, int status) {
        OkHttpPresent.updateDefaultTerminal(terminalNo, status, new OkHttpResultCallBack() {

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 当前默认设备设置成功 = " + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) {
//                        AppSingleton.getInstance().setTerminalNo(terminalNo);//保存选中的设备号
                        //重启应用
//                        ActivityManager.finishAllActivity();
//                        Redirect.reStart(mContext);
                        getDeviceList(true);
                        terminalRv.scrollToPosition(0);
                    } else {
                        ToastUtils.showShort(mContext, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(mContext, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 当前默认设备设置失败 = " + e);
            }
        });
    }




    /**
     * 获取设备列表
     * isChangeTerminal是否切换设备，是的话，请求成功后，通知主页刷新
     */
    private void getDeviceList(boolean isChangeTerminal) {
        //获取设备列表
        OkHttpPresent.getDeviceList(new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 获取设备列表成功 = " + body);
                TerminalListResp terminalListResp = JsonUtils.parseT(body, TerminalListResp.class);
                if (terminalListResp != null && terminalListResp.isSuccess()) {
                    if (terminalListResp.getData().size() > 0) {    //已绑定设备
                        TerminalUtils.setCurrentTerminal(terminalListResp.getData());
                        notifyTerminalList(true);
                        if(isChangeTerminal)
                            ((MainActivity)requireActivity()).refreshCurrentTerminal();
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
                hideLoadingDialog();
            }
        });
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BindTerminalEvent bindTerminalEvent) {  //设备绑定成功
        notifyTerminalList(true);
        ((MainActivity)requireActivity()).refreshCurrentTerminal();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (editTerminalNameDialog != null) {
            editTerminalNameDialog.dismiss();
        }
    }
}
