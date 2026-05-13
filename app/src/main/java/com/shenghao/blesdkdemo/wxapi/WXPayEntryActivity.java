package com.shenghao.blesdkdemo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.shenghao.blesdkdemo.event.WXPayEvent;
import com.shenghao.blesdkdemo.utility.AppSingleton;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	private final String TAG = WXPayEntryActivity.this.getClass().getSimpleName();

    private IWXAPI api;
    private static MyHandler handler;

	private static class MyHandler extends Handler {
		private final WeakReference<WXPayEntryActivity> wxEntryActivityWeakReference;

		public MyHandler(WXPayEntryActivity wxEntryActivity){
			wxEntryActivityWeakReference = new WeakReference<WXPayEntryActivity>(wxEntryActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			int tag = msg.what;

		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String appId = AppSingleton.getInstance().getWxAppID();
		if (!TextUtils.isEmpty(appId)) {
			api = WXAPIFactory.createWXAPI(this, appId, false);
			handler = new MyHandler(this);
			try {
				Intent intent = getIntent();
				api.handleIntent(intent, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		if (api != null) {
			api.handleIntent(intent, this);
		}
	}

	@Override
	public void onReq(BaseReq req) {
		Log.e(TAG, "onReq: "+req.getType() );
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.e(TAG, "onResp: " + resp.errCode);
		//支付功能
		EventBus.getDefault().post(new WXPayEvent(resp.errCode, resp.errStr));
        finish();
	}
}