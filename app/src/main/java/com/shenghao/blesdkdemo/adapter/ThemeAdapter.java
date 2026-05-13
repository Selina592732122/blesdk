package com.shenghao.blesdkdemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.ThemeBean;

import java.util.List;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.AlarmHolder> {
    private Context context;
    private List<ThemeBean> alarmList;
    private OnItemClickListener mOnItemClickListener;

    public ThemeAdapter(Context context, List<ThemeBean> alarmList) {
        this.context = context;
        this.alarmList = alarmList;
    }

    @NonNull
    @Override
    public AlarmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_item_theme, parent, false);
        return new AlarmHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmHolder holder, @SuppressLint("RecyclerView") int position) {
        ThemeBean bean = alarmList.get(position);
        holder.tv.setText(bean.getName());
        Glide.with(context).load(bean.getThemeImage())
                .into(holder.iv);
        if(("1").equals(bean.getIsCurrentTheme())){
            holder.currentTheme.setVisibility(View.VISIBLE);
        }else {
            holder.currentTheme.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(view -> {
            if(mOnItemClickListener != null)
                mOnItemClickListener.onClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return alarmList == null ? 0 : alarmList.size();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    class AlarmHolder extends RecyclerView.ViewHolder {
        TextView tv;
        TextView currentTheme;
        ImageView iv;

        public AlarmHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            tv = itemView.findViewById(R.id.tv);
            currentTheme = itemView.findViewById(R.id.title);
        }
    }
    public interface OnItemClickListener{
        void onClick(int pos);
    }
}
