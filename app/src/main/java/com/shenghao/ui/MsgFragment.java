package com.shenghao.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.shenghao.bean.SysNotice;
import com.shenghao.bean.SysNoticeResp;
import com.shenghao.okhttp.OkHttpBaseResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.Redirect;
import com.shenghao.utils.StatusBarUtils;
import com.shenghao.utils.TimeUtils;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.CommonDialog;
import com.shenghao.widget.IosBottomSheetDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;


public class MsgFragment extends BaseFragment {
    public static final String TAG = "MsgFragment";
    private Context mContext;
    private TextView tvTime1,tvTime2;
    private TextView tvMsg1,tvMsg2;
    public static MsgFragment newInstance() {
        MsgFragment fragment = new MsgFragment();
        return fragment;
    }

    public MsgFragment() {
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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getSysList(0);
        getNoticeList(Integer.MAX_VALUE);
    }

    private void initViews(View view) {
        //设置状态栏高度
        View statusBarView = view.findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(requireActivity(), statusBarView);

        tvTime1 = view.findViewById(R.id.tvTime1);
        tvTime2 = view.findViewById(R.id.tvTime2);
        tvMsg1 = view.findViewById(R.id.tvMsg1);
        tvMsg2 = view.findViewById(R.id.tvMsg2);
        view.findViewById(R.id.llAlarm).setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(),NoticeCenterActivity.class));
        });
        view.findViewById(R.id.llSys).setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(),NoticeSysActivity.class));
        });

    }

    /**
     * 获取通知列表
     */
    private void getSysList(int startId) {
        OkHttpPresent.systemNoticeList(startId, 20, new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                Log.e(TAG, "onResponse: 通知列表请求成功 = " + body);
                SysNoticeResp resp = JsonUtils.parseT(body, SysNoticeResp.class);
                if (resp != null && resp.isSuccess() && resp.getData() != null && resp.getData().size() > 0) {
                    List<SysNotice> result = resp.getData();
                    SysNotice notice = result.get(0);
                    tvMsg1.setVisibility(View.VISIBLE);
                    tvTime1.setVisibility(View.VISIBLE);
                    tvMsg1.setText(notice.getContent());
                    tvTime1.setText(notice.getSysCreated());
                } else { //无数据或请求失败
                    tvMsg1.setVisibility(View.VISIBLE);
                    tvMsg1.setText("暂无最新消息");
                    tvTime1.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                Log.e(TAG, "onFailed: 通知列表请求失败 = " + e);
//                ToastUtils.showShort(NoticeSysActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
            }
        });
    }


    /**
     * 获取通知列表
     */
    private void getNoticeList(int startId) {
        OkHttpPresent.getNoticeList(startId, 20, new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                Log.e(TAG, "onResponse: 通知列表请求成功 = " + body);
                NoticeListResp resp = JsonUtils.parseT(body, NoticeListResp.class);
                if (resp != null && resp.isSuccess() && resp.getData() != null && resp.getData().size() > 0) {
                    List<NoticeData> result = resp.getData();
                    NoticeData notice = result.get(0);
                    tvMsg2.setVisibility(View.VISIBLE);
                    tvTime2.setVisibility(View.VISIBLE);
                    tvMsg2.setText(notice.getContent());
                    tvTime2.setText(TimeUtils.getDateToString(notice.getNoticeTimestamp(),TimeUtils.PATTERN_01));
                } else { //无数据或请求失败
                    tvMsg2.setVisibility(View.VISIBLE);
                    tvMsg2.setText("暂无最新消息");
                    tvTime2.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                Log.e(TAG, "onFailed: 通知列表请求失败 = " + e);
            }

            @Override
            protected void end() {
                super.end();
            }
        });
    }

}
