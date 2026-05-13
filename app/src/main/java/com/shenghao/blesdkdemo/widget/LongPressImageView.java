package com.shenghao.blesdkdemo.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatImageView;

public class LongPressImageView extends AppCompatImageView {
    
    // 长按检测时间阈值（毫秒）
    private static final int LONG_PRESS_TIME = 500;
    
    private Handler handler = new Handler();
    private Runnable longPressRunnable;
    private boolean isLongPressTriggered = false;
    private OnImageActionListener actionListener;
    
    public interface OnImageActionListener {
        void onDown();
        void onUp();
        void onLongPress();
    }
    
    public LongPressImageView(Context context) {
        super(context);
        init();
    }
    
    public LongPressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public LongPressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        // 初始化长按检测Runnable
        longPressRunnable = new Runnable() {
            @Override
            public void run() {
                isLongPressTriggered = true;
                if (actionListener != null) {
                    actionListener.onLongPress();
                }
            }
        };
        
        // 设置触摸监听
        setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handleActionDown();
                    return true;
                    
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handleActionUp();
                    return true;
            }
            return false;
        });
    }
    
    private void handleActionDown() {
        isLongPressTriggered = false;
        
        // 触发按下回调
        if (actionListener != null) {
            actionListener.onDown();
        }
        
        // 延迟检测长按事件
        handler.postDelayed(longPressRunnable, LONG_PRESS_TIME);
    }
    
    private void handleActionUp() {
        // 移除长按检测
        handler.removeCallbacks(longPressRunnable);
        
        // 触发抬起回调
        if (actionListener != null) {
            actionListener.onUp();
        }
        
        // 如果已经触发了长按，这里可以做一些清理工作
        if (isLongPressTriggered) {
            // 长按后的清理操作
        }
    }
    
    public void setOnImageActionListener(OnImageActionListener listener) {
        this.actionListener = listener;
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 移除所有回调，防止内存泄漏
        handler.removeCallbacksAndMessages(null);
    }
}