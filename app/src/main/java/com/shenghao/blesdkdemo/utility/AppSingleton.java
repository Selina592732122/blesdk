package com.shenghao.blesdkdemo.utility;

import android.text.TextUtils;


import com.google.gson.Gson;
import com.shenghao.blesdkdemo.bean.LoginResp;
import com.shenghao.blesdkdemo.bean.SimpleLocationBean;
import com.shenghao.blesdkdemo.bean.TerminalBean;
import com.shenghao.blesdkdemo.bean.UserInfo;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utils.AppExecutors;
import com.shenghao.blesdkdemo.utils.AppUtils;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.SPUtils;
import com.shenghao.blesdkdemo.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AppSingleton {
    public final String TAG = this.getClass().getSimpleName();
    public static final long DURATION_TOKEN_VALID = 12 * 60 * 60 * 1000; //token有效期为24小时，这里12小时就认为token过期
    private static volatile AppSingleton appSingleton;
    public static String virtualTel = "shenghao123456789";//虚拟账号
    private String token;
    private long tokenTimestamp = 0;    //token时间戳
    private String userName;
    private String terminalId;   //当前设备id
    private String terminalNo;   //当前设备号
    private String terminalName;   //当前设备名称
    private int batteryCount;   //当前电池个数
    private String imei;
    private String wxAppID; //微信appID
    private List<TerminalBean> terminalList;    //设备列表
    private SimpleLocationBean lastLocationInfo;    //上次位置信息
    private String nick;//昵称
    private String avatar;//头像
    private UserInfo userInfo;
    private TerminalBean currentTerminal;

    public static AppSingleton getInstance() {
        if (appSingleton == null) {
            synchronized (AppSingleton.class) {
                if (appSingleton == null) {
                    appSingleton = new AppSingleton();
                }
            }
        }
        return appSingleton;
    }

    public AppSingleton() {
    }

    public String getToken() {
        if (TextUtils.isEmpty(token)) {
            token = SPUtils.getInstance().getString(SPUtils.SP_TOKEN);
        }
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        SPUtils.getInstance().putString(SPUtils.SP_TOKEN, token);
        // 每次设置token，同时更新token对应的时间戳
        if (!TextUtils.isEmpty(token)) {
            setTokenTimestamp(System.currentTimeMillis());
        }
    }

    public long getTokenTimestamp() {
        return tokenTimestamp;
    }

    public void setTokenTimestamp(long tokenTimestamp) {
        this.tokenTimestamp = tokenTimestamp;
    }

    public String getUserName() {
        if (TextUtils.isEmpty(userName)) {
            userName = SPUtils.getInstance().getString(SPUtils.SP_USER_NAME);
        }
        return userName;
    }
    public String getUserNameWithMask() {
        if (TextUtils.isEmpty(userName)) {
            userName = SPUtils.getInstance().getString(SPUtils.SP_USER_NAME);
        }
        return StringUtils.maskPhoneNumber(userName);
    }

    public void setUserName(String userName) {
        this.userName = userName;
        SPUtils.getInstance().putString(SPUtils.SP_USER_NAME, userName);
    }

    public String getTerminalId() {
        if (TextUtils.isEmpty(terminalId)) {
            terminalId = SPUtils.getInstance().getString(SPUtils.SP_TERMINAL_ID);
        }
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
        SPUtils.getInstance().putString(SPUtils.SP_TERMINAL_ID, terminalId);
    }

    public String getTerminalNo() {
        if (TextUtils.isEmpty(terminalNo)) {
            terminalNo = SPUtils.getInstance().getString(SPUtils.SP_TERMINAL_NO);
        }
        return terminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
        SPUtils.getInstance().putString(SPUtils.SP_TERMINAL_NO, terminalNo);
    }

    public String getTerminalName() {
        if (TextUtils.isEmpty(terminalName)) {
            terminalName = SPUtils.getInstance().getString(SPUtils.SP_TERMINAL_NAME);
        }
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
        SPUtils.getInstance().putString(SPUtils.SP_TERMINAL_NAME, terminalName);
    }

    public int getBatteryCount() {
        if (batteryCount == 0) {
            batteryCount = SPUtils.getInstance().getInt(SPUtils.SP_BATTERY_COUNT);
        }
        return batteryCount;
    }

    public void setBatteryCount(int batteryCount) {
        this.batteryCount = batteryCount;
        SPUtils.getInstance().putInt(SPUtils.SP_BATTERY_COUNT, batteryCount);
    }
    public String getWxAppID() {
        if (TextUtils.isEmpty(wxAppID)) {
            wxAppID = SPUtils.getInstance().getString(SPUtils.SP_WX_APP_ID);
        }
        return wxAppID;
    }

    public void setWxAppID(String appIdWX) {
        this.wxAppID = appIdWX;
        SPUtils.getInstance().putString(SPUtils.SP_WX_APP_ID, appIdWX);
    }
    public List<TerminalBean> getTerminalList() {
        if (terminalList == null) {
            return new ArrayList<>();
        }
        return terminalList;
    }

    public void setTerminalList(List<TerminalBean> terminalList) {
        this.terminalList = terminalList;
    }

    public SimpleLocationBean getLastLocationInfo() {
        return lastLocationInfo;
    }

    public void setLastLocationInfo(SimpleLocationBean lastLocationInfo) {
        this.lastLocationInfo = lastLocationInfo;
    }

    public String getImei() {
        if (TextUtils.isEmpty(imei)) {
            imei = AppUtils.getImei();
        }
        return imei;
    }

    /**
     * token是否有效
     */
    public boolean isTokenValid() {
        if ((System.currentTimeMillis() - getTokenTimestamp()) > DURATION_TOKEN_VALID) {
            return false;
        }
        return true;
    }

    /**
     * 刷新token
     */
    public void refreshToken() {
        String userName = SPUtils.getInstance().getString(SPUtils.SP_USER_NAME);
        String token = SPUtils.getInstance().getString(SPUtils.SP_TOKEN);
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(token)) {
            AppExecutors.getInstance().executeIoTask(new Runnable() {
                @Override
                public void run() {
                    String result = OkHttpPresent.getValidTokenSync();
                    LogUtils.e(TAG, "token刷新成功: " + result);
                    LoginResp loginResp = JsonUtils.parseT(result, LoginResp.class);
                    if (loginResp != null && loginResp.isSuccess() && !TextUtils.isEmpty(loginResp.getData())) {
                        AppSingleton.getInstance().setToken(loginResp.getData());
                    }
                }
            });
        }
    }

    /**
     * 刷新token（先校验token有效性，再刷新）
     */
    public void refreshTokenByCheck() {
        if (!isTokenValid()) {
            LogUtils.e(TAG, "token过期，开始请求新token");
            AppSingleton.getInstance().refreshToken();
        } else {
            LogUtils.e(TAG, "token有效，无需刷新");
        }
    }

    public void clearAllData() {
        setToken("");
        setTokenTimestamp(0);
        setUserName("");
        setTerminalId("");
        setTerminalNo("");
        setTerminalName("");
        setBatteryCount(0);
        setTerminalList(null);
        setLastLocationInfo(null);
        setUserInfo(null);
        setCurrentTerminal(null);
    }
    public void setCurrentTerminal(TerminalBean currentTerminal) {
        this.currentTerminal = currentTerminal;
        Gson gson = new Gson();
        String json = gson.toJson(currentTerminal); // 将对象序列化为JSON字符串
        SPUtils.getInstance().putString(SPUtils.SP_TERMINAL,json);
    }

    public TerminalBean getCurrentTerminal() {
        if(currentTerminal == null){
            String json = SPUtils.getInstance().getString(SPUtils.SP_TERMINAL);
            Gson gson = new Gson();
            currentTerminal = gson.fromJson(json, TerminalBean.class);
        }
        return currentTerminal;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        Gson gson = new Gson();
        String json = gson.toJson(userInfo); // 将对象序列化为JSON字符串
        SPUtils.getInstance().putString(SPUtils.SP_USER_INFO,json);
    }
    public UserInfo getUserInfo() {
        if(userInfo == null){
            String json = SPUtils.getInstance().getString(SPUtils.SP_USER_INFO);
            Gson gson = new Gson();
            userInfo = gson.fromJson(json, UserInfo.class);
        }
        return userInfo;
    }
}
