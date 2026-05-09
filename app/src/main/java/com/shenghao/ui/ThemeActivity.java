package com.shenghao.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shenghao.R;
import com.shenghao.adapter.MyTerminalAdapter;
import com.shenghao.adapter.ThemeAdapter;
import com.shenghao.bean.TerminalBean;
import com.shenghao.bean.ThemeBean;
import com.shenghao.bean.ThemeResp;
import com.shenghao.okhttp.OkHttpBaseResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.ui.dialog.ThemeDialog;
import com.shenghao.utility.AppSingleton;
import com.shenghao.utils.DensityUtil;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.StatusBarUtils;
import com.shenghao.utils.StringUtils;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.VerticalProgressBar;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class ThemeActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private List<ThemeBean> dataList = new ArrayList<>();
    private ThemeAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        StatusBarUtils.setStatusBarColor(this, R.color.transparent);
        StatusBarUtils.statusBarLightMode(this);
        initViews();
        getThemeList();
    }

    @Override
    protected void initViews() {
        super.initViews();
        View statusBarView = findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(this, statusBarView);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(ThemeActivity.this,2));
        adapter = new ThemeAdapter(ThemeActivity.this, dataList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ThemeAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos) {
                ThemeBean img = dataList.get(pos);
                new ThemeDialog(ThemeActivity.this, new ThemeDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean refresh) {
                        if(refresh){
                            getThemeList();
                        }
                    }
                }).setThemeBean(img).show();
            }
        });
    }

    public void getThemeList() {
        OkHttpPresent.getTheme(new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) {
                ThemeResp loginResp = JsonUtils.parseT(body, ThemeResp.class);
                if (loginResp != null && loginResp.isSuccess()) {
                    List<ThemeBean> data = loginResp.getData();
                    dataList.clear();
                    dataList.addAll(data);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showShort(ThemeActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                ToastUtils.showShort(ThemeActivity.this, getString(R.string.request_retry));
                hideLoadingDialog();
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

}
