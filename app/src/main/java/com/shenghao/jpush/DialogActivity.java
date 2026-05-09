package com.shenghao.jpush;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import com.shenghao.widget.CommonDialog;

public class DialogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CommonDialog(this, new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    openNotificationSettings(DialogActivity.this);
                }
                dialog.dismiss();
                finish();
            }
        })
                .setTitle("通知权限未开启" + "\n请开启通知权限，以便及时接收重要消息")
                .setPositiveButton("确定")
                .setDialogCancelable(false)
                .show();

    }
    public void openNotificationSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        context.startActivity(intent);
    }
}