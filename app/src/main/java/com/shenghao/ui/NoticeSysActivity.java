package com.shenghao.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.shenghao.R;
import com.shenghao.adapter.NoticeSysListAdapter;
import com.shenghao.bean.SysNotice;
import com.shenghao.bean.SysNoticeResp;
import com.shenghao.bean.TerminalListResp;
import com.shenghao.event.BindTerminalEvent;
import com.shenghao.okhttp.OkHttpBaseResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.ui.dialog.TerminalDialog;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.TerminalUtils;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.CommonDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 通知中心
 */
public class NoticeSysActivity extends BaseActivity {
    public static final int PAGE_SIZE = 20;
    private TextView titleSettingTv;
    private SmartRefreshLayout refreshLayout;
    private View noNoticeLayout;
    private RecyclerView noticeRv;
    private NoticeSysListAdapter noticeListAdapter;
    private List<SysNotice> noticeDataList = new ArrayList<>();
    private int noticeStartId;  //通知startId，用于请求下一页通知数据
    private int pos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_sys);
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

        noticeListAdapter = new NoticeSysListAdapter(this, noticeDataList, new NoticeSysListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                NoticeData noticeData = noticeDataList.get(position);
//                if (noticeData == null) {
//                    return;
//                }
//                noticeData.setReadStatus(1);    //打开详情，将该条通知置为已读状态
//                noticeListAdapter.notifyItemChanged(position);
//                Redirect.startNoticeDetailActivity(NoticeSysActivity.this, noticeData.getId());
            }

            @Override
            public void onItemLongClick(int position) {
                showDeleteDialog(position);
            }

            @Override
            public void onAgree(int pos) {
                NoticeSysActivity.this.pos = pos;
                inviteResponse("1",noticeDataList.get(pos).getId()+"");
            }

            @Override
            public void onReject(int pos) {
                NoticeSysActivity.this.pos = pos;
                showConfirmDialog(getString(R.string.sure_reject),getString(R.string.sure_reject_hint), CommonDialog.MessageType.INFO,getString(R.string.dialog_cancel),getString(R.string.reject));
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
//                showEditBottomSheet();
            }
        });
    }

    private void showConfirmDialog(String title,String content,CommonDialog.MessageType type,String negTv,String posTv) {

        CommonDialog noticeDialog = new CommonDialog(this, new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {  //跳转至消息中心
                    dialog.dismiss();
                    if(type == CommonDialog.MessageType.INFO){
//                        ToastUtils.showShort(NoticeSysActivity.this,"拒绝");
                        inviteResponse("0",noticeDataList.get(pos).getId()+"");
                    }
                }else {
                    if(type == CommonDialog.MessageType.SUCCESS){
                        ToastUtils.showShort(NoticeSysActivity.this,"我的设备");
                        new TerminalDialog(NoticeSysActivity.this, new TerminalDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm, int pos) {

                            }
                        })
                                .setDialogCancelable(true)
                                .setTitle(getString(R.string.my_car2))
                                .show();
                    }
                }
            }
        })
                .setMessageType(type)
                .setTitle(title)
                .setContent(content)
                .setContentVisibility(View.VISIBLE, Gravity.CENTER)
                .setPositiveButton(posTv)
                .setNegativeButton(negTv);
        noticeDialog.show();
    }

    /**
     * 获取通知列表
     */
    private void getNoticeList(int startId) {
        OkHttpPresent.systemNoticeList(startId, PAGE_SIZE, new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                Log.e(TAG, "onResponse: 通知列表请求成功 = " + body);
                SysNoticeResp resp = JsonUtils.parseT(body, SysNoticeResp.class);
                if (resp != null && resp.isSuccess() && resp.getData() != null && resp.getData().size() > 0) {
                    List<SysNotice> result = resp.getData();
                    noNoticeLayout.setVisibility(View.GONE);
                    refreshLayout.setVisibility(View.VISIBLE);
                    noticeDataList.addAll(resp.getData());
                    noticeListAdapter.notifyDataSetChanged();
                    noticeStartId = Math.toIntExact(result.get(result.size() - 1).getId()); //记录当前页最小的noticeId，用于下一页请求
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
//                ToastUtils.showShort(NoticeSysActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                finishRefreshAndLoadMoreUI();
            }
        });
    }

    private void inviteResponse(String status,String id) {
        OkHttpPresent.inviteResponse(status,id, new OkHttpResultCallBack() {
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
                        noticeDataList.clear();
                        getNoticeList(noticeStartId);
                        if("1".equals(status)){
                            showConfirmDialog(getString(R.string.share_success),getString(R.string.share_success_hint), CommonDialog.MessageType.SUCCESS,getString(R.string.my_device),getString(R.string.i_know));
                            getDeviceList();
                        }
                    } else {    //登录失败
                        ToastUtils.showShort(NoticeSysActivity.this, loginResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(NoticeSysActivity.this, getString(R.string.request_retry));
                }


                hideLoadingDialog();
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(NoticeSysActivity.this, getString(R.string.request_retry));
                hideLoadingDialog();
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
    private void getDeviceList() {
        //获取设备列表
        OkHttpPresent.getDeviceList(new OkHttpResultCallBack() {

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 获取设备列表成功 = " + body);
                TerminalListResp terminalListResp = JsonUtils.parseT(body, TerminalListResp.class);
                if (terminalListResp != null && terminalListResp.isSuccess()) {
                    if (terminalListResp.getData().size() > 0) {    //已绑定设备
                        TerminalUtils.setCurrentTerminal(terminalListResp.getData());
                        EventBus.getDefault().post(new BindTerminalEvent(""));
                    }
                } else {    //获取设备列表失败
                    ToastUtils.showShort(NoticeSysActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onResponse: 获取设备列表失败 = " + e);
                ToastUtils.showShort(NoticeSysActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }
    /**
     * 删除弹窗
     */
    private void showDeleteDialog(int position) {
//        NoticeData noticeData = noticeDataList.get(position);
//        if (noticeData == null) {
//            return;
//        }
//        new CommonDialog(NoticeSysActivity.this, new CommonDialog.OnCloseListener() {
//            @Override
//            public void onClick(Dialog dialog, boolean confirm) {
//                if (confirm) {  //确定
//                    removeNotice(noticeData.getId(), position);
//                    dialog.dismiss();
//                }
//            }
//        })
//                .setTitle(getString(R.string.sure_delete_msg))
//                .setPositiveButton(getString(R.string.delete))
//                .setPositiveButtonColor(ContextCompat.getColor(NoticeSysActivity.this, R.color.white))
//                .show();
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
                        ToastUtils.showShort(NoticeSysActivity.this, getString(R.string.do_success));
                    } else {
                        ToastUtils.showShort(NoticeSysActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(NoticeSysActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "删除通知消息失败：" + e);
                ToastUtils.showShort(NoticeSysActivity.this, getString(R.string.request_retry));
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
//    private void readAllNotice() {
//        OkHttpPresent.readAllNotice(new OkHttpResultCallBack() {
//            @Override
//            protected void start() {
//                super.start();
//                showLoadingDialog();
//            }
//
//            @Override
//            protected void onResponse(Response response, String body) throws IOException {
//                LogUtils.e(TAG, "已读全部通知消息成功：" + body);
//                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
//                if (baseResp != null) {
//                    if (baseResp.isSuccess()) {
//                        for (NoticeData noticeData : noticeDataList) {
//                            noticeData.setReadStatus(1);
//                        }
//                        noticeListAdapter.notifyDataSetChanged();
//                        ToastUtils.showShort(NoticeSysActivity.this, getString(R.string.do_success));
//                    } else {
//                        ToastUtils.showShort(NoticeSysActivity.this, baseResp.getMsg());
//                    }
//                } else {
//                    ToastUtils.showShort(NoticeSysActivity.this, getString(R.string.request_retry));
//                }
//            }
//
//            @Override
//            protected void onFailed(Request request, Exception e) {
//                super.onFailed(request, e);
//                LogUtils.e(TAG, "已读全部通知消息失败：" + e);
//                ToastUtils.showShort(NoticeSysActivity.this, getString(R.string.request_retry));
//            }
//
//            @Override
//            protected void end() {
//                super.end();
//                hideLoadingDialog();
//            }
//        });
//    }

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
                        ToastUtils.showShort(NoticeSysActivity.this, getString(R.string.do_success));
                    } else {
                        ToastUtils.showShort(NoticeSysActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(NoticeSysActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "删除全部通知消息失败：" + e);
                ToastUtils.showShort(NoticeSysActivity.this, getString(R.string.request_retry));
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
//    private void showEditBottomSheet() {
//        IosBottomSheetDialog.Builder builder = new IosBottomSheetDialog.Builder(NoticeSysActivity.this);
//        builder.addItemView(getString(R.string.all_read), 0, false, new IosBottomSheetDialog.OnItemClickListener() {
//            @Override
//            public void onClick(View view) {
//                readAllNotice();
//            }
//        });
//        builder.addItemView(getString(R.string.all_delete), Color.parseColor("#de001f"), false, new IosBottomSheetDialog.OnItemClickListener() {
//            @Override
//            public void onClick(View view) {
//                new CommonDialog(NoticeSysActivity.this, new CommonDialog.OnCloseListener() {
//                    @Override
//                    public void onClick(Dialog dialog, boolean confirm) {
//                        if (confirm) {
//                            removeAllNotice();
//                            dialog.dismiss();
//                        }
//                    }
//                })
//                        .setTitle(getString(R.string.sure_delete_all_msg))
//                        .setPositiveButton(getString(R.string.delete))
//                        .setPositiveButtonColor(ContextCompat.getColor(NoticeSysActivity.this, R.color.white))
//                        .show();
//            }
//        });
//        builder.build().show();
//    }

}
