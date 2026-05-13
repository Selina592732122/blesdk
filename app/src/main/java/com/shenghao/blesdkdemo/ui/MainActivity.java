package com.shenghao.blesdkdemo.ui;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.TypeReference;
import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.BaseHttpResp;
import com.shenghao.blesdkdemo.bean.NoticeDialogBean;
import com.shenghao.blesdkdemo.bean.ThemeBean;
import com.shenghao.blesdkdemo.bean.ThemeResp;
import com.shenghao.blesdkdemo.bean.UserInfo;
import com.shenghao.blesdkdemo.bean.UserInfoResp;
import com.shenghao.blesdk.enums.BluetoothStatus;
import com.shenghao.blesdk.listener.BluetoothStateChangeListener;
import com.shenghao.blesdk.receiver.BluetoothReceiver;
import com.shenghao.blesdk.service.BluetoothService;
import com.shenghao.blesdkdemo.event.AppBackgroundEvent;
import com.shenghao.blesdkdemo.event.RefreshEvent;
import com.shenghao.blesdkdemo.event.ThemeChangeEvent;
import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.okhttp.OkHttpURLs;
import com.shenghao.blesdkdemo.okhttp.OkSseHelper;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;
import com.shenghao.blesdkdemo.widget.CommonDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private LinearLayout vehicleTabLayout;
    private LinearLayout msgTabLayout;
    private LinearLayout myTabLayout;
    private LinearLayout storeTabLayout;
    private ImageView vehicleTabIcon;
    private ImageView msgTabIcon;
    private ImageView myTabIcon;
    private ImageView storeTabIcon;
    private TextView vehicleTabTv;
    private TextView msgTabTv;
    private TextView myTabTv;
    private ImageView messageRedDot;
    private TextView storeTabTv;

    private CommonDialog noticeDialog;  //报警弹窗
    private String noticeDialogContent; //报警弹窗内容
    private boolean isResume;

    private Fragment[] fragments = new Fragment[4];
    private int currentFragmentIndex = 0;
//    private BluetoothUtils bluetoothUtils;
    private BroadcastReceiver bluetoothStatusReceiver = new BluetoothReceiver(new BluetoothStateChangeListener() {
        @Override
        public void onBluetoothOff() {
            // 蓝牙已关闭
//            List<BleDevice> allConnectedDevice = BleManager.getInstance().getAllConnectedDevice();
//            for (BleDevice device: allConnectedDevice) {
//                BleManager.getInstance().disconnect(device);
//                EventBus.getDefault().post(new BleStatusEvent(BluetoothStatus.DISCONNECTED,device.getMac()));
//            }
//            BluetoothService.updateAutoConnectStatus(MainActivity.this,false);
//            if(bluetoothUtils != null)
//                bluetoothUtils.stop();
        }

        @Override
        public void onBluetoothTurningOff() {
            // 蓝牙正在关闭
        }

        @Override
        public void onBluetoothOn() {
            // 蓝牙已打开
//            List<BleDevice> allConnectedDevice = BleManager.getInstance().getAllConnectedDevice();
//            for (BleDevice device: allConnectedDevice) {
//                EventBus.getDefault().post(new BleStatusEvent(BluetoothStatus.CONNECTED,device.getMac()));
//            }
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    BluetoothService.updateAutoConnectStatus(MainActivity.this,true);
//                }
//            },1000);
//            if(bluetoothUtils != null)
//                bluetoothUtils.start();
        }

        @Override
        public void onBluetoothTurningOn() {
            // 蓝牙正在打开
        }
    });
    private View llBottom;
    private String noticeDialogTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.transparencyBar(this);
//        StatusBarUtils.statusBarDarkMode(this);
        setContentView(R.layout.activity_main);
        initViews();
        // 直接为需要适配的View设置WindowInsets监听
        ViewCompat.setOnApplyWindowInsetsListener(llBottom, (view, windowInsets) -> {
            int bottomInset = windowInsets.getSystemWindowInsetBottom();
            if (bottomInset > 0) {
                // 有底部手势条，设置padding
                view.setPadding(view.getPaddingLeft(),
                        view.getPaddingTop(),
                        view.getPaddingRight(),
                        bottomInset);
            }
            return windowInsets;
        });
        initListener();
        initVehicleFragment();
        doNoticeListener();
        EventBus.getDefault().register(this);
//        bluetoothUtils = new BluetoothUtils(this);
//        bluetoothUtils.start();
        // 启动服务
        Intent serviceIntent = new Intent(this, BluetoothService.class);
        startService(serviceIntent);
        BluetoothService.updateAutoConnectStatus(this,true);
        // 注册蓝牙状态接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStatusReceiver,filter);
        getUserInfo();
        getThemeList();
//        String registrationID = JPushInterface.getRegistrationID(this);
//        bindJpushId(registrationID);
    }
    public void getThemeList() {
        OkHttpPresent.getTheme(new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
            }

            @Override
            protected void onResponse(Response response, String body) {
                ThemeResp loginResp = JsonUtils.parseT(body, ThemeResp.class);
                if (loginResp != null && loginResp.isSuccess()) {
                    List<ThemeBean> data = loginResp.getData();
                    for (int i = 0; i < data.size(); i++) {
                        ThemeBean bean = data.get(i);
                        if("1".equals(bean.getIsCurrentTheme())){
//                            if(fragments[0] != null)
//                                ((VehicleFragment)fragments[0]).refreshTheme(bean.getThemeImage());
                        }
                    }
                } else {
//                    ToastUtils.showShort(MainActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
//                ToastUtils.showShort(MainActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
            }
        });
    }

    /**
     * 获取通知未读数量
     */
    public void getNoticeUnreadNum() {
        OkHttpPresent.getNoticeUnreadNum(new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "通知消息未读数请求成功：" + body);
                BaseHttpResp<Integer> resp = JsonUtils.parseObject(body, new TypeReference<BaseHttpResp<Integer>>() {
                });
                if (resp != null && resp.isSuccess()) {
                    int unreadCount = resp.getData();
                    if (unreadCount > 0) {
                        messageRedDot.setVisibility(View.VISIBLE);
                    } else {
                        messageRedDot.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "通知消息未读数请求失败：" + e);
            }
        });
    }
    private void bindJpushId(String registrationID){
        OkHttpPresent.bindJpushId(registrationID,new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 成功 = " + body);
                OkHttpBaseResp okHttpBaseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (okHttpBaseResp != null && okHttpBaseResp.isSuccess()) {
                }else {
                    ToastUtils.showShort(MainActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 失败 = " + e);
                ToastUtils.showShort(MainActivity.this, getString(R.string.request_retry));
            }
        });
    }
    private void getUserInfo(){
        OkHttpPresent.getUserInfo(new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 成功 = " + body);
                UserInfoResp userInfoResp = JsonUtils.parseT(body, UserInfoResp.class);
                if (userInfoResp != null && userInfoResp.isSuccess()) {
                    UserInfo userInfo = userInfoResp.getData();
                    AppSingleton.getInstance().setUserInfo(userInfo);
                }else {
                    ToastUtils.showShort(MainActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 失败 = " + e);
                ToastUtils.showShort(MainActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
        getNoticeUnreadNum();//消息条数
        showNoticeDialog();
        if(currentFragmentIndex == 0){
            //首页可见，更新蓝牙状态
            updateBle();
        }
        BluetoothService.updateAutoConnectStatus(MainActivity.this,true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }

    @Override
    protected void initViews() {
        super.initViews();
        llBottom = findViewById(R.id.llBottom);
        vehicleTabLayout = findViewById(R.id.vehicleTabLayout);
        msgTabLayout = findViewById(R.id.msgTabLayout);
        myTabLayout = findViewById(R.id.myTabLayout);
        vehicleTabIcon = findViewById(R.id.vehicleTabIcon);
        msgTabIcon = findViewById(R.id.msgTabIcon);
        myTabIcon = findViewById(R.id.myTabIcon);
        storeTabLayout = findViewById(R.id.storeTabLayout);
        storeTabIcon = findViewById(R.id.storeTabIcon);
        storeTabTv = findViewById(R.id.storeTabTv);
        vehicleTabTv = findViewById(R.id.vehicleTabTv);
        msgTabTv = findViewById(R.id.msgTabTv);
        myTabTv = findViewById(R.id.myTabTv);
        messageRedDot = findViewById(R.id.badge_tv);
    }

    private void initListener() {
        vehicleTabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVehicleTab();
                StatusBarUtils.setStatusBarColor(MainActivity.this,R.color.transparent);
                StatusBarUtils.statusBarDarkMode(MainActivity.this);
                updateBle();//首页可见，更新蓝牙状态
            }
        });
        msgTabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMsgTab();
                StatusBarUtils.setStatusBarColor(MainActivity.this,R.color.transparent);
                StatusBarUtils.statusBarLightMode(MainActivity.this);
            }
        });
        myTabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMyTab();
                StatusBarUtils.setStatusBarColor(MainActivity.this,R.color.transparent);
                StatusBarUtils.statusBarLightMode(MainActivity.this);
            }
        });
        storeTabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStoreTab();
                StatusBarUtils.setStatusBarColor(MainActivity.this,R.color.transparent);
                StatusBarUtils.statusBarLightMode(MainActivity.this);
            }
        });
    }

    private void updateBle() {
//        ((VehicleFragment)fragments[0]).updateBle();
    }

    private void selectVehicleTab() {
        if (currentFragmentIndex == 0) {
            return;
        }
        initVehicleFragment();
    }

    private void selectStoreTab() {
        if (currentFragmentIndex == 1) {
            return;
        }
        initStoreFragment();
    }

    private void selectMsgTab() {
        if (currentFragmentIndex == 2) {
            return;
        }
        initMsgFragment();
    }

    private void selectMyTab() {
        if (currentFragmentIndex == 3) {
            return;
        }
        initMyFragment();
    }

    private void initVehicleFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        if (fragments[0] == null) {
//            fragments[0] = VehicleFragment.newInstance();
//            transaction.add(R.id.main_fragment_container, fragments[0], "vehicle");
//        }
//        hideExcludeIndex(0, transaction);
    }

    private void initStoreFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragments[1] == null) {
            fragments[1] = StoreFragment.newInstance();
            transaction.add(R.id.main_fragment_container, fragments[1], "store");
        }
        hideExcludeIndex(1, transaction);
    }

    private void initMsgFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragments[2] == null) {
            fragments[2] = MsgFragment.newInstance();
            transaction.add(R.id.main_fragment_container, fragments[2], "msg");
        }
        hideExcludeIndex(2, transaction);
    }

    private void initMyFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragments[3] == null) {
            fragments[3] = MyFragment.newInstance();
            transaction.add(R.id.main_fragment_container, fragments[3], "my");
        }
        hideExcludeIndex(3, transaction);
    }

    private void hideExcludeIndex(int fragmentIndex, FragmentTransaction transaction) {
        for (int i = 0; i < fragments.length; i++) {
            if (i != fragmentIndex && fragments[i] != null) {
                transaction.hide(fragments[i]);
            }
        }
        transaction.show(fragments[fragmentIndex]);
        transaction.commit();
        updateSelectedTab(fragmentIndex);
        currentFragmentIndex = fragmentIndex;
    }

    private void updateSelectedTab(int fragmentIndex) {
        if (currentFragmentIndex == fragmentIndex) {
            return;
        }

        if (fragmentIndex == 0) {
            vehicleTabIcon.setImageResource(R.drawable.ic_home_selected);
            msgTabIcon.setImageResource(R.drawable.ic_msg_unselected);
            myTabIcon.setImageResource(R.drawable.ic_my_unselected);
            storeTabIcon.setImageResource(R.drawable.ic_store_unselected);
            vehicleTabTv.setTextColor(ContextCompat.getColor(this, R.color.main_tab_selected));
            msgTabTv.setTextColor(ContextCompat.getColor(this, R.color.main_tab_unselected));
            myTabTv.setTextColor(ContextCompat.getColor(this, R.color.main_tab_unselected));
            storeTabTv.setTextColor(ContextCompat.getColor(this, R.color.main_tab_unselected));
        }
        else if (fragmentIndex == 1) {
            vehicleTabIcon.setImageResource(R.drawable.ic_home_unselected);
            msgTabIcon.setImageResource(R.drawable.ic_msg_unselected);
            myTabIcon.setImageResource(R.drawable.ic_my_unselected);
            storeTabIcon.setImageResource(R.drawable.ic_store_selected);
            vehicleTabTv.setTextColor(ContextCompat.getColor(this, R.color.main_tab_unselected));
            msgTabTv.setTextColor(ContextCompat.getColor(this, R.color.main_tab_unselected));
            myTabTv.setTextColor(ContextCompat.getColor(this, R.color.main_tab_unselected));
            storeTabTv.setTextColor(ContextCompat.getColor(this, R.color.main_tab_selected));
        }
        else if (fragmentIndex == 2) {
            vehicleTabIcon.setImageResource(R.drawable.ic_home_unselected);
            msgTabIcon.setImageResource(R.drawable.ic_msg_selected);
            myTabIcon.setImageResource(R.drawable.ic_my_unselected);
            storeTabIcon.setImageResource(R.drawable.ic_store_unselected);
            vehicleTabTv.setTextColor(ContextCompat.getColor(this, R.color.main_tab_unselected));
            msgTabTv.setTextColor(ContextCompat.getColor(this, R.color.main_tab_selected));
            myTabTv.setTextColor(ContextCompat.getColor(this, R.color.main_tab_unselected));
            storeTabTv.setTextColor(ContextCompat.getColor(this, R.color.main_tab_unselected));
        } else {
            vehicleTabIcon.setImageResource(R.drawable.ic_home_unselected);
            msgTabIcon.setImageResource(R.drawable.ic_msg_unselected);
            myTabIcon.setImageResource(R.drawable.ic_my_selected);
            storeTabIcon.setImageResource(R.drawable.ic_store_unselected);
            vehicleTabTv.setTextColor(ContextCompat.getColor(this, R.color.main_tab_unselected));
            msgTabTv.setTextColor(ContextCompat.getColor(this, R.color.main_tab_unselected));
            myTabTv.setTextColor(ContextCompat.getColor(this, R.color.main_tab_selected));
            storeTabTv.setTextColor(ContextCompat.getColor(this, R.color.main_tab_unselected));
        }
    }

    /**
     * 报警监听(SSE)
     */
    private void doNoticeListener() {
        OkSseHelper.getInstance().connectSSE(OkHttpURLs.getNoticeListener(), new EventSourceListener() {
            @Override
            public void onClosed(@NonNull EventSource eventSource) {
                super.onClosed(eventSource);
                LogUtils.e(TAG, "SSE =>> onClosed!!!");
            }

            @Override
            public void onEvent(@NonNull EventSource eventSource, @Nullable String id, @Nullable String type, @NonNull String data) {
                super.onEvent(eventSource, id, type, data);
                LogUtils.e(TAG, "SSE =>> onEvent: id = " + id + ", type = " + type + ", data = " + data);
                if (TextUtils.equals(type, "notice")) {    //只处理notice告警类型的event
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //这里不能直接弹窗，部分设备存在activity处于非resume生命周期时，弹窗弹出失败的问题
                            NoticeDialogBean noticeDialogBean = JsonUtils.parseT(data, NoticeDialogBean.class);
                            if (noticeDialogBean != null) {
                                noticeDialogTitle = noticeDialogBean.getTitle();
                                noticeDialogContent = noticeDialogBean.getContent();    //弹窗内容赋值
                                if (isResume) { //当前activity已处于onResume，直接弹出弹窗；否则，待onResume时再进行弹窗
                                    showNoticeDialog();
                                }
                            }
                        }
                    });
                }else if(TextUtils.equals(type, "updateCarControl")){
                    LogUtils.e(TAG,"当前线程："+Thread.currentThread().getName());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            ToastUtils.showShort(MainActivity.this,"该刷新了！！！");
                            EventBus.getDefault().post(new RefreshEvent(true));
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                super.onFailure(eventSource, t, response);
                LogUtils.e(TAG, "SSE =>> onFailure: " + t);
            }

            @Override
            public void onOpen(@NonNull EventSource eventSource, @NonNull Response response) {
                super.onOpen(eventSource, response);
                LogUtils.e(TAG, "SSE =>> onOpen!!!");
            }
        });
    }

    private void cancelNoticeListener() {
        OkSseHelper.getInstance().closeSSE();
    }

    /**
     * 报警弹窗
     */
    private void showNoticeDialog() {
        if (TextUtils.isEmpty(noticeDialogContent)) {
            return;
        }
        dismissNoticeDialog();
        noticeDialog = new CommonDialog(MainActivity.this, new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {  //跳转至消息中心
//                    Redirect.startNoticeCenterActivity(MainActivity.this);
                    selectMsgTab();
                    StatusBarUtils.setStatusBarColor(MainActivity.this,R.color.transparent);
                    StatusBarUtils.statusBarLightMode(MainActivity.this);
                    dialog.dismiss();
                }
            }
        })
                .setMessageType(CommonDialog.MessageType.ALARM)
                .setTitle(noticeDialogTitle)
                .setContent(noticeDialogContent)
                .setContentVisibility(View.VISIBLE)
                .setPositiveButton("查看")
                .setNegativeButton("知道了");
        noticeDialog.show();
        noticeDialogContent = "";   //将弹窗内容置空
    }

    private void dismissNoticeDialog() {
        if (noticeDialog != null && noticeDialog.isShowing()) {
            noticeDialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AppBackgroundEvent backgroundEvent) {  //前后台切换
        if (backgroundEvent.isBackground()) {    //处于后台
            cancelNoticeListener();
        } else { //回到前台
            doNoticeListener();
        }
    }
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onBleStatusEvent(BleStatusEvent event){
////        ToastUtils.showShort(this,"BleStatus");
//        updateBle();
//    }
//    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onThemeChangeEvent(ThemeChangeEvent event){
//        if(fragments[0] != null)
//            ((VehicleFragment)fragments[0]).refreshTheme(event.getImagePath());
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissNoticeDialog();
        cancelNoticeListener();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(bluetoothStatusReceiver);
    }

    public void refreshCurrentTerminal() {
        //切换其他设备后，主页车辆信息那些都要刷新
//        if(fragments[0] != null)
//            ((VehicleFragment)fragments[0]).refreshCurrentTerminal();
    }

    public void showTab(boolean b) {
        llBottom.setVisibility(b?View.VISIBLE:View.GONE);
    }
}
