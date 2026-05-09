package com.shenghao.widget;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.shenghao.R;

public class SixGearSeekBar extends View {
    private static final String TAG = "SixGearSeekBar";

    // 默认属性
    private int mTrackHeight = dp2px(18);
    private int mThumbRadius = dp2px(12);
    private int mGearPointRadius = dp2px(3);
    private int mSideMargin = dp2px(18); // 头尾边距10dp
    private int mTrackColorStart = Color.parseColor("#FF1C81F2");    // 蓝色
    private int mTrackColorMiddle = Color.parseColor("#FFF1F1F1");   // 灰白色（中间高光）
    private int mTrackColorEnd = Color.parseColor("#FFFF5E00");      // 橙色
    private int mThumbColor = Color.WHITE;
    private int mGearPointColor = Color.WHITE;
    private int mDisabledColor = Color.parseColor("#FFF3F4F5");      // 禁用状态颜色

    // 状态变量
    private int mMaxGear = 7;
    private int mCurrentGear = 1;
    private boolean mIsDragging = false;
    private boolean mEnabled = true;  // 新增：是否可用状态

    // 绘制组件
    private Paint mTrackPaint;
    private Paint mThumbPaint;
    private Paint mGearPointPaint;
    private RectF mTrackRect = new RectF();

    // 滑块图片相关
    private Bitmap mThumbBitmap;
    private Bitmap mGrayThumbBitmap;  // 新增：灰色滑块图片缓存
    private int mThumbWidth = dp2px(24);
    private int mThumbHeight = dp2px(24);

    // 渐变
    private LinearGradient mTrackGradient;
    private LinearGradient mDisabledGradient;  // 新增：禁用状态的渐变

    // 监听器
    private OnGearChangeListener mOnGearChangeListener;
    private Bitmap bitmapOff;

    public interface OnGearChangeListener {
        void onGearChanged(int gear);
        void onStartTrackingTouch();
        void onStopTrackingTouch();
    }

    public SixGearSeekBar(Context context) {
        this(context, null);
    }

    public SixGearSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SixGearSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        initPaints();

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handleTouchEvent(event);
            }
        });
    }

    private void initPaints() {
        // 轨道画笔
        mTrackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrackPaint.setStyle(Paint.Style.FILL);
        //off
        bitmapOff = BitmapFactory.decodeResource(getResources(), R.drawable.ic_air_off);

        // 拇指画笔（保留，可能用于其他绘制）
        mThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbPaint.setColor(mThumbColor);
        mThumbPaint.setStyle(Paint.Style.FILL);
        mThumbPaint.setShadowLayer(dp2px(2), 0, dp2px(1), 0x66000000);

        // 档位点画笔
        mGearPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGearPointPaint.setColor(mGearPointColor);
        mGearPointPaint.setStyle(Paint.Style.FILL);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        // 只需要考虑轨道和滑块的高度
        int height = Math.max(mThumbHeight, mThumbRadius * 2) + dp2px(10);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 重置渐变
        mTrackGradient = null;
        mDisabledGradient = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTrack(canvas);
        drawGearPoints(canvas);
        drawThumb(canvas);
    }

    private void drawTrack(Canvas canvas) {
        int trackTop = (getHeight() - mTrackHeight) / 2;
//        mTrackRect.set(mSideMargin, trackTop, getWidth() - mSideMargin, trackTop + mTrackHeight);
        mTrackRect.set(0, trackTop, getWidth(), trackTop + mTrackHeight);

        if (!mEnabled) {
            // 禁用状态：使用灰色渐变
            if (mDisabledGradient == null) {
                mDisabledGradient = new LinearGradient(
                        mTrackRect.left, mTrackRect.centerY(),
                        mTrackRect.right, mTrackRect.centerY(),
                        mDisabledColor, mDisabledColor,
                        Shader.TileMode.CLAMP
                );
            }
            mTrackPaint.setShader(mDisabledGradient);
        } else {
            // 正常状态：使用三色渐变
            if (mTrackGradient == null) {
                // 修改为三个颜色的渐变
                int[] colors = {
                        mTrackColorStart,     // 蓝色
                        mTrackColorMiddle,    // 灰白色（中间）
                        mTrackColorEnd        // 橙色
                };

                // 定义颜色分布位置（百分比）
                // 0f -> 起点，0.5f -> 中间点，1f -> 终点
                float[] positions = {0f, 0.5f, 1f};

                mTrackGradient = new LinearGradient(
                        mTrackRect.left, mTrackRect.centerY(),
                        mTrackRect.right, mTrackRect.centerY(),
                        colors, positions,
                        Shader.TileMode.CLAMP
                );
            }
            mTrackPaint.setShader(mTrackGradient);
        }

        float cornerRadius = mTrackHeight / 2f;
        canvas.drawRoundRect(mTrackRect, cornerRadius, cornerRadius, mTrackPaint);
    }

    private void drawGearPoints(Canvas canvas) {
        float gearAreaStart = mTrackRect.left + mSideMargin;
        float gearAreaEnd = mTrackRect.right - mSideMargin;
        float gearAreaWidth = gearAreaEnd - gearAreaStart;

        float segmentWidth = gearAreaWidth / (mMaxGear - 1);

        for (int i = 0; i < mMaxGear; i++) {
            float x = gearAreaStart + i * segmentWidth;
            float y = mTrackRect.centerY();

            // 设置档位点颜色
            mGearPointPaint.setColor(mGearPointColor);

            canvas.drawCircle(x, y, mGearPointRadius, mGearPointPaint);
            if(i == 3)
                canvas.drawBitmap(bitmapOff, x - bitmapOff.getWidth()/2.0f, y - bitmapOff.getHeight()/2.f, null);
        }


    }

    private void drawThumb(Canvas canvas) {
        float thumbX = getThumbPosition();
        float thumbY = mTrackRect.centerY();

        if (mThumbBitmap != null && !mThumbBitmap.isRecycled()) {
            // 使用图片作为滑块
            float left = thumbX - mThumbWidth / 2f;
            float top = thumbY - mThumbHeight / 2f;

            if (!mEnabled) {
                // 禁用状态：绘制灰色滑块
                if (mGrayThumbBitmap == null) {
                    mGrayThumbBitmap = convertToGrayScale(mThumbBitmap);
                }
                canvas.drawBitmap(mGrayThumbBitmap, left, top, null);
            } else {
                // 正常状态：绘制彩色滑块
                canvas.drawBitmap(mThumbBitmap, left, top, null);
            }
        } else {
            // 保持原有的圆形滑块作为备选
            if (!mEnabled) {
                mThumbPaint.setColor(mDisabledColor);
            } else {
                mThumbPaint.setColor(mThumbColor);
            }
            canvas.drawCircle(thumbX, thumbY, mThumbRadius, mThumbPaint);
        }
    }

    /**
     * 将图片转换为灰度图
     */
    private Bitmap convertToGrayScale(Bitmap original) {
        int width = original.getWidth();
        int height = original.getHeight();

        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();

        // 创建颜色矩阵，将图片转换为灰度
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);

        canvas.drawBitmap(original, 0, 0, paint);
        return grayBitmap;
    }

    private float getThumbPosition() {
        float gearAreaStart = mTrackRect.left + mSideMargin;
        float gearAreaEnd = mTrackRect.right - mSideMargin;
        float gearAreaWidth = gearAreaEnd - gearAreaStart;

        float segmentWidth = gearAreaWidth / (mMaxGear - 1);
        return gearAreaStart + (mCurrentGear - 1) * segmentWidth;
    }

    private boolean handleTouchEvent(MotionEvent event) {
        // 如果控件不可用，不处理触摸事件
//        if (!mEnabled) {
//            return false;
//        }

        float x = event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsDragging = true;
                if (mOnGearChangeListener != null) {
                    mOnGearChangeListener.onStartTrackingTouch();
                }
                updateGearFromPosition(x);
                return true;

            case MotionEvent.ACTION_MOVE:
                if (mIsDragging) {
                    updateGearFromPosition(x);
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsDragging = false;
                snapToNearestGear(x);
                if (mOnGearChangeListener != null) {
                    mOnGearChangeListener.onStopTrackingTouch();
                }
                return true;
        }
        return false;
    }

    private void updateGearFromPosition(float x) {
        int newGear = calculateGearFromPosition(x);
        if (newGear != mCurrentGear) {
            setCurrentGear(newGear, true);
        }
    }

    private void snapToNearestGear(float x) {
        int newGear = calculateGearFromPosition(x);
        setCurrentGear(newGear, true);
    }

    private int calculateGearFromPosition(float x) {
        float gearAreaStart = mTrackRect.left + mSideMargin;
        float gearAreaEnd = mTrackRect.right - mSideMargin;
        float gearAreaWidth = gearAreaEnd - gearAreaStart;
        float segmentWidth = gearAreaWidth / (mMaxGear - 1);

        // 限制x在档位区域内
        x = Math.max(gearAreaStart, Math.min(x, gearAreaEnd));

        float position = (x - gearAreaStart) / segmentWidth;
        int gear = Math.round(position) + 1;

        return Math.max(1, Math.min(gear, mMaxGear));
    }

    public void setCurrentGear(int gear) {
        setCurrentGear(gear, false);
    }

    public void setCurrentGear(int gear, boolean notify) {
        if (gear < 1 || gear > mMaxGear) {
            return;
        }

        int oldGear = mCurrentGear;
        mCurrentGear = gear;

        if (oldGear != mCurrentGear) {
            invalidate();

            if (notify && mOnGearChangeListener != null && mEnabled) {
                mOnGearChangeListener.onGearChanged(mCurrentGear);
            }
        }
    }

    public int getCurrentGear() {
        return mCurrentGear;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mEnabled != enabled) {
            mEnabled = enabled;
            // 清理灰色图片缓存
            if (mGrayThumbBitmap != null && !mGrayThumbBitmap.isRecycled()) {
                mGrayThumbBitmap.recycle();
                mGrayThumbBitmap = null;
            }
            invalidate();
        }
    }

    @Override
    public boolean isEnabled() {
        return mEnabled;
    }

    public void setOnGearChangeListener(OnGearChangeListener listener) {
        mOnGearChangeListener = listener;
    }

    /**
     * 设置滑块图片
     * @param bitmap 滑块图片
     */
    public void setThumbBitmap(Bitmap bitmap) {
        this.mThumbBitmap = bitmap;
        if (bitmap != null) {
            // 根据图片大小自动调整滑块尺寸
            this.mThumbWidth = bitmap.getWidth();
            this.mThumbHeight = bitmap.getHeight();
            // 更新滑块半径，用于测量
            this.mThumbRadius = Math.max(mThumbWidth, mThumbHeight) / 2;
        }
        // 清理灰色图片缓存
        if (mGrayThumbBitmap != null && !mGrayThumbBitmap.isRecycled()) {
            mGrayThumbBitmap.recycle();
            mGrayThumbBitmap = null;
        }
        invalidate();
    }

    /**
     * 从资源ID设置滑块图片
     * @param context Context
     * @param resId 图片资源ID
     */
    public void setThumbResource(Context context, int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        setThumbBitmap(bitmap);
    }

    /**
     * 设置滑块图片大小
     * @param width 宽度（像素）
     * @param height 高度（像素）
     */
    public void setThumbSize(int width, int height) {
        this.mThumbWidth = width;
        this.mThumbHeight = height;
        this.mThumbRadius = Math.max(width, height) / 2;
        invalidate();
    }

    /**
     * 设置滑块图片大小（dp单位）
     * @param widthDp 宽度（dp）
     * @param heightDp 高度（dp）
     */
    public void setThumbSizeDp(float widthDp, float heightDp) {
        setThumbSize(dp2px(widthDp), dp2px(heightDp));
    }

    /**
     * 设置禁用状态的颜色
     * @param color 颜色值
     */
    public void setDisabledColor(int color) {
        mDisabledColor = color;
        mDisabledGradient = null;
        if (!mEnabled) {
            invalidate();
        }
    }

    /**
     * 设置禁用状态的颜色
     * @param colorStr 颜色字符串，如 "#FFF3F4F5"
     */
    public void setDisabledColor(String colorStr) {
        try {
            setDisabledColor(Color.parseColor(colorStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int dp2px(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 清理资源
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 清理灰色图片缓存
        if (mGrayThumbBitmap != null && !mGrayThumbBitmap.isRecycled()) {
            mGrayThumbBitmap.recycle();
            mGrayThumbBitmap = null;
        }
    }
}