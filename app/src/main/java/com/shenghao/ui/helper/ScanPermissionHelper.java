package com.shenghao.ui.helper;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.os.Build;

import androidx.annotation.NonNull;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.base.IPermission;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.shenghao.R;
import com.shenghao.XXPermissions.PermissionDescription;
import com.shenghao.XXPermissions.PermissionInterceptor;
import com.shenghao.ui.BindTerminalActivity;
import com.shenghao.utils.SPUtils;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.CommonDialog;

import java.util.ArrayList;
import java.util.List;

public class ScanPermissionHelper {
    private static final String TAG = "ScanPermissionHelper";
    
    /**
     * 检查并请求扫描权限
     * @param activity 当前活动
     * @param requestCode 扫描请求码
     */
    public static void checkAndRequestScanPermission(Activity activity, int requestCode) {
        boolean needShowScanPermissionDialog = SPUtils.getInstance().getBoolean(
            SPUtils.SP_NEED_SHOW_SCAN_PERMISSION_DIALOG, true);
            
        if (needShowScanPermissionDialog) {
            showPermissionDialog(activity, requestCode);
        } else {
            startScan(activity, requestCode);
        }
    }
    
    /**
     * 显示权限提示对话框
     * @param activity 当前活动
     * @param requestCode 扫描请求码
     */
    private static void showPermissionDialog(Activity activity, int requestCode) {
        new CommonDialog(activity, new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    dialog.dismiss();
                    startScan(activity, requestCode);
                }
            }
        })
        .setTitle(activity.getString(R.string.permission_for_camera))
        .setPositiveButton(activity.getString(R.string.dialog_ok))
        .setDialogCancelable(false)
        .show();
    }
    
    /**
     * 开始扫描流程（权限请求）
     * @param activity 当前活动
     * @param requestCode 扫描请求码
     */
    private static void startScan(Activity activity, int requestCode) {
//        Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
        List<String> permissions = new ArrayList<>();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            permissions.add(Manifest.permission.CAMERA);
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
        }else {
            permissions.add(Manifest.permission.CAMERA);
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
//            permissions.add(Manifest.permission.CAMERA);
//            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        XXPermissions.with(activity)
//                .permission(permissions)
                .permission(PermissionLists.getCameraPermission())
                .permission(PermissionLists.getReadMediaImagesPermission())
                // 设置权限请求拦截器（局部设置）
//                .interceptor(new PermissionInterceptor(getString(R.string.permissionCamera)))
                .interceptor(new PermissionInterceptor())
                .description(new PermissionDescription())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onResult(@NonNull List<IPermission> grantedList, @NonNull List<IPermission> deniedList) {
                        boolean allGranted = deniedList.isEmpty();
                        if (!allGranted) {
                            // 判断请求失败的权限是否被用户勾选了不再询问的选项
                            boolean doNotAskAgain = XXPermissions.isDoNotAskAgainPermissions(activity, deniedList);
                            // 在这里处理权限请求失败的逻辑
                            if (doNotAskAgain) {
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                                XXPermissions.startPermissionActivity(BindTerminalActivity.this, deniedList);
                            } else {
                                ToastUtils.showShort(activity, activity.getString(R.string.permissions_fail));
                            }
                            return;
                        }
                        // 在这里处理权限请求成功的逻辑
                        SPUtils.getInstance().putBoolean(SPUtils.SP_NEED_SHOW_SCAN_PERMISSION_DIALOG, false);
                        ScanUtil.startScan(activity, requestCode, null);
                    }

//                    @Override
//                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
//                        if (!allGranted) {
//                            ToastUtils.showShort(BindTerminalActivity.this,"获取部分权限成功，但部分权限未正常授予");
//                            return;
//                        }
//                        SPUtils.getInstance().putBoolean(SPUtils.SP_NEED_SHOW_SCAN_PERMISSION_DIALOG, false);
//                        ScanUtil.startScan(BindTerminalActivity.this, REQUEST_CODE_SCAN, null);
//                    }
//
//                    @Override
//                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
//                        if (doNotAskAgain) {
//                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                            XXPermissions.startPermissionActivity(BindTerminalActivity.this, permissions);
//                        } else {
//                            ToastUtils.showShort(BindTerminalActivity.this,"获取权限失败");
//                        }
//                    }
                });
    }
}