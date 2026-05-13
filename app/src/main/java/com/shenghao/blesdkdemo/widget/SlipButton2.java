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

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.utils.DensityUtil;
import com.shenghao.blesdkdemo.utils.LogUtils;
//第一次那个
public class SlipButton2 extends View implements OnTouchListener{
	private Bitmap bg_on, bg_off, slip_btn;
	private float downX, nowX=0;// 按下时的x,当前的x
	private boolean onSlip = false;
	private OnChangedListener listener;
	private boolean nowStatus = false;
	private boolean lastStatus = false;//记录最后一次状态是开还是关，相同状态不用再回调

	public SlipButton2(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public SlipButton2(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SlipButton2(Context context) {
		super(context);
		init(context);
	}
 
	@SuppressLint("ClickableViewAccessibility")
	private void init(Context context){
//		 bg_on = BitmapFactory.decodeResource(getResources(), R.drawable.ic_bg_power_on);
//	     bg_off = BitmapFactory.decodeResource(getResources(), R.drawable.ic_bg_power_off);

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
		case MotionEvent.ACTION_MOVE://滑动时
			getParent().requestDisallowInterceptTouchEvent(true);
			onSlip = true;
			nowX = event.getX();
			break;
		case MotionEvent.ACTION_DOWN://按下
			getParent().requestDisallowInterceptTouchEvent(true);
			onSlip = true;
			downX = event.getX();
			nowX = downX;
			break;
		case MotionEvent.ACTION_UP://触摸抬起
			onSlip = false;
			LogUtils.e("ACTION_UP",event.getX()+","+bg_on.getWidth());
			if(event.getX()>= bg_on.getWidth()- slip_btn.getWidth()){//超出1/2
				nowStatus = true;
//                nowX = bg_on.getWidth() - slip_btn.getWidth();
			}else{
				nowStatus = false;
                nowX = 0;
			}
			if(listener!=null){
				if(lastStatus != nowStatus){
					listener.OnChanged(SlipButton2.this, nowStatus);
					lastStatus = nowStatus;
				}
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

    public interface OnChangedListener{
		 public void OnChanged(SlipButton2 slipButton, boolean checkState);
	}
}