package com.shenghao.ui.helper;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;


import com.shenghao.R;
import com.shenghao.bean.TerminalBean;
import com.shenghao.okhttp.OkHttpBaseResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.utility.AppSingleton;
import com.shenghao.utils.ActivityManager;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.Redirect;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.CommonDialog;
import com.shenghao.widget.IosBottomSheetDialog;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class TerminalOperationHelper {
    private Context mContext;
    private TerminalOperationCallback mCallback;

    public TerminalOperationHelper(Context context, TerminalOperationCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
    }

    public void showBottomSheetDialog(TerminalBean terminalBean, int pos) {
        String terminalNo = terminalBean.getTerminalNo();
        String terminalName = terminalBean.getName();
        int batteryCount = terminalBean.getBatteries();
        boolean isCurrentTerminal = terminalBean.isSelected();
        int shareStatus = terminalBean.getShareStatus();//0-未共享 1-共享 2-被共享
        
        if(shareStatus == 2 && isCurrentTerminal)//当前车辆是共享的就不弹窗了
            return;
            
        IosBottomSheetDialog.Builder builder = new IosBottomSheetDialog.Builder(mContext);
        builder.addTitleView(mContext.getString(R.string.terminal_no) + terminalNo, 0);
        
        if (!isCurrentTerminal) {   //非当前设备
            builder.addItemView(mContext.getString(R.string.change_to_terminal), 0, false, view -> {
                new CommonDialog(mContext, (dialog, confirm) -> {
                    if (confirm) {  //确定
                        dialog.dismiss();
                        updateDefaultTerminal(terminalNo, 1);
                    }
                }).setTitle(mContext.getString(R.string.sure_to_change)).show();
            });
        }
        
        if(shareStatus != 2){
            builder.addItemView(mContext.getString(R.string.rename), 0, false, view -> {
                showEditTerminalNameDialog(terminalNo, terminalName);
            });
            
            builder.addItemView(mContext.getString(R.string.change_battery_num), 0, false, view -> {
                showEditBatteryCountDialog(terminalNo, batteryCount);
            });
        }

        if (!isCurrentTerminal && shareStatus != 2) {   //非当前设备
            builder.addItemView(mContext.getString(R.string.unbind), Color.parseColor("#de001f"), false, view -> {
                new CommonDialog(mContext, (dialog, confirm) -> {
                    if (confirm) {
                        dialog.dismiss();
                        unbindTerminal(terminalNo, pos);
                    }
                }).setTitle(mContext.getString(R.string.sure_to_unbind)).show();
            });
        }
        
        builder.build().show();
    }

    /**
     * 解绑设备
     */
    public void unbindTerminal(String terminalNo, int pos) {
        OkHttpPresent.unbindTerminal(terminalNo, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                if (mCallback != null) mCallback.showLoading();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e("TerminalOperationHelper", "onResponse: 解绑成功 = " + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null && baseResp.isSuccess()) {
                    if (mCallback != null) mCallback.refreshDeviceList();
                } else {
                    ToastUtils.showShort(mContext, mContext.getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e("TerminalOperationHelper", "onFailed: 解绑失败 = " + e);
                ToastUtils.showShort(mContext, mContext.getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                if (mCallback != null) mCallback.hideLoading();
            }
        });
    }

    /**
     * 设备重命名弹窗
     */
    public void showEditTerminalNameDialog(String terminalNo, String terminalName) {
        CommonDialog editTerminalNameDialog = new CommonDialog(mContext, (dialog, confirm) -> {
            if (confirm) {  //确定
                String contentEtText =  ((CommonDialog)dialog).getContentEtText().trim();
                if (!TextUtils.isEmpty(contentEtText)) {
                    reNameTerminal(terminalNo, contentEtText);
                    dialog.dismiss();
                } else {
                    ToastUtils.showShort(mContext, mContext.getString(R.string.input_terminal_name));
                }
            }
        })
        .setTitle(mContext.getString(R.string.terminal_name))
        .setHint(mContext.getString(R.string.input_terminal_name))
        .setPositiveButton(mContext.getString(R.string.save))
        .setContentEtVisibility(View.VISIBLE);
        
        editTerminalNameDialog.setEditContent(terminalName);
        editTerminalNameDialog.show();
    }

    /**
     * 重命名设备
     */
    private void reNameTerminal(String terminalNo, String contentEtText) {
        OkHttpPresent.reNameTerminal(terminalNo, contentEtText, new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e("TerminalOperationHelper", "onResponse: 重命名成功 = " + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) {
                        if (mCallback != null) mCallback.refreshDeviceList();
                    } else {
                        ToastUtils.showShort(mContext, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(mContext, mContext.getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e("TerminalOperationHelper", "onFailed: 重命名失败 = " + e);
            }
        });
    }

    /**
     * 修改电池数量弹窗
     */
    public void showEditBatteryCountDialog(String terminalNo, int batteryCount) {
        CommonDialog editBatteryCountDialog = new CommonDialog(mContext, (dialog, confirm) -> {
            if (confirm) {  //确定
                String contentEtText = ((CommonDialog)dialog).getContentEtText().trim();
                if (!TextUtils.isEmpty(contentEtText)) {
                    changeBatteryCount(terminalNo, contentEtText);
                    dialog.dismiss();
                } else {
                    ToastUtils.showShort(mContext, mContext.getString(R.string.battery_hint));
                }
            }
        })
        .setTitle(mContext.getString(R.string.battery_num))
        .setHint(mContext.getString(R.string.battery_hint))
        .setPositiveButton(mContext.getString(R.string.save))
        .setContentEtVisibility(View.VISIBLE);
        
        editBatteryCountDialog.setEditContent(batteryCount + "");
        editBatteryCountDialog.setEditContentInputType(EditorInfo.TYPE_CLASS_NUMBER);
        editBatteryCountDialog.show();
    }

    /**
     * 修改电池数量
     */
    private void changeBatteryCount(String terminalNo, String contentEtText) {
        OkHttpPresent.changeBatteryCount(terminalNo, Integer.parseInt(contentEtText), new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e("TerminalOperationHelper", "onResponse: 修改电池数量成功 = " + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) {
                        if (mCallback != null) mCallback.refreshDeviceList();
                    } else {
                        ToastUtils.showShort(mContext, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(mContext, mContext.getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e("TerminalOperationHelper", "onFailed: 修改电池数量失败 = " + e);
            }
        });
    }

    /**
     * 设置当前默认设备
     */
    private void updateDefaultTerminal(String terminalNo, int status) {
        OkHttpPresent.updateDefaultTerminal(terminalNo, status, new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e("TerminalOperationHelper", "onResponse: 当前默认设备设置成功 = " + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) {
                        AppSingleton.getInstance().setTerminalNo(terminalNo);   //保存选中的设备号
                        //重启应用
                        ActivityManager.finishAllActivity();
                        Redirect.reStart(mContext);
                    } else {
                        ToastUtils.showShort(mContext, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(mContext, mContext.getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e("TerminalOperationHelper", "onFailed: 当前默认设备设置失败 = " + e);
            }
        });
    }

    public interface TerminalOperationCallback {
        void showLoading();
        void hideLoading();
        void refreshDeviceList();
    }
}