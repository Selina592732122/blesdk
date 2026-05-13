package com.shenghao.blesdkdemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shenghao.blesdkdemo.R;

public class PasswordEditText extends RelativeLayout {

    private boolean isVisible = false;//密码不可见
    private ImageView ivEye;
    private EditText mPassword;

    public PasswordEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.password_edittext, this);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.PasswordEditText);

        String hint = a.getString(R.styleable.PasswordEditText_hint);
        int drawable = a.getResourceId(R.styleable.PasswordEditText_background,R.drawable.bg_gray_edittext_radius_shape);
        int textColor = a.getColor(R.styleable.PasswordEditText_textColor, Color.parseColor("#FF181818"));
        int textColorHint = a.getColor(R.styleable.PasswordEditText_textColorHint, Color.parseColor("#66000000"));
        ivEye = view.findViewById(R.id.iv);
        mPassword = view.findViewById(R.id.et);
        ivEye.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode();
            }
        });
        mPassword.setHint(hint);
        mPassword.setTextColor(textColor);
        mPassword.setHintTextColor(textColorHint);
        mPassword.setBackgroundResource(drawable);
    }

    private void changeMode(){
        //如果不可见
        if (!isVisible) {
            ivEye.setImageResource(R.drawable.ic_eye_unlook);//设置隐藏图标
            mPassword.setInputType(InputType.
                    TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            //修改inputtype为可见

            //将光标移动至最后
            int textLength1 = mPassword.getText().length();
            mPassword.setSelection(textLength1, textLength1);

            //将是否可见设置为是
            isVisible = true;

            //如果可见
        } else {
            ivEye.setImageResource(R.drawable.ic_eye_look);//设置显示图标
            mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
                    InputType.TYPE_CLASS_TEXT);
            //修改inputtype为不可见

            //将光标移动至最后
            int textLength2 = mPassword.getText().length();
            mPassword.setSelection(textLength2, textLength2);
            isVisible = false;
        }
    }

    public String getText(){
        return mPassword.getText().toString();
    }

    public void setTextChangedListener(TextWatcher watcher){
        mPassword.addTextChangedListener(watcher);
    }
}
