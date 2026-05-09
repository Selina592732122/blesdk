package com.shenghao.widget;// CustomDotIndicator.java

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shenghao.R;
import com.shenghao.utils.DensityUtil;
import com.shenghao.utils.LogUtils;


public class CustomDotIndicator extends HorizontalScrollView {
    private int totalItems;
    private ImageView[] dots;
    private LinearLayout linearLayout;
    private int mSelectIndex;

    public CustomDotIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public CustomDotIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomDotIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = inflate(context, R.layout.custom_dot_indicator, this);
        linearLayout = view.findViewById(R.id.dot_container);
        setHorizontalScrollBarEnabled(false);
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
        createDots();
    }

    private void createDots() {
        linearLayout.removeAllViews();
        dots = new ImageView[totalItems];
        for (int i = 0; i < totalItems; i++) {
            dots[i] = new ImageView(getContext());
//            dots[i].setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            dots[i].setImageResource(R.drawable.dot_inactive); // 设置默认的圆点图片
            dots[i].setPadding(8, 0, 8, 0); // 设置圆点的间距
            linearLayout.addView(dots[i]);
        }
        linearLayout.requestLayout();
        requestLayout();
        if (totalItems > 0) {
            dots[0].setImageResource(R.drawable.dot_active); // 设置第一个圆点为激活状态
        }
    }

    public boolean isItemVisible(HorizontalScrollView horizontalScrollView, View item) {
        int scrollX = horizontalScrollView.getScrollX();
        int scrollViewWidth = horizontalScrollView.getWidth() - DensityUtil.dip2px(getContext(),16);//扣除左右8dp

        int itemLeft = item.getLeft();
        int itemRight = item.getRight();

        return itemLeft >= scrollX && itemRight <= (scrollX + scrollViewWidth);
    }

    public void updateCurrentItem(int currentItem) {
        for (int i = 0; i < totalItems; i++) {
            if (i == currentItem) {
                dots[i].setImageResource(R.drawable.dot_active); // 设置当前圆点为激活状态
            } else {
                dots[i].setImageResource(R.drawable.dot_inactive); // 设置其他圆点为非激活状态
            }
        }
        if(linearLayout.getWidth() > getWidth()){
                    // 获取选中项的宽度和高度
            View selectedItem = linearLayout.getChildAt(currentItem);
            boolean itemVisible = isItemVisible(this, selectedItem);
            if(itemVisible)
                return;
            int itemWidth = selectedItem.getWidth();
            int itemHeight = selectedItem.getHeight();

            // 计算选中项在父视图中的左边距
            int leftMargin = selectedItem.getLeft();
            // 计算 HorizontalScrollView 的宽度
            int scrollViewWidth = getWidth();
            int scrollX = leftMargin;
            scrollTo(leftMargin,0);
            LogUtils.e("updateCurrentItem",scrollViewWidth+","+linearLayout.getWidth()+",leftMargin ="+leftMargin+",scrollX"+scrollX+","+currentItem);

//            // 计算滚动的目标位置，使选中项居中
//            int scrollX = leftMargin + itemWidth / 2 - scrollViewWidth / 2;
//            // 判断是否需要滚动
//            if (scrollX < 0) {
//                scrollX = 0; // 确保不超出左边界
//            } else if (scrollX > linearLayout.getWidth() - scrollViewWidth) {
//                scrollX = linearLayout.getWidth() - scrollViewWidth; // 确保不超出右边界
//            }
//            // 滚动到目标位置
//            scrollTo(scrollX, 0); // 或者使用 smoothScrollTo(scrollX, 0) 以平滑滚动
        }
    }
}
