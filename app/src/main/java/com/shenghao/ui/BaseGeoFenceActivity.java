package com.shenghao.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.shenghao.R;
import com.shenghao.bean.GeoFenceBean;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.CommonDialog;

/**
 * 地理围栏基类
 */
public abstract class BaseGeoFenceActivity extends BaseActivity {
    private static final int DURATION_MAP_CLICK = 250; // 触发一次地图点击事件的时长
    public static final String EXTRA_ID = "extra_id";

    protected TextView nextStepTv;
    protected MapView mMapView;
    protected AMap mAMap;
    protected UiSettings mUiSettings;
    protected float mDownX, mDownY;
    private CommonDialog editNameDialog;
    protected int geoFenceId; //0-新增；非0-编辑
    protected GeoFenceBean beforeGeoFenceData;  //上次围栏数据

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResID());
        resolveIntent();
        initViews();
        initMap(savedInstanceState);
    }

    private void resolveIntent() {
        if (getIntent() != null) {
            geoFenceId = getIntent().getIntExtra(EXTRA_ID, 0);
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        nextStepTv = findViewById(R.id.nextStepTv);

        if (nextStepTv != null) {
            nextStepTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doNextStep();
                }
            });
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {    //地图点击事件
                performMapClick();
            }
        }
    };

    private final Runnable mMapClickRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(0);
        }
    };

    protected void initMap(Bundle savedInstanceState) {
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mUiSettings = mAMap.getUiSettings();  //实例化UiSettings类对象
            mUiSettings.setZoomControlsEnabled(false);
            mUiSettings.setScaleControlsEnabled(false);
        }

        mAMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        mDownX = event.getX();
                        mDownY = event.getY();
                        mHandler.removeCallbacks(mMapClickRunnable);
                        mHandler.postDelayed(mMapClickRunnable, DURATION_MAP_CLICK);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 如果手指移动了，取消点击检测
                        if ((Math.abs(event.getX() - mDownX) > ViewConfiguration.get(BaseGeoFenceActivity.this).getScaledTouchSlop())
                                || (Math.abs(event.getY() - mDownY) > ViewConfiguration.get(BaseGeoFenceActivity.this).getScaledTouchSlop())) {
                            mHandler.removeCallbacks(mMapClickRunnable);
                        }
                        break;
//                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mHandler.removeCallbacks(mMapClickRunnable);
                        break;
                }
            }
        });
    }

    protected void showEditNameDialog() {
        editNameDialog = new CommonDialog(BaseGeoFenceActivity.this, new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {  //确定
                    String contentEtText = editNameDialog.getContentEtText().trim();
                    if (!TextUtils.isEmpty(contentEtText)) {
                        onSaveGeofence(contentEtText);
                        dialog.dismiss();
                    } else {
                        ToastUtils.showShort(BaseGeoFenceActivity.this, "请输入围栏名称");
                    }

                }
            }
        })
                .setTitle("围栏名称")
                .setHint("请输入围栏名称")
                .setPositiveButton("保存")
                .setContentEtVisibility(View.VISIBLE);
        if (beforeGeoFenceData != null) { //将编辑的围栏名称传进来
            editNameDialog.setEditContent(beforeGeoFenceData.getFenceName());
        }
        editNameDialog.show();
    }

    /**
     * 是否新增围栏
     *
     * @return 0-新增；非0-编辑
     */
    protected boolean isAddGeoFence() {
        return geoFenceId == 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (editNameDialog != null) {
            editNameDialog.dismiss();
        }
    }

    abstract int getLayoutResID();

    abstract void performMapClick();    //地图点击事件

    abstract void doNextStep(); //下一步

    abstract void onSaveGeofence(String geofenceName); //保存

}
