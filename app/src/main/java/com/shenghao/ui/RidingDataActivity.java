package com.shenghao.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.TypeReference;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.shenghao.R;
import com.shenghao.adapter.RidingDataAdapter;
import com.shenghao.bean.BaseHttpResp;
import com.shenghao.bean.RidingDataBean;
import com.shenghao.bean.RidingDataResp;
import com.shenghao.bean.RidingDataWithTotal;
import com.shenghao.okhttp.OkHttpBaseResp;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.present.OkHttpPresent;
import com.shenghao.ui.BaseActivity;
import com.shenghao.utility.AppSingleton;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.LogUtils;
import com.shenghao.utils.Redirect;
import com.shenghao.utils.ToastUtils;
import com.shenghao.widget.CommonDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 骑行记录
 */
public class RidingDataActivity extends BaseActivity {
    public static final int PAGE_SIZE = 20;
    private View emptyDataLayout;
    private RecyclerView ridingDataRv;
    private SmartRefreshLayout refreshLayout;

    private RidingDataAdapter ridingDataAdapter;
    private List<RidingDataBean> ridingDataList = new ArrayList<>();

    private int ridingStartId;  //行程startId，用于下一页行程记录请求
    private GeocodeSearch geocoderSearch = null;
    private int pageNum = 1;
    private int total;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riding_data);
        initViews();
        initMap();
        refreshLayout.autoRefresh(1);
    }

    @Override
    protected void initViews() {
        super.initViews();
        emptyDataLayout = findViewById(R.id.emptyDataLayout);
        ridingDataRv = findViewById(R.id.ridingDataRv);
        refreshLayout = findViewById(R.id.refreshLayout);
        ridingDataAdapter = new RidingDataAdapter(this, ridingDataList, new RidingDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(ridingDataList.isEmpty())//预防刷新时候立马点击
                    return;
                Redirect.startRidingDataDetailActivity(RidingDataActivity.this, ridingDataList.get(position));
            }

            @Override
            public void onItemLongClick(int position) {
                showDeleteDialog(position, ridingDataList.get(position).getId());
            }
        });
        ridingDataRv.setLayoutManager(new LinearLayoutManager(this));
        ridingDataRv.setAdapter(ridingDataAdapter);

        refreshLayout.setEnableOverScrollBounce(false);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        refreshLayout.setRefreshHeader(new MaterialHeader(this).setColorSchemeResources(R.color.theme_orange));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                LogUtils.e(TAG, "onRefresh: 正在刷新！");
                ridingStartId = 0;
                ridingDataList.clear();
                pageNum = 1;
                getRidingData(pageNum,PAGE_SIZE);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                LogUtils.e(TAG, "onLoadMore: 正在加载更多！");
//                if (ridingStartId > 0) {
                getRidingData(++pageNum,PAGE_SIZE);
//                }
            }
        });
    }

    private void initMap() {
        try {
            geocoderSearch = new GeocodeSearch(this);
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    private void getRidingData(int pageNum,int pageSize) {
        Observable.create(new ObservableOnSubscribe<List<RidingDataBean>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<RidingDataBean>> emitter) throws Exception {
                        if (pageNum == 1) { //每次刷新，请求总里程数
                            String allDistanceResult = OkHttpPresent.getRidingAllDistanceSync(AppSingleton.getInstance().getTerminalNo());
                            BaseHttpResp<Double> resp = JsonUtils.parseObject(allDistanceResult, new TypeReference<BaseHttpResp<Double>>() {
                            });
                            if (resp != null && resp.getData() != null) {
                                ridingDataAdapter.setRidingAllDistance(resp.getData());
                            }
                        }
                        //getRidingRecordListSync2     pageNum   页数      pageSize    条数
                        String result = OkHttpPresent.getRidingRecordListSync2(pageNum,pageSize,AppSingleton.getInstance().getTerminalNo());
                        LogUtils.e(TAG, "行程记录请求成功: " + result);
                        RidingDataResp ridingDataResp = JsonUtils.parseT(result, RidingDataResp.class);

                        if (ridingDataResp != null && ridingDataResp.isSuccess()) {
                            List<RidingDataBean> ridingDataList = new ArrayList<>();
                            try {
                                if (geocoderSearch == null) {
                                    geocoderSearch = new GeocodeSearch(RidingDataActivity.this);
                                }
                                RidingDataWithTotal data = ridingDataResp.getData();
                                if (data != null && data.getList() != null && data.getList().size() > 0) {
                                    total = data.getTotal();
                                    for (RidingDataBean ridingDataBean : data.getList()) {
                                        // 服务器没有返回地理位置信息，则请求高德逆地理编码接口
                                        if (TextUtils.isEmpty(ridingDataBean.getStartAddress()) || TextUtils.isEmpty(ridingDataBean.getEndAddress())) {
                                            // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                                            RegeocodeQuery queryStart = new RegeocodeQuery(new LatLonPoint(ridingDataBean.getStartLat(), ridingDataBean.getStartLng()), 200, GeocodeSearch.AMAP);
                                            RegeocodeQuery queryEnd = new RegeocodeQuery(new LatLonPoint(ridingDataBean.getEndLat(), ridingDataBean.getEndLng()), 200, GeocodeSearch.AMAP);
                                            RegeocodeAddress startAddress = geocoderSearch.getFromLocation(queryStart);
                                            RegeocodeAddress endAddress = geocoderSearch.getFromLocation(queryEnd);
                                            ridingDataBean.setStartAddress(startAddress.getFormatAddress());
                                            ridingDataBean.setEndAddress(endAddress.getFormatAddress());
                                        }
                                        ridingDataList.add(ridingDataBean);
                                    }
                                }
                            } catch (AMapException e) {
                                emitter.onError(e);
                            }
                            emitter.onNext(ridingDataList);
                        } else {
                            emitter.onNext(new ArrayList<>());
                        }
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<RidingDataBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<RidingDataBean> result) {
                        if (result != null) {
                            if (result.size() == 0) {
                                emptyDataLayout.setVisibility(View.VISIBLE);
                                refreshLayout.setVisibility(View.GONE);
                            } else {
                                emptyDataLayout.setVisibility(View.GONE);
                                refreshLayout.setVisibility(View.VISIBLE);
                                ridingDataList.addAll(result);
                                ridingDataAdapter.notifyDataSetChanged();
                                ridingStartId = result.get(result.size() - 1).getStartId(); //记录当前页最小的行程startId，用于下一页行程请求
                                if (result.size() < PAGE_SIZE || ridingDataList.size() == total) {    //不满一页说明所有数据请求完毕
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                }
                            }
                        } else {
                            emptyDataLayout.setVisibility(View.VISIBLE);
                            refreshLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        finishRefreshAndLoadMoreUI();
                        emptyDataLayout.setVisibility(View.VISIBLE);
                        refreshLayout.setVisibility(View.GONE);
                        ToastUtils.showShort(RidingDataActivity.this, getString(R.string.request_retry));
                    }

                    @Override
                    public void onComplete() {
                        finishRefreshAndLoadMoreUI();
                    }
                });

    }
//    private void getRidingData2(int startId) {
//        Observable.create(new ObservableOnSubscribe<List<RidingDataBean>>() {
//                    @Override
//                    public void subscribe(ObservableEmitter<List<RidingDataBean>> emitter) throws Exception {
//                        if (startId == Integer.MAX_VALUE) { //每次刷新，请求总里程数
//                            String allDistanceResult = OkHttpPresent.getRidingAllDistanceSync(AppSingleton.getInstance().getTerminalNo());
//                            BaseHttpResp<Double> resp = JsonUtils.parseObject(allDistanceResult, new TypeReference<BaseHttpResp<Double>>() {
//                            });
//                            if (resp != null && resp.getData() != null) {
//                                ridingDataAdapter.setRidingAllDistance(resp.getData());
//                            }
//                        }
//
//                        //getRidingRecordListSync2     pageNum   页数      pageSize    条数
//                        String result = OkHttpPresent.getRidingRecordListSync(startId, AppSingleton.getInstance().getTerminalNo());
//                        LogUtils.e(TAG, "行程记录请求成功: " + result);
//                        RidingDataResp ridingDataResp = JsonUtils.parseT(result, RidingDataResp.class);
//                        if (ridingDataResp != null && ridingDataResp.isSuccess()) {
//                            List<RidingDataBean> ridingDataList = new ArrayList<>();
//                            try {
//                                if (geocoderSearch == null) {
//                                    geocoderSearch = new GeocodeSearch(RidingDataActivity.this);
//                                }
//                                if (ridingDataResp.getData() != null && ridingDataResp.getData().size() > 0) {
//                                    for (RidingDataBean ridingDataBean : ridingDataResp.getData()) {
//                                        // 服务器没有返回地理位置信息，则请求高德逆地理编码接口
//                                        if (TextUtils.isEmpty(ridingDataBean.getStartAddress()) || TextUtils.isEmpty(ridingDataBean.getEndAddress())) {
//                                            // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//                                            RegeocodeQuery queryStart = new RegeocodeQuery(new LatLonPoint(ridingDataBean.getStartLat(), ridingDataBean.getStartLng()), 200, GeocodeSearch.AMAP);
//                                            RegeocodeQuery queryEnd = new RegeocodeQuery(new LatLonPoint(ridingDataBean.getEndLat(), ridingDataBean.getEndLng()), 200, GeocodeSearch.AMAP);
//                                            RegeocodeAddress startAddress = geocoderSearch.getFromLocation(queryStart);
//                                            RegeocodeAddress endAddress = geocoderSearch.getFromLocation(queryEnd);
//                                            ridingDataBean.setStartAddress(startAddress.getFormatAddress());
//                                            ridingDataBean.setEndAddress(endAddress.getFormatAddress());
//                                        }
//                                        ridingDataList.add(ridingDataBean);
//                                    }
//                                }
//                            } catch (AMapException e) {
//                                emitter.onError(e);
//                            }
//                            emitter.onNext(ridingDataList);
//                        } else {
//                            emitter.onNext(new ArrayList<>());
//                        }
//                        emitter.onComplete();
//                    }
//                }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<List<RidingDataBean>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                    }
//
//                    @Override
//                    public void onNext(List<RidingDataBean> result) {
//                        if (result != null) {
//                            if (result.size() == 0) {
//                                emptyDataLayout.setVisibility(View.VISIBLE);
//                                refreshLayout.setVisibility(View.GONE);
//                            } else {
//                                emptyDataLayout.setVisibility(View.GONE);
//                                refreshLayout.setVisibility(View.VISIBLE);
//                                ridingDataList.addAll(result);
//                                ridingDataAdapter.notifyDataSetChanged();
//                                ridingStartId = result.get(result.size() - 1).getStartId(); //记录当前页最小的行程startId，用于下一页行程请求
//                                if (result.size() < PAGE_SIZE) {    //不满一页说明所有数据请求完毕
//                                    refreshLayout.finishLoadMoreWithNoMoreData();
//                                }
//                            }
//                        } else {
//                            emptyDataLayout.setVisibility(View.VISIBLE);
//                            refreshLayout.setVisibility(View.GONE);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        finishRefreshAndLoadMoreUI();
//                        emptyDataLayout.setVisibility(View.VISIBLE);
//                        refreshLayout.setVisibility(View.GONE);
//                        ToastUtils.showShort(RidingDataActivity.this, getString(R.string.request_retry));
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        finishRefreshAndLoadMoreUI();
//                    }
//                });
//
//    }

    private void finishRefreshAndLoadMoreUI() {
        if (refreshLayout != null) {
            if (refreshLayout.isRefreshing()) {
                refreshLayout.finishRefresh();
            }
            if (refreshLayout.isLoading()) {
                refreshLayout.finishLoadMore();
            }
        }
    }

    /**
     * 删除弹窗
     */
    private void showDeleteDialog(int position,int id) {
        new CommonDialog(RidingDataActivity.this, new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {  //确定
                    removeRidingRecord(position, id);
                    dialog.dismiss();
                }
            }
        })
                .setTitle("确定删除该骑行记录吗？")
                .setPositiveButton("删除")
                .setPositiveButtonColor(ContextCompat.getColor(RidingDataActivity.this, R.color.white))
                .show();
    }

    /**
     * 删除骑行记录
     */
    private void removeRidingRecord(int position, int id) {
        OkHttpPresent.removeRidingRecord(AppSingleton.getInstance().getTerminalNo(), id, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "删除围栏成功：" + body);
                OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                if (baseResp != null) {
                    if (baseResp.isSuccess()) {
                        ridingDataList.remove(position);
                        ridingDataAdapter.notifyDataSetChanged();
                        ToastUtils.showShort(RidingDataActivity.this, "删除成功");
                    } else {
                        ToastUtils.showShort(RidingDataActivity.this, baseResp.getMsg());
                    }
                } else {
                    ToastUtils.showShort(RidingDataActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                ToastUtils.showShort(RidingDataActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

}
