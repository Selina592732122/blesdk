//package com.shenghao.blesdkdemo.jpush;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.Looper;
//import android.widget.Toast;
//
//import androidx.core.app.NotificationManagerCompat;
//
//import com.shenghao.blesdkdemo.R;
//import com.shenghao.blesdkdemo.utils.LogUtils;
//
//import cn.jpush.android.api.CmdMessage;
//import cn.jpush.android.api.CustomMessage;
//import cn.jpush.android.api.NotificationMessage;
//import cn.jpush.android.service.JPushMessageReceiver;
//
//public class JPushReceiver extends JPushMessageReceiver {
//    public final String TAG = this.getClass().getSimpleName();
//    @Override
//    public void onMessage(Context context, CustomMessage customMessage) {
//        // 处理自定义消息
//        String message = customMessage.message;
//        String title = customMessage.title;
//        // 你的逻辑代码
//    }
//    @Override
//    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
//        // 处理通知到达的逻辑
//        String title = notificationMessage.notificationTitle;
//        String content = notificationMessage.notificationContent;
//        // 你的逻辑代码
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                LogUtils.e(TAG,context+",0"+Thread.currentThread().getName());
//                Toast.makeText(context, "收到消息，但是没有打开通知权限", Toast.LENGTH_SHORT).show();
//                check(context);
//            }
//        });
//
//    }
//    @Override
//    public void onCommandResult(Context context, CmdMessage cmdMessage) {
//        // 处理命令消息的逻辑
//        String cmd = String.valueOf(cmdMessage.cmd);
//        // 你的逻辑代码
//    }
//
//    private void check(Context context) {
//        // 如果通知权限未开启，可以通过其他方式提醒用户
//        if (!areNotificationsEnabled(context)) {
//            showNotificationPermissionDialog(context);
//        } else {
//            // 如果通知权限已开启，可以正常显示通知
////            showNotification(context, title, message);
//        }
//    }
//    public boolean areNotificationsEnabled(Context context) {
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        return notificationManager.areNotificationsEnabled();
//    }
//
//    public void showNotificationPermissionDialog(Context context) {
//        // 启动 DialogActivity
//        Intent dialogIntent = new Intent(context, DialogActivity.class);
//        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 必须添加 FLAG_ACTIVITY_NEW_TASK
//        context.startActivity(dialogIntent);
////        new AlertDialog.Builder(context)
////                .setTitle("通知权限未开启")
////                .setMessage("请开启通知权限，以便及时接收重要消息")
////                .setPositiveButton("去开启", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        openNotificationSettings(context);
////                    }
////                })
////                .setNegativeButton("取消", null)
////                .show();
//
//    }
//}