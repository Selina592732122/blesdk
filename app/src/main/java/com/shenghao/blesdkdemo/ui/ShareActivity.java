package com.shenghao.blesdkdemo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.adapter.ShareUserAdapter;
import com.shenghao.blesdkdemo.bean.QRCodeResp;
import com.shenghao.blesdkdemo.bean.ShareUser;
import com.shenghao.blesdkdemo.bean.ShareUserResp;
import com.shenghao.blesdkdemo.event.BindTerminalEvent;
import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.ui.dialog.AcountShareDialog;
import com.shenghao.blesdkdemo.ui.dialog.QrcodeDialog;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.ActivityManager;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.Redirect;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;
import com.shenghao.blesdkdemo.widget.EmptyRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class ShareActivity extends BaseActivity {
    public final String TAG = this.getClass().getSimpleName();
    private EmptyRecyclerView recyclerViewUser;
    private TextView tvShareUser;
    private List<ShareUser> users = new ArrayList<>();
    private ShareUserAdapter userAdapter;
    private TextView tvEnough,tvNum,tvExit;
    private View llInvite,llUsers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setStatusBarColor(this,R.color.transparent);
        StatusBarUtils.statusBarLightMode(this);
        setContentView(R.layout.activity_share);
        initViews();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getSharedList();
        EventBus.getDefault().post(new BindTerminalEvent(""));//刷新我的页面的分享列表
    }

    @Override
    protected void initViews() {
        super.initViews();
        //设置状态栏高度
        View statusBarView = findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(this, statusBarView);
        TextView tvShare = findViewById(R.id.tvShare);
        tvShare.setText(AppSingleton.getInstance().getTerminalNo()+getString(R.string.someone_is_share));
        tvShareUser = findViewById(R.id.tvShareUser);
        tvEnough = findViewById(R.id.tvEnough);
        tvNum = findViewById(R.id.tvNum);
        tvExit = findViewById(R.id.tvExit);
        llInvite = findViewById(R.id.llInvite);
        llUsers = findViewById(R.id.llUsers);
        findViewById(R.id.llCode).setOnClickListener(v -> {
            generateQRCode();
        });
        findViewById(R.id.llAcount).setOnClickListener(v -> {
            new AcountShareDialog(this,null)
                    .setDialogCancelable(true)
                    .show();
        });
        tvExit.setOnClickListener(v -> {
            //退出共享
            cancelShare();
        });

        recyclerViewUser = findViewById(R.id.recyclerViewUser);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new ShareUserAdapter(this, users);
        recyclerViewUser.setAdapter(userAdapter);
        recyclerViewUser.setEmptyView(findViewById(R.id.tvEmpty));
//        notifyRecyclerView();
    }

    private void generateQRCode() {
        OkHttpPresent.generateQRCode(AppSingleton.getInstance().getTerminalNo(), new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "登录成功: " + body);
                QRCodeResp loginResp = JsonUtils.parseT(body, QRCodeResp.class);
                if (loginResp != null) {
                    if (loginResp.isSuccess()&& loginResp.getData()!=null) {
                        String code = loginResp.getData().getTerminalNo() + "&" + loginResp.getData().getInviterPhone() + "&" + loginResp.getData().getTimestamp();
                        new QrcodeDialog(ShareActivity.this,null)
                        .setContent(code)
                        .setDialogCancelable(true)
                        .show();
                    } else {    //登录失败
                        ToastUtils.showShort(ShareActivity.this, loginResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(ShareActivity.this, getString(R.string.request_retry));
                }


                hideLoadingDialog();
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(ShareActivity.this, getString(R.string.request_retry));
                 hideLoadingDialog();
            }

        });
    }
    private void getSharedList() {
        OkHttpPresent.getSharedList(AppSingleton.getInstance().getTerminalNo(), new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "登录成功: " + body);
                ShareUserResp loginResp = JsonUtils.parseT(body, ShareUserResp.class);
                if (loginResp != null) {
                    List<ShareUser> data = loginResp.getData();
                    if (loginResp.isSuccess()&& data !=null) {
                        notifyRecyclerView(data);
                        llUsers.setVisibility(View.VISIBLE);
                    } else {    //登录失败
                        ToastUtils.showShort(ShareActivity.this, loginResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(ShareActivity.this, getString(R.string.request_retry));
                }


                hideLoadingDialog();
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(ShareActivity.this, getString(R.string.request_retry));
                 hideLoadingDialog();
            }

        });
    }
    private void cancelShare() {
        OkHttpPresent.cancelShare(AppSingleton.getInstance().getTerminalNo(),null, new OkHttpResultCallBack() {
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
                        //重启应用
                        ActivityManager.finishAllActivity();
                        Redirect.reStart(ShareActivity.this);
                    } else {    //登录失败
                        ToastUtils.showShort(ShareActivity.this, loginResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(ShareActivity.this, getString(R.string.request_retry));
                }


                hideLoadingDialog();
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(ShareActivity.this, getString(R.string.request_retry));
                 hideLoadingDialog();
            }

        });
    }

    
    private void notifyRecyclerView(List<ShareUser> data){
        users.clear();
        int shareStatus = AppSingleton.getInstance().getCurrentTerminal().getShareStatus();
        for (int i = 0; i < data.size(); i++) {
            users.add(data.get(i));
        }

        tvShareUser.setText(getString(R.string.share_user)+"（"+users.size()+"/3）");
        userAdapter.notifyDataSetChanged();
        tvEnough.setVisibility(users.size() == 3 ? View.VISIBLE:View.GONE);
        llInvite.setVisibility(users.size() == 3 ||  shareStatus == 2 ? View.GONE:View.VISIBLE);
        tvNum.setText(users.size()+"");
        tvExit.setVisibility(shareStatus == 2 ? View.VISIBLE:View.GONE);
    }

}
