package com.shenghao.blesdkdemo.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.utils.DensityUtil;
import com.shenghao.blesdkdemo.utils.ScreenUtils;
import com.shenghao.blesdkdemo.utils.SizeUtils;


public class IosBottomSheetDialog extends Dialog {

    private Context mContext;
    private View container;
    private boolean cancelOnTouchOutside;

    public IosBottomSheetDialog(@NonNull Builder builder, int themeResId) {
        super(builder.context, themeResId);
        mContext = builder.context;
        container = builder.dialogContentView;
        cancelOnTouchOutside = builder.cancelOnTouchOutside;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(container);
        setCanceledOnTouchOutside(cancelOnTouchOutside);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = ScreenUtils.getScreenWidth(getContext()) - 2* DensityUtil.dip2px(getContext(),16);
        window.setAttributes(layoutParams);
    }

    public interface OnItemClickListener {

        void onClick(View view);
    }

    public static class Builder {

        private Context context;
        private View dialogContentView;
        private LinearLayout container;
        private TextView cancelTv;
        private boolean cancelOnTouchOutside = true;
        private int itemCount = 0;
        IosBottomSheetDialog dialog;

        public Builder(Context context) {
            this.context = context;
            dialogContentView = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_sheet, null);
            container = dialogContentView.findViewById(R.id.sheet_list_container);
            cancelTv = dialogContentView.findViewById(R.id.bottomSheetCancelTv);
//            LinearLayout layout = new LinearLayout(context);
//            layout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT));
//            layout.setOrientation(LinearLayout.VERTICAL);
//            layout.setBackgroundResource(R.drawable.shape_bg_bottom_sheet_dialog);
//            container = layout;

            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
        }

        public Builder cancelTouchOnOutside(boolean enable) {
            cancelOnTouchOutside = enable;
            return this;
        }

        public Builder addTitleView(String content, int textColor) {
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    SizeUtils.dp2px(context, 42));
            TextView itemView = createTextView(content, textColor, null, true);
            itemView.setTextSize(14);
            itemView.setTextColor(Color.parseColor("#999999"));
            if (itemCount > 0) {
                params.setMargins(0, SizeUtils.dp2px(context, 0.5f), 0, 0);
            }
            itemView.setBackgroundColor(Color.WHITE);
            itemView.setLayoutParams(params);
            container.addView(itemView);
            itemCount++;
            return this;

//            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    SizeUtils.dp2px(context, 42));
//            TextView itemView = createTextView(content, textColor, null, true);
//            if (itemCount > 0) {
//                params.setMargins(0, SizeUtils.dp2px(context, 1), 0, 0);
//            }
//            itemView.setLayoutParams(params);
//            container.addView(itemView);
//            itemCount++;
//            return this;
        }

        public Builder addItemView(String content, int textColor, boolean topRadius,
                                   OnItemClickListener onItemClickListener) {
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    SizeUtils.dp2px(context, 54));
            if (itemCount > 0) {
                params.setMargins(0, SizeUtils.dp2px(context, 0.5f), 0, 0);
            }
            TextView itemView = createTextView(content, textColor, onItemClickListener, topRadius);
            itemView.setBackgroundColor(Color.WHITE);
            itemView.setLayoutParams(params);
            container.addView(itemView);
            itemCount++;
            return this;
        }

        private TextView createTextView(String text, int textColor, OnItemClickListener listener,
                                        boolean topRadius) {
            TextView textView = new TextView(context);
//            if (topRadius) {
//                textView.setBackgroundResource(R.drawable.shape_bg_bottom_sheet_dialog);
//            } else {
//                textView.setBackgroundColor(Color.parseColor("#FFFFFF"));
//            }
            textView.setGravity(Gravity.CENTER);
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
            textView.setTextSize(16);
            if (textColor == 0) {
                textView.setTextColor(Color.parseColor("#333333"));
            } else {
                textView.setTextColor(textColor);
            }
            textView.setTag(text);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onClick(view);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                }
            });
            return textView;
        }

        public IosBottomSheetDialog build() {
            if (dialog == null) {
                dialog = new IosBottomSheetDialog(this, R.style.IosBottomSheetDialog);
            }
            return dialog;
        }

    }
}
