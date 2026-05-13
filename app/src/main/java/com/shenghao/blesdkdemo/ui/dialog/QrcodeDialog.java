package com.shenghao.blesdkdemo.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.shenghao.blesdkdemo.R;

import java.util.HashMap;
import java.util.Map;


public class QrcodeDialog extends Dialog implements View.OnClickListener {
    private TextView titleTxt;
    private Context mContext;
    private CharSequence content;
    private OnCloseListener listener;
    private String title;

    private boolean cancelable = true;

    private ImageView iv;

    public QrcodeDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public QrcodeDialog(Context context, OnCloseListener listener) {
        super(context, R.style.Common_Dialog);
        this.mContext = context;
        this.listener = listener;
    }

    protected QrcodeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public QrcodeDialog setDialogCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public QrcodeDialog setTitle(String title) {
        this.title = title;
        return this;
    }


    public QrcodeDialog setContent(CharSequence content) {
        this.content = content;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_qrcode);
        setCanceledOnTouchOutside(cancelable);
        setCancelable(cancelable);
        initView();
    }

    private void initView() {
        titleTxt = (TextView) findViewById(R.id.title);
        iv = (ImageView) findViewById(R.id.iv);
        if (!TextUtils.isEmpty(title)) {
            titleTxt.setText(title);
        }
        iv.setImageBitmap(generateQRCode(content.toString(),400,400));
    }

    // 使用ZXing生成二维码
    public Bitmap generateQRCode(String content, int width, int height) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = new QRCodeWriter().encode(
                    content,
                    BarcodeFormat.QR_CODE,
                    width,
                    height,
                    hints
            );

            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixels[y * width + x] = bitMatrix.get(x, y) ?
                            Color.BLACK : Color.WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (com.google.zxing.WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onClick(View v) {
    }

    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean confirm,int index);
    }
}
