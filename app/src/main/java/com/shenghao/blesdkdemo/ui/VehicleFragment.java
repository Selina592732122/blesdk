//package com.shenghao.blesdkdemo.ui;
//
//import android.annotation.SuppressLint;
//import android.app.Dialog;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothProfile;
//import android.content.ActivityNotFoundException;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.graphics.drawable.Drawable;
//import android.location.Location;
//import android.net.Uri;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.content.res.AppCompatResources;
//
//import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
//import com.alibaba.fastjson.TypeReference;
//import com.amap.api.maps.AMap;
//import com.amap.api.maps.CameraUpdate;
//import com.amap.api.maps.CameraUpdateFactory;
//import com.amap.api.maps.MapView;
//import com.amap.api.maps.UiSettings;
//import com.amap.api.maps.model.BitmapDescriptorFactory;
//import com.amap.api.maps.model.CameraPosition;
//import com.amap.api.maps.model.LatLng;
//import com.amap.api.maps.model.Marker;
//import com.amap.api.maps.model.MarkerOptions;
//import com.amap.api.maps.model.MyLocationStyle;
//import com.amap.api.services.core.LatLonPoint;
//import com.amap.api.services.geocoder.GeocodeSearch;
//import com.amap.api.services.geocoder.RegeocodeAddress;
//import com.amap.api.services.geocoder.RegeocodeQuery;
//import com.bumptech.glide.Glide;
//import com.clj.fastble.BleManager;
//import com.clj.fastble.data.BleDevice;
//import com.hjq.permissions.OnPermissionCallback;
//import com.hjq.permissions.XXPermissions;
//import com.hjq.permissions.permission.PermissionLists;
//import com.hjq.permissions.permission.base.IPermission;
//import com.shenghao.blesdkdemo.R;
//import com.shenghao.blesdkdemo.XXPermissions.PermissionDescription;
//import com.shenghao.blesdkdemo.XXPermissions.PermissionInterceptor;
//import com.shenghao.blesdkdemo.bean.BaseHttpResp;
//import com.shenghao.blesdkdemo.bean.GpsInfo;
//import com.shenghao.blesdkdemo.bean.GpsInfoResp;
//import com.shenghao.blesdkdemo.bean.RidingDataBean;
//import com.shenghao.blesdkdemo.bean.RidingDataResp;
//import com.shenghao.blesdkdemo.bean.RidingDataWithTotal;
//import com.shenghao.blesdkdemo.bean.SimpleLocationBean;
//import com.shenghao.blesdkdemo.bean.TerminalBean;
//import com.shenghao.blesdkdemo.constant.Const;
//import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;
//import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
//import com.shenghao.blesdkdemo.present.OkHttpPresent;
//import com.shenghao.blesdkdemo.utility.AppSingleton;
//import com.shenghao.blesdkdemo.utils.AppUtils;
//import com.shenghao.blesdkdemo.utils.JsonUtils;
//import com.shenghao.blesdkdemo.utils.LogUtils;
//import com.shenghao.blesdkdemo.utils.Redirect;
//import com.shenghao.blesdkdemo.utils.SPUtils;
//import com.shenghao.blesdkdemo.utils.StatusBarUtils;
//import com.shenghao.blesdkdemo.utils.StringUtils;
//import com.shenghao.blesdkdemo.utils.TimeUtils;
//import com.shenghao.blesdkdemo.utils.ToastUtils;
//import com.shenghao.blesdkdemo.utils.VehicleUtils;
//import com.shenghao.blesdkdemo.widget.CircleMenuPager;
//import com.shenghao.blesdkdemo.widget.CommonDialog;
//import com.shenghao.blesdkdemo.widget.IosBottomSheetDialog;
//import com.shenghao.blesdkdemo.widget.MapFriendlyNestedScrollView;
//import com.shenghao.blesdkdemo.widget.SlipButton;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import io.reactivex.Observable;
//import io.reactivex.ObservableEmitter;
//import io.reactivex.ObservableOnSubscribe;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Consumer;
//import io.reactivex.schedulers.Schedulers;
//import okhttp3.Request;
//import okhttp3.Response;
//
///**
// * 车况
// */
//public class VehicleFragment extends BaseFragment {
//    public static final String TAG = "VehicleFragment";
//    private Context mContext;
//
//    private View statusBarView;
//    private LinearLayout ridingDataLayout;
////    private LinearLayout alarmSettingLayout;
////    private LinearLayout powerControlLayout;
////    private LinearLayout findCarLayout;
//    private TextView gpsAddressTv;
//    private TextView gpsTimeTv;
//    private TextView batteryPercentTv;
//    private TextView powerTv;
//    private TextView verticalNameTv,tvVirtualMode;
//    private ImageView ivCar;
//    private TextView sumRidingDistanceTv;
//    private LinearLayout sumRidingDistanceLayout;
//    private View locationBtn;
//    private View gpsBtn;
//    private ImageView gpsRefreshIv;
//    private ImageView ivBle;
//    private RoundCornerProgressBar batteryRoundPb;
//    private MapView mMapView = null;
//    private AMap aMap;
//    private Disposable mDisposable;
//    private GeocodeSearch geocoderSearch = null;
//    private Marker mMarker;
//    private CameraUpdate mCameraUpdateTerminal; //设备
//    private CameraUpdate mCameraUpdateUser; //用户
//    private LatLng mLatLngTerminal; //设备经纬度
//    private LatLng mLatLngUser; //用户经纬度
//    private SlipButton powerBtn;
////    private ImageView ivLight,ivLock;
////    private TextView tvLight,tvLock;
//    private TextView tvStatus;
//    private boolean isLightOn = false;//大灯是否开启
//    private double powerPercent;
//    private TextView tvControl;
//    private TextView tvControl2;
////    private View llLight;
//    private TextView tvLastDistance;
//    private MapFriendlyNestedScrollView nestedScrollView;
//    private CircleMenuPager circleMenuPager;
//    private ImageView bg;
//
//    public static VehicleFragment newInstance() {
//        VehicleFragment fragment = new VehicleFragment();
//        return fragment;
//    }
//
//    public VehicleFragment() {
//        super();
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        mContext = context; // ✅ 在这里获取 Context
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mContext = null; // ✅ 避免内存泄漏
//    }
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_vehicle, null);
//        initViews(view);
//        initCircleMenu(view);
//        initMap(view, savedInstanceState);
//        nestedScrollView.setMapView(mMapView);
//        return view;
//    }
//
//    private void initCircleMenu(View view) {
//        circleMenuPager = view.findViewById(R.id.circleMenuPager);
//
//        // 创建菜单数据
//        List<CircleMenuPager.MenuItem> menuItems = new ArrayList<>();
//
//        // 可以继续添加更多菜单项，会自动分页
//
//        // 设置菜单数据
//        circleMenuPager.setMenuData(menuItems);
//
//        // 点击事件通过tag识别
//        circleMenuPager.setOnMenuClickListener(new CircleMenuPager.OnMenuClickListener() {
//            @Override
//            public void onMenuClick(String tag, CircleMenuPager.MenuItem item) {
//                handleMenuClick(tag, item);
//            }
//        });
//    }
//    private void handleMenuClick(String tag, CircleMenuPager.MenuItem item) {
//        switch (tag) {
//            case "menu_ming":
//                // 鸣笛寻车改导航
//                showVehicleNavigationSheet();
//                break;
//            case "menu_fanglang":
//                antiWolf(0);
//                break;
//            case "menu_peiban":
//
//                showWindowAndSeatBottomSheet(30);
//                break;
//            case "menu_zhedie":
//                showWindowAndSeatBottomSheet(29);
//                break;
//            case "menu_danger":
//                showWindowAndSeatBottomSheet(10);
//                break;
//            case "menu_setting":
//                // 设防关
//                Redirect.startAlarmSettingActivity(mContext);
//                break;
//            case "menu_lock":
//                // 切换锁状态
//                showPowerControlBottomSheet();
//                break;
//            case "menu_light":
//                //开灯
////                setCarControl();
//                showWindowAndSeatBottomSheet(27);
//                break;
//            case "menu_find_car":
//                // 寻车，调指令
//                showWindowAndSeatBottomSheet(9);
//                break;
//            case "menu_window":
//                // 车窗
//                showWindowBottomSheet(11);//0x0B
//                break;
//            case "menu_open_door":
//                //后备箱
//                airControl(6,1);
//                break;
//            case "menu_heat":
//                //座椅加热
////                seatHeatOn = !seatHeatOn;
////                airControl(17,seatHeatOn?1:0);
//                break;
//        }
//    }
//
//    private void initViews(View view) {
//        statusBarView = view.findViewById(R.id.statusBarView);
//        bg = view.findViewById(R.id.img);
//        gpsAddressTv = view.findViewById(R.id.gpsAddressTv);
//        gpsTimeTv = view.findViewById(R.id.gpsTimeTv);
//        locationBtn = view.findViewById(R.id.locationBtn);
//        gpsBtn = view.findViewById(R.id.gpsBtn);
//        gpsRefreshIv = view.findViewById(R.id.gpsRefreshIv);
//        batteryRoundPb = view.findViewById(R.id.batteryRoundPb);
//        ridingDataLayout = view.findViewById(R.id.ridingDataLayout);
////        alarmSettingLayout = view.findViewById(R.id.alarmSettingLayout);
////        powerControlLayout = view.findViewById(R.id.powerControlLayout);
////        findCarLayout = view.findViewById(R.id.findCarLayout);
//        batteryPercentTv = view.findViewById(R.id.batteryPercentTv);
//        powerTv = view.findViewById(R.id.powerTv);
//        verticalNameTv = view.findViewById(R.id.verticalNameTv);
//        ivCar = view.findViewById(R.id.ivCar);
//        tvLastDistance = view.findViewById(R.id.tvLastDistance);
//        tvVirtualMode = view.findViewById(R.id.tvVirtualMode);
//        sumRidingDistanceTv = view.findViewById(R.id.sumRidingDistanceTv);
//        sumRidingDistanceLayout = view.findViewById(R.id.sumRidingDistanceLayout);
//        powerBtn = view.findViewById(R.id.powerBtn);
//        tvStatus = view.findViewById(R.id.tvStatus);
//        ivBle = view.findViewById(R.id.ivBle);
////        ivLight = view.findViewById(R.id.ivLight);
////        tvLight = view.findViewById(R.id.tvLight);
////        tvLock = view.findViewById(R.id.tvLock);
////        ivLock = view.findViewById(R.id.ivLock);
////        llLight = view.findViewById(R.id.llLight);
//        tvControl = view.findViewById(R.id.tvControl);
//        tvControl2 = view.findViewById(R.id.tvControl2);
//        tvControl.setOnClickListener(view1 -> {
//            if("1".equals(AppSingleton.getInstance().getCurrentTerminal().getCarControlType())){
//                Intent intent = new Intent(getActivity(), ControlActivity.class);
//                startActivity(intent);
//            }else {
//                ToastUtils.showShort(requireActivity(),"当前设备不支持");
//            }
//        });
//
//        nestedScrollView = view.findViewById(R.id.nestedScrollView);
//        //设置状态栏高度
//        StatusBarUtils.setStatusBarHeight(mContext, statusBarView);
//
//        if(AppSingleton.getInstance().getUserName().equals(AppSingleton.virtualTel)){//虚拟账号
//            verticalNameTv.setVisibility(View.INVISIBLE);
//            tvVirtualMode.setVisibility(View.VISIBLE);
//            ((MainActivity)mContext).showTab(false);
//        }else {
//            verticalNameTv.setVisibility(View.VISIBLE);
//            tvVirtualMode.setVisibility(View.INVISIBLE);
//            ((MainActivity)mContext).showTab(true);
//        }
//
//        tvVirtualMode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(requireActivity(),SplashActivity.class));
//                requireActivity().finish();
//            }
//        });
//
//        ivBle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(requireActivity(),BleActivity.class));
//            }
//        });
//        gpsRefreshIv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AppSingleton.getInstance().refreshTokenByCheck();
//                getLatestGpsInfo(true,true);
////                ToastUtils.showShort(mContext, "正在更新位置信息");
//            }
//        });
//
//        view.findViewById(R.id.restPowerLayout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //电量
////                Intent intent = new Intent(getActivity(), BatteryActivity.class);
////                intent.putExtra("progress",powerPercent);
////                startActivity(intent);
//            }
//        });
//        view.findViewById(R.id.llOnekey).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //一件挪车
//                String protocol = AppSingleton.getInstance().getCurrentTerminal().getProtocol();
//                if("CAN".equals(protocol)){
//                    Intent intent = new Intent(getActivity(), OneKeyPortraitActivity.class);
//                    startActivity(intent);
//                }else {
//                    ToastUtils.showShort(requireActivity(),"当前设备不支持");
//                }
//            }
//        });
//        tvControl2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String protocol = AppSingleton.getInstance().getCurrentTerminal().getProtocol();
//                if("CAN".equals(protocol)){
//                    Intent intent = new Intent(getActivity(), Control2Activity.class);
//                    startActivity(intent);
//                }else {
//                    ToastUtils.showShort(requireActivity(),"当前设备不支持");
//                }
//            }
//        });
//
//        ridingDataLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), RidingDataActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        locationBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (aMap == null || mLatLngUser == null) {
//                    requestLocationPermission();
//                    return;
//                }
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(
//                        new CameraPosition(mLatLngUser, aMap.getCameraPosition().zoom, 0, 0));
//                aMap.moveCamera(cameraUpdate);
//            }
//        });
//
//        gpsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (aMap == null || mLatLngTerminal == null) {
//                    return;
//                }
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(
//                        new CameraPosition(mLatLngTerminal, aMap.getCameraPosition().zoom, 0, 0));
//                aMap.moveCamera(cameraUpdate);
//            }
//        });
//
//        //设防关
////        alarmSettingLayout.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Redirect.startAlarmSettingActivity(mContext);
////            }
////        });
//
//        //油电控制
////        powerControlLayout.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                showPowerControlBottomSheet();
////            }
////        });
//        //开启大灯
////        llLight.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                setCarControl();
////            }
////        });
//
//        //导航寻车
////        findCarLayout.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                showVehicleNavigationSheet();
////            }
////        });
//
//
//        // 启动、停止车辆
//        powerBtn.setOnChangedListener(new SlipButton.OnChangedListener() {
//            @Override
//            public void OnChanged(SlipButton slipButton, boolean checkState) {
//                if(checkState){
//                    //开启
//                    requestPowerControl(Const.COMMAND_ACC_ON);
//                }
//            }
//
//            @Override
//            public void OnClick(SlipButton slipButton) {
//                new CommonDialog(mContext, new CommonDialog.OnCloseListener() {
//                    @Override
//                    public void onClick(Dialog dialog, boolean confirm) {
//                        if (confirm) {
//                            requestPowerControl(Const.COMMAND_ACC_OFF);
//                            powerBtn.setStatus(false);
//                            dialog.dismiss();
//                        }
//                    }
//                })
//                        .setTitle("确定停止车辆？")
//                        .setPositiveButton("确定")
//                        .show();
//            }
//        });
//
//        view.findViewById(R.id.tvShare).setOnClickListener(v -> {
//            startActivity(new Intent(mContext,ShareActivity.class));
//        });
//    }
//
//    private void initMap(View view, Bundle savedInstanceState) {
//        //获取地图控件引用
//        mMapView = (MapView) view.findViewById(R.id.vehicleMapView);
//        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
//        mMapView.onCreate(savedInstanceState);
//        //初始化地图控制器对象
//        if (aMap == null) {
//            aMap = mMapView.getMap();
//            UiSettings mUiSettings = aMap.getUiSettings();  //实例化UiSettings类对象
//            mUiSettings.setZoomControlsEnabled(false);
//            mUiSettings.setAllGesturesEnabled(true);
////            mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);
//        }
//
////        HandlerCompat.createAsync(Looper.getMainLooper()).postDelayed(new Runnable() {
////            @Override
////            public void run() {
////                boolean needShowScanPermissionDialog = SPUtils.getInstance().getBoolean(SPUtils.SP_NEED_SHOW_LOCATION_PERMISSION_DIALOG, true);
////                if (needShowScanPermissionDialog) {
////                    new CommonDialog(mContext, new CommonDialog.OnCloseListener() {
////                        @Override
////                        public void onClick(Dialog dialog, boolean confirm) {
////                            if (confirm) {
////                                dialog.dismiss();
////                                requestLocationPermission();
////                            }
////                        }
////                    })
////                            .setTitle("请您授权" + getString(R.string.app_name) + "App位置权限才能在地图中查看本人当前所在位置")
////                            .setPositiveButton("确定")
////                            .setDialogCancelable(false)
////                            .show();
////                } else {
////                    requestLocationPermission();
////                }
////            }
////        }, 200);
//    }
//
//    private void requestLocationPermission() {
//        XXPermissions.with(this)
//                // 申请单个权限
//                .permission(PermissionLists.getAccessFineLocationPermission())
//                .permission(PermissionLists.getAccessCoarseLocationPermission())
////                .permission(Permission.ACCESS_FINE_LOCATION)
////                .permission(Permission.ACCESS_COARSE_LOCATION)
//                // 设置权限请求拦截器（局部设置）
////                .interceptor(new PermissionInterceptor(getString(R.string.permissionLocation)))
//                .interceptor(new PermissionInterceptor())
//                .description(new PermissionDescription())
//                // 设置不触发错误检测机制（局部设置）
//                //.unchecked()
//                .request(new OnPermissionCallback() {
//
//                    @Override
//                    public void onResult(@NonNull List<IPermission> grantedList, @NonNull List<IPermission> deniedList) {
//                        boolean allGranted = deniedList.isEmpty();
//                        if (!allGranted) {
//                            // 判断请求失败的权限是否被用户勾选了不再询问的选项
//                            boolean doNotAskAgain = XXPermissions.isDoNotAskAgainPermissions(requireActivity(), deniedList);
//                            // 在这里处理权限请求失败的逻辑
//                            if (doNotAskAgain) {
//                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                                XXPermissions.startPermissionActivity(requireActivity(), deniedList);
//                            } else {
//                                ToastUtils.showShort(requireActivity(),"获取权限失败");
//                            }
//                            return;
//                        }
//                        // 在这里处理权限请求成功的逻辑
//                        SPUtils.getInstance().putBoolean(SPUtils.SP_NEED_SHOW_LOCATION_PERMISSION_DIALOG, false);
//                        initUserLocation(); //初始化用户定位
//                    }
//
////                    @Override
////                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
////                        if (!allGranted) {
////                            ToastUtils.showShort(requireActivity(),"获取部分权限成功，但部分权限未正常授予");
////                            return;
////                        }
////                        SPUtils.getInstance().putBoolean(SPUtils.SP_NEED_SHOW_LOCATION_PERMISSION_DIALOG, false);
////                        initUserLocation(); //初始化用户定位
////                    }
////
////                    @Override
////                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
////                        if (doNotAskAgain) {
////                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
////                            XXPermissions.startPermissionActivity(requireActivity(), permissions);
////                        } else {
////                            ToastUtils.showShort(requireActivity(),"获取权限失败");
////                        }
////                    }
//                });
//    }
//
//    /**
//     * 初始化用户定位
//     */
//    private void initUserLocation() {
//        MyLocationStyle myLocationStyle;
//        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
//        myLocationStyle.interval(10 * 1000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                .decodeResource(getResources(), R.drawable.ic_my_location_marker)));
//        myLocationStyle.strokeWidth(0);
//        myLocationStyle.radiusFillColor(Color.parseColor("#00000000"));
//        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
////        aMap.getUiSettings().setMyLocationButtonEnabled(true);  //设置默认定位按钮是否显示，非必需设置。
//        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
//        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
//            @Override
//            public void onMyLocationChange(Location location) {
//                mLatLngUser = new LatLng(location.getLatitude(), location.getLongitude());
//                mCameraUpdateUser = CameraUpdateFactory.newCameraPosition(
//                        new CameraPosition(mLatLngUser, aMap.getCameraPosition().zoom, 0, 0));
//            }
//        });
//    }
//
//    /**
//     * 请求最新位置信息
//     */
//    private void getLatestGpsInfo(boolean force,boolean toast) {
//        //获取最新位置信息
//        OkHttpPresent.getLatestGpsInfo(AppSingleton.getInstance().getTerminalNo(), new OkHttpResultCallBack() {
//            @Override
//            protected void onResponse(Response response, String body) throws IOException {
//                LogUtils.e(TAG, "onResponse: 最新位置请求成功 = " + body);
//                GpsInfoResp gpsInfoResp = JsonUtils.parseT(body, GpsInfoResp.class);
//                if (gpsInfoResp == null || !gpsInfoResp.isSuccess()) {
//                    return;
//                }
//                GpsInfo gpsInfo = gpsInfoResp.getData();
//                if (gpsInfo != null) {
//                    mLatLngTerminal = new LatLng(gpsInfo.getLat(), gpsInfo.getLng());
//                    // 显示当前地图中心位置
//                    mCameraUpdateTerminal = CameraUpdateFactory.newCameraPosition(
//                            new CameraPosition(mLatLngTerminal, mMarker == null ? 16 : aMap.getCameraPosition().zoom, 0, 0));
//                    if (mMarker == null) {
//                        MarkerOptions markerOption = new MarkerOptions();
//                        markerOption.position(mLatLngTerminal);
//                        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                                .decodeResource(getResources(), R.drawable.ic_vehicle_marker)));
//                        mMarker = aMap.addMarker(markerOption);
//                        aMap.moveCamera(mCameraUpdateTerminal); //直接移动过去，不带移动过程动画
//                    } else {
//                        mMarker.setPosition(mLatLngTerminal);
//                        if (force) {
//                            aMap.moveCamera(mCameraUpdateTerminal);
//                            if(toast)
//                                ToastUtils.showShort(mContext,"定位已刷新");
////                            aMap.animateCamera(mCameraUpdate);    //带有移动过程的动画
//                        }
//                    }
//
//                    // 刷新当前位置信息
//                    refreshGpsAddress(mLatLngTerminal.latitude, mLatLngTerminal.longitude, gpsInfo.getAddress(), gpsInfo.getPositioningTime());
//
//                    //设置电量
//                    powerPercent = VehicleUtils.getPowerPercent(gpsInfo.getVoltage());
//                    powerTv.setText(StringUtils.getFormatNumber(gpsInfo.getVoltage()) + "v");
////                    batteryPercentTv.setText(StringUtils.getFormatNumber(powerPercent));
//                    if(AppSingleton.getInstance().getCurrentTerminal().getIsQuantity() == 0){//0-后端给 1-前端自己算
//                        powerPercent = gpsInfo.getQuantity();
//                    }
//                    batteryPercentTv.setText((int)powerPercent+"");
//
//                    batteryRoundPb.setProgress((float) powerPercent);
//
//                    //刷新车辆启动、停止状态
//                    refreshAccStatus(gpsInfo.getAccState() == 1);
//                    refreshLockStatus(gpsInfo.getLockStatus()== 1);
//                }
//            }
//
//            @Override
//            protected void onFailed(Request request, Exception e) {
//                super.onFailed(request, e);
//                LogUtils.e(TAG, "onFailed: 最新位置请求失败" + e);
//            }
//        });
//    }
//
//    /**
//     * 刷新启动、停止状态
//     */
//    private void refreshAccStatus(boolean accOn) {
////        ToastUtils.showShort(requireActivity(),accOn+"");
//        powerBtn.setStatusImmediately(accOn);
//        if(accOn){
//            tvStatus.setText("已启动");
//            Drawable drawableLeft = AppCompatResources.getDrawable(requireActivity(), R.drawable.bg_yellow_dot_shape);
//            tvStatus.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null); // 使用
//        }else {
//            tvStatus.setText("未启动");
//            Drawable drawableLeft = AppCompatResources.getDrawable(requireActivity(), R.drawable.bg_gray_dot_shape);
//            tvStatus.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null); // 使用
//        }
//    }
//    private void refreshWindowStatus(boolean on){
//        if (on) {  // 开锁状态
//            // 方法1：使用位置更新
//            circleMenuPager.updateMenuItemByTag("menu_window", R.drawable.ic_home05_blue, "车窗", Color.parseColor("#FFF57F2A"));
//        } else { // 关锁状态
//            circleMenuPager.updateMenuItemByTag("menu_window", R.drawable.ic_home05, "车窗", Color.parseColor("#99000000"));
//        }
//    }
////    private void refreshSeatStatus(boolean on){
////        if (on) {  // 开锁状态
////            // 方法1：使用位置更新
////            circleMenuPager.updateMenuItemByTag("menu_heat", R.drawable.ic_home07_blue, "关闭加热", Color.parseColor("#FFF57F2A"));
////        } else { // 关锁状态
////            circleMenuPager.updateMenuItemByTag("menu_heat", R.drawable.ic_home07, "座椅加热", Color.parseColor("#99000000"));
////        }
////    }
//    /**
//     * 刷新开锁关锁状态
//     */
//    private void refreshLockStatus(boolean on) {
////        if (on) {  //开锁
////            ivLock.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_home02));
////            tvLock.setText("点击设防");
////            tvLock.setTextColor(Color.parseColor("#99000000"));
////        } else { //关锁
////            ivLock.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_home02_blue));
////            tvLock.setText("点击解防");
////            tvLock.setTextColor(Color.parseColor("#FFF57F2A"));
////        }
//        if (on) {  // 开锁状态
//            // 方法1：使用位置更新
//            circleMenuPager.updateMenuItemByTag("menu_lock", R.drawable.ic_home02, "点击设防", Color.parseColor("#99000000"));
//        } else { // 关锁状态
//            circleMenuPager.updateMenuItemByTag("menu_lock", R.drawable.ic_home02_blue, "点击解防", Color.parseColor("#FFF57F2A"));
//        }
//    }
//    /**
//     * 刷新开灯状态
//     */
//    private void refreshLightStatus(boolean on) {
////        if (on) {  //开
////            ivLight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_home03_blue));
////            tvLight.setText("关闭大灯");
////            tvLight.setTextColor(Color.parseColor("#FFF57F2A"));
////
////        } else { //关
////            ivLight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_home03));
////            tvLight.setText("开启大灯");
////            tvLight.setTextColor(Color.parseColor("#99000000"));
////        }
//        if (on) {  // 开锁状态
//            // 方法1：使用位置更新
//            circleMenuPager.updateMenuItemByTag("menu_light", R.drawable.ic_home03_blue, "关闭大灯", Color.parseColor("#FFF57F2A"));
//        } else { // 关锁状态
//            circleMenuPager.updateMenuItemByTag("menu_light", R.drawable.ic_home03, "开启大灯", Color.parseColor("#99000000"));
//        }
//    }
//    private void setCarControl() {
//        OkHttpPresent.setCarControl(AppSingleton.getInstance().getTerminalNo(),"headlight",!isLightOn ? "1":"0",new OkHttpResultCallBack() {
//            @Override
//            protected void start() {
//                super.start();
//                ((MainActivity)requireActivity()).showLoadingDialog();
//            }
//
//            @Override
//            protected void onResponse(Response response, String body) throws IOException {
//                LogUtils.e(TAG, "onResponse: 成功 = " + body);
//                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
//                if (baseResp != null && baseResp.isSuccess()) {
//                    ToastUtils.showShort(requireActivity(), getString(R.string.do_success));
//                    isLightOn = !isLightOn;
//                    refreshLightStatus(isLightOn);
//                }else {
//                    ToastUtils.showShort(requireActivity(), TextUtils.isEmpty(baseResp.getMsg())? getString(R.string.request_retry):baseResp.getMsg());
//                }
//            }
//
//            @Override
//            protected void onFailed(Request request, Exception e) {
//                super.onFailed(request, e);
//                LogUtils.e(TAG, "onFailed: 失败 = " + e);
//                ToastUtils.showShort(requireActivity(), getString(R.string.request_retry));
//            }
//
//            @Override
//            protected void end() {
//                super.end();
//                ((MainActivity)requireActivity()).hideLoadingDialog();
//            }
//        });
//    }
//    private void antiWolf(int param) {
//        OkHttpPresent.antiWolf(AppSingleton.getInstance().getTerminalNo(),param,new OkHttpResultCallBack() {
//            @Override
//            protected void start() {
//                super.start();
//                ((MainActivity)requireActivity()).showLoadingDialog();
//            }
//
//            @Override
//            protected void onResponse(Response response, String body) throws IOException {
//                LogUtils.e(TAG, "onResponse: 成功 = " + body);
//                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
//                if (baseResp != null && baseResp.isSuccess()) {
//                    ToastUtils.showShort(requireActivity(), getString(R.string.do_success));
//                }else {
//                    ToastUtils.showShort(requireActivity(), TextUtils.isEmpty(baseResp.getMsg())? getString(R.string.request_retry):baseResp.getMsg());
//                }
//            }
//
//            @Override
//            protected void onFailed(Request request, Exception e) {
//                super.onFailed(request, e);
//                LogUtils.e(TAG, "onFailed: 失败 = " + e);
//                ToastUtils.showShort(requireActivity(), getString(R.string.request_retry));
//            }
//
//            @Override
//            protected void end() {
//                super.end();
//                ((MainActivity)requireActivity()).hideLoadingDialog();
//            }
//        });
//    }
//
//    /**
//     * 刷新gps位置信息
//     */
//    @SuppressLint("CheckResult")
//    private void refreshGpsAddress(double lat, double lng, String gpsAddress, long gpsTime) {
//        Observable.create(new ObservableOnSubscribe<String>() {
//                    @Override
//                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
//                        SimpleLocationBean lastLocation = AppSingleton.getInstance().getLastLocationInfo();
//                        // 当前定位经纬度与上次经纬度一致，取上次位置信息
//                        if (lastLocation != null && !TextUtils.isEmpty(lastLocation.getAddress()) &&
//                                lastLocation.getLat() == lat && lastLocation.getLng() == lng) {
//                            String address = lastLocation.getAddress();
////                            LogUtils.e(TAG, "经纬度不变，取上次位置信息: " + address);
//                            emitter.onNext(address);
//                        } else {    //当前位置信息与上次不一致
//                            if (!TextUtils.isEmpty(gpsAddress)) {   //服务器返回地址信息不为空，直接用
//                                emitter.onNext(gpsAddress);
//                            } else {    //服务器地址信息为空，将经纬度进行逆地理位置请求
//                                try {
//                                    if (geocoderSearch == null) {
//                                        geocoderSearch = new GeocodeSearch(mContext);
//                                    }
//                                    // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//                                    RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(lat, lng), 200, GeocodeSearch.AMAP);
//                                    RegeocodeAddress regeocodeAddress = geocoderSearch.getFromLocation(query);
//                                    String address = regeocodeAddress.getFormatAddress();
//                                    LogUtils.e(TAG, "请求高德当前地理位置: " + address);
//                                    emitter.onNext(address);
//                                } catch (Exception e) {
//                                    emitter.onError(e);
//                                }
//                            }
//                        }
//                        emitter.onComplete();
//                    }
//                }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String address) throws Exception {
//                        if (!TextUtils.isEmpty(address)) {
//                            gpsAddressTv.setText(address);
//                            gpsTimeTv.setText(TimeUtils.getDateToString(gpsTime, TimeUtils.PATTERN_05));
//
//                            SimpleLocationBean location = new SimpleLocationBean(lat, lng, address);
//                            AppSingleton.getInstance().setLastLocationInfo(location);
//                        }
//                    }
//                });
//    }
//
//    /**
//     * 15s轮询获取一次最新位置信息
//     */
//    @SuppressLint("CheckResult")
//    private void startGpsInfoInterval() {
//        mDisposable = Observable.interval(0, 15 * 1000, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
////                        LogUtils.e(TAG, "accept: 15s轮询获取一次最新位置信息!");
//                        getLatestGpsInfo(false,false);
//                    }
//                });
//    }
//
//    /**
//     * 开关窗户弹窗
//     */
//    private void showWindowAndSeatBottomSheet(int type) {
//        IosBottomSheetDialog.Builder builder = new IosBottomSheetDialog.Builder(mContext);
//        builder.addItemView("开", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
//            @Override
//            public void onClick(View view) {
////                requestPowerControl(Const.COMMAND_ACC_ON);
////                new Handler().postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
//                        airControl(type,1);
////                    }
////                },200);
//            }
//        });
//        builder.addItemView("关", Color.parseColor("#de001f"), false, new IosBottomSheetDialog.OnItemClickListener() {
//            @Override
//            public void onClick(View view) {
//                airControl(type,0);
//            }
//        });
//        builder.build().show();
//    }
//    private void showWindowBottomSheet(int type) {
//        IosBottomSheetDialog.Builder builder = new IosBottomSheetDialog.Builder(mContext);
//        builder.addItemView("左前窗关", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
//            @Override
//            public void onClick(View view) {
//                airControl2(type,1,16);
//            }
//        });
//        builder.addItemView("左前窗开", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
//            @Override
//            public void onClick(View view) {
//                airControl2(type,1,32);
//            }
//        });
//        builder.addItemView("右前窗关", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
//            @Override
//            public void onClick(View view) {
//                airControl2(type,1,1);
//            }
//        });
//        builder.addItemView("右前窗开", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
//            @Override
//            public void onClick(View view) {
//                airControl2(type,1,2);
//            }
//        });
//        builder.addItemView("全车关", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
//            @Override
//            public void onClick(View view) {
//                airControl2(type,1,17);
//            }
//        });
//        builder.addItemView("全车开", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
//            @Override
//            public void onClick(View view) {
//                airControl2(type,1,34);
//            }
//        });
//        builder.build().show();
//    }
//
//    private void airControl(int type,int param) {
//        OkHttpPresent.airControl(AppSingleton.getInstance().getTerminalNo(),type,param, new OkHttpResultCallBack() {
//            @Override
//            protected void start() {
//                super.start();
//                showLoadingDialog(mContext);
//            }
//
//            @Override
//            protected void onResponse(Response response, String body) throws IOException {
//                LogUtils.e(TAG, "登录成功: " + body);
//                OkHttpBaseResp loginResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
//                if (loginResp != null) {
//                    if (loginResp.isSuccess()) {
//                        if(type == 11)
//                            refreshWindowStatus(param == 1);
//                        else if(type == 17){
////                            refreshSeatStatus(param == 1);
//                        }
//                    } else {    //登录失败
//                        ToastUtils.showShort(requireActivity(), loginResp.getMsg());
//                    }
//                } else {
//                    ToastUtils.showShort(requireActivity(), getString(R.string.request_retry));
//                }
//            }
//
//
//            @Override
//            protected void onFailed(Request request, Exception e) {
//                super.onFailed(request, e);
//                LogUtils.e(TAG, "登录失败: " + e);
//                ToastUtils.showShort(requireActivity(), getString(R.string.request_retry));
//            }
//
//            @Override
//            protected void end() {
//                super.end();
//                hideLoadingDialog();
//            }
//        });
//    }
//    private void airControl2(int type,int param,int angle) {
//        OkHttpPresent.airControl2(AppSingleton.getInstance().getTerminalNo(),type,param,angle, new OkHttpResultCallBack() {
//            @Override
//            protected void start() {
//                super.start();
//                showLoadingDialog(mContext);
//            }
//
//            @Override
//            protected void onResponse(Response response, String body) throws IOException {
//                LogUtils.e(TAG, "登录成功: " + body);
//                OkHttpBaseResp loginResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
//                if (loginResp != null) {
//                    if (loginResp.isSuccess()) {
//                        ToastUtils.showShort(requireActivity(), "发送成功");
//                    } else {    //登录失败
//                        ToastUtils.showShort(requireActivity(), loginResp.getMsg());
//                    }
//                } else {
//                    ToastUtils.showShort(requireActivity(), getString(R.string.request_retry));
//                }
//            }
//
//
//            @Override
//            protected void onFailed(Request request, Exception e) {
//                super.onFailed(request, e);
//                LogUtils.e(TAG, "登录失败: " + e);
//                ToastUtils.showShort(requireActivity(), getString(R.string.request_retry));
//            }
//
//            @Override
//            protected void end() {
//                super.end();
//                hideLoadingDialog();
//            }
//        });
//    }
//
//    /**
//     * 油电控制弹窗
//     */
//    private void showPowerControlBottomSheet() {
//        IosBottomSheetDialog.Builder builder = new IosBottomSheetDialog.Builder(mContext);
//        builder.addItemView("解锁", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
//            @Override
//            public void onClick(View view) {
//                requestPowerControl(Const.COMMAND_LOCK_ON);
//            }
//        });
//        builder.addItemView("锁车", Color.parseColor("#de001f"), false, new IosBottomSheetDialog.OnItemClickListener() {
//            @Override
//            public void onClick(View view) {
//                requestPowerControl(Const.COMMAND_LOCK_OFF);
////                new CommonDialog(mContext, new CommonDialog.OnCloseListener() {
////                    @Override
////                    public void onClick(Dialog dialog, boolean confirm) {
////                        if (confirm) {
////                            requestPowerControl(Const.COMMAND_LOCK_OFF);
////                            dialog.dismiss();
////                        }
////                    }
////                })
////                        .setTitle("确定锁车吗？")
////                        .setPositiveButton("确定")
////                        .setPositiveButtonColor(ContextCompat.getColor(mContext, R.color.white))
////                        .show();
//            }
//        });
//        builder.build().show();
//    }
//
//    /**
//     * 请求断开/恢复油电
//     *
//     * @param command 64-断开油电，65-恢复油电(64/65废弃)；100-关锁，101-开锁；116-关闭车辆，117-启动车辆
//     */
//    private void requestPowerControl(int command) {
//        OkHttpPresent.changePowerControl(AppSingleton.getInstance().getTerminalNo(), command, new OkHttpResultCallBack() {
//
//            @Override
//            protected void start() {
//                super.start();
//                showLoadingDialog(mContext);
//            }
//
//            @Override
//            protected void onResponse(Response response, String body) throws IOException {
//                LogUtils.e(TAG, "车辆控制成功：" + body + ", command = " + command);
//                OkHttpBaseResp resp = JsonUtils.parseT(body, OkHttpBaseResp.class);
//                if (resp != null) {
//                    if (resp.isSuccess()) {
//                        if (command == Const.COMMAND_ACC_ON) {   // 启动车辆
//                            refreshAccStatus(true);
//                        } else if (command == Const.COMMAND_ACC_OFF) {    // 关闭车辆
//                            refreshAccStatus(false);
//                        } else if(command == Const.COMMAND_LOCK_ON){
//                            refreshLockStatus(true);
//                        } else if(command == Const.COMMAND_LOCK_OFF){
//                            refreshLockStatus(false);
//                        }
//                        ToastUtils.showShort(mContext, getString(R.string.do_success));
//                    } else {
//                        ToastUtils.showShort(mContext, resp.getMsg());
//                    }
//                } else {
//                    ToastUtils.showShort(mContext, getString(R.string.request_retry));
//                }
//            }
//
//            @Override
//            protected void onFailed(Request request, Exception e) {
//                super.onFailed(request, e);
//                LogUtils.e(TAG, "油电控制失败：" + e);
//                ToastUtils.showShort(mContext, getString(R.string.request_retry));
//            }
//
//            @Override
//            protected void end() {
//                super.end();
//                hideLoadingDialog();
//            }
//        });
//    }
//
//    /**
//     * 导航寻车弹窗
//     */
//    private void showVehicleNavigationSheet() {
//        IosBottomSheetDialog.Builder builder = new IosBottomSheetDialog.Builder(mContext);
//        builder.addItemView("高德地图", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
//            @Override
//            public void onClick(View view) {
//                toGaoDeNavi();
//            }
//        });
//        builder.addItemView("百度地图", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
//            @Override
//            public void onClick(View view) {
//                toBaiDuNavi();
//            }
//        });
//        builder.build().show();
//    }
//
//    /**
//     * 高德导航
//     */
//    public void toGaoDeNavi() {
//        if (mLatLngTerminal == null) {
//            return;
//        }
//        try {
//            String appName = AppUtils.getAppName(mContext);
//            Intent intent = new Intent("android.intent.action.VIEW",
//                    Uri.parse("amapuri://route/plan/?sourceApplication=" + appName + "&dlat=" + mLatLngTerminal.latitude + "&dlon=" + mLatLngTerminal.longitude + "&dev=0&t=0"));
//            mContext.startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            e.printStackTrace();
//            ToastUtils.showShort(mContext, "请安装高德地图");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 百度导航
//     */
//    public void toBaiDuNavi() {
//        if (mLatLngTerminal == null) {
//            return;
//        }
//        try {
//            String appPackageName = AppUtils.getPackageName(mContext);
//            Intent intent = new Intent("android.intent.action.VIEW",
//                    Uri.parse("baidumap://map/direction?destination=" + mLatLngTerminal.latitude + "," + mLatLngTerminal.longitude + "&coord_type=gcj02&mode=driving&src=" + appPackageName));
//            mContext.startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            e.printStackTrace();
//            ToastUtils.showShort(mContext, "请安装百度地图");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 获取总里程数
//     */
//    private void getRidingAllDistance() {
//        OkHttpPresent.getRidingAllDistance(AppSingleton.getInstance().getTerminalNo(), new OkHttpResultCallBack() {
//            @Override
//            protected void onResponse(Response response, String body) throws IOException {
//                BaseHttpResp<Double> resp = JsonUtils.parseObject(body, new TypeReference<BaseHttpResp<Double>>() {
//                });
//                if (resp != null && resp.getData() != null) {
//                    sumRidingDistanceTv.setText(String.format("%.1f",resp.getData()));
//                    sumRidingDistanceLayout.setVisibility(View.VISIBLE);
//                    //查询上一次里程
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            String result = OkHttpPresent.getRidingRecordListSync2(1,1,AppSingleton.getInstance().getTerminalNo());
//                            LogUtils.e(TAG, "行程记录请求成功: " + result);
//                            RidingDataResp ridingDataResp = JsonUtils.parseT(result, RidingDataResp.class);
//                            if(ridingDataResp == null)
//                                return;
//                            if (isAdded() && getActivity() != null) {
//                                try {
//                                    requireActivity().runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            RidingDataWithTotal data = ridingDataResp.getData();
//                                            if (data != null && data.getList() != null && data.getList().size() > 0) {
//                                                RidingDataBean ridingDataBean = data.getList().get(0);
//                                                tvLastDistance.setText(String.format("%.1f",ridingDataBean.getDistance()));
//                                            }else {
//                                                tvLastDistance.setText("0.0");
//                                            }
//                                        }
//                                    });
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }).start();
//
//                } else {
//                    sumRidingDistanceLayout.setVisibility(View.GONE);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
//        LogUtils.e("vehicleFragment_onResume","");
//        mMapView.onResume();
//        refreshCurrentTerminal();
////        getRidingAllDistance();
////        startGpsInfoInterval();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
//        mMapView.onPause();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        startGpsInfoInterval();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mDisposable != null) {
//            mDisposable.dispose();
//        }
//    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
//        mMapView.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
//        mMapView.onDestroy();
//    }
//
//    public void updateBle() {
//        // 首先检查系统蓝牙是否开启
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        boolean isBluetoothEnabled = bluetoothAdapter != null && bluetoothAdapter.isEnabled();
//
//        if (!isBluetoothEnabled) {
//            // 系统蓝牙关闭，直接显示断开状态
//            ivBle.setImageResource(R.drawable.ic_bluetooth_gray);
//            return;
//        }
//
//        // 系统蓝牙开启，再检查具体设备连接状态
//        boolean haveConnect = false;
//        List<BleDevice> bleDevices = BleManager.getInstance().getAllConnectedDevice();
//        for (BleDevice bleDevice : bleDevices) {
//            int state = BleManager.getInstance().getConnectState(bleDevice);
//            if (state == BluetoothProfile.STATE_CONNECTED) {
//                haveConnect = true;
//                break;
//            }
//        }
//
//        if(haveConnect){
//            ivBle.setImageResource(R.drawable.ic_bluetooth_blue);
//        }else {
//            ivBle.setImageResource(R.drawable.ic_bluetooth_gray);
//        }
//    }
//
//    public void refreshTheme(String imagePath){
//        Glide.with(mContext).load(imagePath).centerCrop().placeholder(R.drawable.bg_main_vehicle).into(bg);
////        ToastUtils.showShort(mContext,"主题应用成功");
//    }
//
//    public void refreshCurrentTerminal() {
//        getRidingAllDistance();
//        getLatestGpsInfo(true,false);
//        verticalNameTv.setText(TextUtils.isEmpty(AppSingleton.getInstance().getTerminalName())?AppSingleton.getInstance().getTerminalNo():AppSingleton.getInstance().getTerminalName());
//        //根据车型显示
//        TerminalBean currentTerminal = AppSingleton.getInstance().getCurrentTerminal();
//        Glide.with(requireActivity())
//                .load(currentTerminal.getCarPicture())
//                .placeholder(R.drawable.ic_item_vehicle) // 加载中的占位图
//                .error(R.drawable.ic_item_vehicle)
//                .into(ivCar);
//        //有无车控
//        if("1".equals(currentTerminal.getCarControlType())){
//            Drawable drawableLeft = AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_main_control);
//            Drawable drawableRight = AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_enter_arrow);
//            tvControl.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableRight, null); // 使用
//            tvControl.setBackgroundResource(R.drawable.bg_control_radius_shape);
////            llLight.setVisibility(View.VISIBLE);
////            circleMenuPager.addMenuItem(2,new CircleMenuPager.MenuItem(R.drawable.ic_home03,"开启大灯","menu_light"));
//        }else {
//            Drawable drawableLeft = AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_main_control_disable);
//            Drawable drawableRight = AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_enter_arrow);
//            tvControl.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableRight, null); // 使用
//            tvControl.setBackgroundResource(R.drawable.bg_control_disable_radius_shape);
////            llLight.setVisibility(View.GONE);
////            circleMenuPager.removeMenuItemByTag("menu_light");
//        }
//        if("CAN".equals(currentTerminal.getProtocol())){//挪车，车控可用
//            Drawable drawableLeft = AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_main_control2);
//            Drawable drawableRight = AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_enter_arrow);
//            tvControl2.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableRight, null); // 使用
//            tvControl2.setBackgroundResource(R.drawable.bg_control_radius_shape);
//        }else {
//            Drawable drawableLeft = AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_main_control2_disable);
//            Drawable drawableRight = AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_enter_arrow);
//            tvControl2.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableRight, null); // 使用
//            tvControl2.setBackgroundResource(R.drawable.bg_control_disable_radius_shape);
//        }
//        //车况(除了文曲星，E01，G7，剩下的都可用)
//        Drawable drawableLeft = null;
//        Drawable drawableRight = null;
//        switch (currentTerminal.getVehicleModel()){
//            case Const.VEHICLE_MODEL_WENQUXING:
//            case Const.VEHICLE_MODEL_G7NOMI:
//            case Const.VEHICLE_MODEL_E01:
//                drawableLeft = AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_main_control2_disable);
//                drawableRight = AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_enter_arrow);
//                tvControl2.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableRight, null); // 使用
//                tvControl2.setBackgroundResource(R.drawable.bg_control_disable_radius_shape);
//                tvControl2.setEnabled(false);
//                tvControl2.setClickable(false);
//                break;
//            default:
//                drawableLeft = AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_main_control2);
//                drawableRight = AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_enter_arrow);
//                tvControl2.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableRight, null); // 使用
//                tvControl2.setBackgroundResource(R.drawable.bg_control_radius_shape);
//                tvControl2.setEnabled(true);
//                tvControl2.setClickable(true);
//                break;
//        }
//
//
//        updateMenuDynamically();
//        //有无车架号
//        if(TextUtils.isEmpty(currentTerminal.getVin())){
//            startActivity(new Intent(requireActivity(),BindTerminalActivity.class).putExtra("terminalNo",currentTerminal.getTerminalNo()));
//            requireActivity().finish();
//        }
//    }
//
//    private void updateMenuDynamically() {
//        TerminalBean currentTerminal = AppSingleton.getInstance().getCurrentTerminal();
//        List<CircleMenuPager.MenuItem> menuItems = new ArrayList<>();
//
//        // 始终显示的菜单
//        menuItems.add(new CircleMenuPager.MenuItem(R.drawable.ic_home01, null,"设防关", "menu_setting"));
//        menuItems.add(new CircleMenuPager.MenuItem(R.drawable.ic_home02,null, "点击设防", "menu_lock"));
//        // 根据条件动态添加
////        if ("1".equals(currentTerminal.getCarControlType())) {
////            menuItems.add(new CircleMenuPager.MenuItem(R.drawable.ic_home03, null,"开启大灯", "menu_light"));
////        }
//        menuItems.add(new CircleMenuPager.MenuItem(R.drawable.ic_home00, null,"寻车", "menu_find_car"));
//
//        if ("CAN".equals(currentTerminal.getProtocol())) {
////            menuItems.add(new CircleMenuPager.MenuItem(R.drawable.ic_home05, null,"车窗", "menu_window"));
////            menuItems.add(new CircleMenuPager.MenuItem(R.drawable.ic_home06, null,"尾门解锁", "menu_open_door"));
////            menuItems.add(new CircleMenuPager.MenuItem(R.drawable.ic_home07, null,"座椅加热", "menu_heat"));
//        }
//        menuItems.add(new CircleMenuPager.MenuItem(R.drawable.ic_home04, R.drawable.ic_ming_circle, "寻车导航","menu_ming"));
//        if(currentTerminal.getVehicleModel() != Const.VEHICLE_MODEL_WENQUXING &&
//                currentTerminal.getVehicleModel() != Const.VEHICLE_MODEL_E01 &&
//                currentTerminal.getVehicleModel() != Const.VEHICLE_MODEL_G7NOMI &&
//                currentTerminal.getVehicleModel() != Const.VEHICLE_MODEL_T30){
//            menuItems.add(new CircleMenuPager.MenuItem(R.drawable.ic_home09, null, "一键警报","menu_fanglang"));
//            menuItems.add(new CircleMenuPager.MenuItem(R.drawable.ic_home12, null, "伴我回家","menu_peiban"));//伴我回家
//        }
//        if(currentTerminal.getVehicleModel() == Const.VEHICLE_MODEL_QingTing){
//            menuItems.add(new CircleMenuPager.MenuItem(R.drawable.ic_home13, null, "后座开锁","menu_kaisuo"));
//        }
//
////        menuItems.add(new CircleMenuPager.MenuItem(R.drawable.ic_home10, null, "后视镜折叠","menu_zhedie"));
////        menuItems.add(new CircleMenuPager.MenuItem(R.drawable.ic_home11, null, "危险报警灯","menu_danger"));
//        circleMenuPager.setMenuData(menuItems);
//    }
//}
