package com.shenghao.blesdkdemo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.SubscribeMessage;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelbiz.WXOpenBusinessView;
import com.tencent.mm.opensdk.modelbiz.WXOpenBusinessWebview;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	private static String TAG = "MicroMsg.WXEntryActivity";

    private IWXAPI api;
    private static MyHandler handler;

	private class MyHandler extends Handler {
		private final WeakReference<WXEntryActivity> wxEntryActivityWeakReference;

		public MyHandler(WXEntryActivity wxEntryActivity){
			wxEntryActivityWeakReference = new WeakReference<WXEntryActivity>(wxEntryActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			int tag = msg.what;
			switch (tag) {
				case NetworkUtil.GET_TOKEN: {
					Bundle data = msg.getData();
					JSONObject json = null;
					try {
						json = new JSONObject(data.getString("result"));
						String openId, accessToken, refreshToken, scope;
						openId = json.getString("openid");
						accessToken = json.getString("access_token");
						refreshToken = json.getString("refresh_token");
						scope = json.getString("scope");

						fetchUserInfo(accessToken,openId);
						//GET https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
//						NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/userinfo?" +
//								"access_token=%s&openid=%s", accessToken,openId), NetworkUtil.GET_INFO);
//							Intent intent = new Intent(wxEntryActivityWeakReference.get(), LoginActivity.class);
//							intent.putExtra("openId", openId);
//							intent.putExtra("accessToken", accessToken);
//							intent.putExtra("refreshToken", refreshToken);
//							intent.putExtra("scope", scope);
//							wxEntryActivityWeakReference.get().startActivity(intent);

					} catch (JSONException e) {
//						Log.e(TAG, e.getMessage());
					}
					break;
				}
				case NetworkUtil.GET_INFO:{
//					Bundle data = msg.getData();
//					try {
//						// 关键修改：获取原始字节数组
//						byte[] jsonBytes = data.getByteArray("result");
//						if (jsonBytes == null) return;
//
//						// 用UTF-8解码字节数组
//						String jsonStr = new String(jsonBytes, StandardCharsets.UTF_8);
//						JSONObject json = new JSONObject(jsonStr);
//
//						String openId = json.getString("openid");
//						String nickname = json.getString("nickname"); // 此时已是正确编码
//						String avatar = json.getString("headimgurl");
//
//						// ...后续广播逻辑
//						Intent intent = new Intent("WXEntryActivity.intent");
//						intent.putExtra("openId", openId);
//						intent.putExtra("nickname", nickname);
//						intent.putExtra("avatar", avatar);
//						wxEntryActivityWeakReference.get().sendBroadcast(intent);
//						wxEntryActivityWeakReference.get().finish();
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
					break;
				}
			}
		}
	}

	// 添加依赖：implementation 'com.squareup.okhttp3:okhttp:4.9.3'
	private void fetchUserInfo(String accessToken, String openId) {
		new Thread(() -> {
			try {
				String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
						+ accessToken + "&openid=" + openId;

				OkHttpClient client = new OkHttpClient();
				Request request = new Request.Builder().url(url).build();
				Response response = client.newCall(request).execute();

				if (response.isSuccessful()) {
					String jsonStr = response.body().string();
					JSONObject json = new JSONObject(jsonStr);
					String nickname = json.getString("nickname"); // 此时已是正确编码
					String avatar = json.getString("headimgurl");
					// 发送广播更新UI
					runOnUiThread(() -> {
						Intent intent = new Intent("WXEntryActivity.intent");
						intent.putExtra("openId", openId);
						intent.putExtra("nickname", nickname);
						intent.putExtra("avatar", avatar);
						// ...其他参数
						sendBroadcast(intent);
						finish();
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
		handler = new MyHandler(this);

        try {
            Intent intent = getIntent();
        	api.handleIntent(intent, this);
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			goToGetMsg();		
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			goToShowMsg((ShowMessageFromWX.Req) req);
			break;
		default:
			break;
		}
        finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		String result = "";

		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				result = "errcode_success";
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				result = "errcode_cancel";
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				result = "errcode_deny";
				break;
			case BaseResp.ErrCode.ERR_UNSUPPORT:
				result = "errcode_unsupported";
				break;
			default:
				result = "errcode_unknown";
				break;
		}

//		Toast.makeText(this, result+ ", type=" + resp.getType(), Toast.LENGTH_SHORT).show();


		if (resp.getType() == ConstantsAPI.COMMAND_SUBSCRIBE_MESSAGE) {
			SubscribeMessage.Resp subscribeMsgResp = (SubscribeMessage.Resp) resp;
			String text = String.format("openid=%s\ntemplate_id=%s\nscene=%d\naction=%s\nreserved=%s",
					subscribeMsgResp.openId, subscribeMsgResp.templateID, subscribeMsgResp.scene, subscribeMsgResp.action, subscribeMsgResp.reserved);

			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		}

		if (resp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
			WXLaunchMiniProgram.Resp launchMiniProgramResp = (WXLaunchMiniProgram.Resp) resp;
			String text = String.format("openid=%s\nextMsg=%s\nerrStr=%s",
					launchMiniProgramResp.openId, launchMiniProgramResp.extMsg,launchMiniProgramResp.errStr);

			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		}

		if (resp.getType() == ConstantsAPI.COMMAND_OPEN_BUSINESS_VIEW) {
			WXOpenBusinessView.Resp launchMiniProgramResp = (WXOpenBusinessView.Resp) resp;
			String text = String.format("openid=%s\nextMsg=%s\nerrStr=%s\nbusinessType=%s",
					launchMiniProgramResp.openId, launchMiniProgramResp.extMsg,launchMiniProgramResp.errStr,launchMiniProgramResp.businessType);

			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		}

		if (resp.getType() == ConstantsAPI.COMMAND_OPEN_BUSINESS_WEBVIEW) {
			WXOpenBusinessWebview.Resp response = (WXOpenBusinessWebview.Resp) resp;
			String text = String.format("businessType=%d\nresultInfo=%s\nret=%d",response.businessType,response.resultInfo,response.errCode);

			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		}

		if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
			SendAuth.Resp authResp = (SendAuth.Resp)resp;
			final String code = authResp.code;
			//调用自己的后台
			NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/oauth2/access_token?" +
							"appid=%s&secret=%s&code=%s&grant_type=authorization_code", Constants.APP_ID,
					Constants.APP_SECRET, code), NetworkUtil.GET_TOKEN);
		}
	}
	
	private void goToGetMsg() {
//		Intent intent = new Intent(this, GetFromWXActivity.class);
//		intent.putExtras(getIntent());
//		startActivity(intent);
//		finish();
	}
	
	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
		WXMediaMessage wxMsg = showReq.message;		
		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
		
		StringBuffer msg = new StringBuffer();
		msg.append("description: ");
		msg.append(wxMsg.description);
		msg.append("\n");
		msg.append("extInfo: ");
		msg.append(obj.extInfo);
		msg.append("\n");
		msg.append("filePath: ");
		msg.append(obj.filePath);
		
//		Intent intent = new Intent(this, ShowFromWXActivity.class);
//		intent.putExtra(Constants.ShowMsgActivity.STitle, wxMsg.title);
//		intent.putExtra(Constants.ShowMsgActivity.SMessage, msg.toString());
//		intent.putExtra(Constants.ShowMsgActivity.BAThumbData, wxMsg.thumbData);
//		startActivity(intent);
//		finish();
	}
}