package com.shenghao.widget;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenghao.R;


public class CommonDialog extends Dialog implements View.OnClickListener {

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
    private MessageType messageType = MessageType.Normal;
    private ImageView ivTop,ivBg;
    private String mIknow;
    private int gravity = Gravity.START;

    public CommonDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public CommonDialog(Context context, int submitTxtColor, boolean cancelable,
                        OnCloseListener listener) {
        super(context, R.style.Common_Dialog);
        this.mContext = context;
        this.submitTxtColor = submitTxtColor;
        this.cancelable = cancelable;
        this.listener = listener;
    }

    public CommonDialog(Context context, OnCloseListener listener) {
        super(context, R.style.Common_Dialog);
        this.mContext = context;
        this.listener = listener;
    }

    protected CommonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public CommonDialog setDialogCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }
    public CommonDialog setMessageType(MessageType type) {
        messageType = type;
        return this;
    }
    public CommonDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public CommonDialog setHint(String hint) {
        this.hint = hint;
        return this;
    }

    public CommonDialog setContent(CharSequence content) {
        this.content = content;
        return this;
    }

    public CommonDialog setEditContent(String editContent) {
        this.editContent = editContent;
        return this;
    }

    public CommonDialog setEditContentInputType(int editContentInputType) {
        this.editContentInputType = editContentInputType;
        return this;
    }

    public CommonDialog setContentVisibility(int visibility) {
        setContentVisibility(visibility,Gravity.START);
        return this;
    }

    public CommonDialog setContentVisibility(int visibility,int gravity) {
        this.contentVisible = visibility;
        this.gravity = gravity;
        return this;
    }
    public CommonDialog setContentEtVisibility(int visibility) {
        this.contentEtVisible = visibility;
        return this;
    }

    public CommonDialog setPositiveButton(String name) {
        this.positiveName = name;
        return this;
    }
    public CommonDialog setIKnowButton(String name) {
        this.mIknow = name;
        return this;
    }

    public CommonDialog setPositiveButtonColor(int submitTxtColor) {
        this.submitTxtColor = submitTxtColor;
        return this;
    }

    public CommonDialog setNegativeButton(String name) {
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
        setContentView(R.layout.dialog_commom);
        setCanceledOnTouchOutside(cancelable);
        setCancelable(cancelable);
        initView();
    }

    private void initView() {
        contentTxt = (TextView) findViewById(R.id.content);
        ivTop = (ImageView) findViewById(R.id.ivTop);
        ivBg = (ImageView) findViewById(R.id.ivBg);
        contentEt = findViewById(R.id.contentEt);
        contentEtLayout = findViewById(R.id.contentEtLayout);
        titleTxt = (TextView) findViewById(R.id.title);
        submitTxt = (TextView) findViewById(R.id.submitTv);
        submitTxt.setOnClickListener(this);
        cancelTxt = (TextView) findViewById(R.id.cancelTv);
        cancelTxt.setOnClickListener(this);

        contentTxt.setText(content);
        if (content instanceof Spannable) {
            contentTxt.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if (!TextUtils.isEmpty(positiveName)) {
            submitTxt.setText(positiveName);
        }

        if (submitTxtColor != 0) {
            submitTxt.setTextColor(submitTxtColor);
        }

        if (!TextUtils.isEmpty(negativeName)) {
            cancelTxt.setText(negativeName);
        }

        if (!TextUtils.isEmpty(title)) {
            titleTxt.setText(title);
        }

        if (!TextUtils.isEmpty(hint)) {
            contentEt.setHint(hint);
        }

        if (!TextUtils.isEmpty(editContent)) {
            contentEt.setText(editContent);
        }

        if (editContentInputType != 0) {
            contentEt.setInputType(editContentInputType);
        }

        contentTxt.setVisibility(contentVisible);
        contentTxt.setGravity(gravity);
        contentEtLayout.setVisibility(contentEtVisible);

        switch (messageType) {
            case INFOREAD:
                ivBg.setImageResource(R.drawable.ic_dialog_bg_blue);
                ivTop.setImageResource(R.drawable.ic_dialog_top01);
                break;
            case INFO:
                ivBg.setImageResource(R.drawable.ic_dialog_bg_blue);
                ivTop.setImageResource(R.drawable.ic_dialog_top02);
                break;
            case ALARM:
                ivBg.setImageResource(R.drawable.ic_dialog_bg_red);
                ivTop.setImageResource(R.drawable.ic_dialog_alarm);
                break;
            case SUCCESS:
                ivBg.setImageResource(R.drawable.ic_dialog_bg_blue);
                ivTop.setImageResource(R.drawable.ic_dialog_top_success);
                break;
            case Normal:
                ivBg.setVisibility(View.GONE);
                ivTop.setVisibility(View.GONE);
                break;
        }
        if(!TextUtils.isEmpty(mIknow)){
            submitTxt.setText(mIknow);
            cancelTxt.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancelTv) {
            if (listener != null) {
                listener.onClick(this, false);
            }
            this.dismiss();
        } else if (v.getId() == R.id.submitTv) {
            if (listener != null) {
                listener.onClick(this, true);
            }
        }
    }
    public enum MessageType {
        INFOREAD,//温馨提示阅读
        INFO,
        ALARM,
        SUCCESS,
        Normal,
    }

    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean confirm);
    }
}
