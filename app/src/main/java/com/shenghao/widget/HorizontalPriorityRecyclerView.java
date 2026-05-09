package com.shenghao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

public class HorizontalPriorityRecyclerView extends RecyclerView {
    private float startX, startY;
    private boolean isHorizontalScroll;
    private static final int HORIZONTAL_SLOP = 5; // 水平移动阈值（像素）
    
    public HorizontalPriorityRecyclerView(Context context) {
        super(context);
        init();
    }

    public HorizontalPriorityRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizontalPriorityRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // 禁用嵌套滚动
        setNestedScrollingEnabled(false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                startX = e.getX();
                startY = e.getY();
                isHorizontalScroll = false;
                // 初始请求不拦截
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
                
            case MotionEvent.ACTION_MOVE:
                if (!isHorizontalScroll) {
                    float dx = Math.abs(e.getX() - startX);
                    float dy = Math.abs(e.getY() - startY);
                    
                    // 只要检测到水平移动（即使很小）就优先处理
                    if (dx > HORIZONTAL_SLOP || dx > dy) {
                        isHorizontalScroll = true;
                        // 请求父容器不要拦截触摸事件
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
                
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isHorizontalScroll = false;
                // 释放拦截请求
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // 如果是水平滑动，始终请求不拦截
        if (isHorizontalScroll) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(e);
    }
}