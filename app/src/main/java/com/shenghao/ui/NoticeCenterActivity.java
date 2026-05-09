package com.shenghao.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.shenghao.R;
import com.shenghao.adapter.NoticeListAdapter;
import com.shenghao.bean.NoticeData;
import com.shenghao.bean.NoticeListResp;
import com.shenghao.okhttp.OkHttpBaseResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.Redirect;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.CommonDialog;
import com.shenghao.widget.IosBottomSheetDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 通知中心
 */
public class NoticeCenterActivity extends BaseActivity {
    public static final int PAGE_SIZE = 20;
    private TextView titleSettingTv;
    private SmartRefreshLayout refreshLayout;
    private View noNoticeLayout;
    private RecyclerView noticeRv;
    private NoticeListAdapter noticeListAdapter;
    private List<NoticeData> noticeDataList = new ArrayList<>();
    private int noticeStartId;  //通知startId，用于请求下一页通知数据

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_center);
        initViews();
        refreshLayout.autoRefresh(1);
    }

    @Override
    protected void initViews() {
        super.initViews();
        titleSettingTv = findViewById(R.id.titleSettingTv);
        noNoticeLayout = findViewById(R.id.noNoticeLayout);
        refreshLayout = findViewById(R.id.refreshLayout);
        noticeRv = findViewById(R.id.noticeRv);

        noticeListAdapter = new NoticeListAdapter(this, noticeDataList, new NoticeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                NoticeData noticeData = noticeDataList.get(position);
                if (noticeData == null) {
                    return;
                }
                noticeData.setReadStatus(1);    //打开详情，将该条通知置为已读状态
                noticeListAdapter.notifyItemChanged(position);
                Redirect.startNoticeDetailActivity(NoticeCenterActivity.this, noticeData.getId());
            }

            @Override
            public void onItemLongClick(int position) {
                showDeleteDialog(position);
            }
        });
        noticeRv.setLayoutManager(new LinearLayoutManager(this));
        noticeRv.setAdapter(noticeListAdapter);

        refreshLayout.setEnableOverScrollBounce(false);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        refreshLayout.setRefreshHeader(new MaterialHeader(this).setColorSchemeResources(R.color.theme_orange));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                LogUtils.e(TAG, "onRefresh: 正在刷新！");
                noticeStartId = 0;
                noticeDataList.clear();
                getNoticeList(Integer.MAX_VALUE);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                LogUtils.e(TAG, "onLoadMore: 正在加载更多！");
                if (noticeStartId > 0) {
                    getNoticeList(noticeStartId);
                }
            }
        });

        titleSettingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditBottomSheet();
            }
        });
    }

    /**
     * 获取通知列表
     */
    private void getNoticeList(int startId) {
        OkHttpPresent.getNoticeList(startId, PAGE_SIZE, new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                Log.e(TAG, "onResponse: 通知列表请求成功 = " + body);
                NoticeListResp resp = JsonUtils.parseT(body, NoticeListResp.class);
                if (resp != null && resp.isSuccess() && resp.getData() != null && resp.getData().size() > 0) {
                    List<NoticeData> result = resp.getData();
                    noNoticeLayout.setVisibility(View.GONE);
                    refreshLayout.setVisibility(View.VISIBLE);
                    noticeDataList.addAll(resp.getData());
                    noticeListAdapter.notifyDataSetChanged();
                    noticeStartId = result.get(result.size() - 1).getId(); //记录当前页最小的noticeId，用于下一页请求
                    if (result.size() < PAGE_SIZE) {    //不满一页说明所有数据请求完毕
                        refreshLayout.finishLoadMoreWithNoMoreData();
                    }
                } else { //无数据或请求失败
                    noNoticeLayout.setVisibility(View.VISIBLE);
                    refreshLayout.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                Log.e(TAG, "onFailed: 通知列表请求失败 = " + e);
                noNoticeLayout.setVisibility(View.VISIBLE);
                refreshLayout.setVisibility(View.GONE);
                ToastUtils.showShort(NoticeCenterActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                finishRefreshAndLoadMoreUI();
            }
        });
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
    private void showDeleteDialog(int position) {
        NoticeData noticeData = noticeDataList.get(position);
        if (noticeData == null) {
            return;
        }
        new CommonDialog(NoticeCenterActivity.this, new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {  //确定
                    removeNotice(noticeData.getId(), position);
                    dialog.dismiss();
                }
            }
        })
                .setTitle("确定删除通知消息？")
                .setPositiveButton("删除")
                .setPositiveButtonColor(ContextCompat.getColor(NoticeCenterActivity.this, R.color.white))
                .show();
    }

    /**
     * 删除通知消息
     */
    private void removeNotice(int noticeId, int position) {
        OkHttpPresent.removeNotice(String.valueOf(noticeId), new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "删除通知消息成功：" + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) {
                        noticeDataList.remove(position);
                        noticeListAdapter.notifyDataSetChanged();
                        ToastUtils.showShort(NoticeCenterActivity.this, "删除成功");
                    } else {
                        ToastUtils.showShort(NoticeCenterActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(NoticeCenterActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "删除通知消息失败：" + e);
                ToastUtils.showShort(NoticeCenterActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    /**
     * 已读全部通知消息
     */
    private void readAllNotice() {
        OkHttpPresent.readAllNotice(new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "已读全部通知消息成功：" + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) {
                        for (NoticeData noticeData : noticeDataList) {
                            noticeData.setReadStatus(1);
                        }
                        noticeListAdapter.notifyDataSetChanged();
                        ToastUtils.showShort(NoticeCenterActivity.this, getString(R.string.do_success));
                    } else {
                        ToastUtils.showShort(NoticeCenterActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(NoticeCenterActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "已读全部通知消息失败：" + e);
                ToastUtils.showShort(NoticeCenterActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    /**
     * 删除全部通知消息
     */
    private void removeAllNotice() {
        OkHttpPresent.removeAllNotice(new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "删除全部通知消息成功：" + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) {
                        noticeDataList.clear();
                        noticeListAdapter.notifyDataSetChanged();
                        noNoticeLayout.setVisibility(View.VISIBLE);
                        refreshLayout.setVisibility(View.GONE);
                        ToastUtils.showShort(NoticeCenterActivity.this, "删除成功");
                    } else {
                        ToastUtils.showShort(NoticeCenterActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(NoticeCenterActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "删除全部通知消息失败：" + e);
                ToastUtils.showShort(NoticeCenterActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    /**
     * 编辑弹窗
     */
    private void showEditBottomSheet() {
        IosBottomSheetDialog.Builder builder = new IosBottomSheetDialog.Builder(NoticeCenterActivity.this);
        builder.addItemView("全部已读", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
            @Override
            public void onClick(View view) {
                readAllNotice();
            }
        });
        builder.addItemView("全部删除", Color.parseColor("#de001f"), false, new IosBottomSheetDialog.OnItemClickListener() {
            @Override
            public void onClick(View view) {
                new CommonDialog(NoticeCenterActivity.this, new CommonDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            removeAllNotice();
                            dialog.dismiss();
                        }
                    }
                })
                        .setTitle("确定删除全部消息吗？")
                        .setPositiveButton("删除")
                        .setPositiveButtonColor(ContextCompat.getColor(NoticeCenterActivity.this, R.color.white))
                        .show();
            }
        });
        builder.build().show();
    }

}
