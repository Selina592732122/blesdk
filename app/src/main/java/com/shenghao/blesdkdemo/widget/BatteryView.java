package com.shenghao.blesdkdemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.utils.DensityUtil;

public class BatteryView extends RelativeLayout {

    private ImageView ivBattery;
    public BatteryView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.layout_battery, this);
        ivBattery = findViewById(R.id.ivBattery);
    }

    public void setBattery(int p){
        ViewGroup.LayoutParams layoutParams = ivBattery.getLayoutParams();
        layoutParams.width = (int) (DensityUtil.dip2px(getContext(),187) * p /100.f);
        ivBattery.setLayoutParams(layoutParams);
        if(p <= 15){
            ivBattery.setImageResource(R.drawable.bg_red_battery);
        }else{
            ivBattery.setImageResource(R.drawable.bg_blue_battery);
        }
        invalidate();
    }
}
