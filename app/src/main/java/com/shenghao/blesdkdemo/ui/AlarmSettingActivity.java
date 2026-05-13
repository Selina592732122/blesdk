package com.shenghao.blesdkdemo.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.TypeReference;
import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.adapter.AlarmSettingAdapter;
import com.shenghao.blesdkdemo.adapter.BleSettingAdapter;
import com.shenghao.blesdkdemo.bean.AlarmSettingBean;
import com.shenghao.blesdkdemo.bean.BaseHttpResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 报警设置界面
 */
public class AlarmSettingActivity extends BaseActivity {
    private RecyclerView alarmRv,bleRv;
    private AlarmSettingAdapter alarmSettingAdapter;
    private List<AlarmSettingBean> alarmSettingList = new ArrayList<>();
    private BleSettingAdapter bleSettingAdapter;
    private List<AlarmSettingBean> bleSettingList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);
        initViews();
        getAlarmSetting();
        getBleSetting();
    }

    private void getBleSetting() {
        bleRv = findViewById(R.id.bleRv);
        bleRv.setLayoutManager(new LinearLayoutManager(this));
        bleSettingAdapter = new BleSettingAdapter(this, bleSettingList, new BleSettingAdapter.onSwitchCheckedListener() {
            @Override
            public void onCheckedChanged(int position, boolean isChecked) {
                AlarmSettingBean alarmSettingBean = bleSettingList.get(position);
                changeAlarmSetting(AppSingleton.getInstance().getTerminalNo(),alarmSettingBean.getNoticeType(), isChecked ? 0 : 1);
            }
        });
        bleRv.setAdapter(bleSettingAdapter);
    }

    @Override
    protected void initViews() {
        super.initViews();
        alarmRv = findViewById(R.id.alarmRv);
        alarmRv.setLayoutManager(new LinearLayoutManager(this));
        alarmSettingAdapter = new AlarmSettingAdapter(this, alarmSettingList, new AlarmSettingAdapter.onSwitchCheckedListener() {
            @Override
            public void onCheckedChanged(int position, boolean isChecked) {
                AlarmSettingBean alarmSettingBean = alarmSettingList.get(position);
                changeAlarmSetting(AppSingleton.getInstance().getTerminalNo(),alarmSettingBean.getNoticeType(), isChecked ? 0 : 1);
            }
        });
        alarmRv.setAdapter(alarmSettingAdapter);
    }

    /**
     * 获取报警设置列表
     */
    private void getAlarmSetting() {
        OkHttpPresent.getAlarmSettingList(AppSingleton.getInstance().getTerminalNo(),new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 报警设置列表获取成功 = " + body);
                BaseHttpResp<List<AlarmSettingBean>> resp = JsonUtils.parseObject(body, new TypeReference<BaseHttpResp<List<AlarmSettingBean>>>() {
                });
                if (resp != null && resp.isSuccess()) {
                    alarmSettingList.clear();
                    bleSettingList.clear();
                    List<AlarmSettingBean> data = resp.getData();
                    for (int i = 0; i < data.size(); i++) {
                        if(i < data.size() - 3){
                            alarmSettingList.add(data.get(i));
                        }else {
                            bleSettingList.add(data.get(i));
                        }
                    }
                    alarmSettingAdapter.notifyDataSetChanged();
                    bleSettingAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showShort(AlarmSettingActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onResponse: 报警设置列表获取失败 = " + e);
                ToastUtils.showShort(AlarmSettingActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    /**
     * 修改报警设置
     */
    private void changeAlarmSetting(String terminalNo,String noticeType, int value) {
        OkHttpPresent.changeAlarmSetting(terminalNo,noticeType, value, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "修改报警设置成功：" + body);
                OkHttpBaseResp resp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (resp != null) {
                    if (resp.isSuccess()) {
                        ToastUtils.showShort(AlarmSettingActivity.this, getString(R.string.do_success));
                    } else {
                        getAlarmSetting();
                        ToastUtils.showShort(AlarmSettingActivity.this, resp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(AlarmSettingActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "修改报警设置失败：" + e);
                ToastUtils.showShort(AlarmSettingActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }
}
