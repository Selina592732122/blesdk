package com.shenghao.blesdkdemo.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shenghao.blesdkdemo.R;


public class ModeDialog extends Dialog implements View.OnClickListener {

    private TextView contentTxt;
    private EditText contentEt;
    private ViewGroup contentEtLayout;
    private TextView titleTxt;
    private TextView submitTxt;
    private TextView cancelTxt;

    private Context mContext;
    private CharSequence content;
    private OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;
    private String hint;
    private String editContent;
    private int editContentInputType;
    private int submitTxtColor;

    private boolean cancelable = true;

    private int contentVisible = View.GONE;
    private int contentEtVisible = View.GONE;
    private int selectIndex;

    public ModeDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public ModeDialog(Context context, int submitTxtColor, boolean cancelable,
                      OnCloseListener listener) {
        super(context, R.style.Common_Dialog);
        this.mContext = context;
        this.submitTxtColor = submitTxtColor;
        this.cancelable = cancelable;
        this.listener = listener;
    }

    public ModeDialog(Context context, OnCloseListener listener) {
        super(context, R.style.Common_Dialog);
        this.mContext = context;
        this.listener = listener;
    }

    protected ModeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public ModeDialog setDialogCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public ModeDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public ModeDialog setHint(String hint) {
        this.hint = hint;
        return this;
    }

    public ModeDialog setContent(CharSequence content) {
        this.content = content;
        return this;
    }

    public ModeDialog setEditContent(String editContent) {
        this.editContent = editContent;
        return this;
    }

    public ModeDialog setEditContentInputType(int editContentInputType) {
        this.editContentInputType = editContentInputType;
        return this;
    }

    public ModeDialog setContentVisibility(int visibility) {
        this.contentVisible = visibility;
        return this;
    }

    public ModeDialog setContentEtVisibility(int visibility) {
        this.contentEtVisible = visibility;
        return this;
    }

    public ModeDialog setPositiveButton(String name) {
        this.positiveName = name;
        return this;
    }

    public ModeDialog setPositiveButtonColor(int submitTxtColor) {
        this.submitTxtColor = submitTxtColor;
        return this;
    }

    public ModeDialog setNegativeButton(String name) {
        this.negativeName = name;
        return this;
    }

    public String getContentEtText() {
        if (this.contentEt != null) {
            return this.contentEt.getText().toString();
        }
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_mode);
        setCanceledOnTouchOutside(cancelable);
        setCancelable(cancelable);
        initView();
    }

    private void initView() {
        titleTxt = (TextView) findViewById(R.id.title);
        submitTxt = (TextView) findViewById(R.id.submitTv);
        submitTxt.setOnClickListener(this);
        cancelTxt = (TextView) findViewById(R.id.cancelTv);
        cancelTxt.setOnClickListener(this);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb1){
                    selectIndex = 0;
                }else if(checkedId == R.id.rb2){
                    selectIndex = 1;
                }else if(checkedId == R.id.rb3){
                    selectIndex = 2;
                }
            }
        });


        if (!TextUtils.isEmpty(title)) {
            titleTxt.setText(title);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancelTv) {
            if (listener != null) {
                listener.onClick(this, false,-1);
            }
            this.dismiss();
        } else if (v.getId() == R.id.submitTv) {
            if (listener != null) {
                listener.onClick(this, true,selectIndex);
            }
        }
    }

    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean confirm,int index);
    }
}
