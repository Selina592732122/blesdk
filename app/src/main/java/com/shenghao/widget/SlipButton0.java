package com.shenghao.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.shenghao.R;
import com.shenghao.utils.DensityUtil;
import com.shenghao.utils.LogUtils;

//改完但是没有动画
public class SlipButton0 extends View implements OnTouchListener{
	private Bitmap bg_on, bg_off, slip_btn;
	private float downX, nowX=0;// 按下时的x,当前的x
	private boolean onSlip = false;
	private OnChangedListener listener;
	private boolean nowStatus = false;
	private boolean lastStatus = false;//记录最后一次状态是开还是关，相同状态不用再回调
	private Bitmap bitmapBg;
	private GestureDetector gestureDetector;
	private float ax;//动画x点
	public SlipButton0(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public SlipButton0(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SlipButton0(Context context) {
		super(context);
		init(context);
	}

	@SuppressLint("ClickableViewAccessibility")
	private void init(Context context){
//		 bg_on = BitmapFactory.decodeResource(getResources(), R.drawable.ic_bg_power_on);
//	     bg_off = BitmapFactory.decodeResource(getResources(), R.drawable.ic_bg_power_off);

		bg_on = xmlToBitmap(context,R.layout.btn_on, DensityUtil.dip2px(context,210),DensityUtil.dip2px(context,54));
		bg_off = xmlToBitmap(context,R.layout.btn_off, DensityUtil.dip2px(context,210),DensityUtil.dip2px(context,54));
//		slip_btn = BitmapFactory.decodeResource(getResources(), R.drawable.ic_slip_btn);
		slip_btn = xmlToBitmap(context,R.layout.btn_slip, DensityUtil.dip2px(context,54),DensityUtil.dip2px(context,54));
		setOnTouchListener(this);
		gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// 处理点击事件
				if(nowStatus)
					setStatus(false);
				if(listener!=null){
					if(lastStatus != nowStatus){
						listener.OnChanged(SlipButton0.this, nowStatus);
						lastStatus = nowStatus;
					}
				}
				return true;
			}
		});
		// 创建动画
		ValueAnimator animatorX = ValueAnimator.ofFloat(0, getWidth());
		animatorX.setDuration(2000); // 动画持续时间
		animatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {


			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				ax = (float) animation.getAnimatedValue();
				invalidate(); // 触发重绘
			}
		});
		animatorX.start();
	}

	// 将XML布局文件转换为Bitmap
	public Bitmap xmlToBitmap(Context context, int layoutResId, int width, int height) {
		// 加载XML布局文件
		View view = LayoutInflater.from(context).inflate(layoutResId, null);
		view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

		// 绘制到Canvas
		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);

		return bitmap;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		float x = slip_btn.getWidth();
		if(onSlip){
			if(nowX>bg_on.getWidth() - slip_btn.getWidth()){
				x = bg_on.getWidth()-slip_btn.getWidth();//x为停止时候slip_btn左侧坐标
			} else{
				x = nowX - slip_btn.getWidth()/2;
				if(x<0) x=0;
			}
		}else{
			x = nowX;
		}

		if(nowX>=bg_on.getWidth()- slip_btn.getWidth() && !onSlip){
//			canvas.drawBitmap(bg_on, matrix, paint);//画出开启时候
			canvas.drawBitmap(bg_on,ax,0,paint);
			canvas.drawBitmap(slip_btn, bg_off.getWidth()/2.f - slip_btn.getWidth()/2f, 0, paint);//画出按钮
		}else {
			canvas.drawBitmap(bg_off, matrix, paint);//画出关闭时的背景
			// 设置画布的大小和 Bitmap 的绘制位置
			RectF rect = new RectF(0, 0, x+slip_btn.getWidth(), bg_on.getHeight());;
			// 如果需要设置Drawable背景，可以使用以下代码
			Drawable background = getResources().getDrawable(R.drawable.ic_btn_bg_black);
			background.setBounds((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
			background.draw(canvas);
			canvas.drawBitmap(slip_btn, x, 0, paint);//画出按钮
		}
//        canvas.drawBitmap(slip_btn, x, 0, paint);//画出按钮
	}

	private Bitmap createRoundedBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final RectF rectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final float roundPx = pixels;
		final float roundPxHalf = roundPx / 2;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);

		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(nowStatus){
			gestureDetector.onTouchEvent(event);
			return true;
		}
		switch(event.getAction()){
			case MotionEvent.ACTION_MOVE://滑动时
				if(onSlip){
					getParent().requestDisallowInterceptTouchEvent(true);
					nowX = event.getX();
				}
				break;
			case MotionEvent.ACTION_DOWN://按下
				if(event.getX() < slip_btn.getWidth()){
					getParent().requestDisallowInterceptTouchEvent(true);
					onSlip = true;
					downX = event.getX();
					nowX = downX;
				}else {
					onSlip = false;
					nowX = 0;
				}
				break;
			case MotionEvent.ACTION_UP://触摸抬起
				LogUtils.e("ACTION_UP",event.getX()+","+bg_on.getWidth());
				if(onSlip){
					if(event.getX()>= bg_on.getWidth()- slip_btn.getWidth()){//超出1/2
						nowStatus = true;
//                nowX = bg_on.getWidth() - slip_btn.getWidth();
					}else{
						nowStatus = false;
						nowX = 0;
					}
					if(listener!=null){
						if(lastStatus != nowStatus){
							listener.OnChanged(SlipButton0.this, nowStatus);
							lastStatus = nowStatus;
						}
					}
					onSlip = false;
				}
				break;
			default:
				return true;
		}
		invalidate();
		return true;
	}

	public void setOnChangedListener(OnChangedListener listener){
		this.listener = listener;
	}

	public void setStatus(boolean accOn) {
		nowStatus = accOn;
		nowX = nowStatus ? bg_on.getWidth():0;
		invalidate();
	}

//	@Override
//	public void onClick(View v) {
//		ToastUtils.showShort(getContext(),"onclick");
//		if(nowStatus)
//			setStatus(false);
//	}

	public interface OnChangedListener{
		public void OnChanged(SlipButton0 slipButton, boolean checkState);
	}
}