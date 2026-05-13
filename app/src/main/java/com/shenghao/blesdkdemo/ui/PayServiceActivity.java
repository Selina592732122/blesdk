package com.shenghao.blesdkdemo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.adapter.MemberPayTypeAdapter;
import com.shenghao.blesdkdemo.bean.BaseHttpResp;
import com.shenghao.blesdkdemo.bean.MemberPayType;
import com.shenghao.blesdkdemo.bean.WxPayReq;
import com.shenghao.blesdkdemo.event.WXPayEvent;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.TimeUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class PayServiceActivity extends BaseActivity {
    public final String TAG = this.getClass().getSimpleName();
    private IWXAPI wxApi; // IWXAPI 是第三方app和微信通信的openApi接口
    private TextView userNameTv;
    private TextView serviceDeadLineTv;
    private TextView payTv;
    private ViewGroup payLayout;
    private RecyclerView payRv;
    private MemberPayTypeAdapter memberPayTypeAdapter;
    private List<MemberPayType> memberPayTypeList = new ArrayList<>();
    private MemberPayType currentPayType = new MemberPayType();
    private TextView tvYear;
    private ImageView ivUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.transparencyBar(this);
        StatusBarUtils.statusBarLightMode(this);
        setContentView(R.layout.activity_pay_service);
        initViews();
        initPayRecyclerView();
        getMemberPayType();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMemberExpireAt();

    }

    @Override
    protected void initViews() {
        super.initViews();
        userNameTv = findViewById(R.id.userNameTv);
        serviceDeadLineTv = findViewById(R.id.serviceDeadLineTv);
        payTv = findViewById(R.id.payTv);
        tvYear = findViewById(R.id.tvYear);
        ivUser = findViewById(R.id.ivUser);
        payLayout = findViewById(R.id.payLayout);

        //设置状态栏高度
        View statusBarView = findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(this, statusBarView);

        //TODO superLT 正式版将支付相关内容移除
        //显示昵称和头像
        if(TextUtils.isEmpty(AppSingleton.getInstance().getUserInfo().getNickName()))
            userNameTv.setText(AppSingleton.getInstance().getUserName());
        else userNameTv.setText(AppSingleton.getInstance().getUserInfo().getNickName());
        Glide.with(this)
                .load(AppSingleton.getInstance().getUserInfo().getAvatar())
                .placeholder(R.drawable.ic_user_default_portrait) // 加载中的占位图
                .error(R.drawable.ic_user_default_portrait)
                .into(ivUser);
        serviceDeadLineTv.setText("到期时间：yyyy年MM月dd日");

        findViewById(R.id.llPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMemberPayOrder();
            }
        });
    }


    private void initPayRecyclerView() {
        payRv = findViewById(R.id.payRv);
        payRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        memberPayTypeAdapter = new MemberPayTypeAdapter(this, memberPayTypeList);
        memberPayTypeAdapter.setOnItemClickListener(new MemberPayTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                for (int i = 0; i < memberPayTypeList.size(); i++) {
                    MemberPayType memberPayType = memberPayTypeList.get(i);
                    if (i == position) {
                        memberPayType.setSelected(true);
                        memberPayType.copyToSelf(currentPayType);
                        payTv.setText(currentPayType.getDisplayAmount());
                        tvYear.setText("/"+currentPayType.getDesc()+"（已优惠¥"+(currentPayType.getOriginalAmount() - currentPayType.getAmount())/100+"）");
                    } else {
                        memberPayType.setSelected(false);
                    }
                }
                memberPayTypeAdapter.notifyDataSetChanged();
            }
        });
        payRv.setAdapter(memberPayTypeAdapter);
    }

    /**
     * 会员-查询套餐类型
     */
    private void getMemberPayType() {
        OkHttpPresent.getMemberPayType(new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                Log.e(TAG, "会员-查询套餐类型，onResponse: " + body);
                BaseHttpResp<List<MemberPayType>> resp = JsonUtils.parseObject(body, new TypeReference<BaseHttpResp<List<MemberPayType>>>() {
                });
                if (resp != null && resp.isSuccess() && resp.getData() != null && resp.getData().size() > 0) {
                    memberPayTypeList.clear();
                    memberPayTypeList.addAll(resp.getData());
                    MemberPayType firstPay = memberPayTypeList.get(0);
                    firstPay.setSelected(true); // 将第一个置为默认选中
                    firstPay.copyToSelf(currentPayType);
                    memberPayTypeAdapter.notifyDataSetChanged();
                    payTv.setText(currentPayType.getDisplayAmount());
                    tvYear.setText("/"+currentPayType.getDesc()+"（已优惠¥"+(currentPayType.getOriginalAmount() - currentPayType.getAmount())/100+"）");
                    payLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                Log.e(TAG, "会员-查询套餐类型，onFailed: " + e);
            }
        });
    }

    /**
     * 会员-查询设备套餐有效期
     */
    private void getMemberExpireAt() {
        OkHttpPresent.getMemberExpireAt(AppSingleton.getInstance().getTerminalNo(), new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                Log.e(TAG, "会员-查询设备套餐有效期，onResponse: " + body);
                BaseHttpResp<Long> resp = JsonUtils.parseObject(body, new TypeReference<BaseHttpResp<Long>>() {
                });
                if (resp != null && resp.isSuccess()) {
                    long time = resp.getData();
                    serviceDeadLineTv.setText("到期时间：" + TimeUtils.getDateToString(time, TimeUtils.PATTERN_04));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                Log.e(TAG, "会员-查询设备套餐有效期，onFailed: " + e);
            }
        });
    }

    /**
     * 会员-下订单
     */
    private void createMemberPayOrder() {
        String terminalNo = AppSingleton.getInstance().getTerminalNo();
        int amount = currentPayType.getAmount();
        String amountType = currentPayType.getAmountType();
        Log.e(TAG, "createMemberPayOrder: amount = " + amount + ", amountType = " + amountType);

        OkHttpPresent.createMemberPayOrder(terminalNo, amount, amountType, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                Log.e(TAG, "会员-下订单，onResponse: " + body);
                BaseHttpResp<WxPayReq> resp = JsonUtils.parseObject(body, new TypeReference<BaseHttpResp<WxPayReq>>() {
                });
                if (resp != null) {
                    if (resp.isSuccess()) {
                        startMemberPay(resp.getData());
                    } else {
                        ToastUtils.showShort(PayServiceActivity.this, resp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(PayServiceActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                Log.e(TAG, "会员-下订单，onFailed: " + e);
                ToastUtils.showShort(PayServiceActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    /**
     * 开始支付
     */
    private void startMemberPay(WxPayReq wxPayReq) {
        if (wxPayReq == null) {
            return;
        }
        AppSingleton.getInstance().setWxAppID(wxPayReq.getAppid());

        registerWXPay(wxPayReq.getAppid());
        PayReq request = new PayReq();
        request.appId = wxPayReq.getAppid();
        request.partnerId = wxPayReq.getPartnerId();
        request.prepayId = wxPayReq.getPrepayId();
        request.packageValue = wxPayReq.getPackageVal();
        request.nonceStr = wxPayReq.getNonceStr();
        request.timeStamp = wxPayReq.getTimestamp();
        request.sign = wxPayReq.getSign();
        wxApi.sendReq(request);
    }

    /**
     * 调用API前，需要先向微信注册您的APPID
     */
    private void registerWXPay(String appID) {
        if (TextUtils.isEmpty(appID)) {
            return;
        }
        if (wxApi == null) {
            // 通过WXAPIFactory工厂，获取IWXAPI的实例
            wxApi = WXAPIFactory.createWXAPI(this, appID, true);
//            wxApi = WXAPIFactory.createWXAPI(this, null);
            // 将应用的appId注册到微信
            wxApi.registerApp(appID);
            //建议动态监听微信启动广播进行注册到微信
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // 将该app注册到微信
                        wxApi.registerApp(appID);
                    }
                }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP), Context.RECEIVER_NOT_EXPORTED);
            }else {
                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // 将该app注册到微信
                        wxApi.registerApp(appID);
                    }
                }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WXPayEvent wxPayEvent) {
        if (wxPayEvent.getErrCode() == 0) {    //支付成功
            ToastUtils.showShort(this, "支付成功");
        } else if (wxPayEvent.getErrCode() == -1) {    //错误：可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
            ToastUtils.showShort(this, "支付失败，请稍后重试");
        } else if (wxPayEvent.getErrCode() == -2) {    //用户取消
            ToastUtils.showShort(this, "已取消");
        } else {
            ToastUtils.showShort(this, getString(R.string.request_retry));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
