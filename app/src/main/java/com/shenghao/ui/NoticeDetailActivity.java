package com.shenghao.ui;


import static com.shenghao.utils.TimeUtils.PATTERN_08;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


import com.shenghao.R;
import com.shenghao.bean.NoticeData;
import com.shenghao.bean.NoticeDetailResp;
import com.shenghao.constant.Const;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.TimeUtils;
import com.shenghao.utils.ToastUtils;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 通知消息详情界面
 */
public class NoticeDetailActivity extends BaseActivity {
    public static final String BUNDLE_NOTICE_ID = "notice_id";
    private TextView noticeTitleTv;
    private TextView noticeContentTv;
    private TextView noticeDateTv;
    private ImageView noticeIcon;

    private int mNoticeId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);
        resolveIntent();
        initViews();
        getNoticeDetail();
    }

    private void resolveIntent() {
        mNoticeId = getIntent().getIntExtra(BUNDLE_NOTICE_ID, 0);
    }

    @Override
    protected void initViews() {
        super.initViews();
        noticeTitleTv = findViewById(R.id.noticeTitleTv);
        noticeContentTv = findViewById(R.id.noticeContentTv);
        noticeDateTv = findViewById(R.id.noticeDateTv);
        noticeIcon = findViewById(R.id.noticeIcon);
    }

    /**
     * 获取通知消息详情
     */
    private void getNoticeDetail() {
        OkHttpPresent.getNoticeDetail(mNoticeId, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 通知详情请求成功 = " + body);
                NoticeDetailResp resp = JsonUtils.parseT(body, NoticeDetailResp.class);
                if (resp != null) {
                    if (resp.isSuccess() && resp.getData() != null) {
                        NoticeData noticeData = resp.getData();
                        noticeTitleTv.setText(noticeData.getTitle());
                        noticeContentTv.setText(noticeData.getContent());
                        noticeDateTv.setText(TimeUtils.getDateToString(noticeData.getNoticeTimestamp(), PATTERN_08));

                        if (TextUtils.equals(noticeData.getType(), Const.NOTICE_TYPE_MOVE_ALARM)) { //震动告警
                            noticeIcon.setImageDrawable(ContextCompat.getDrawable(NoticeDetailActivity.this, R.drawable.ic_notify_power_shake));
                        } else if (TextUtils.equals(noticeData.getType(), Const.NOTICE_TYPE_EXTERNAL_POWER_ALARM)) {    //电瓶被拆告警
                            noticeIcon.setImageDrawable(ContextCompat.getDrawable(NoticeDetailActivity.this, R.drawable.ic_notify_power_remove));
                        } else if (TextUtils.equals(noticeData.getType(), Const.NOTICE_TYPE_LOW_VOLTAGE_ALARM)) {   //低电量告警
                            noticeIcon.setImageDrawable(ContextCompat.getDrawable(NoticeDetailActivity.this, R.drawable.ic_notify_power_low));
                        } else if (TextUtils.equals(noticeData.getType(), Const.NOTICE_TYPE_HIGH_VOLTAGE_ALARM)) {  //高电压告警
                            noticeIcon.setImageDrawable(ContextCompat.getDrawable(NoticeDetailActivity.this, R.drawable.ic_notify_power_max));
                        } else {    //其他告警（电子围栏告警）
                            noticeIcon.setImageDrawable(ContextCompat.getDrawable(NoticeDetailActivity.this, R.drawable.ic_notify_msg_default));
                        }
                    } else {
                        ToastUtils.showShort(NoticeDetailActivity.this, resp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(NoticeDetailActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 通知详情请求失败 = " + e);
                ToastUtils.showShort(NoticeDetailActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }
}
