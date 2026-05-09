package com.shenghao.utils;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

public class GPSUtils {

    public static final int requestGPSCode = 1001;

    /**
     *
     * @param context
     * @return
     */
    public static boolean isGpsOpen(Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快
//        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

//        String AA = locationManager==null?"YES":"NO";
//
//        RBQLog.i("locationManager为null:"+AA);

        return locationManager!=null&&locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {

            PendingIntent.getBroadcast(context, 0, GPSIntent, FLAG_IMMUTABLE).send();

        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param activity
     */
    public static void goGPSSetting(Activity activity){

        LocationManager lm = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
        if (lm!=null&&lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Toast.makeText(activity, "GPS模块正常", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(activity, "请开启GPS！", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        activity.startActivityForResult(intent,requestGPSCode); //此为设置完成后返回到获取界面
    }

    public static void goGPSSetting1(Activity activity){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivityForResult(intent, requestGPSCode);
    }

}
