package com.shenghao;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.amap.api.maps.MapsInitializer;
import com.clj.fastble.BleManager;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.shenghao.blesdk.BleSdk;
import com.shenghao.event.AppBackgroundEvent;
import com.shenghao.ui.SplashActivity;
import com.shenghao.utility.AppSingleton;
import com.shenghao.utils.SPUtils;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;


//
public class ShengHaoApp extends Application {
    private static ShengHaoApp app;
    private int countActivity = 0;
    private boolean isBackground = false;   //是否进入后台

    public static ShengHaoApp getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        initBackgroundCallBack();
        initBle();
//        initJPush();
        //崩溃检测
        if(SPUtils.getInstance().getBoolean(SPUtils.SP_HAS_SHOW_PRIVACY, false)){
            initBugly();
        }
    }
    public void initBugly() {
        CrashReport.initCrashReport(getApplicationContext(), "bb53cc35bb", false);
        initMap();
    }

    private void initMap() {
        MapsInitializer.updatePrivacyShow(this, true, true);
        MapsInitializer.updatePrivacyAgree(this, true);
    }
//    private void initJPush() {
//        JPushInterface.setDebugMode(true);
//
//        // 调整点一：初始化代码前增加setAuth调用
//        boolean isPrivacyReady = SPUtils.getInstance().getBoolean(SPUtils.SP_HAS_SHOW_PRIVACY, false); // app根据是否已弹窗获取隐私授权来赋值
//        if(!isPrivacyReady){
//            JCollectionAuth.setAuth(this, false); // 后续初始化过程将被拦截
//        }
//        JPushInterface.init(this);
//    }

    private void initBle() {
        BleManager.getInstance().init(this);
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setOperateTimeout(5000);
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setScanTimeOut(15000)
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
        
        BleSdk.getInstance().initialize(this);
    }

    private void initBackgroundCallBack() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                countActivity++;
                if (countActivity == 1 && isBackground) {   //应用进入前台
                    isBackground = false;
                    AppSingleton.getInstance().refreshTokenByCheck();   //每次回到前台，检测token是否需要刷新
                    EventBus.getDefault().post(new AppBackgroundEvent(isBackground));
                }

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                countActivity--;
                if (countActivity <= 0 && !isBackground) {  //应用进入后台
                    isBackground = true;
                    EventBus.getDefault().post(new AppBackgroundEvent(isBackground));
                }

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }
}
