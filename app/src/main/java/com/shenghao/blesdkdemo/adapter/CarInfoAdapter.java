package com.shenghao.blesdkdemo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.utils.TextViewTintUtils;

import java.util.List;

public class CarInfoAdapter extends RecyclerView.Adapter<CarInfoAdapter.ViewHolder> {
    private List<CarInfo> dataList;
    private Context context;
    public CarInfoAdapter(Context context,List<CarInfo> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car_info_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CarInfo item = dataList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvStatus.setText(item.getStatus() == 1? "正常":"异常");
        holder.tvStatus.setTextColor(item.getStatus() == 1? Color.parseColor("#FF343A56"):Color.parseColor("#FFDA3838"));
        TextViewTintUtils.setDrawableRightTint(holder.tvName,item.getStatus() == 1? Color.parseColor("#FF343A56"):Color.parseColor("#FFDA3838"));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }

    public static class CarInfo{
        private String name;
        private int status;

        public CarInfo(String name, int status) {
            this.name = name;
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}