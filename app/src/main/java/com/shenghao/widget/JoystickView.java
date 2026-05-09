package com.shenghao.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.shenghao.R;
import com.shenghao.utils.DensityUtil;


public class JoystickView extends View {
    private static final String TAG = "JoystickView";
    private int directionType = DIRECTION_HORIZONTAL; // 默认水平方向
    public static final int DIRECTION_HORIZONTAL = 0;
    public static final int DIRECTION_VERTICAL = 1;
    private static final int LEVEL_COUNT = 3; // 四个档位
    // 新增变量：跟踪当前活动的手指ID
    private int activePointerId = MotionEvent.INVALID_POINTER_ID;
    // 画笔
    private Paint innerCirclePaint;
    private Paint[] levelPaints = new Paint[LEVEL_COUNT];
    private Paint[] outerCirclePaints = new Paint[LEVEL_COUNT];
    private Paint[] outerCircleStrokePaints = new Paint[LEVEL_COUNT];
    private int[] outerCircleColors = new int[]{
            Color.argb(63, 135, 164, 206),
            Color.argb(63, 135, 164, 206),
            Color.argb(63, 135, 164, 206),
    };
    private int[] levelCircleColors = new int[]{
            Color.argb(255, 192, 239, 255),
            Color.argb(255,255, 226, 163),
            Color.argb(255, 255, 149, 149),
    };
    // 圆圈的半径
    private float outerCircleRadius;
    private float innerCircleRadius;
    private float innerCircleRadiusPressed; // 按下时的内圈半径
    private float[] levelRadii = new float[LEVEL_COUNT];

    // 中心坐标
    private float centerX;
    private float centerY;

    // 内圆当前坐标
    private float innerCircleX;
    private float innerCircleY;

    // 是否正在触摸
    private boolean isTouching = false;
    // 当前激活的档位（0表示无，1-4表示四个档位）
    private int activeLevel = 0;

    // 手柄图片相关
    private Bitmap joystickBitmap;
    private RectF joystickRect = new RectF();
    private float joystickWidth;
    private float joystickHeight;
    private float pressedScaleFactor = 1.0f; // 按下时的放大系数
    private Paint levelFillPaint;
    private int direction;//前进后退

    public JoystickView(Context context) {
        super(context);
        setLayerType(LAYER_TYPE_SOFTWARE, null); // 确保在这里设置
        init(context, null);
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null); // 确保在这里设置
        init(context, attrs);
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null); // 确保在这里设置
        init(context, attrs);
    }
    private void init(Context context, AttributeSet attrs) {
        // 加载图片资源
        joystickBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.joystick_thumb); // 替换为你的图片资源

        // 内圈(手柄)画笔设置 - 不再需要，因为我们将使用图片
        innerCirclePaint = new Paint();
        innerCirclePaint.setAntiAlias(true);

        // 初始化档位画笔（渐变色）
        for (int i = 0; i < LEVEL_COUNT; i++) {
            levelPaints[i] = new Paint();
            levelPaints[i].setColor(levelCircleColors[i]); // 透明度递减
            levelPaints[i].setStyle(Paint.Style.STROKE);
            levelPaints[i].setStrokeWidth(DensityUtil.dip2px(context,1));
            levelPaints[i].setAntiAlias(true);
        }
        levelFillPaint = new Paint();
        levelFillPaint.setColor(Color.parseColor("#7305182B"));
        levelFillPaint.setStyle(Paint.Style.FILL);
        levelFillPaint.setAntiAlias(true);

        //初始化四个圆形底图
        for (int i = 0; i < LEVEL_COUNT; i++) {
            outerCirclePaints[i] = new Paint();
            outerCirclePaints[i].setColor(outerCircleColors[i]);
            outerCirclePaints[i].setStyle(Paint.Style.FILL);
        }
        for (int i = 0; i < LEVEL_COUNT; i++) {
            outerCircleStrokePaints[i] = new Paint();
            outerCircleStrokePaints[i].setColor(Color.BLACK);
            outerCircleStrokePaints[i].setStyle(Paint.Style.STROKE);
            outerCircleStrokePaints[i].setStrokeWidth(DensityUtil.dip2px(context,0.5f));
        }


        // 添加方向类型属性获取（可选）
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.JoystickView);
            directionType = a.getInt(R.styleable.JoystickView_directionType, DIRECTION_HORIZONTAL);
            a.recycle();
        }
//        createBreathingAnimation();
    }
    // 修改您的modifyAlpha方法
    private int modifyAlpha(int color, float alphaFactor) {
        int originalAlpha = Color.alpha(color);
        int newAlpha = (int)(originalAlpha * alphaFactor);

        // 确保透明度足够低（重要！）
        if(newAlpha < 30) newAlpha = 30; // 最小透明度阈值

        return Color.argb(
                newAlpha,
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 计算中心点
        centerX = w / 2f;
        centerY = h / 2f;

        // 初始化内圆位置
        innerCircleX = centerX;
        innerCircleY = centerY;

        // 设置半径
        outerCircleRadius = Math.min(w, h) / 2f * 0.78f; // 外圈半径为视图大小的80%
        innerCircleRadius = outerCircleRadius * 0.264f; // 默认内圈半径
        innerCircleRadiusPressed = innerCircleRadius * pressedScaleFactor; // 按下时放大

        // 计算手柄图片的尺寸
        if (joystickBitmap != null) {
            // 保持图片宽高比
            float scale = (innerCircleRadius * 2) / Math.max(joystickBitmap.getWidth(), joystickBitmap.getHeight());
            joystickWidth = joystickBitmap.getWidth() * scale;
            joystickHeight = joystickBitmap.getHeight() * scale;
        }

        // 设置各档位半径
//        for (int i = 0; i < LEVEL_COUNT; i++) {
//            levelRadii[i] = outerCircleRadius * (i + 1) / LEVEL_COUNT;
//        }
        levelRadii[0] = outerCircleRadius * 0.42f;
        levelRadii[1] = outerCircleRadius * 0.7f;
        levelRadii[2] = outerCircleRadius;
//        createLayoutBitmap();
    }

//    private void createLayoutBitmap() {
//        // 初始化布局（示例）
//        View layout = LayoutInflater.from(getContext())
//                .inflate(R.layout.circle_blue, null);
//
//        int size = 200; // 直径
//        layout.measure(
//                View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY),
//                View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)
//        );
//        layout.layout(0, 0, size, size);
//
//        blueBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
//        Canvas tempCanvas = new Canvas(blueBitmap);
//        layout.draw(tempCanvas);
//
//        if (blueBitmap != null) {
//            // 保持图片宽高比
//            float scale = (levelRadii[0] * 2) / Math.max(blueBitmap.getWidth(), blueBitmap.getHeight());
//            blueBitmapWidth = blueBitmap.getWidth() * scale;
//            blueBitmapHeight = blueBitmap.getHeight() * scale;
//        }
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制各档位分界线
        for (int i = 0; i < LEVEL_COUNT; i++) {
            if(i == LEVEL_COUNT - 1)
                canvas.drawCircle(centerX, centerY, levelRadii[i], outerCirclePaints[i]);
            canvas.drawCircle(centerX, centerY, levelRadii[i], outerCircleStrokePaints[i]);//绘制黑色边框
        }

        // 绘制档位背景（有激活的档位才绘制）
//        for (int i = activeLevel - 1; i >= 0; i--) {
//            canvas.drawCircle(centerX, centerY, levelRadii[i], levelPaints[i]);
//        }
        for (int i = 0; i < activeLevel; i++) {
            if(i == activeLevel - 1) {//绘制最后一个
                canvas.drawCircle(centerX,centerY,levelRadii[i],levelFillPaint);//绘制实心圆
                //绘制阴影
                Paint shadowPaint = new Paint();
                shadowPaint.setAntiAlias(true);
                shadowPaint.setColor(Color.TRANSPARENT); // 设置圆环颜色为透明
                shadowPaint.setStyle(Paint.Style.STROKE);
                shadowPaint.setStrokeWidth(DensityUtil.dip2px(getContext(),15));
                shadowPaint.setShadowLayer(40f, 0f, 0f, modifyAlpha(levelCircleColors[i], 0.5f)); // 阴影颜色为baseColor
                canvas.drawCircle(centerX, centerY, levelRadii[i], shadowPaint);

                canvas.drawCircle(centerX, centerY, levelRadii[i], levelPaints[i]);//绘制光圈

                // 计算反向关系：光环变亮时阴影范围变小
//                float shadowRadius = mMaxShadowRadius - (mCurrentIntensity * (mMaxShadowRadius - mMinShadowRadius));
//                // 计算光环透明度：光环变亮时透明度高
//                int glowAlpha = (int) (155 + 100 * mCurrentIntensity); // 100-255范围
//                // 绘制光晕阴影（变暗时阴影范围更大）
//                shadowPaint.setShadowLayer(shadowRadius, 0, 0, modifyAlpha(levelCircleColors[i], 0.4f));
//                canvas.drawCircle(centerX, centerY, levelRadii[i], shadowPaint);
//                // 绘制主光环（变亮时更不透明）
//                levelPaints[i].setAlpha(glowAlpha);
//                canvas.drawCircle(centerX, centerY, levelRadii[i], levelPaints[i]);
            }
        }

        // 绘制手柄图片 - 根据是否按下调整大小
        if (joystickBitmap != null) {
            float currentWidth = isTouching ? joystickWidth * pressedScaleFactor : joystickWidth;
            float currentHeight = isTouching ? joystickHeight * pressedScaleFactor : joystickHeight;

            joystickRect.left = innerCircleX - currentWidth / 2;
            joystickRect.top = innerCircleY - currentHeight / 2;
            joystickRect.right = innerCircleX + currentWidth / 2;
            joystickRect.bottom = innerCircleY + currentHeight / 2;

            canvas.drawBitmap(joystickBitmap, null, joystickRect, innerCirclePaint);
        }
    }

    private float mMaxShadowRadius = 50f;
    private float mMinShadowRadius = 30f;
    private float mCurrentIntensity = 0f; // 0-1 表示呼吸强度
    private void createBreathingAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(animation -> {
            // 使用正弦曲线实现更自然的呼吸节奏
            float rawValue = (float) animation.getAnimatedValue();
            mCurrentIntensity = (float) (0.5f * (1 + Math.sin(Math.PI * rawValue - Math.PI/2)));
            invalidate();
        });
        animator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int pointerIndex;
        int pointerId;
        float x, y;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                // 获取当前触发事件的手指索引
                pointerIndex = event.getActionIndex();
                x = event.getX(pointerIndex);
                y = event.getY(pointerIndex);

                // 如果没有活动手指且触摸点在摇杆区域内
                if (activePointerId == MotionEvent.INVALID_POINTER_ID &&
                        isInJoystickArea(x, y)) {

                    // 记录这个手指ID
                    activePointerId = event.getPointerId(pointerIndex);
                    isTouching = true;
                    updateInnerCirclePosition(x, y);
                    invalidate();
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                // 只处理活动手指的移动
                if (isTouching && activePointerId != MotionEvent.INVALID_POINTER_ID) {
                    // 查找活动手指在事件中的索引
                    pointerIndex = event.findPointerIndex(activePointerId);
                    if (pointerIndex != -1) {
                        x = event.getX(pointerIndex);
                        y = event.getY(pointerIndex);
                        updateInnerCirclePosition(x, y);
                        invalidate();
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                // 获取触发抬起事件的手指ID
                pointerIndex = event.getActionIndex();
                pointerId = event.getPointerId(pointerIndex);

                // 如果抬起的是活动手指
                if (pointerId == activePointerId) {
                    isTouching = false;
                    resetInnerCirclePosition();
                    activePointerId = MotionEvent.INVALID_POINTER_ID;
                    invalidate();
                    return true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                // 取消时重置所有状态
                if (isTouching) {
                    isTouching = false;
                    resetInnerCirclePosition();
                    activePointerId = MotionEvent.INVALID_POINTER_ID;
                    invalidate();
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    // 更新内圈位置（限制在外圈内）
    private void updateInnerCirclePosition(float touchX, float touchY) {
        // 计算手指与中心点的偏移量
        float dx = touchX - centerX;
        float dy = touchY - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // 如果超出外圈，则限制在边界上
        if (distance > outerCircleRadius) {
            float ratio = outerCircleRadius / distance;
            dx *= ratio;
            dy *= ratio;
            distance = outerCircleRadius;
        }

        // 更新红点位置
        innerCircleX = centerX + dx;
        innerCircleY = centerY + dy;

        // 计算当前档位
        int newLevel = 0;
        for (int i = 0; i < LEVEL_COUNT; i++) {
            if (distance <= levelRadii[i]) {
                newLevel = i + 1;
                break;
            }
        }

        // 档位变化时重绘背景
        if (newLevel != activeLevel) {
            activeLevel = newLevel;
            invalidate();
        }

        // 触发回调
        if (listener != null) {
            float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
            if (angle < 0) angle += 360;
            float normalizedDistance = distance / outerCircleRadius;
            float xPercent = dx / outerCircleRadius;
            float yPercent = dy / outerCircleRadius;
            // 添加详细日志
            Log.d(TAG, "摇杆移动 - " +
                    "X偏移: " + String.format("%.2f", xPercent) + " (" + (int)(xPercent * 100) + "%), " +
                    "Y偏移: " + String.format("%.2f", yPercent) + " (" + (int)(yPercent * 100) + "%), " +
                    "距离: " + String.format("%.2f", normalizedDistance) + ", " +
                    "角度: " + String.format("%.1f", angle) + "°, " +
                    "档位: " + activeLevel);
            if(directionType == DIRECTION_VERTICAL){
                //前进，后退
                if(angle > 180 && angle < 360){
                    Log.d(TAG,"方向:前进");
                    direction = 1;
                }else {
                    Log.d(TAG,"方向:后退");
                    direction = 2;
                }
            } else if (directionType == DIRECTION_HORIZONTAL) {
                if(angle > 90 && angle < 270){
                    Log.d(TAG,"方向:向左,角度"+(angle - 270));
                    angle = angle - 270;
                }else {
                    if(angle > 270){
                        Log.d(TAG,"方向:向右,角度"+(angle - 270));
                        angle = angle - 270;
                    } else{
                        Log.d(TAG,"方向:向右,角度"+(angle + 90));
                        angle = angle + 90;
                    }
                }
            }
//            listener.onJoystickMoved(directionType,xPercent, yPercent, normalizedDistance, angle, activeLevel);
//            if(activeLevel == 3)
            if(listener != null)
                if(activeLevel >= 2)
                    listener.onJoystickMoved(directionType,direction,angle, activeLevel);
        }
    }

    // 检查触摸点是否在手柄区域内
    private boolean isInJoystickArea(float x, float y) {
        float halfWidth = joystickWidth / 2 * 1.5f; // 扩大点击区域
        float halfHeight = joystickHeight / 2 * 1.5f;

        return x >= innerCircleX - halfWidth &&
                x <= innerCircleX + halfWidth &&
                y >= innerCircleY - halfHeight &&
                y <= innerCircleY + halfHeight;
    }

    // 重置内圈位置到中心（直接复位，无动画）
    private void resetInnerCirclePosition() {
        innerCircleX = centerX;
        innerCircleY = centerY;
        activeLevel = 0;
        invalidate();

        // 触发回调
        if (listener != null) {
            listener.onJoystickReleased(directionType);
        }
    }

    // 回调接口
    public interface OnJoystickMoveListener {
        void onJoystickMoved(int directionType,int direction,float angle, int level);

        void onJoystickReleased(int directionType);
    }

    private OnJoystickMoveListener listener;

    public void setOnJoystickMoveListener(OnJoystickMoveListener listener) {
        this.listener = listener;
    }

    // 释放资源
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (joystickBitmap != null && !joystickBitmap.isRecycled()) {
            joystickBitmap.recycle();
            joystickBitmap = null;
        }
    }
}