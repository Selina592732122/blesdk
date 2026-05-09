package com.shenghao.ui;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.OnPermissionInterceptor;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.base.IPermission;
import com.shenghao.R;
import com.shenghao.XXPermissions.PermissionDescription;
import com.shenghao.XXPermissions.PermissionInterceptor;
import com.shenghao.adapter.AddressAdapter;
import com.shenghao.bean.AddressBean;
import com.shenghao.bean.AddressResp;
import com.shenghao.bean.StoreBean;
import com.shenghao.bean.StoreResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.utils.AppUtils;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.StatusBarUtils;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.AddressDialog;
import com.shenghao.widget.EmptyRecyclerView;
import com.shenghao.widget.IosBottomSheetDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class AddressActivity extends BaseActivity {
    private EmptyRecyclerView recyclerView;
    private LinearLayout llShi,llQu;
    private AddressDialog addressShiDialog,addressQuDialog;
    private TextView tvShi,tvQu;
    private List<StoreBean> addressList = new ArrayList<>();
    private List<AddressBean> shiList = new ArrayList<>();
    private List<AddressBean> quList = new ArrayList<>();
    private AddressAdapter addressAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.transparencyBar(this);
        StatusBarUtils.statusBarDarkMode(this);
        setContentView(R.layout.activity_address);
        initViews();
        initList();
        getRegionList();
    }

    private void getStoreList(String cityId) {
        OkHttpPresent.getStoreList(cityId,new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "登录成功: " + body);
                StoreResp storeResp = JsonUtils.parseT(body, StoreResp.class);
                if (storeResp != null) {
                    List<StoreBean> data = storeResp.getData();
                    if (storeResp.isSuccess() && data !=null) {
                       addressList.clear();
                       addressList.addAll(storeResp.getData());
                       addressAdapter.notifyDataSetChanged();
                    } else {    //失败
                        ToastUtils.showShort(AddressActivity.this, storeResp.getMsg());
                    }
                    hideLoadingDialog();
                } else {
                    ToastUtils.showShort(AddressActivity.this, getString(R.string.request_retry));
                    hideLoadingDialog();
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(AddressActivity.this, getString(R.string.request_retry));
                hideLoadingDialog();
            }

        });
    }

    private void getRegionList() {
        OkHttpPresent.getRegionList(new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "登录成功: " + body);
                AddressResp addressResp = JsonUtils.parseT(body, AddressResp.class);
                if (addressResp != null) {
                    List<AddressBean> data = addressResp.getData();
                    if (addressResp.isSuccess() && data !=null) {
                        shiList.clear();
                        shiList.addAll(data);
                        addressShiDialog.setList(shiList);
                        quList.clear();
                        quList.addAll(shiList.get(0).getChildren());
                        addressQuDialog.setList(quList);
                        tvShi.setText(shiList.get(0).getRegionName());
                        tvQu.setText(shiList.get(0).getChildren().get(0).getRegionName());
//                        getStoreList(shiList.get(0).getChildren().get(0).getId()+"");
                        requestLocationPermission();
                    } else {    //失败
                        ToastUtils.showShort(AddressActivity.this, addressResp.getMsg());
                    }
                    hideLoadingDialog();
                } else {
                    ToastUtils.showShort(AddressActivity.this, getString(R.string.request_retry));
                    hideLoadingDialog();
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "登录失败: " + e);
                ToastUtils.showShort(AddressActivity.this, getString(R.string.request_retry));
                hideLoadingDialog();
            }

        });
    }

    //门店列表
    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addressAdapter = new AddressAdapter(this, addressList, new AddressAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos) {
                StoreBean storeBean = addressList.get(pos);
                showNaviSheet(storeBean);
            }
        });
        recyclerView.setAdapter(addressAdapter);
        recyclerView.setEmptyView(findViewById(R.id.tvEmptyAddress));
    }

    @Override
    protected void initViews() {
        super.initViews();
        //设置状态栏高度
        View statusBarView = findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(this, statusBarView);
        addressQuDialog = new AddressDialog(AddressActivity.this,quList, new AddressDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm, int index) {
                if(!confirm) return;
                if(quList.isEmpty()) return;
                tvQu.setText(quList.get(index).getRegionName());
                getStoreList(quList.get(index).getId()+"");
//                Toast.makeText(AddressActivity.this, "" + quList.get(index).getRegionName(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).setDialogCancelable(true);
        addressShiDialog = new AddressDialog(AddressActivity.this,shiList, new AddressDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm, int index) {
                if(!confirm) return;
                if(shiList.isEmpty()) return;
                tvShi.setText(shiList.get(index).getRegionName());
                tvQu.setText(shiList.get(index).getChildren().get(0).getRegionName());
//                Toast.makeText(AddressActivity.this, "" + shiList.get(index).getRegionName(), Toast.LENGTH_SHORT).show();
                quList.clear();
                quList.addAll(shiList.get(index).getChildren());
                addressQuDialog.setList(quList);
                getStoreList(shiList.get(index).getChildren().get(0).getId()+"");
                dialog.dismiss();
            }
        }).setDialogCancelable(true);
        llShi = findViewById(R.id.llShi);
        llQu = findViewById(R.id.llQu);
        tvShi = findViewById(R.id.tvShi);
        tvQu = findViewById(R.id.tvQu);
        recyclerView = findViewById(R.id.recyclerView);
        llShi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressShiDialog.show();
            }
        });
        llQu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressQuDialog.show();
            }
        });
    }

    private void requestLocationPermission() {
        XXPermissions.with(this)
                // 申请单个权限
                .permission(PermissionLists.getAccessFineLocationPermission())
                .permission(PermissionLists.getAccessCoarseLocationPermission())
//                .permission(Permission.ACCESS_FINE_LOCATION)
//                .permission(Permission.ACCESS_COARSE_LOCATION)
                // 设置权限请求拦截器（局部设置）
//                .interceptor(new PermissionInterceptor(getString(R.string.permissionLocation)))
                .interceptor(new PermissionInterceptor())
                .description(new PermissionDescription())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onResult(@NonNull List<IPermission> grantedList, @NonNull List<IPermission> deniedList) {
                        boolean allGranted = deniedList.isEmpty();
                        if (!allGranted) {
                            // 判断请求失败的权限是否被用户勾选了不再询问的选项
                            boolean doNotAskAgain = XXPermissions.isDoNotAskAgainPermissions(AddressActivity.this, deniedList);
                            // 在这里处理权限请求失败的逻辑
                            if (doNotAskAgain) {
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(AddressActivity.this, deniedList);
                            } else {
                                getStoreList(shiList.get(0).getChildren().get(0).getId()+"");
                                ToastUtils.showShort(AddressActivity.this,"获取权限失败");
                            }
                            return;
                        }
                        // 在这里处理权限请求成功的逻辑
                        try {
                            startLocation(); //初始化用户定位
                        } catch (Exception e) {
                            getStoreList(shiList.get(0).getChildren().get(0).getId()+"");
                            throw new RuntimeException(e);
                        }
                    }

//                    @Override
//                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
//                        if (!allGranted) {
//                            ToastUtils.showShort(AddressActivity.this,"获取部分权限成功，但部分权限未正常授予");
//                            return;
//                        }
////                        SPUtils.getInstance().putBoolean(SPUtils.SP_NEED_SHOW_LOCATION_PERMISSION_DIALOG, false);
//                        try {
//                            startLocation(); //初始化用户定位
//                        } catch (Exception e) {
//                            getStoreList(shiList.get(0).getChildren().get(0).getId()+"");
//                            throw new RuntimeException(e);
//                        }
//                    }
//
//                    @Override
//                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
//                        if (doNotAskAgain) {
//                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                            XXPermissions.startPermissionActivity(AddressActivity.this, permissions);
//                        } else {
//                            getStoreList(shiList.get(0).getChildren().get(0).getId()+"");
//                            ToastUtils.showShort(AddressActivity.this,"获取权限失败");
//                        }
//                    }
                });
    }

    private void startLocation() throws Exception {
        // 初始化定位客户端
        AMapLocationClient locationClient = new AMapLocationClient(getApplicationContext());

        // 设置定位参数
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy); // 高精度模式
        locationOption.setOnceLocation(true); // 只定位一次
        locationOption.setNeedAddress(true); // 返回地址信息
        locationClient.setLocationOption(locationOption);

        // 设置定位监听器
        locationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                    // 定位成功，获取省市信息
                    String province = aMapLocation.getProvince(); // 获取省份
                    String city = aMapLocation.getCity(); // 获取城市

                    int parentId = getParentId(province);
                    int childId = getChildId(parentId, city);
                    tvShi.setText(shiList.get(parentId).getRegionName());
                    tvQu.setText(shiList.get(parentId).getChildren().get(childId).getRegionName());
                    getStoreList(shiList.get(parentId).getChildren().get(childId).getId()+"");
                    quList.clear();
                    quList.addAll(shiList.get(parentId).getChildren());
                    addressQuDialog.setList(quList);
                    Log.d("LocationInfo", "Province: " + province + ", City: " + city);
                } else {
                    // 定位失败
                    Log.e("LocationError", "Error Code: " + aMapLocation.getErrorCode() + ", Error Info: " + aMapLocation.getErrorInfo());
                }
            }
        });

        // 启动定位
        locationClient.startLocation();
    }

    private int getChildId(int index,String city) {
        List<AddressBean> children = shiList.get(index).getChildren();
        for (int i = 0; i < children.size(); i++) {
            AddressBean bean = children.get(i);
            if(bean.getRegionName().equals(city))
                return i;
        }
        return 0;
    }

    private int getParentId(String province) {
        for (int i = 0; i < shiList.size(); i++) {
            AddressBean bean = shiList.get(i);
            if(bean.getRegionName().equals(province))
                return i;
        }
        return 0;
    }

    /**
     * 导航寻车弹窗
     */
    private void showNaviSheet(StoreBean storeBean) {
        IosBottomSheetDialog.Builder builder = new IosBottomSheetDialog.Builder(this);
        builder.addItemView("高德地图", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
            @Override
            public void onClick(View view) {
                toGaoDeNavi(storeBean);
            }
        });
        builder.addItemView("百度地图", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
            @Override
            public void onClick(View view) {
                toBaiDuNavi(storeBean);
            }
        });
        builder.build().show();
    }

    /**
     * 高德导航
     */
    public void toGaoDeNavi(StoreBean storeBean) {
        if (storeBean == null) {
            return;
        }
        try {
            String appName = AppUtils.getAppName(this);
            Intent intent = new Intent("android.intent.action.VIEW",
                    Uri.parse("amapuri://route/plan/?sourceApplication=" + appName + "&dlat=" + storeBean.getLat() + "&dlon=" + storeBean.getLog() + "&dev=0&t=0"));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastUtils.showShort(this, "请安装高德地图");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 百度导航
     */
    public void toBaiDuNavi(StoreBean storeBean) {
        if (storeBean == null) {
            return;
        }
        //,
        try {
            String appPackageName = AppUtils.getPackageName(this);
            Intent intent = new Intent("android.intent.action.VIEW",
                    Uri.parse("baidumap://map/direction?destination=" +storeBean.getLat() + "," + storeBean.getLog()+ "&coord_type=gcj02&mode=driving&src=" + appPackageName));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastUtils.showShort(this, "请安装百度地图");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
