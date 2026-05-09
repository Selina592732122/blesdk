package com.shenghao.utils;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MultiTouchDelegate extends TouchDelegate {
        private List<ViewDelegateInfo> delegateInfos = new ArrayList<>();

        public MultiTouchDelegate(View delegateView) {
            super(new Rect(), delegateView);
        }

        public void addDelegate(Rect bounds, View delegateView) {
            delegateInfos.add(new ViewDelegateInfo(bounds, delegateView));
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            for (ViewDelegateInfo info : delegateInfos) {
                if (info.bounds.contains((int) x, (int) y)) {
                    // 转换为子视图坐标
                    event.setLocation(
                            x - info.delegateView.getLeft(),
                            y - info.delegateView.getTop()
                    );
                    return info.delegateView.dispatchTouchEvent(event);
                }
            }
            return false;
        }

        private static class ViewDelegateInfo {
            Rect bounds;
            View delegateView;

            ViewDelegateInfo(Rect bounds, View delegateView) {
                this.bounds = bounds;
                this.delegateView = delegateView;
            }
        }
    }