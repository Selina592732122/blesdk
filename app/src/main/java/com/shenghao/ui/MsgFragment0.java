package com.shenghao.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.shenghao.utils.StatusBarUtils;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.CommonDialog;
import com.shenghao.widget.IosBottomSheetDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;


public class MsgFragment0 extends BaseFragment {
    public static final String TAG = "StoreFragment";
    public static final int PAGE_SIZE = 20;
    private TextView titleSettingTv;
    private SmartRefreshLayout refreshLayout;
    private View noNoticeLayout;
    private RecyclerView noticeRv;
    private NoticeListAdapter noticeListAdapter;
    private List<NoticeData> noticeDataList = new ArrayList<>();
    private int noticeStartId;  //通知startId，用于请求下一页通知数据
    private Context mContext;

    public static MsgFragment0 newInstance() {
        MsgFragment0 fragment = new MsgFragment0();
        return fragment;
    }

    public MsgFragment0() {
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_msg, null);
        initViews(view);
        refreshLayout.autoRefresh(1);
        return view;
    }

    private void initViews(View view) {
        //设置状态栏高度
        View statusBarView = view.findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(requireActivity(), statusBarView);

        titleSettingTv = view.findViewById(R.id.titleSettingTv);
        noNoticeLayout = view.findViewById(R.id.noNoticeLayout);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        noticeRv = view.findViewById(R.id.noticeRv);

        noticeListAdapter = new NoticeListAdapter(mContext, noticeDataList, new NoticeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                NoticeData noticeData = noticeDataList.get(position);
                if (noticeData == null) {
                    return;
                }
                noticeData.setReadStatus(1);    //打开详情，将该条通知置为已读状态
                noticeListAdapter.notifyItemChanged(position);
                Redirect.startNoticeDetailActivity(mContext, noticeData.getId());
            }

            @Override
            public void onItemLongClick(int position) {
                showDeleteDialog(position);
            }
        });
        noticeRv.setLayoutManager(new LinearLayoutManager(mContext));
        noticeRv.setAdapter(noticeListAdapter);

        refreshLayout.setEnableOverScrollBounce(false);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        refreshLayout.setRefreshHeader(new MaterialHeader(mContext).setColorSchemeResources(R.color.theme_orange));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
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
                    noticeRv.setVisibility(View.VISIBLE);
                    noticeDataList.addAll(resp.getData());
                    noticeListAdapter.notifyDataSetChanged();
                    noticeStartId = result.get(result.size() - 1).getId(); //记录当前页最小的noticeId，用于下一页请求
                    if (result.size() < PAGE_SIZE) {    //不满一页说明所有数据请求完毕
                        refreshLayout.finishLoadMoreWithNoMoreData();
                    }
                } else { //无数据或请求失败
                    noNoticeLayout.setVisibility(View.VISIBLE);
                    noticeRv.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                Log.e(TAG, "onFailed: 通知列表请求失败 = " + e);
                noNoticeLayout.setVisibility(View.VISIBLE);
                noticeRv.setVisibility(View.GONE);
                ToastUtils.showShort(mContext, getString(R.string.request_retry));
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
        new CommonDialog(mContext, new CommonDialog.OnCloseListener() {
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
                .setPositiveButtonColor(ContextCompat.getColor(mContext, R.color.white))
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
                showLoadingDialog(mContext);
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "删除通知消息成功：" + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) {
                        noticeDataList.remove(position);
                        noticeListAdapter.notifyDataSetChanged();
                        ToastUtils.showShort(mContext, "删除成功");
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
                LogUtils.e(TAG, "删除通知消息失败：" + e);
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
     * 已读全部通知消息
     */
    private void readAllNotice() {
        OkHttpPresent.readAllNotice(new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog(mContext);
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
                        ToastUtils.showShort(mContext, getString(R.string.do_success));
                        refreshMsgCount();
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
                LogUtils.e(TAG, "已读全部通知消息失败：" + e);
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
     * 删除全部通知消息
     */
    private void removeAllNotice() {
        OkHttpPresent.removeAllNotice(new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog(mContext);
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
                        noticeRv.setVisibility(View.GONE);
                        ToastUtils.showShort(mContext, "删除成功");
                        refreshMsgCount();
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
                LogUtils.e(TAG, "删除全部通知消息失败：" + e);
                ToastUtils.showShort(mContext, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    private void refreshMsgCount() {
        ((MainActivity)mContext).getNoticeUnreadNum();//刷新消息条数
    }

    /**
     * 编辑弹窗
     */
    private void showEditBottomSheet() {
        IosBottomSheetDialog.Builder builder = new IosBottomSheetDialog.Builder(mContext);
        builder.addItemView("全部已读", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
            @Override
            public void onClick(View view) {
                readAllNotice();
            }
        });
        builder.addItemView("全部删除", Color.parseColor("#de001f"), false, new IosBottomSheetDialog.OnItemClickListener() {
            @Override
            public void onClick(View view) {
                new CommonDialog(mContext, new CommonDialog.OnCloseListener() {
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
                        .setPositiveButtonColor(ContextCompat.getColor(mContext, R.color.white))
                        .show();
            }
        });
        builder.build().show();
    }
}
