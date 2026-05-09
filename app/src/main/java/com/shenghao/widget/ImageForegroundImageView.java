package com.shenghao.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.shenghao.R;

/**
 * 带图片前景圈的ImageView
 * 点击时前景图片会旋转2秒
 */
public class ImageForegroundImageView extends FrameLayout {
    
    // 默认值
    private static final int DEFAULT_ROTATION_DURATION = 200; // 2秒
    private static final int DEFAULT_FOREGROUND_VISIBILITY = View.INVISIBLE;
    
    // 子视图
    private ImageView backgroundImageView;
    private ImageView foregroundImageView;
    
    // 动画相关
    private ObjectAnimator rotationAnimator;
    private int rotationDuration = DEFAULT_ROTATION_DURATION;
    
    // 状态标志
    private boolean isAnimating = false;
    
    public ImageForegroundImageView(Context context) {
        this(context, null);
    }
    
    public ImageForegroundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public ImageForegroundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs) {
        // 设置布局参数
        setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT
        ));
        
        // 从XML属性读取配置
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ImageForegroundImageView);
        
        // 背景图片资源
        int backgroundSrc = ta.getResourceId(
                R.styleable.ImageForegroundImageView_backgroundSrc, 0);
        
        // 前景图片资源
        int foregroundSrc = ta.getResourceId(
                R.styleable.ImageForegroundImageView_foregroundSrc, 0);
        
        // 前景可见性
        int foregroundVisibility = ta.getInteger(
                R.styleable.ImageForegroundImageView_foregroundVisibility, 
                DEFAULT_FOREGROUND_VISIBILITY);
        
        // 旋转持续时间
        int duration = ta.getInteger(
                R.styleable.ImageForegroundImageView_rotationDuration, 
                DEFAULT_ROTATION_DURATION);
        if (duration > 0) {
            rotationDuration = duration;
        }
        
        ta.recycle();
        
        // 创建背景ImageView（主图片）
        backgroundImageView = new ImageView(context);
        backgroundImageView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, 
                LayoutParams.MATCH_PARENT
        ));
        
        // 设置背景图片
        if (backgroundSrc != 0) {
            backgroundImageView.setImageResource(backgroundSrc);
        }
        
        // 创建前景ImageView（圆环图片）
        foregroundImageView = new ImageView(context);
        foregroundImageView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, 
                LayoutParams.MATCH_PARENT
        ));
        
        // 设置前景图片
        if (foregroundSrc != 0) {
            foregroundImageView.setImageResource(foregroundSrc);
        }
        
        // 设置前景可见性
        foregroundImageView.setVisibility(foregroundVisibility);
        foregroundImageView.setVisibility(View.INVISIBLE);

        // 添加子视图（背景在下，前景在上）
        addView(backgroundImageView);
        addView(foregroundImageView);
        
        // 初始化动画
        setupRotationAnimator();
        
        // 设置点击监听
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startRotation();
//            }
//        });
    }
    
    private void setupRotationAnimator() {
        rotationAnimator = ObjectAnimator.ofFloat(
                foregroundImageView, 
                "rotation", 
                0f, 
                360f
        );
        rotationAnimator.setDuration(rotationDuration);
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.setRepeatCount(0); // 不重复，只旋转一次

        rotationAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
                getForegroundImageView().setVisibility(View.INVISIBLE);
                // 动画结束后执行的操作
                Log.d("ImageForegroundImageView", "旋转动画结束");

                // 例如：动画结束后隐藏前景圆圈
                // foregroundImageView.setVisibility(View.GONE);

                // 或者：动画结束后改变背景图片
                // backgroundImageView.setImageResource(R.drawable.new_image);

                // 调用自定义监听器
//                if (rotationListener != null) {
//                    rotationListener.onRotationEnd();
//                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimating = true;
                getForegroundImageView().setVisibility(View.VISIBLE);
//                if (rotationListener != null) {
//                    rotationListener.onRotationStart();
//                }
            }
        });
    }
    
    /**
     * 设置背景图片资源
     */
    public void setBackgroundImageResource(int resId) {
        backgroundImageView.setImageResource(resId);
    }
    
    /**
     * 设置背景图片Drawable
     */
    public void setBackgroundImageDrawable(android.graphics.drawable.Drawable drawable) {
        backgroundImageView.setImageDrawable(drawable);
    }
    
    /**
     * 获取背景ImageView（用于设置scaleType等属性）
     */
    public ImageView getBackgroundImageView() {
        return backgroundImageView;
    }
    
    /**
     * 设置前景圆环图片资源
     */
    public void setForegroundImageResource(int resId) {
        foregroundImageView.setImageResource(resId);
    }
    
    /**
     * 设置前景图片Drawable
     */
    public void setForegroundImageDrawable(android.graphics.drawable.Drawable drawable) {
        foregroundImageView.setImageDrawable(drawable);
    }
    
    /**
     * 获取前景ImageView（用于设置scaleType等属性）
     */
    public ImageView getForegroundImageView() {
        return foregroundImageView;
    }
    
    /**
     * 设置前景圆环是否可见
     */
    public void setForegroundVisible(boolean visible) {
        foregroundImageView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
    
    /**
     * 开始旋转动画
     */
    public void startRotation() {
//        if (rotationAnimator != null && !isAnimating) {
//            isAnimating = true;
//            rotationAnimator.start();
//        }
    }
    
    /**
     * 停止旋转动画
     */
    public void stopRotation() {
        if (rotationAnimator != null && isAnimating) {
            rotationAnimator.cancel();
            isAnimating = false;
            foregroundImageView.setRotation(0f);
        }
    }
    
    /**
     * 设置旋转持续时间（单位：毫秒）
     */
    public void setRotationDuration(int duration) {
        this.rotationDuration = duration;
        if (rotationAnimator != null) {
            rotationAnimator.setDuration(duration);
        }
    }
    
    /**
     * 检查是否正在动画中
     */
    public boolean isAnimating() {
        return isAnimating;
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 停止动画，防止内存泄漏
        stopRotation();
    }
}