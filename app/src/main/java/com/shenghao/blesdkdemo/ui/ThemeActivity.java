package com.shenghao.blesdkdemo.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.adapter.ThemeAdapter;
import com.shenghao.blesdkdemo.bean.ThemeBean;
import com.shenghao.blesdkdemo.bean.ThemeResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.ui.dialog.ThemeDialog;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;

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
