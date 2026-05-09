package com.shenghao.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class VerticalProgressBar extends View {
    private Paint backgroundPaint;
    private Paint progressPaint;
    private int max = 100;
    private int progress = 0;
    private float cornerRadius = 10f; // 圆角半径

    public VerticalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#66FFFFFF"));
        backgroundPaint.setAntiAlias(true); // 开启抗锯齿

        progressPaint = new Paint();
        progressPaint.setColor(Color.parseColor("#FF02C991"));
        progressPaint.setAntiAlias(true); // 开启抗锯齿
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 创建圆角矩形背景
        RectF backgroundRect = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, backgroundPaint);

        // 计算进度高度
        float progressHeight = ((float)progress / max) * getHeight();

        // 创建圆角进度矩形（从底部向上）
        RectF progressRect = new RectF(0, getHeight() - progressHeight,
                getWidth(), getHeight());

        // 确保进度条底部圆角完整显示
        if(progressHeight < cornerRadius) {
            // 当进度高度小于圆角半径时，调整圆角半径
            canvas.drawRoundRect(progressRect, progressHeight, progressHeight, progressPaint);
        } else {
            canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, progressPaint);
        }
    }

    // 设置圆角半径
    public void setCornerRadius(float radius) {
        this.cornerRadius = radius;
        invalidate();
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }
}