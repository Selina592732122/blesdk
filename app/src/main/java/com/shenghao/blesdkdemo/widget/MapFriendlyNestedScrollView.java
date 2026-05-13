package com.shenghao.blesdkdemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.widget.NestedScrollView;

public class MapFriendlyNestedScrollView extends NestedScrollView {
    private View mapView;

    public MapFriendlyNestedScrollView(Context context) {
        super(context);
    }

    public MapFriendlyNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapFriendlyNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMapView(View mapView) {
        this.mapView = mapView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mapView != null && isTouchInView(ev, mapView)) {
            requestDisallowInterceptTouchEvent(true);
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean performClick() {
        // 处理点击事件以支持无障碍功能
        super.performClick();
        return true;
    }

    private boolean isTouchInView(MotionEvent ev, View view) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        return x >= viewLocation[0] && x <= (viewLocation[0] + view.getWidth()) &&
               y >= viewLocation[1] && y <= (viewLocation[1] + view.getHeight());
    }
}