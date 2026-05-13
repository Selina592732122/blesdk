package com.shenghao.blesdkdemo.ui;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shenghao.blesdkdemo.R;


public class JFWebActivity extends BaseActivity {
    public static final String WEB_NAME = "web_name";
    public static final String WEB_URL = "web_url";

    private TextView titleTv;
    private WebView mWebView;

    private String webName;
    private String webUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        resolveIntent();
        initViews();
    }

    private void resolveIntent() {
        webName = getIntent().getStringExtra(WEB_NAME);
        webUrl = getIntent().getStringExtra(WEB_URL);
    }

    @Override
    protected void initViews() {
        super.initViews();
        titleTv = findViewById(R.id.titleTv);
        mWebView = findViewById(R.id.webView);

        titleTv.setText(webName);
        mWebView.loadUrl(webUrl);
    }
}
