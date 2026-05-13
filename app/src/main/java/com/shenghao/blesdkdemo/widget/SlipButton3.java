package com.shenghao.blesdkdemo.widget;
 
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.utils.DensityUtil;
import com.shenghao.blesdkdemo.utils.LogUtils;

public class SlipButton3 extends View implements OnTouchListener{
	private Bitmap bg_on, bg_off, slip_btn;
	private float downX, nowX=0;// 按下时的x,当前的x
	private boolean onSlip = false;
	private OnChangedListener listener;
	private boolean nowStatus = false;
	private boolean lastStatus = false;//记录最后一次状态是开还是关，相同状态不用再回调

	private ImageView ivBg,ivFront;
	private RelativeLayout rlBtn;
	public SlipButton3(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public SlipButton3(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SlipButton3(Context context) {
		super(context);
		init(context);
	}
 
	@SuppressLint("ClickableViewAccessibility")
	private void init(Context context){
//		View.inflate(context, R.layout.layout_slip_btn, this);
//
//		ivBg = findViewById(R.id.ivBg);
//		ivFront = findViewById(R.id.ivFront);
//		rlBtn = findViewById(R.id.rlBtn);

//		rlBtn.setOnTouchListener(new View.OnTouchListener() {
//			private float dX,dY;
//			@Override
//			public boolean onTouch(View view, MotionEvent event) {
//				switch (event.getAction()) {
//					case MotionEvent.ACTION_DOWN:
//						LogUtils.e("ACTION_DOWN",view.getX()+","+dX);
//						getParent().requestDisallowInterceptTouchEvent(true);
//						dX = view.getX() - event.getRawX();
//						dY = view.getY() - event.getRawY();
//						break;
//					case MotionEvent.ACTION_MOVE:
//						LogUtils.e("ACTION_MOVE",event.getX()+","+event.getRawX());
//						getParent().requestDisallowInterceptTouchEvent(true);
//						view.animate()
//								.x(event.getRawX() + dX)
////								.y(event.getRawY() + dY)
//								.setDuration(0)
//								.start();
//						layoutParams.width = (int) (event.getRawX() + dX + DensityUtil.dip2px(context,54));
//						ivBg.setLayoutParams(layoutParams);
//					case MotionEvent.ACTION_UP:
//						LogUtils.e("ACTION_UP",event.getX()+","+event.getRawX());
//						break;
//					default:
//						return false;
//				}
//				return true;
//			}
//		});

		bg_on = xmlToBitmap(context,R.layout.btn_on, DensityUtil.dip2px(context,210),DensityUtil.dip2px(context,54));
		 bg_off = xmlToBitmap(context,R.layout.btn_off, DensityUtil.dip2px(context,210),DensityUtil.dip2px(context,54));
	     slip_btn = BitmapFactory.decodeResource(getResources(), R.drawable.ic_slip_btn);
	     setOnTouchListener(this);
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

		if(nowX>=bg_on.getWidth()- slip_btn.getWidth()){
			canvas.drawBitmap(bg_on, matrix, paint);//画出关闭时的背景
		}else {
			canvas.drawBitmap(bg_off, matrix, paint);//画出关闭时的背景
			canvas.drawBitmap(slip_btn, x, 0, paint);//画出按钮
		}
//        canvas.drawBitmap(slip_btn, x, 0, paint);//画出按钮
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			getParent().requestDisallowInterceptTouchEvent(true);
			LogUtils.e("ACTION_DOWN",event.getX()+","+event.getRawX());
			break;
		case MotionEvent.ACTION_MOVE:
			LogUtils.e("ACTION_MOVE",event.getX()+","+event.getRawX());
//			getParent().requestDisallowInterceptTouchEvent(true);
//			rlBtn.animate()
//					.x(event.getX()-DensityUtil.dip2px(getContext(),27))
////								.y(event.getRawY() + dY)
//					.setDuration(0)
//					.start();
			//背景拉长
//			ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
//			layoutParams.width = (int) (event.getX()+ DensityUtil.dip2px(getContext(),27));
//			ivBg.setLayoutParams(layoutParams);
		case MotionEvent.ACTION_UP:
			LogUtils.e("ACTION_UP",event.getX()+","+event.getRawX());
			if(event.getX() > SlipButton3.this.getWidth() /2){
//				switchToOn();
			}else {
//				switchToOff();
			}
			break;
		default:
			return true;
		}
		invalidate();
		return true;
	}

	private void switchToOff() {
//		ToastUtils.showShort(getContext(),"off");
		ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
		layoutParams.width = DensityUtil.dip2px(getContext(),54);
		ivBg.setLayoutParams(layoutParams);
		rlBtn.setVisibility(View.VISIBLE);
		ivFront.setVisibility(View.GONE);
		nowStatus = false;
	}

	private void switchToOn() {
//		ToastUtils.showShort(getContext(),"on");
		ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
		layoutParams.width = DensityUtil.dip2px(getContext(),210);
		ivBg.setLayoutParams(layoutParams);
		rlBtn.setVisibility(View.GONE);
		ivFront.setVisibility(View.VISIBLE);
		nowStatus = true;
	}

	public void setOnChangedListener(OnChangedListener listener){  
        this.listener = listener;  
    }

	public void setStatus(boolean accOn) {
		nowStatus = accOn;
		nowX = nowStatus ? bg_on.getWidth():0;
		invalidate();
	}

    public interface OnChangedListener{
		 public void OnChanged(SlipButton3 slipButton, boolean checkState);
	}
}