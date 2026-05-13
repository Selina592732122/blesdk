package com.shenghao.blesdkdemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.RidingDataBean;
import com.shenghao.blesdkdemo.utils.TimeUtils;

import java.util.List;

public class RidingDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<RidingDataBean> ridingDataList;
    private Double ridingAllDistance;
    private OnItemClickListener onItemClickListener;

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    public RidingDataAdapter(Context context, List<RidingDataBean> ridingDataList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.ridingDataList = ridingDataList;
        this.onItemClickListener = onItemClickListener;
    }

    public void setRidingAllDistance(Double ridingAllDistance) {
        this.ridingAllDistance = ridingAllDistance;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_item_riding_distance_sum, parent, false);
            return new RidingAllDistanceHolder(view);
        } else {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_item_riding_data, parent, false);
            return new RidingDataHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof RidingAllDistanceHolder) {
            RidingAllDistanceHolder ridingAllDistanceHolder = (RidingAllDistanceHolder) holder;
            if (ridingAllDistance != null && ridingAllDistance > 0) {
                ridingAllDistanceHolder.ridingAllDistanceLayout.setVisibility(View.VISIBLE);
                ridingAllDistanceHolder.ridingAllDistanceTv.setText(ridingAllDistance + "");
            } else {
                ridingAllDistanceHolder.ridingAllDistanceLayout.setVisibility(View.GONE);
            }
        } else {
            int realPosition = position - 1;
            RidingDataHolder ridingDataHolder = (RidingDataHolder) holder;
            RidingDataBean ridingData = ridingDataList.get(realPosition);
            ridingDataHolder.itemDateTv.setText(TimeUtils.getRidingDisplayDate(ridingData.getStartTime()));
            ridingDataHolder.itemTimeTv.setText(TimeUtils.getRidingDisplayStartTime(ridingData.getStartTime()) + "-" + TimeUtils.getRidingDisplayEndTime(ridingData.getEndTime()));
            ridingDataHolder.itemStartPointTv.setText(ridingData.getStartAddress());
            ridingDataHolder.itemEndPointTv.setText(ridingData.getEndAddress());
//        ridingDataHolder.itemDurationTv.setText(ridingData.getDuration());
//        ridingDataHolder.itemSpeedTv.setText(ridingData.getSpeed());

            if (ridingData.getDistance() > 0) {
                ridingDataHolder.itemDistanceTv.setText(ridingData.getDistance() + "");
                ridingDataHolder.itemDistanceLayout.setVisibility(View.VISIBLE);
            } else {
                ridingDataHolder.itemDistanceLayout.setVisibility(View.GONE);
            }

            ridingDataHolder.itemDateLayout.setVisibility(View.VISIBLE);
            if (realPosition > 0 && TextUtils.equals(TimeUtils.getRidingDisplayDate(ridingData.getStartTime()), TimeUtils.getRidingDisplayDate(ridingDataList.get(realPosition - 1).getStartTime()))) {
                ridingDataHolder.itemDateLayout.setVisibility(View.GONE);
            }

            ridingDataHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(realPosition);
                    }
                }
            });

            ridingDataHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemLongClick(realPosition);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return ridingDataList == null ? 0 : ridingDataList.size() + 1;
    }

    class RidingDataHolder extends RecyclerView.ViewHolder {
        LinearLayout itemDateLayout;
        LinearLayout itemDistanceLayout;
        TextView itemDateTv;
        TextView itemTimeTv;
        TextView itemStartPointTv;
        TextView itemEndPointTv;
        TextView itemDistanceTv;
//        TextView itemDurationTv;
//        TextView itemSpeedTv;

        public RidingDataHolder(@NonNull View itemView) {
            super(itemView);
            itemDateLayout = itemView.findViewById(R.id.itemDateLayout);
            itemDistanceLayout = itemView.findViewById(R.id.itemDistanceLayout);
            itemDateTv = itemView.findViewById(R.id.itemDateTv);
            itemTimeTv = itemView.findViewById(R.id.itemTimeTv);
            itemStartPointTv = itemView.findViewById(R.id.itemStartPointTv);
            itemEndPointTv = itemView.findViewById(R.id.itemEndPointTv);
            itemDistanceTv = itemView.findViewById(R.id.itemDistanceTv);
//            itemDurationTv = itemView.findViewById(R.id.itemDurationTv);
//            itemSpeedTv = itemView.findViewById(R.id.itemSpeedTv);
        }
    }

    class RidingAllDistanceHolder extends RecyclerView.ViewHolder {
        LinearLayout ridingAllDistanceLayout;
        TextView ridingAllDistanceTv;

        public RidingAllDistanceHolder(@NonNull View itemView) {
            super(itemView);
            ridingAllDistanceLayout = itemView.findViewById(R.id.ridingAllDistanceLayout);
            ridingAllDistanceTv = itemView.findViewById(R.id.ridingAllDistanceTv);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onItemLongClick(int position);
    }
}
