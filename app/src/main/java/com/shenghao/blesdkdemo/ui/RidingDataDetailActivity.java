package com.shenghao.blesdkdemo.ui;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.RidingPointBean;
import com.shenghao.blesdkdemo.bean.RidingPointResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.DistanceUtil;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.MapUtils;
import com.shenghao.blesdkdemo.utils.TimeUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 骑行轨迹
 */
public class RidingDataDetailActivity extends BaseActivity {
    public static final String BUNDLE_RIDING_ID = "riding_id";
    public static final String BUNDLE_RIDING_START_TIME = "riding_start_time";
    public static final String BUNDLE_RIDING_END_TIME = "riding_end_time";
    public static final String BUNDLE_RIDING_START_ADDRESS = "riding_start_address";
    public static final String BUNDLE_RIDING_END_ADDRESS = "riding_end_address";
    public static final String BUNDLE_RIDING_TOTAL_MILE = "riding_total_mile";

    private TextView ridingDateTv;
    private TextView ridingTimeTv,ridingTimeTv2;
    private TextView ridingStartPointTv;
    private TextView ridingEndPointTv;
    private TextView ridingDistanceTv;
    private TextView ridingDurationTv;
    private TextView ridingSpeedTv;
    private MapView mMapView;
    private AMap aMap;

    private String mRidingStartTime;
    private String mRidingEndTime;
    private String mRidingStartAddress;
    private String mRidingEndAddress;
    private int mRidingId;
    private double mRidingTotalMile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riding_data_detail);
        resolveIntent();
        initViews();
        initMap(savedInstanceState);
        getRidingData();
    }

    private void resolveIntent() {
        if (getIntent() == null) {
            return;
        }
        mRidingId = getIntent().getIntExtra(BUNDLE_RIDING_ID, 0);
        mRidingStartTime = getIntent().getStringExtra(BUNDLE_RIDING_START_TIME);
        mRidingEndTime = getIntent().getStringExtra(BUNDLE_RIDING_END_TIME);
        mRidingStartAddress = getIntent().getStringExtra(BUNDLE_RIDING_START_ADDRESS);
        mRidingEndAddress = getIntent().getStringExtra(BUNDLE_RIDING_END_ADDRESS);
        mRidingTotalMile = getIntent().getDoubleExtra(BUNDLE_RIDING_TOTAL_MILE,0);
    }

    @Override
    protected void initViews() {
        super.initViews();
        ridingDateTv = findViewById(R.id.ridingDateTv);
        ridingTimeTv = findViewById(R.id.ridingTimeTv);
        ridingTimeTv2 = findViewById(R.id.ridingTimeTv2);
        ridingStartPointTv = findViewById(R.id.ridingStartPointTv);
        ridingEndPointTv = findViewById(R.id.ridingEndPointTv);
        ridingDistanceTv = findViewById(R.id.ridingDistanceTv);
        ridingDurationTv = findViewById(R.id.ridingDurationTv);
        ridingSpeedTv = findViewById(R.id.ridingSpeedTv);
        mMapView = findViewById(R.id.vehicleMapView);

        ridingDateTv.setText(TimeUtils.getRidingDisplayDate(mRidingStartTime));
        ridingTimeTv.setText(TimeUtils.getRidingDisplayStartTime(mRidingStartTime));
        ridingTimeTv2.setText(TimeUtils.getRidingDisplayEndTime(mRidingEndTime));
        ridingStartPointTv.setText(mRidingStartAddress);
        ridingEndPointTv.setText(mRidingEndAddress);
    }

    private void initMap(Bundle savedInstanceState) {
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView.getMap();
            UiSettings mUiSettings = aMap.getUiSettings();  //实例化UiSettings类对象
            mUiSettings.setZoomControlsEnabled(false);
            mUiSettings.setScaleControlsEnabled(false);
        }

    }

    /**
     * 请求行程轨迹点
     */
    private void getRidingData() {
        OkHttpPresent.getRidingRecordDetail(AppSingleton.getInstance().getTerminalNo(), mRidingId, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 轨迹详情请求成功 = " + body);
                RidingPointResp ridingPointResp = JsonUtils.parseT(body, RidingPointResp.class);
                if (ridingPointResp != null) {
                    drawRidingLine(ridingPointResp.getData());
                } else {
                    ToastUtils.showShort(RidingDataDetailActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 轨迹详情请求失败 = " + e);
                ToastUtils.showShort(RidingDataDetailActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    /**
     * 绘制行程轨迹
     */
    private void drawRidingLine(List<RidingPointBean> ridingPointList) {
        if (ridingPointList == null || ridingPointList.size() == 0) {
            return;
        }

        //绘制起始点
        LatLng startLatLng = new LatLng(ridingPointList.get(0).getLat(), ridingPointList.get(0).getLng());
        LatLng endLatLng = new LatLng(ridingPointList.get(ridingPointList.size() - 1).getLat(), ridingPointList.get(ridingPointList.size() - 1).getLng());
        long startTimeMillis = Long.parseLong(ridingPointList.get(0).getPositioningTime());
        long endTimeMillis = Long.parseLong(ridingPointList.get(ridingPointList.size() - 1).getPositioningTime());

        //起点图标
        MarkerOptions startMarkerOption = new MarkerOptions();
        startMarkerOption.position(startLatLng);
        startMarkerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.ic_start_location_marker)));
        aMap.addMarker(startMarkerOption);

        //终点图标
        MarkerOptions endMarkerOption = new MarkerOptions();
        endMarkerOption.position(endLatLng);
        endMarkerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.ic_vehicle_marker)));
        aMap.addMarker(endMarkerOption);

        //绘制轨迹
        float allDistance = 0;  //总距离
        double maxLng = startLatLng.longitude;
        double maxLat = startLatLng.latitude;
        double minLng = startLatLng.longitude;
        double minLat = startLatLng.latitude;

        List<LatLng> latLngs = new ArrayList<>();
        for (int i = 0; i < ridingPointList.size(); i++) {
            RidingPointBean ridingPoint = ridingPointList.get(i);
            double lat = ridingPoint.getLat();
            double lng = ridingPoint.getLng();
            if (lat == 0.0f || lng == 0.0f) {   //过滤掉经纬度为0的异常数据
                continue;
            }
            LatLng latLng = new LatLng(lat, lng);
            latLngs.add(latLng);
            if (i > 0) {
                RidingPointBean ridingPointBefore = ridingPointList.get(i - 1);
                double latBefore = ridingPointBefore.getLat();
                double lngBefore = ridingPointBefore.getLng();
                if (latBefore == 0.0f || lngBefore == 0.0f) {   //过滤掉经纬度为0的异常数据
                    continue;
                }
                LatLng beforeLatLng = new LatLng(latBefore, lngBefore);
                if (!latLng.equals(beforeLatLng)) {
                    allDistance += AMapUtils.calculateLineDistance(latLng, beforeLatLng);
                }
            }
            maxLng = Math.max(maxLng, ridingPoint.getLng());
            maxLat = Math.max(maxLat, ridingPoint.getLat());
            minLng = Math.min(minLng, ridingPoint.getLng());
            minLat = Math.min(minLat, ridingPoint.getLat());
        }
        aMap.addPolyline(new PolylineOptions().
                addAll(latLngs).width(12).color(Color.parseColor("#FFFF710C")));


        //最大经纬度和最小经纬度之间的直线距离
        float maxDistance = AMapUtils.calculateLineDistance(new LatLng(maxLat, maxLng), new LatLng(minLat, minLng));
        LogUtils.e(TAG, "最大经纬度和最小经纬度之间的直线距离: " + maxDistance);

        //简单的计算缩放zoom
        float zoom = MapUtils.calculateZoomByDistance(maxDistance);
        LogUtils.e(TAG, "zoom: " + zoom);

        //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        LatLng cameraLatLng = new LatLng((maxLat + minLat) / 2, (maxLng + minLng) / 2);
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(cameraLatLng, zoom, 0, 0));
        // aMap.animateCamera(mCameraUpdate);    //带有移动过程的动画
        aMap.moveCamera(mCameraUpdate); //直接移动过去，不带移动过程动画

        //设置时间
        long durationTimeMillis = Math.abs(endTimeMillis - startTimeMillis);    //骑行时长
//        ridingDistanceTv.setText(DistanceUtil.getKM(allDistance) + "");
        ridingDistanceTv.setText(String.valueOf(mRidingTotalMile));
        ridingDurationTv.setText(TimeUtils.formatMilliseconds(durationTimeMillis));
        ridingSpeedTv.setText(DistanceUtil.calculateSpeed(allDistance, durationTimeMillis) + "km/h");
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
    }


}
