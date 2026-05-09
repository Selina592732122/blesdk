package com.shenghao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.shenghao.R;

public class RippleView2 extends FrameLayout {

    private ImageView iv1,iv2;
    public RippleView2(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RippleView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.layout_ripple, this);
        iv1 = findViewById(R.id.iv_wave_1);
        iv2 = findViewById(R.id.iv_wave_2);
        setAnim1();
        setAnim2();
    }
    private void setAnim1() {
        AnimationSet as = new AnimationSet(true);
        //缩放动画，以中心从原始放大到1.4倍
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 2.4f, 1.0f, 2.4f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        //渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        as.setDuration(1000);
        as.addAnimation(scaleAnimation);
        as.addAnimation(alphaAnimation);
        iv1.startAnimation(as);
    }
    private void setAnim2() {
        AnimationSet as = new AnimationSet(true);
        //缩放动画，以中心从1.4倍放大到1.8倍
        ScaleAnimation scaleAnimation = new ScaleAnimation(2.4f, 3.8f, 2.4f, 3.8f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        //渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 0.01f);
        scaleAnimation.setDuration(800);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        as.setDuration(800);
        as.addAnimation(scaleAnimation);
        as.addAnimation(alphaAnimation);
        iv2.startAnimation(as);
    }
}
