package com.shenghao.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.shenghao.R;
import com.shenghao.adapter.CarInfoAdapter;
import com.shenghao.adapter.ExamplePagerAdapter;
import com.shenghao.adapter.GridSpacingItemDecoration;
import com.shenghao.bean.ControlInfo;
import com.shenghao.bean.ControlInfoResp;
import com.shenghao.event.RefreshEvent;
import com.shenghao.event.WXPayEvent;
import com.shenghao.okhttp.OkHttpBaseResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.utility.AppSingleton;
import com.shenghao.utils.DensityUtil;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.StatusBarUtils;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.ClipPagerTitleView;
import com.shenghao.widget.ModeDialog;
import com.shenghao.widget.PasswordEditText;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

public class ControlActivity extends BaseActivity {
    private LinearLayout ll1,ll2,ll3,ll4;
    private Switch switchP,switchEBS,switchXun,switchXian;
    private TextView tvP,tvEBS,tvXun,tvXian;
    private static final String[] CHANNELS = new String[]{"车辆信息", "状态调节", "速度调节","功能开关"};
    private static final String[] InfoTitle = new String[]{"控制系统", "电机传感器", "转把","刹车系统","高压保护","欠压保护","高温保护","过流保护"};
    private final List<String> mDataList = Arrays.asList(CHANNELS);
    private List<CarInfoAdapter.CarInfo> dataList = new ArrayList<>();
    private  CarInfoAdapter adapter;
    private ViewPager mViewPager;
    private RecyclerView recyclerView;
    private int visibleIndex = 0;
    private final ExamplePagerAdapter mExamplePagerAdapter = new ExamplePagerAdapter(mDataList);
    private RadioGroup rgVoltage,rgStartWay,rgDao,rgFangLiu,rgDou,rgForward,rgBack,rgTurn,rgPower;
    private ModeDialog travelMode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.transparencyBar(this);
        StatusBarUtils.statusBarLightMode(this);
        setContentView(R.layout.activity_control);
        EventBus.getDefault().register(this);
        initViews();
        initRecyclerView();
        showIndex(0);
        initIndicator();
        getCarControlInfo();
    }

    @Override
    protected void initViews() {
        super.initViews();
        ll1 = findViewById(R.id.ll1);
        ll2 = findViewById(R.id.ll2);
        ll3 = findViewById(R.id.ll3);
        ll4 = findViewById(R.id.ll4);
        rgVoltage = findViewById(R.id.rgVoltage);
        rgStartWay = findViewById(R.id.rgStartWay);
        rgDao = findViewById(R.id.rgDao);
        rgFangLiu = findViewById(R.id.rgFangLiu);
        rgDou = findViewById(R.id.rgDou);
        rgForward = findViewById(R.id.rgForward);
        rgBack = findViewById(R.id.rgBack);
        rgTurn = findViewById(R.id.rgTurn);
        rgPower = findViewById(R.id.rgPower);

        switchP = findViewById(R.id.switchP);
        switchEBS = findViewById(R.id.switchEBS);
        switchXun = findViewById(R.id.switchXun);
        switchXian = findViewById(R.id.switchXian);
        tvP = findViewById(R.id.tvP);
        tvEBS = findViewById(R.id.tvEBS);
        tvXun = findViewById(R.id.tvXun);
        tvXian = findViewById(R.id.tvXian);
        recyclerView = findViewById(R.id.recyclerView);
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(mExamplePagerAdapter);
        findViewById(R.id.tvRecover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCarControl("0"+visibleIndex,"0");
            }
        });
        findViewById(R.id.tvMode).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(travelMode == null)
                    travelMode = new ModeDialog(ControlActivity.this, new ModeDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm, int index) {
                            if (confirm)
                                setCarControl("travelMode", String.valueOf(index));
                            dialog.dismiss();
                        }
                    });
                travelMode.show();
            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setNestedScrollingEnabled(false);
        // 创建 GridLayoutManager，设置列数为 2
        GridLayoutManager layoutManager = new GridLayoutManager(ControlActivity.this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, DensityUtil.dip2px(ControlActivity.this,16), false));
        for (String s : InfoTitle) {
            dataList.add(new CarInfoAdapter.CarInfo(s, 1));
        }
        adapter = new CarInfoAdapter(ControlActivity.this,dataList); // dataList 是你的数据源
        recyclerView.setAdapter(adapter);
    }
    private void initIndicator() {
        MagicIndicator magicIndicator = (MagicIndicator)findViewById(R.id.magic_indicator);
        magicIndicator.setBackgroundResource(R.drawable.bg_control_title_radius_shape);
        CommonNavigator commonNavigator = new CommonNavigator(ControlActivity.this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
                clipPagerTitleView.setText(mDataList.get(index));
                clipPagerTitleView.setTextColor(Color.parseColor("#99000000"));
                clipPagerTitleView.setClipColor(Color.WHITE);
                clipPagerTitleView.setTextSize(DensityUtil.sp2px(ControlActivity.this,12));
                clipPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                        showIndex(index);
                    }
                });
                return clipPagerTitleView;
            }

            @Override
            public float getTitleWeight(Context context, int index) {
                return 1.f;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                float navigatorHeight = DensityUtil.dip2px(context,45);
                float borderWidth = UIUtil.dip2px(context, 1);
                float lineHeight = navigatorHeight - 2 * borderWidth;
                indicator.setLineHeight(lineHeight);
                indicator.setRoundRadius(lineHeight / 2);
                indicator.setYOffset(borderWidth);
                indicator.setColors(Color.parseColor("#FFF57F2A"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    //展示第index页
    private void showIndex(int index) {
        if(visibleIndex == index)
            return;
        visibleIndex = index;
        if(index == 0){
            ll1.setVisibility(View.VISIBLE);
            ll2.setVisibility(View.GONE);
            ll3.setVisibility(View.GONE);
            ll4.setVisibility(View.GONE);
        } else if(index == 1){
            ll1.setVisibility(View.GONE);
            ll2.setVisibility(View.VISIBLE);
            ll3.setVisibility(View.GONE);
            ll4.setVisibility(View.GONE);
        } else if(index == 2){
            ll1.setVisibility(View.GONE);
            ll2.setVisibility(View.GONE);
            ll3.setVisibility(View.VISIBLE);
            ll4.setVisibility(View.GONE);
        } else if(index == 3){
            ll1.setVisibility(View.GONE);
            ll2.setVisibility(View.GONE);
            ll3.setVisibility(View.GONE);
            ll4.setVisibility(View.VISIBLE);
        }
        getCarControlInfo();
    }

    public void getCarControlInfo(){
        OkHttpPresent.getCarControlInfo(AppSingleton.getInstance().getTerminalNo(),new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
//                ((MainActivity)ControlActivity.this).showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 成功 = " + body);
                ControlInfoResp controlInfoResp = JsonUtils.parseT(body, ControlInfoResp.class);
                if (controlInfoResp != null && controlInfoResp.isSuccess()) {
                    updateUI(controlInfoResp.getData());
                }else {
                    updateUI(controlInfoResp.getData());
                    ToastUtils.showShort(ControlActivity.this, TextUtils.isEmpty(controlInfoResp.getMsg())? getString(R.string.request_retry):controlInfoResp.getMsg());
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 失败 = " + e);
                ToastUtils.showShort(ControlActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
//                ((MainActivity)ControlActivity.this).hideLoadingDialog();
            }
        });
    }

    //只刷新第一页
    public void getCarControlInfoRefreshOnlyFirstPage(){
        OkHttpPresent.getCarControlInfo(AppSingleton.getInstance().getTerminalNo(),new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
//                ((MainActivity)requireActivity()).showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 成功 = " + body);
                ControlInfoResp controlInfoResp = JsonUtils.parseT(body, ControlInfoResp.class);
                if (controlInfoResp != null && controlInfoResp.isSuccess()) {
                    ControlInfo data = controlInfoResp.getData();
                    if(data == null)
                        data = new ControlInfo();
                    dataList.clear();
                    dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[0],data.getControlSys()));
                    dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[1],data.getMotorSensor()));
                    dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[2],data.getTurnAround()));
                    dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[3],data.getBrakingSys()));
                    dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[4],data.getHighvoltageProtect()));
                    dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[5],data.getUndervoltageProtect()));
                    dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[6],data.getHighProtect()));
                    dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[7],data.getOvercurrentProtect()));
                    adapter.notifyDataSetChanged();
                }else {
                    ControlInfo data = controlInfoResp.getData();
                    if(data == null)
                        data = new ControlInfo();
                    dataList.clear();
                    dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[0],data.getControlSys()));
                    dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[1],data.getMotorSensor()));
                    dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[2],data.getTurnAround()));
                    dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[3],data.getBrakingSys()));
                    dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[4],data.getHighvoltageProtect()));
                    dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[5],data.getUndervoltageProtect()));
                    dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[6],data.getHighProtect()));
                    dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[7],data.getOvercurrentProtect()));
                    adapter.notifyDataSetChanged();
                    ToastUtils.showShort(ControlActivity.this, TextUtils.isEmpty(controlInfoResp.getMsg())? getString(R.string.request_retry):controlInfoResp.getMsg());
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 失败 = " + e);
                ToastUtils.showShort(ControlActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
//                ((MainActivity)requireActivity()).hideLoadingDialog();
            }
        });
    }
    public void setCarControl(String type,String status){
        OkHttpPresent.setCarControl(AppSingleton.getInstance().getTerminalNo(),type,status,new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 成功 = " + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null && baseResp.isSuccess()) {
                    ToastUtils.showShort(ControlActivity.this, getString(R.string.do_success));
                }else {
                    getCarControlInfo();
                    ToastUtils.showShort(ControlActivity.this, TextUtils.isEmpty(baseResp.getMsg())? getString(R.string.request_retry):baseResp.getMsg());
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 失败 = " + e);
                ToastUtils.showShort(ControlActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    //刷新UI
    private void updateUI(ControlInfo data) {
        if(data == null)
            data = new ControlInfo();
        dataList.clear();
        dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[0],data.getControlSys()));
        dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[1],data.getMotorSensor()));
        dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[2],data.getTurnAround()));
        dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[3],data.getBrakingSys()));
        dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[4],data.getHighvoltageProtect()));
        dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[5],data.getUndervoltageProtect()));
        dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[6],data.getHighProtect()));
        dataList.add(new CarInfoAdapter.CarInfo(InfoTitle[7],data.getOvercurrentProtect()));
        adapter.notifyDataSetChanged();

        setDefaultRadioButton(rgVoltage, getCheckId("VoltageSelect", data.getVoltageSelect()), new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int status = 0;
                if(checkedId == R.id.rb48) status = 0;
                else if(checkedId == R.id.rb60) status = 1;
                else if(checkedId == R.id.rb72) status = 2;
                setCarControl("voltageSelect",status+"");
            }
        });
        setDefaultRadioButton(rgStartWay, getCheckId("StartWay", data.getStartWay()), new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int status = 0;
                if(checkedId == R.id.rbStart1) status = 0;
                else if(checkedId == R.id.rbStart2) status = 1;
                else if(checkedId == R.id.rbStart3) status = 2;
                setCarControl("startWay",status+"");
            }
        });
        setDefaultRadioButton(rgDao, data.getAsternwayStartWay() == 0 ?R.id.rbDao1:R.id.rbDao2, new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setCarControl("asternwayStartWay",checkedId == R.id.rbDao1?"0":"1");
            }
        });
        setDefaultRadioButton(rgFangLiu, data.getLiupoVolRegulate() == 0 ?R.id.rbFan1:R.id.rbFan2, new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setCarControl("liupoVolRegulate",checkedId == R.id.rbFan1?"0":"1");
            }
        });
        setDefaultRadioButton(rgDou, data.getDoupoVolRegulate() == 0 ?R.id.rbDou1:R.id.rbDou2, new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setCarControl("doupoVolRegulate",checkedId == R.id.rbDou1?"0":"1");
            }
        });
        setDefaultRadioButton(rgForward, getCheckId("ForwardRegulate", data.getForwardRegulate()), new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int status = 0;
                if(checkedId == R.id.rbMove1) status = 0;
                else if(checkedId == R.id.rbMove2) status = 1;
                else if(checkedId == R.id.rbMove3) status = 2;
                setCarControl("forwardRegulate",status+"");
            }
        });
        setDefaultRadioButton(rgBack, getCheckId("AsternwayRegulate", data.getAsternwayRegulate()), new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int status = 0;
                if(checkedId == R.id.rbBack1) status = 0;
                else if(checkedId == R.id.rbBack2) status = 1;
                else if(checkedId == R.id.rbBack3) status = 2;
                setCarControl("asternwayRegulate",status+"");
            }
        });
        setDefaultRadioButton(rgTurn, data.getReversible() == 0 ?R.id.rbTurnOver:R.id.rbTurnBack, new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setCarControl("reversible",checkedId == R.id.rbTurnOver?"0":"1");
            }
        });
        setDefaultRadioButton(rgPower, data.getStartMode() == 0 ?R.id.rbSoft:R.id.rbHard, new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setCarControl("startMode",checkedId == R.id.rbSoft?"0":"1");
            }
        });
        //其他开关
        setSwitchStateWithoutTrigger(switchP, data.getPgearSelect() == 1, tvP, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tvP.setText("开");
                    tvP.setTextColor(Color.parseColor("#FFF57F2A"));
                }else {
                    tvP.setText("关");
                    tvP.setTextColor(Color.parseColor("#181818"));
                }
                setCarControl("pgearSelect",isChecked?"1":"0");
            }
        });
        setSwitchStateWithoutTrigger(switchEBS, data.getEbsSelect() == 1, tvEBS, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tvEBS.setText("开");
                    tvEBS.setTextColor(Color.parseColor("#FFF57F2A"));
                }else {
                    tvEBS.setText("关");
                    tvEBS.setTextColor(Color.parseColor("#181818"));
                }
                setCarControl("ebsSelect",isChecked?"1":"0");
            }
        });
        setSwitchStateWithoutTrigger(switchXun, data.getCruiseSelect() == 1, tvXun, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tvXun.setText("开");
                    tvXun.setTextColor(Color.parseColor("#FFF57F2A"));
                }else {
                    tvXun.setText("关");
                    tvXun.setTextColor(Color.parseColor("#181818"));
                }
                setCarControl("cruiseSelect",isChecked?"1":"0");
            }
        });
        setSwitchStateWithoutTrigger(switchXian, data.getCurrentLimitingSet() == 1, tvXian, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tvXian.setText("开");
                    tvXian.setTextColor(Color.parseColor("#FFF57F2A"));
                }else {
                    tvXian.setText("关");
                    tvXian.setTextColor(Color.parseColor("#181818"));
                }
                setCarControl("currentLimitingSet",isChecked?"1":"0");
            }
        });
    }

    private int getCheckId(String key, int i) {
        if(key.equals("VoltageSelect")){
            switch (i){
                case 0:
                    return R.id.rb48;
                case 1:
                    return R.id.rb60;
                case 2:
                    return R.id.rb72;
            }
        }else if(key.equals("StartWay")){
            switch (i){
                case 0:
                    return R.id.rbStart1;
                case 1:
                    return R.id.rbStart2;
                case 2:
                    return R.id.rbStart3;
            }
        }else if(key.equals("ForwardRegulate")){
            switch (i){
                case 0:
                    return R.id.rbMove1;
                case 1:
                    return R.id.rbMove2;
                case 2:
                    return R.id.rbMove3;
            }
        }else if(key.equals("AsternwayRegulate")){
            switch (i){
                case 0:
                    return R.id.rbBack1;
                case 1:
                    return R.id.rbBack2;
                case 2:
                    return R.id.rbBack3;
            }
        }
        return 0;
    }

    /**
     * 设置默认选中的 RadioButton，并避免触发监听器
     *
     * @param radioGroup    RadioGroup
     * @param checkedId     要选中的 RadioButton ID
     * @param listener      监听器
     */
    private void setDefaultRadioButton(RadioGroup radioGroup, int checkedId, RadioGroup.OnCheckedChangeListener listener) {
        // 移除监听器
        radioGroup.setOnCheckedChangeListener(null);

        // 设置默认选项
        radioGroup.check(checkedId);

        // 重新设置监听器
        radioGroup.setOnCheckedChangeListener(listener);
    }
    /**
     * 设置 Switch 状态，但不触发监听器
     *
     * @param switchButton Switch 组件
     * @param isChecked    要设置的状态
     * @param listener     监听器
     */
    private void setSwitchStateWithoutTrigger(Switch switchButton, boolean isChecked, TextView tv,CompoundButton.OnCheckedChangeListener listener) {
        // 移除监听器
        switchButton.setOnCheckedChangeListener(null);

        // 设置状态
        switchButton.setChecked(isChecked);
        if(isChecked){
            tv.setText("开");
            tv.setTextColor(Color.parseColor("#FFF57F2A"));
        }else {
            tv.setText("关");
            tv.setTextColor(Color.parseColor("#181818"));
        }

        // 重新设置监听器
        switchButton.setOnCheckedChangeListener(listener);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshEvent event) {
        if (event.isShouldRefresh()) getCarControlInfoRefreshOnlyFirstPage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}