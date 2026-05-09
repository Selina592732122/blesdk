package com.shenghao.wxapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.shenghao.bean.BindWxBean;
import com.shenghao.bean.LoginWxResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.utility.AppSingleton;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.ToastUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class WeChatLoginHelper {
    private static final String TAG = "WeChatLoginHelper";
    private IWXAPI api;
    private Context context;
    private BroadcastReceiver receiver;
    private LoginCallback callback;

    // 回调接口
    public interface LoginCallback {
        void onLoginSuccess(BindWxBean userInfo);
        void onLoginFailed(String error);
        void onUnboundUser(String openId, String nickname, String avatar);
    }

    public WeChatLoginHelper(Context context, LoginCallback callback) {
        this.context = context.getApplicationContext();
        this.callback = callback;
        regToWx(Constants.APP_ID);
    }

    private void regToWx(String appId) {
        api = WXAPIFactory.createWXAPI(context, appId, true);
        api.registerApp(appId);

        // 注册微信启动广播接收器
        BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                api.registerApp(appId);
            }
        };
        context.registerReceiver(refreshReceiver, 
            new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP),
            Context.RECEIVER_NOT_EXPORTED);

        // 注册登录结果接收器
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String openId = intent.getStringExtra("openId");
                String nickname = intent.getStringExtra("nickname");
                String avatar = intent.getStringExtra("avatar");
                
                // 收到微信用户信息后，自动调用绑定接口
                wxLoginBindPhone(openId, nickname, avatar);
            }
        };
        context.registerReceiver(receiver, 
            new IntentFilter("WXEntryActivity.intent"),
            Context.RECEIVER_EXPORTED);
    }

    public void performLogin() {
        if (api == null) {
            regToWx(Constants.APP_ID);
        }
        if (!api.isWXAppInstalled()) {
            Toast.makeText(context, "请安装微信应用", Toast.LENGTH_SHORT).show();
            return;
        }
        getWxCode();
    }

    private void getWxCode() {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        api.sendReq(req);
    }

    private void wxLoginBindPhone(String openId, String nickname, String avatar) {
        OkHttpPresent.wxLoginBindPhone(openId, new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LoginWxResp loginResp = JsonUtils.parseT(body, LoginWxResp.class);
                BindWxBean data = loginResp.getData();
                
                if (loginResp.isSuccess() && data != null) {
                    // 保存用户信息
                    AppSingleton.getInstance().setToken(data.getToken());
                    AppSingleton.getInstance().setUserName(data.getPhone());
                    
                    if (callback != null) {
                        callback.onLoginSuccess(data);
                    }
                } else {    // 未绑定
                    if (loginResp.getCode() == 40070) {
                        ToastUtils.showShort(context,loginResp.getMsg());
                        if (callback != null) {
                            callback.onUnboundUser(openId, nickname, avatar);
                        }
                    } else {
                        if (callback != null) {
                            callback.onLoginFailed("登录失败: " + loginResp.getMsg());
                        }
                    }
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                if (callback != null) {
                    callback.onLoginFailed("网络请求失败: " + e.getMessage());
                }
            }

            @Override
            protected void end() {
                // 可在此处处理结束逻辑
            }
        });
    }

    public void release() {
        if (receiver != null) {
            try {
                context.unregisterReceiver(receiver);
            } catch (Exception e) {
                // 忽略可能的未注册异常
            }
        }
    }
}