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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.VehicleModelBean;

import java.util.List;

public class VehicleModeAdapter extends RecyclerView.Adapter<VehicleModeAdapter.ModeHolder> {
    private Context context;
    private List<VehicleModelBean> mDataList;
    private OnItemClickListener onItemClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION; // 当前选中的位置

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public VehicleModeAdapter(Context context, List<VehicleModelBean> mDataList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ModeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_vehicle_mode_list, parent, false);
        return new ModeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModeHolder holder, @SuppressLint("RecyclerView") int position) {
        VehicleModelBean item = mDataList.get(position);
        holder.bind(item, position == selectedPosition,context);

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            // 更新选中状态
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected); // 刷新之前选中的 item
            notifyItemChanged(selectedPosition); // 刷新当前选中的 item
            if(onItemClickListener != null)
                onItemClickListener.onItemClick(position);
        });
        holder.tvSub.setText(item.getColour());
        if(TextUtils.isEmpty(item.getColour()))
            holder.tvSub.setVisibility(View.GONE);
        else
            holder.tvSub.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public static class ModeHolder extends RecyclerView.ViewHolder {
        TextView rb,tvSub;
        ImageView iv,ivCheck;
        public ModeHolder(@NonNull View itemView) {
            super(itemView);
            rb = itemView.findViewById(R.id.rb);
            tvSub = itemView.findViewById(R.id.tvSub);
            iv = itemView.findViewById(R.id.iv);
            ivCheck = itemView.findViewById(R.id.ivCheck);
        }
        public void bind(VehicleModelBean item, boolean isSelected,Context context) {
            rb.setText(item.getName());
            String url = item.getPicture();
            if(!TextUtils.isEmpty(url)){
                Glide.with(context).load(url).fitCenter().into(iv);
            }else {
                iv.setImageResource(R.drawable.ic_item_vehicle);
            }

            // 根据选中状态更新 UI
            if(isSelected)
                ivCheck.setImageResource(R.drawable.ic_cartype_checked);
            else ivCheck.setImageResource(R.drawable.ic_cartype_unchecked);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
