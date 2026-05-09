package com.shenghao.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.shenghao.R;
import com.shenghao.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;


public class TimeDialog extends Dialog implements View.OnClickListener {
    private final Context mContext;
    private OnCloseListener listener;
    private boolean cancelable;
    private View submitTxt,cancelTv;
    private String title;
    private TextView titleTxt;
    private TimeType timeType = TimeType.hoursAndMinutes;
    private String hoursItem = "00",minutesItem = "00",daysItem = "3";
    private WheelView wheelviewLeft,wheelviewRight;
    private LinearLayout llRight;
    public TimeDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public TimeDialog(Context context, OnCloseListener listener) {
        super(context, R.style.Common_Dialog);
        this.mContext = context;
        this.listener = listener;
    }

    protected TimeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public TimeDialog setDialogCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time);
        setCanceledOnTouchOutside(cancelable);
        setCancelable(cancelable);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.gravity = Gravity.BOTTOM;
        p.height = (int) (ScreenUtils.getScreenHeight(mContext)*0.4f);
        p.width = (int) (ScreenUtils.getScreenWidth(mContext) * 1f);
        getWindow().setAttributes(p);

        initView();
        initWheelView();
    }

    private void initWheelView() {
        if(timeType == TimeType.hoursAndMinutes){
            titleTxt.setText("选择时间");
            llRight.setVisibility(View.VISIBLE);
            wheelviewLeft.setOffset(2);
            List<String> hours = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                if(i<10)
                    hours.add("0"+i+"时");
                else hours.add(i+"时");
            }
            wheelviewLeft.setItems(hours);
//            wheelviewLeft.setSeletion(1);//默认选中下标
            wheelviewLeft.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                    hoursItem = item.substring(0,2);
                }
            });

            wheelviewRight.setOffset(2);
            List<String> minutes = new ArrayList<>();
            for (int i = 0; i < 60; i++) {
                if(i<10)
                    minutes.add("0"+i+"分");
                else minutes.add(i+"分");
            }
            wheelviewRight.setItems(minutes);
            wheelviewRight.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                    Log.d("onSelected", "selectedIndex: " + selectedIndex + ", item: " + item);
                    minutesItem = item.substring(0,2);
                }
            });
        }else {
            titleTxt.setText("超时时间");
            llRight.setVisibility(View.GONE);
            wheelviewLeft.setOffset(2);
            List<String> days = new ArrayList<>();
            for (int i = 3; i < 33; i++) {
                days.add(i+"");
            }
            wheelviewLeft.setItems(days);
            wheelviewLeft.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                    daysItem = item;
                }
            });
        }


    }

    private void initView() {
        submitTxt = findViewById(R.id.submitTv);
        cancelTv = findViewById(R.id.cancelTv);
        submitTxt.setOnClickListener(this);
        cancelTv.setOnClickListener(this);
        wheelviewLeft = findViewById(R.id.wheelviewLeft);
        wheelviewRight = findViewById(R.id.wheelviewRight);
        llRight = findViewById(R.id.llRight);
        titleTxt = (TextView) findViewById(R.id.title);
//        if (!TextUtils.isEmpty(title)) {
//            titleTxt.setText(title);
//        }

    }

    public TimeDialog setTitle(String title) {
        this.title = title;
        return this;
    }
    public TimeDialog setTimeType(TimeType type) {
        this.timeType = type;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancelTv) {
            if (listener != null) {
                listener.onClick(this, false,"");
            }
            this.dismiss();
        } else if (v.getId() == R.id.submitTv) {
            if (listener != null) {
                if(timeType == TimeType.hoursAndMinutes)
                    listener.onClick(this, true,hoursItem+":"+minutesItem);
                else
                    listener.onClick(this, true,daysItem);
            }
        }
    }

    public void setOnCloseListener(OnCloseListener callBack) {
        this.listener = callBack;
    }

    public enum TimeType {
        hoursAndMinutes,
        days
    }
    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean confirm,String item);
    }
}
