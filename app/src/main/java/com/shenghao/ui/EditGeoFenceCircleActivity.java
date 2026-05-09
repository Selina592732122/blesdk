package com.shenghao.ui;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.amap.api.location.DPoint;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.shenghao.R;
import com.shenghao.bean.GeoFenceResp;
import com.shenghao.bean.GpsInfo;
import com.shenghao.bean.GpsInfoResp;
import com.shenghao.event.AddGeoFenceEvent;
import com.shenghao.okhttp.OkHttpBaseResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.utility.AppSingleton;
import com.shenghao.utils.DataConverter;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.MapUtils;
import com.shenghao.utils.SizeUtils;
import com.shenghao.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 添加圆形地理围栏
 */
public class EditGeoFenceCircleActivity extends BaseGeoFenceActivity {
    private ViewGroup contentLayout;
    private TextView titleNameTv;
    private TextView currentRadiusTv;
    private TextView centerPointTv;
    private SeekBar radiusSeekBar;

    private GeocodeSearch geocoderSearch = null;
    private LatLng mLatLngCircle; //围栏中心经纬度
    private Circle mCircle; //圆形围栏
    private int mSeekBarProgress;
    private static final float MIN_RADIUS = 50f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isAddGeoFence()) {   //新增围栏
            titleNameTv.setText("添加围栏");
            initSeekBar();
            getTerminalLocation(true);
            currentRadiusTv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showLong(EditGeoFenceCircleActivity.this, "可在地图上点击选择围栏中心");
                }
            }, 500);
        } else { //编辑围栏
            titleNameTv.setText("编辑围栏");
            contentLayout.setVisibility(View.GONE);
            nextStepTv.setVisibility(View.GONE);
            getGeofenceDetail();
        }

    }

    @Override
    int getLayoutResID() {
        return R.layout.activity_add_geofence_circle;
    }

    @Override
    protected void initViews() {
        super.initViews();
        contentLayout = findViewById(R.id.contentLayout);
        mMapView = findViewById(R.id.geoFenceMapView);
        radiusSeekBar = findViewById(R.id.radiusSeekBar);
        titleNameTv = findViewById(R.id.titleNameTv);
        currentRadiusTv = findViewById(R.id.currentRadiusTv);
        centerPointTv = findViewById(R.id.centerPointTv);
    }

    private void initSeekBar() {
        // 设置SeekBar的进度改变监听器
        mSeekBarProgress = radiusSeekBar.getProgress();
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当SeekBar的进度改变时调用此方法
                // progress：当前进度值（0-100）
                mSeekBarProgress = progress;
                drawCircle();
                setCurrentRadiusText();

                // fromUser：如果为true，则表示进度是由用户更改的；否则，是由程序更改的
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 当用户开始拖动SeekBar时调用此方法
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 当用户停止拖动SeekBar时调用此方法
            }
        });

        // 设置当前围栏范围
        setCurrentRadiusText();
    }

    /**
     * 获取围栏详情
     */
    private void getGeofenceDetail() {
        OkHttpPresent.getGeofenceDetail(geoFenceId, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "围栏详情获取成功：" + body);
                GeoFenceResp geoFenceResp = JsonUtils.parseT(body, GeoFenceResp.class);
                if (geoFenceResp != null && geoFenceResp.isSuccess() && geoFenceResp.getData() != null) {
                    contentLayout.setVisibility(View.VISIBLE);
                    nextStepTv.setVisibility(View.VISIBLE);
                    beforeGeoFenceData = geoFenceResp.getData();
                    radiusSeekBar.setProgress(beforeGeoFenceData.getRadius() * 2 / 100);
                    initSeekBar();
                    getTerminalLocation(false);
                } else {
                    ToastUtils.showShort(EditGeoFenceCircleActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "围栏详情获取失败：" + e);
                ToastUtils.showShort(EditGeoFenceCircleActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    /**
     * 获取设备当前定位
     */
    private void getTerminalLocation(boolean isAdd) {
        //获取最新位置信息
        OkHttpPresent.getLatestGpsInfo(AppSingleton.getInstance().getTerminalNo(), new OkHttpResultCallBack() {
            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 最新位置请求成功 = " + body);
                GpsInfoResp gpsInfoResp = JsonUtils.parseT(body, GpsInfoResp.class);
                if (gpsInfoResp == null || !gpsInfoResp.isSuccess()) {
                    return;
                }
                GpsInfo gpsInfo = gpsInfoResp.getData();
                if (gpsInfo != null) {
                    LatLng latLngGps = new LatLng(gpsInfo.getLat(), gpsInfo.getLng());
                    MarkerOptions markerOption = new MarkerOptions();
                    markerOption.position(latLngGps);
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.drawable.ic_vehicle_marker)));
                    mAMap.addMarker(markerOption);

                    float zoom = 14;
                    if (isAdd) { //新增围栏
                        mLatLngCircle = new LatLng(gpsInfo.getLat(), gpsInfo.getLng());
                    } else if (beforeGeoFenceData != null) { //编辑围栏
                        zoom = MapUtils.calculateZoomByDistance(beforeGeoFenceData.getRadius() * 2);
                        mLatLngCircle = DataConverter.toLatLng(beforeGeoFenceData.getPoints());
                    }

                    // 显示当前地图中心位置
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(
                            new CameraPosition(mLatLngCircle, zoom, 0, 0));
                    mAMap.moveCamera(cameraUpdate); //直接移动过去，不带移动过程动画

                    //绘制围栏
                    drawCircle();
                    //更新围栏中心位置信息
                    updateCenterPointAddress(mLatLngCircle.latitude, mLatLngCircle.longitude);
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 最新位置请求失败" + e);
            }
        });
    }

    /**
     * 更新围栏中心位置信息
     */
    @SuppressLint("CheckResult")
    private void updateCenterPointAddress(double lat, double lng) {
        Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        try {
                            if (geocoderSearch == null) {
                                geocoderSearch = new GeocodeSearch(EditGeoFenceCircleActivity.this);
                            }
                            // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                            RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(lat, lng), 200, GeocodeSearch.AMAP);
                            RegeocodeAddress regeocodeAddress = geocoderSearch.getFromLocation(query);
                            String address = regeocodeAddress.getFormatAddress();
                            LogUtils.e(TAG, "请求高德当前地理位置: " + address);
                            emitter.onNext(address);
                        } catch (AMapException e) {
                            emitter.onError(e);
                        }
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String address) throws Exception {
                        if (!TextUtils.isEmpty(address)) {
                            centerPointTv.setText(address);
                        }
                    }
                });
    }

    /**
     * 处理地图点击事件
     */
    @Override
    void performMapClick() {
        LatLng latLng = mAMap.getProjection().fromScreenLocation(new Point((int) mDownX, (int) mDownY));
        if (latLng != null) {
            mLatLngCircle = new LatLng(latLng.latitude, latLng.longitude);
        }
        drawCircle();

        CameraUpdate cameraUpdateTerminal = CameraUpdateFactory.newCameraPosition(
                new CameraPosition(mLatLngCircle, mAMap.getCameraPosition().zoom, 0, 0));
        mAMap.animateCamera(cameraUpdateTerminal);

        //更新围栏中心位置信息
        updateCenterPointAddress(mLatLngCircle.latitude, mLatLngCircle.longitude);
    }

    /**
     * 设置当前围栏范围
     */
    private void setCurrentRadiusText() {
        int currentRadius = (int) (getCircleRadius() * 2);
        currentRadiusTv.setText(currentRadius + "m");
    }

    /**
     * 绘制圆形
     */
    private void drawCircle() {
        if (mLatLngCircle == null) {
            return;
        }
        // 绘制前，移除上次围栏
        if (mCircle != null) {
            mCircle.remove();
        }
        CircleOptions option = new CircleOptions();
        option.fillColor(getResources().getColor(R.color.fenced_fill));
        option.strokeColor(getResources().getColor(R.color.fenced_stroke));
        option.strokeWidth(SizeUtils.dp2px(this,1.5f));
        option.radius(getCircleRadius());
        DPoint dPoint = new DPoint(mLatLngCircle.latitude, mLatLngCircle.longitude);
        option.center(new LatLng(dPoint.getLatitude(), dPoint.getLongitude()));
        mCircle = mAMap.addCircle(option);
    }

    /**
     * 获取圆形围栏半径
     */
    private float getCircleRadius() {
        return mSeekBarProgress < 1 ? MIN_RADIUS : mSeekBarProgress * MIN_RADIUS;
    }

    @Override
    void doNextStep() {
        showEditNameDialog();
    }

    @Override
    void onSaveGeofence(String geofenceName) {
        if (mLatLngCircle == null) {
            return;
        }
        String centerPoint = mLatLngCircle.longitude + "," + mLatLngCircle.latitude;
        int radius = (int) getCircleRadius();
        OkHttpPresent.addGeofenceCircle(centerPoint, radius, geoFenceId, AppSingleton.getInstance().getTerminalNo(), geofenceName, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "圆形围栏添加成功：" + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) { //添加成功
                        ToastUtils.showShort(EditGeoFenceCircleActivity.this, "创建成功");
                        EventBus.getDefault().post(new AddGeoFenceEvent());
                        finish();
                    } else {
                        ToastUtils.showShort(EditGeoFenceCircleActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(EditGeoFenceCircleActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "圆形围栏添加失败：" + e);
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }
}
