package com.shenghao.blesdkdemo.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.AddressBean;
import com.shenghao.blesdkdemo.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;


public class AddressDialog extends Dialog implements View.OnClickListener {
    private final Context mContext;
    private OnCloseListener listener;
    private boolean cancelable;
    private View submitTxt,cancelTv;
    private String title;
    private TextView titleTxt;
    private TimeType timeType = TimeType.hoursAndMinutes;
    private WheelView wheelviewLeft,wheelviewRight;
    private int selectItem;
    private List<AddressBean> list;
    public AddressDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public AddressDialog(Context context, OnCloseListener listener) {
        super(context, R.style.Common_Dialog);
        this.mContext = context;
        this.listener = listener;
    }

    protected AddressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }
    public AddressDialog(Context context, List<AddressBean> list, OnCloseListener listener) {
        super(context, R.style.Common_Dialog);
        this.mContext = context;
        this.listener = listener;
        this.list = list;
    }

    public AddressDialog setDialogCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_address);
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
    private List<String> getStringList(){
        List<String> hours = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            hours.add(list.get(i).getRegionName());
        }
        return hours;
    }

    private void initWheelView() {
            wheelviewLeft.setOffset(2);
            wheelviewLeft.setItems(getStringList());
            wheelviewLeft.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                    selectItem = selectedIndex - 2;
//                    if (listener != null) {
//                        listener.onClick(AddressDialog.this, true,item);
//                    }
                }
            });
    }

    private void initView() {
        submitTxt = findViewById(R.id.submitTv);
        cancelTv = findViewById(R.id.cancelTv);
        submitTxt.setOnClickListener(this);
        cancelTv.setOnClickListener(this);
        wheelviewLeft = findViewById(R.id.wheelviewLeft);
//        wheelviewRight = findViewById(R.id.wheelviewRight);
//        llRight = findViewById(R.id.llRight);
        titleTxt = (TextView) findViewById(R.id.title);
//        if (!TextUtils.isEmpty(title)) {
//            titleTxt.setText(title);
//        }

    }

    public AddressDialog setTitle(String title) {
        this.title = title;
        return this;
    }
    public AddressDialog setTimeType(TimeType type) {
        this.timeType = type;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancelTv) {
            if (listener != null) {
                listener.onClick(this, false,-1);
            }
            this.dismiss();
        } else if (v.getId() == R.id.submitTv) {
            if (listener != null) {
                listener.onClick(this, true,selectItem);
            }
        }
    }

    public void setList(List<AddressBean> shiList) {
        this.list = shiList;
        if(wheelviewLeft!=null)
             wheelviewLeft.setItems(getStringList());
    }

    public enum TimeType {
        hoursAndMinutes,
        days
    }
    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean confirm,int index);
    }
}
