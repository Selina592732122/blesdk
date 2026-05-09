package com.shenghao.utils;

import static com.shenghao.ui.BaseGeoFenceActivity.EXTRA_ID;

import android.content.Context;
import android.content.Intent;

import com.shenghao.R;
import com.shenghao.bean.RidingDataBean;
import com.shenghao.okhttp.OkHttpURLs;
import com.shenghao.ui.AlarmSettingActivity;
import com.shenghao.ui.BindTerminalActivity;
import com.shenghao.ui.ComingSoonActivity;
import com.shenghao.ui.EditGeoFenceCircleActivity;
import com.shenghao.ui.EditGeoFencePolygonActivity;
import com.shenghao.ui.GeoFenceListActivity;
import com.shenghao.ui.JFWebActivity;
import com.shenghao.ui.LoginActivity;
import com.shenghao.ui.MainActivity;
import com.shenghao.ui.NoticeCenterActivity;
import com.shenghao.ui.NoticeDetailActivity;
import com.shenghao.ui.PayServiceActivity;
import com.shenghao.ui.RidingDataDetailActivity;
import com.shenghao.ui.SettingActivity;
import com.shenghao.ui.SplashActivity;
import com.shenghao.ui.UploadTerminalInfoActivity;


public class Redirect {
    public static void startSplashActivity(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        context.startActivity(intent);
    }
    public static void startLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void startSettingActivity(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    public static void startPayServiceActivity(Context context) {
        Intent intent = new Intent(context, PayServiceActivity.class);
        context.startActivity(intent);
    }

    public static void startComingSoonActivity(Context context, String title) {
        Intent intent = new Intent(context, ComingSoonActivity.class);
        intent.putExtra(ComingSoonActivity.BUNDLE_TITLE, title);
        context.startActivity(intent);
    }

    public static void startBindTerminalActivity(Context context) {
        Intent intent = new Intent(context, BindTerminalActivity.class);
        context.startActivity(intent);
    }

    public static void startUploadTerminalInfoActivity(Context context) {
        Intent intent = new Intent(context, UploadTerminalInfoActivity.class);
        context.startActivity(intent);
    }

    public static void startRidingDataDetailActivity(Context context, RidingDataBean ridingData) {
        Intent intent = new Intent(context, RidingDataDetailActivity.class);
        intent.putExtra(RidingDataDetailActivity.BUNDLE_RIDING_ID, ridingData.getId());
        intent.putExtra(RidingDataDetailActivity.BUNDLE_RIDING_START_TIME, ridingData.getStartTime());
        intent.putExtra(RidingDataDetailActivity.BUNDLE_RIDING_END_TIME, ridingData.getEndTime());
        intent.putExtra(RidingDataDetailActivity.BUNDLE_RIDING_START_ADDRESS, ridingData.getStartAddress());
        intent.putExtra(RidingDataDetailActivity.BUNDLE_RIDING_END_ADDRESS, ridingData.getEndAddress());
        intent.putExtra(RidingDataDetailActivity.BUNDLE_RIDING_TOTAL_MILE, ridingData.getDistance());
        context.startActivity(intent);
    }

    /**
     * 电子围栏列表
     */
    public static void startGeoFenceListActivity(Context context) {
        Intent intent = new Intent(context, GeoFenceListActivity.class);
        context.startActivity(intent);
    }

    /**
     * 添加、编辑圆形电子围栏
     */
    public static void startAddGeoFenceCircleActivity(Context context, int id) {
        Intent intent = new Intent(context, EditGeoFenceCircleActivity.class);
        intent.putExtra(EXTRA_ID, id);
        context.startActivity(intent);
    }

    /**
     * 添加、编辑多边形电子围栏
     */
    public static void startAddGeoFencePolygonActivity(Context context, int id) {
        Intent intent = new Intent(context, EditGeoFencePolygonActivity.class);
        intent.putExtra(EXTRA_ID, id);
        context.startActivity(intent);
    }

    /**
     * 通知中心
     */
    public static void startNoticeCenterActivity(Context context) {
        Intent intent = new Intent(context, NoticeCenterActivity.class);
        context.startActivity(intent);
    }

    /**
     * 通知详情
     */
    public static void startNoticeDetailActivity(Context context, int id) {
        Intent intent = new Intent(context, NoticeDetailActivity.class);
        intent.putExtra(NoticeDetailActivity.BUNDLE_NOTICE_ID, id);
        context.startActivity(intent);
    }

    /**
     * 报警设置
     */
    public static void startAlarmSettingActivity(Context context) {
        Intent intent = new Intent(context, AlarmSettingActivity.class);
        context.startActivity(intent);
    }

    public static void reStart(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 跳转到隐私协议页面
     */
    public static void startPrivacyUrl(Context context) {
        Intent intent = new Intent(context, JFWebActivity.class);
        intent.putExtra(JFWebActivity.WEB_NAME, context.getString(R.string.privacy_policy));
        intent.putExtra(JFWebActivity.WEB_URL, OkHttpURLs.getPrivacyUrl());
        context.startActivity(intent);
    }

}
