package com.shenghao.blesdkdemo.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.utils.DensityUtil;
import com.shenghao.blesdkdemo.utils.LogUtils;

public class SlipButton extends RelativeLayout {
	// 常量定义 - 使用旧版尺寸
	private static final float WIDTH_OFF = 42f; // 关闭状态宽度 dp (旧版高度)
	private static final float WIDTH_ON = 142f; // 开启状态宽度 dp (旧版宽度)
	private ImageView ivBg;
	private RelativeLayout rlBtn;
	private ImageView ivCenterIcon; // 中心图标
	private SlipButton.OnChangedListener listener;
	private boolean isSlip;//是否滑动
	private boolean nowStatus;
	private GestureDetector gestureDetector;
	private boolean lastStatus;
	// 添加操作锁
	private volatile boolean isOperating = false;
	private final Object statusLock = new Object();

	// 添加动画相关变量
	private ValueAnimator widthAnimator;
	private ObjectAnimator moveAnim;
	private RotateAnimation rotateAnimation;
	private Handler handler = new Handler();
	private ProgressBar progressBar;

	public SlipButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public SlipButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SlipButton(Context context) {
		super(context);
		init(context);
	}

	@SuppressLint("ClickableViewAccessibility")
	private void init(Context context){
		View.inflate(context, R.layout.layout_slip_btn, this);

		ivBg = findViewById(R.id.ivBg);
		rlBtn = findViewById(R.id.rlBtn);
		ivCenterIcon = findViewById(R.id.ivCenterIcon); // 获取中心图标

		// 添加 ProgressBar
		progressBar = findViewById(R.id.progressBar);

		gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				synchronized (statusLock) {
					if (isOperating) {
						LogUtils.e("SlipButton", "Operation in progress, ignore tap");
						return true;
					}
				}

				if(listener != null){
					listener.OnClick(SlipButton.this);
				}
				return true;
			}
		});
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	// 修改触摸事件处理
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 关键：当nowStatus为true时，直接交给gestureDetector处理点击事件
		if(nowStatus){
			gestureDetector.onTouchEvent(event);
			return true;
		}

		// 对于非开启状态的滑动处理
		switch(event.getAction()){
			case MotionEvent.ACTION_MOVE://滑动时
				synchronized (statusLock) {
					if (isOperating) {
						return true; // 正在操作中，忽略滑动
					}
				}
				if(isSlip){
					getParent().requestDisallowInterceptTouchEvent(true);
					LogUtils.e("ACTION_MOVE",event.getX()+"");
					float bgWidth = event.getX() + DensityUtil.dip2px(getContext(), WIDTH_OFF)/2.f;
					if(bgWidth < rlBtn.getWidth()){
						bgWidth = rlBtn.getWidth();
					}else if(bgWidth > DensityUtil.dip2px(getContext(), WIDTH_ON)){
						bgWidth = DensityUtil.dip2px(getContext(), WIDTH_ON);
					}
					//rlBtn跟随手走
					rlBtn.animate()
							.x(bgWidth-rlBtn.getWidth())
							.setDuration(0)
							.start();
					//背景拉长
					ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
					layoutParams.width = (int) bgWidth;
					ivBg.setLayoutParams(layoutParams);
				}
				break;
			case MotionEvent.ACTION_DOWN://按下
				LogUtils.e("ACTION_DOWN",event.getX()+"，"+rlBtn.getWidth());
				// 检查是否正在操作
				synchronized (statusLock) {
					if (isOperating) {
						// 标记为滑动无效，但允许事件继续传递
						isSlip = false;
						return true;
					}
				}
				if(event.getX() < rlBtn.getWidth()){
					isSlip = true;
				}
				break;
			// 修改 ACTION_UP 处理
			case MotionEvent.ACTION_UP://触摸抬起
				synchronized (statusLock) {
					if (isOperating) {
						return true;
					}
				}
				if(isSlip){
					isSlip = false;
					synchronized (statusLock) {
						isOperating = true;
					}
					if(event.getX() > DensityUtil.dip2px(getContext(), WIDTH_ON) - rlBtn.getWidth()){
						// 开启状态 - 需要转圈动画
						nowStatus = true;
						switchToOn();
					}else {
						// 关闭状态 - 直接缩回，不需要转圈
						nowStatus = false;
						switchToOffImmediately();
					}
					if(listener!=null){
						if(lastStatus != nowStatus){
							lastStatus = nowStatus;
							listener.OnChanged(SlipButton.this, nowStatus);
						}
					}
				}
				break;
			default:
				return true;
		}
		return true;
	}

	private void cancelAllAnimations() {
		if (widthAnimator != null && widthAnimator.isRunning()) {
			widthAnimator.cancel();
		}
		if (moveAnim != null && moveAnim.isRunning()) {
			moveAnim.cancel();
		}
		// 停止旋转动画
		if (rotateAnimation != null) {
			ivCenterIcon.clearAnimation();
		}
		// 移除延迟任务
		handler.removeCallbacksAndMessages(null);
	}

	private void resetToCorrectState() {
		// 根据当前状态重置到正确位置
		if (nowStatus) {
			// ON状态：背景全长，按钮在中间
			ViewGroup.LayoutParams params = ivBg.getLayoutParams();
			params.width = DensityUtil.dip2px(getContext(), WIDTH_ON);
			ivBg.setLayoutParams(params);
			rlBtn.setX(DensityUtil.dip2px(getContext(), WIDTH_ON) / 2f - rlBtn.getWidth() / 2f);
			// 保留旧版背景变色
			ivBg.setImageResource(R.drawable.ic_btn_bg_green);
		} else {
			// OFF状态：背景最短，按钮在最左边
			ViewGroup.LayoutParams params = ivBg.getLayoutParams();
			params.width = DensityUtil.dip2px(getContext(), WIDTH_OFF);
			ivBg.setLayoutParams(params);
			rlBtn.setX(0);
			// 保留旧版背景变色
			ivBg.setImageResource(R.drawable.ic_btn_bg_black);
		}
		rlBtn.setAlpha(1f);
	}

	private void switchToOn() {
		final int targetWidth = DensityUtil.dip2px(getContext(), WIDTH_ON);
		final float targetRightX = targetWidth - rlBtn.getWidth(); // 最右侧位置
		final float targetCenterX = targetWidth / 2f - rlBtn.getWidth() / 2f; // 居中位置

		// 取消可能正在进行的动画
		cancelAllAnimations();

		// 保留旧版背景变色
		ivBg.setImageResource(R.drawable.ic_btn_bg_green);

		// 第一阶段：背景拉长 + 按钮移动到最右侧
		ValueAnimator stage1WidthAnimator = ValueAnimator.ofInt(ivBg.getWidth(), targetWidth);
		stage1WidthAnimator.setDuration(300);
		stage1WidthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

		ObjectAnimator stage1MoveAnim = ObjectAnimator.ofFloat(rlBtn, "x", rlBtn.getX(), targetRightX);
		stage1MoveAnim.setDuration(300);
		stage1MoveAnim.setInterpolator(new AccelerateDecelerateInterpolator());

		AnimatorSet stage1Set = new AnimatorSet();
		stage1Set.playTogether(stage1WidthAnimator, stage1MoveAnim);

		// 第二阶段：按钮从最右侧移动到居中 + 显示加载动画
		ObjectAnimator stage2MoveAnim = ObjectAnimator.ofFloat(rlBtn, "x", targetRightX, targetCenterX);
		stage2MoveAnim.setDuration(200);
		stage2MoveAnim.setInterpolator(new AccelerateDecelerateInterpolator());

		stage1Set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				// 第一阶段完成后开始第二阶段
				ivCenterIcon.setVisibility(GONE);//先隐藏不然会有回去中间的效果
				stage2MoveAnim.start();

				// 第二阶段开始时显示加载动画
				stage2MoveAnim.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						showLoadingAnimation();

						// 5秒后显示电源图标
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								showPowerIcon();
								synchronized (statusLock) {
									isOperating = false;
								}
							}
						}, 500);
					}
				});
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				synchronized (statusLock) {
					isOperating = false;
					resetToCorrectState();
				}
			}
		});

		// 设置宽度动画监听器
		stage1WidthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int animatedValue = (Integer) animation.getAnimatedValue();
				ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
				layoutParams.width = animatedValue;
				ivBg.setLayoutParams(layoutParams);
			}
		});

		stage1Set.start();
	}

	// 添加立即关闭的方法（不需要转圈动画）
	private void switchToOffImmediately() {
		final int targetWidth = DensityUtil.dip2px(getContext(), WIDTH_OFF);
		final float targetX = 0f;

		cancelAllAnimations();

		// 保留旧版背景变色
		ivBg.setImageResource(R.drawable.ic_btn_bg_black);

		// 直接显示电源图标，不显示转圈
		showPowerIcon();

		// 创建宽度动画
		widthAnimator = ValueAnimator.ofInt(ivBg.getWidth(), targetWidth);
		widthAnimator.setDuration(300); // 可以设置更短的动画时间
		widthAnimator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
		widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int animatedValue = (Integer) animation.getAnimatedValue();
				ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
				layoutParams.width = animatedValue;
				ivBg.setLayoutParams(layoutParams);
			}
		});

		// 创建按钮移动动画
		moveAnim = ObjectAnimator.ofFloat(rlBtn, "x", rlBtn.getX(), targetX);
		moveAnim.setDuration(300);
		moveAnim.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());

		// 同时执行两个动画
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(widthAnimator, moveAnim);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				synchronized (statusLock) {
					isOperating = false;
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				synchronized (statusLock) {
					isOperating = false;
					resetToCorrectState();
				}
			}
		});
		animatorSet.start();
	}

	// 修改原有的 switchToOff 方法，重命名为 switchToOffWithLoading
	private void switchToOffWithLoading() {
		final int targetWidth = DensityUtil.dip2px(getContext(), WIDTH_OFF);
		final float targetX = 0f;

		cancelAllAnimations();

		// 保留旧版背景变色
		ivBg.setImageResource(R.drawable.ic_btn_bg_black);

		// 显示转圈动画
		showLoadingAnimation();

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				performCloseAnimation(targetWidth, targetX);
			}
		}, 500);
	}

	// 修改 setStatus 方法
	public void setStatus(boolean accOn) {
		LogUtils.e("ACTION_UP2", lastStatus + "," + nowStatus);

		synchronized (statusLock) {
			if (isOperating) {
				LogUtils.e("SlipButton", "Operation in progress, ignore setStatus");
				return;
			}

			if (accOn != nowStatus) {
				nowStatus = accOn;
				lastStatus = accOn;
				isOperating = true;

				if (accOn) {
					switchToOn(); // 开启时需要转圈
				} else {
					switchToOffWithLoading(); // 关闭时直接关闭
				}
			}
		}
	}

	// 添加立即设置状态的方法（供外部调用）
	public void setStatusImmediately(boolean accOn) {
		synchronized (statusLock) {
			if (isOperating) {
				LogUtils.e("SlipButton", "Operation in progress, ignore setStatus");
				return;
			}

			if (accOn != nowStatus) {
				nowStatus = accOn;
				lastStatus = accOn;
				isOperating = true;

				if (accOn) {
					switchToOnImmediately(); // 立即开启，不需要转圈
				} else {
					switchToOffImmediately(); // 立即关闭，不需要转圈
				}
			}
		}
	}

	// 添加立即开启的方法（不需要转圈动画）
	private void switchToOnImmediately() {
		final int targetWidth = DensityUtil.dip2px(getContext(), WIDTH_ON);
		final float targetX = targetWidth / 2f - rlBtn.getWidth() / 2f;

		cancelAllAnimations();

		// 保留旧版背景变色
		ivBg.setImageResource(R.drawable.ic_btn_bg_green);

		// 直接显示电源图标，不显示转圈
		showPowerIcon();

		// 创建宽度动画
		widthAnimator = ValueAnimator.ofInt(ivBg.getWidth(), targetWidth);
		widthAnimator.setDuration(300);
		widthAnimator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
		widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int animatedValue = (Integer) animation.getAnimatedValue();
				ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
				layoutParams.width = animatedValue;
				ivBg.setLayoutParams(layoutParams);
			}
		});

		// 创建按钮移动动画
		moveAnim = ObjectAnimator.ofFloat(rlBtn, "x", rlBtn.getX(), targetX);
		moveAnim.setDuration(300);
		moveAnim.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());

		// 同时执行两个动画
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(widthAnimator, moveAnim);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				synchronized (statusLock) {
					isOperating = false;
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				synchronized (statusLock) {
					isOperating = false;
					resetToCorrectState();
				}
			}
		});
		animatorSet.start();
	}

	private void performCloseAnimation(final int targetWidth, final float targetX) {
		// 创建宽度动画
		widthAnimator = ValueAnimator.ofInt(ivBg.getWidth(), targetWidth);
		widthAnimator.setDuration(500);
		widthAnimator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
		widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int animatedValue = (Integer) animation.getAnimatedValue();
				ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
				layoutParams.width = animatedValue;
				ivBg.setLayoutParams(layoutParams);

				float progress = (float)(animatedValue - ivBg.getWidth()) / (targetWidth - ivBg.getWidth());
				float currentTargetX = rlBtn.getX() * (1 - progress);
				rlBtn.setX(currentTargetX);
			}
		});

		// 创建按钮移动动画
		moveAnim = ObjectAnimator.ofFloat(rlBtn, "x", rlBtn.getX(), targetX);
		moveAnim.setDuration(500);
		moveAnim.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());

		// 同时执行两个动画
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(widthAnimator, moveAnim);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				showPowerIcon();
				synchronized (statusLock) {
					isOperating = false; // 标记操作完成
					ViewGroup.LayoutParams params = ivBg.getLayoutParams();
					params.width = targetWidth;
					ivBg.setLayoutParams(params);
					rlBtn.setX(targetX);
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				synchronized (statusLock) {
					isOperating = false; // 标记操作完成
					resetToCorrectState();
				}
			}
		});
		animatorSet.start();
	}

	// 修改显示加载动画的方法
	private void showLoadingAnimation() {
		// 隐藏中心图标，显示 ProgressBar
		ivCenterIcon.setVisibility(GONE);
		progressBar.setVisibility(VISIBLE);
	}

	// 修改显示电源图标的方法
	private void showPowerIcon() {
		// 隐藏 ProgressBar，显示中心图标
		progressBar.setVisibility(GONE);
		ivCenterIcon.setVisibility(VISIBLE);
	}

	public void setOnChangedListener(OnChangedListener listener){
		this.listener = listener;
	}

	public interface OnChangedListener{
		public void OnChanged(SlipButton slipButton, boolean checkState);
		void OnClick(SlipButton slipButton);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		// 清理资源
		cancelAllAnimations();
	}
}