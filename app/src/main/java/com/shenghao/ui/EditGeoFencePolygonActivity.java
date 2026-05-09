package com.shenghao.ui;

import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
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
import com.shenghao.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class EditGeoFencePolygonActivity extends BaseGeoFenceActivity {
    public static final int MAX_POLYGON_POINT_SIZE = 100;   // 可选点的最大数
    private ViewGroup contentLayout;
    private View revokeBtn;
    private TextView titleNameTv;

    private List<LatLng> mPointList = new ArrayList<>();
    private List<Marker> mPointMarkerList = new ArrayList<>();
    private Polygon mPolygon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isAddGeoFence()) {   //新增围栏
            titleNameTv.setText("添加围栏");
            getTerminalLocation(true);
        } else { //编辑围栏
            titleNameTv.setText("编辑围栏");
            contentLayout.setVisibility(View.GONE);
            nextStepTv.setVisibility(View.GONE);
            getGeofenceDetail();
        }
    }

    @Override
    int getLayoutResID() {
        return R.layout.activity_add_geofence_polygon;
    }

    @Override
    protected void initViews() {
        super.initViews();
        contentLayout = findViewById(R.id.contentLayout);
        mMapView = findViewById(R.id.geoFenceMapView);
        revokeBtn = findViewById(R.id.revokeBtn);
        titleNameTv = findViewById(R.id.titleNameTv);

        // 撤回上次绘制点
        revokeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPointList.size() > 0 && mPointMarkerList.size() > 0) {
                    mPointList.remove(mPointList.size() - 1);
                    Marker marker = mPointMarkerList.remove(mPointMarkerList.size() - 1);
                    if (marker != null) {
                        marker.remove();
                    }
                    drawPolygon();
                }
            }
        });
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
                    getTerminalLocation(false);
                } else {
                    ToastUtils.showShort(EditGeoFencePolygonActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "围栏详情获取失败：" + e);
                ToastUtils.showShort(EditGeoFencePolygonActivity.this, getString(R.string.request_retry));
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
                    LatLng latLngCenter = new LatLng(gpsInfo.getLat(), gpsInfo.getLng());
                    MarkerOptions markerOption = new MarkerOptions();
                    markerOption.position(latLngCenter);
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.drawable.ic_vehicle_marker)));
                    mAMap.addMarker(markerOption);

                    float zoom = 14;
                    if (!isAdd) { //编辑围栏，添加围栏顶点
                        List<LatLng> latLngList = DataConverter.toLatLngList(beforeGeoFenceData.getPoints());
                        if (latLngList != null && latLngList.size() > 0) {
                            mPointList.addAll(latLngList);
                            //计算地图缩放级别
                            double maxLng = latLngList.get(0).longitude;
                            double maxLat = latLngList.get(0).latitude;
                            double minLng = latLngList.get(0).longitude;
                            double minLat = latLngList.get(0).latitude;
                            for (LatLng latLng : mPointList) {  // 遍历绘制各个顶点
                                drawPointMarker(latLng);
                                maxLng = Math.max(maxLng, latLng.longitude);
                                maxLat = Math.max(maxLat, latLng.latitude);
                                minLng = Math.min(minLng, latLng.longitude);
                                minLat = Math.min(minLat, latLng.latitude);
                            }
                            drawPolygon();

                            //最大经纬度和最小经纬度之间的直线距离
                            float maxDistance = AMapUtils.calculateLineDistance(new LatLng(maxLat, maxLng), new LatLng(minLat, minLng));
                            zoom = MapUtils.calculateZoomByDistance(maxDistance);
                            latLngCenter = new LatLng((maxLat + minLat) / 2, (maxLng + minLng) / 2);    //计算当前地图中心位置
                        }
                    }

                    // 显示当前地图中心位置
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(
                            new CameraPosition(latLngCenter, zoom, 0, 0));
                    mAMap.moveCamera(cameraUpdate); //直接移动过去，不带移动过程动画
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
     * 地图点击事件
     */
    @Override
    void performMapClick() {
        if (mPointList.size() >= MAX_POLYGON_POINT_SIZE || mPointMarkerList.size() >= MAX_POLYGON_POINT_SIZE) {
            ToastUtils.showShort(EditGeoFencePolygonActivity.this, "可选择的点已达上限");
            return;
        }
        LatLng latLng = mAMap.getProjection().fromScreenLocation(new Point((int) mDownX, (int) mDownY));
        if (latLng != null) {
//            mLatLngTerminal = new LatLng(latLng.latitude, latLng.longitude);
            mPointList.add(latLng);
        }
        drawPointMarker(latLng);
        drawPolygon();
    }

    /**
     * 绘制顶点
     */
    private void drawPointMarker(LatLng latLng) {
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.ic_polygon_point)));
        Marker marker = mAMap.addMarker(markerOption);
        if (marker != null) {
//            marker.setPositionByPixels((int) mDownX, (int) (mDownY + 20));  // y坐标+20 覆盖物保持在中间位置，但是地图放大后覆盖物位置有误
            mPointMarkerList.add(marker);
        }
    }

    /**
     * 绘制多边形
     */
    private void drawPolygon() {
        if (mPointList == null || mPointList.size() == 0) {
            return;
        }
        // 绘制前，移除上次图案
        if (mPolygon != null) {
            mPolygon.remove();
        }
        PolygonOptions polygonOption = new PolygonOptions();
        polygonOption.addAll(mPointList);
        polygonOption.fillColor(getResources().getColor(R.color.fenced_fill));
        polygonOption.strokeColor(getResources().getColor(R.color.fenced_stroke));
        polygonOption.strokeWidth(8);
        mPolygon = mAMap.addPolygon(polygonOption);
    }

    @Override
    void doNextStep() {
        if (mPointList.size() > 2 && mPointMarkerList.size() > 2) {
            showEditNameDialog();
        } else {
            ToastUtils.showShort(EditGeoFencePolygonActivity.this, "请在地图上点击至少3个点");
        }
    }

    /**
     * 保存围栏
     */
    @Override
    void onSaveGeofence(String geofenceName) {
        if (mPointList == null || mPointList.size() < 3) {
            return;
        }

        //组装points数据
        StringBuilder pointSb = new StringBuilder();
        for (int index = 0; index < mPointList.size(); index++) {
            LatLng latLng = mPointList.get(index);
            if (index == mPointList.size() - 1) {  //最后一条
                pointSb.append(latLng.longitude).append(",").append(latLng.latitude);
            } else {
                pointSb.append(latLng.longitude).append(",").append(latLng.latitude).append("|");
            }
        }

        OkHttpPresent.addGeofencePolygon(pointSb.toString(), geoFenceId, AppSingleton.getInstance().getTerminalNo(), geofenceName, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "多边形围栏添加成功：" + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) { //添加成功
                        ToastUtils.showShort(EditGeoFencePolygonActivity.this, "创建成功");
                        EventBus.getDefault().post(new AddGeoFenceEvent());
                        finish();
                    } else {
                        ToastUtils.showShort(EditGeoFencePolygonActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(EditGeoFencePolygonActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "多边形围栏添加失败：" + e);
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }
}
