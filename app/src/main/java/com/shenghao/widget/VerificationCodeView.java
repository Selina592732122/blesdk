package com.shenghao.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shenghao.R;

import java.util.ArrayList;
import java.util.List;

public class VerificationCodeView extends RelativeLayout {

    private EditText editText;

    private List textViewList = new ArrayList<>();

    private StringBuffer stringBuffer = new StringBuffer();
    private OnCompleteListener mOnCompleteListener;

    public VerificationCodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerificationCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerificationCodeView);
        int codeSize = a.getInt(R.styleable.VerificationCodeView_code_num, 6);
//添加布局内容
        View.inflate(context, R.layout.view_verification_code, this);
        editText = findViewById(R.id.editCode);
        editText.requestFocus();
        LinearLayout llCode = findViewById(R.id.llCode);
        // 创建LayoutParams，设置weight为1
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // 宽度
                LinearLayout.LayoutParams.WRAP_CONTENT, // 高度
                1.0f // weight
        );
        for (int i = 0; i < codeSize; i++) {
            View view = View.inflate(context, R.layout.code_layout, null);
            llCode.addView(view,params);
            textViewList.add(view.findViewById(R.id.txtCode));
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
//如果有字符输入时才进行操作
                if (!s.toString().equals("")) {
//我们限制了4个验证码
                    if (stringBuffer.length() > textViewList.size()-1) {
                        editText.setText("");
                        return;
                    } else {
                        stringBuffer.append(s);
//因为editText是辅助的，根本字符串是stringBuffer，所以将EditText置空
                        editText.setText("");
//现在很多App都是输入完毕后自动进入下一步逻辑，所以咱们一般都是在这监听，完成后进行回调业务即可
                        if (stringBuffer.length() == textViewList.size()) {
//验证码输入完毕了，自动进行验证逻辑
                            if(mOnCompleteListener !=null)
                                mOnCompleteListener.onFinish(stringBuffer.toString());
                        }
                    }
                    for (int i = 0; i < stringBuffer.length(); i++) {
                        ((TextView) textViewList.get(i)).setText(stringBuffer.charAt(i) + "");
                    }
                }
            }
        });

//设置删除按键的监听

        editText.setOnKeyListener(new OnKeyListener() {

            @Override

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (stringBuffer.length() > 0) {
//删除字符
                        stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());
//将TextView显示内容置空
                        TextView tv = (TextView) textViewList.get(stringBuffer.length());
                        tv.setText("");
                    }
                    return true;
                }
                return false;
            }
        });
    }

    //清空验证框
    public void clear(){
        editText.setText("");
        stringBuffer = new StringBuffer();
        for (int i = 0; i < textViewList.size(); i++) {
            TextView tv = (TextView) textViewList.get(i);
            tv.setText("");
        }
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener){
        this.mOnCompleteListener = onCompleteListener;
    }

    public interface OnCompleteListener{
        void onFinish(String code);
    }
}
