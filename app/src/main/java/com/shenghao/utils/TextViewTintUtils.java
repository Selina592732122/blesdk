package com.shenghao.utils;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

public class TextViewTintUtils {
    
    /**
     * 设置TextView右侧drawable的着色
     * @param textView 目标TextView
     * @param colorResId 颜色资源ID（如R.color.red）
     */
    public static void setDrawableRightTint(TextView textView, int colorResId) {
        // 获取所有drawables（left/top/right/bottom）
        Drawable[] drawables = textView.getCompoundDrawables();
        
        // 只处理右侧drawable（index=2）
        if (drawables[2] != null) {
            // 必须mutate()避免影响其他实例
            Drawable rightDrawable = drawables[2].mutate();
            
            // 设置颜色滤镜
            rightDrawable.setColorFilter(
                    colorResId,
                PorterDuff.Mode.SRC_IN
            );
            
            // 重新设置drawables（保持其他方向drawable不变）
            textView.setCompoundDrawables(
                drawables[0], // left
                drawables[1], // top
                rightDrawable, // 着色的right
                drawables[3]  // bottom
            );
        }
    }
}