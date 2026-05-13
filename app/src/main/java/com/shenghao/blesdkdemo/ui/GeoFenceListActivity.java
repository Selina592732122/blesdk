package com.shenghao.blesdkdemo.ui;


import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.adapter.GeoFenceAdapter;
import com.shenghao.blesdkdemo.bean.GeoFenceBean;
import com.shenghao.blesdkdemo.bean.GeoFenceListResp;
import com.shenghao.blesdkdemo.constant.Const;
import com.shenghao.blesdkdemo.event.AddGeoFenceEvent;
import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.Redirect;
import com.shenghao.blesdkdemo.utils.ToastUtils;
import com.shenghao.blesdkdemo.widget.CommonDialog;
import com.shenghao.blesdkdemo.widget.IosBottomSheetDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 电子围栏列表页
 */
public class GeoFenceListActivity extends BaseActivity {
    private ViewGroup addGeofenceLayout;
    private TextView noGeofenceDataTv;
    private SmartRefreshLayout refreshLayout;

    private RecyclerView geoFenceRv;
    private GeoFenceAdapter geoFenceAdapter;
    private List<GeoFenceBean> geoFenceList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence_list);
        initViews();
        initListener();
        initList();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        addGeofenceLayout = findViewById(R.id.addGeofenceLayout);
        noGeofenceDataTv = findViewById(R.id.noGeofenceDataTv);
        refreshLayout = findViewById(R.id.refreshLayout);
        geoFenceRv = findViewById(R.id.geoFenceRv);
    }

    private void initListener() {
        addGeofenceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddGeofenceBottomSheet();
            }
        });
    }

    private void initList() {
        geoFenceAdapter = new GeoFenceAdapter(this, geoFenceList, new GeoFenceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                GeoFenceBean geoFenceBean = geoFenceList.get(position);
                if (geoFenceBean == null) {
                    return;
                }
                if (TextUtils.equals(Const.TYPE_GEOFENCE_CIRCULAR, geoFenceBean.getFenceType())) {    //圆形围栏
                    Redirect.startAddGeoFenceCircleActivity(GeoFenceListActivity.this, geoFenceBean.getId());
                } else {    //自定义围栏
                    Redirect.startAddGeoFencePolygonActivity(GeoFenceListActivity.this, geoFenceBean.getId());
                }
            }

            @Override
            public void onItemLongClick(int position) {
                GeoFenceBean geoFenceBean = geoFenceList.get(position);
                if (geoFenceBean == null) {
                    return;
                }
                showDeleteDialog(geoFenceBean.getId());
            }
        });
        geoFenceRv.setLayoutManager(new LinearLayoutManager(this));
        geoFenceRv.setAdapter(geoFenceAdapter);

        refreshLayout.setEnableOverScrollBounce(false);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setRefreshHeader(new MaterialHeader(this).setColorSchemeResources(R.color.theme_orange));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                LogUtils.e(TAG, "onRefresh: 正在刷新！");
                getGeofenceList();
            }
        });
        refreshLayout.autoRefresh(1);
    }

    /**
     * 获取电子围栏列表
     */
    private void getGeofenceList() {
        OkHttpPresent.getGeofenceList(AppSingleton.getInstance().getTerminalNo(), new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "电子围栏列表获取成功：" + body);
                GeoFenceListResp geoFenceListResp = JsonUtils.parseT(body, GeoFenceListResp.class);
                if (geoFenceListResp != null && geoFenceListResp.isSuccess()) {
                    if (geoFenceListResp.getData() != null && geoFenceListResp.getData().size() > 0) {
                        geoFenceList.clear();
                        geoFenceList.addAll(geoFenceListResp.getData());
                        geoFenceAdapter.notifyDataSetChanged();
                        noGeofenceDataTv.setVisibility(View.GONE);
                        refreshLayout.setVisibility(View.VISIBLE);
                    } else { //没有电子围栏
                        noGeofenceDataTv.setVisibility(View.VISIBLE);
                        refreshLayout.setVisibility(View.GONE);
                    }
                } else {
                    ToastUtils.showShort(GeoFenceListActivity.this, getString(R.string.request_retry));
                    noGeofenceDataTv.setVisibility(View.VISIBLE);
                    refreshLayout.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "电子围栏列表获取失败：" + e);
                ToastUtils.showShort(GeoFenceListActivity.this, getString(R.string.request_retry));
                noGeofenceDataTv.setVisibility(View.VISIBLE);
                refreshLayout.setVisibility(View.GONE);
            }

            @Override
            protected void end() {
                super.end();
                finishRefreshAndLoadMoreUI();
            }
        });
    }

    private void showAddGeofenceBottomSheet() {
        IosBottomSheetDialog.Builder builder = new IosBottomSheetDialog.Builder(this);
        builder.addItemView("添加圆形围栏", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
            @Override
            public void onClick(View view) {
                Redirect.startAddGeoFenceCircleActivity(GeoFenceListActivity.this, 0);
            }
        });
        builder.addItemView("添加自定义围栏", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
            @Override
            public void onClick(View view) {
                Redirect.startAddGeoFencePolygonActivity(GeoFenceListActivity.this, 0);
            }
        });
        builder.build().show();
    }

    private void finishRefreshAndLoadMoreUI() {
        if (refreshLayout != null) {
            if (refreshLayout.isRefreshing()) {
                refreshLayout.finishRefresh();
            }
            if (refreshLayout.isLoading()) {
                refreshLayout.finishLoadMore();
            }
        }
    }

    /**
     * 删除弹窗
     */
    private void showDeleteDialog(int geofenceId) {
        new CommonDialog(GeoFenceListActivity.this, new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {  //确定
                    removeGeofence(geofenceId);
                    dialog.dismiss();
                }
            }
        })
                .setTitle("确定删除电子围栏？")
                .setPositiveButton("删除")
                .setPositiveButtonColor(ContextCompat.getColor(GeoFenceListActivity.this, R.color.white))
                .show();
    }

    /**
     * 删除电子围栏
     */
    private void removeGeofence(int geofenceId) {
        OkHttpPresent.removeGeofence(geofenceId, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "删除围栏成功：" + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) {
                        getGeofenceList();
                        ToastUtils.showShort(GeoFenceListActivity.this, "删除成功");
                    } else {
                        ToastUtils.showShort(GeoFenceListActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(GeoFenceListActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                ToastUtils.showShort(GeoFenceListActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AddGeoFenceEvent addGeoFenceEvent) {  //添加电子围栏成功
        getGeofenceList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
