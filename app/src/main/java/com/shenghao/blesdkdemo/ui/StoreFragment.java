package com.shenghao.blesdkdemo.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;


public class StoreFragment extends BaseFragment {
    public static final String TAG = "StoreFragment";
    private WebView mWebview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OnBackPressedCallback backPressedCallback;

    public static StoreFragment newInstance() {
        StoreFragment fragment = new StoreFragment();
        return fragment;
    }

    public StoreFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_store, null);
        initViews(view);
        setupSwipeRefresh();

        // 创建返回键回调
        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 在这里处理返回键逻辑
                if (mWebview.canGoBack()) {
                    mWebview.goBack();
                } else {
                    // 如果不拦截，则允许默认返回行为
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        };

        // 注册回调
        requireActivity().getOnBackPressedDispatcher().addCallback(
                requireActivity(), // LifecycleOwner
                backPressedCallback
        );

        return view;
    }

    private void initViews(View view) {
        //设置状态栏高度
        View statusBarView = view.findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(requireActivity(), statusBarView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        mWebview = view.findViewById(R.id.webView);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        // 配置WebView
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true); // 启用JavaScript支持
        webSettings.setDomStorageEnabled(true); // 启用DOM存储API
        webSettings.setLoadWithOverviewMode(true); // 适应屏幕
        webSettings.setUseWideViewPort(true); // 将图片调整到适合webview的大小

        // 设置WebViewClient
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(ProgressBar.VISIBLE); // 显示进度条
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(ProgressBar.GONE); // 隐藏进度条
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // 设置WebChromeClient以监听进度变化
        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE); // 隐藏进度条
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    progressBar.setVisibility(ProgressBar.VISIBLE); // 显示进度条
                    progressBar.setProgress(newProgress); // 更新进度条
                    swipeRefreshLayout.setRefreshing(true);
                }
            }
        });
        // 加载指定的URL
        mWebview.loadUrl("https://shop168233457.m.taobao.com");
    }
    private void setupSwipeRefresh() {

        // 设置下拉刷新颜色
//        swipeRefreshLayout.setColorSchemeResources(
//                android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light
//        );

        // 下拉刷新监听器
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // 重新加载当前页面
//            mWebview.reload();
            mWebview.loadUrl("https://shop168233457.m.taobao.com");
        });
    }

}
