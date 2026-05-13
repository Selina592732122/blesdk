package com.shenghao.blesdkdemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.GeoFenceBean;
import com.shenghao.blesdkdemo.constant.Const;

import java.util.List;

/**
 * 电子围栏adapter
 */
public class GeoFenceAdapter extends RecyclerView.Adapter<GeoFenceAdapter.GeoFenceHolder> {
    private Context context;
    private List<GeoFenceBean> geoFenceList;
    private OnItemClickListener onItemClickListener;

    public GeoFenceAdapter(Context context, List<GeoFenceBean> geoFenceList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.geoFenceList = geoFenceList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public GeoFenceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_item_geofence, parent, false);
        return new GeoFenceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GeoFenceHolder holder, @SuppressLint("RecyclerView") int position) {
        GeoFenceBean geoFenceBean = geoFenceList.get(position);
        holder.itemFenceNameTv.setText(geoFenceBean.getFenceName());
        if (TextUtils.equals(Const.TYPE_GEOFENCE_CIRCULAR, geoFenceBean.getFenceType())) {    //圆形围栏
            holder.itemFenceTypeTv.setText("圆形围栏");
            holder.itemFenceIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_geofence_circle));
        } else { //自定义围栏
            holder.itemFenceTypeTv.setText("自定义围栏");
            holder.itemFenceIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_geofence_polygon));
        }

        if (geoFenceBean.getRadius() == 0) {
            holder.itemFenceRadiusTv.setVisibility(View.GONE);
        } else {
            holder.itemFenceRadiusTv.setVisibility(View.VISIBLE);
            holder.itemFenceRadiusTv.setText(geoFenceBean.getRadius() * 2 + "m");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemLongClick(position);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return geoFenceList == null ? 0 : geoFenceList.size();
    }

    class GeoFenceHolder extends RecyclerView.ViewHolder {
        TextView itemFenceNameTv;
        ImageView itemFenceIcon;
        TextView itemFenceTypeTv;
        TextView itemFenceRadiusTv;

        public GeoFenceHolder(@NonNull View itemView) {
            super(itemView);
            itemFenceNameTv = itemView.findViewById(R.id.itemFenceNameTv);
            itemFenceIcon = itemView.findViewById(R.id.itemFenceIcon);
            itemFenceTypeTv = itemView.findViewById(R.id.itemFenceTypeTv);
            itemFenceRadiusTv = itemView.findViewById(R.id.itemFenceRadiusTv);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }
}
