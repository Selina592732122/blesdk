package com.shenghao.blesdkdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.ShareUser;
import com.shenghao.blesdkdemo.ui.ShareUserActivity;
import com.shenghao.blesdkdemo.utility.AppSingleton;

import java.util.List;

public class ShareUserAdapter extends RecyclerView.Adapter<ShareUserAdapter.ViewHolder> {
    private List<ShareUser> dataList;
    private Context context;
    public ShareUserAdapter(Context context, List<ShareUser> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share_user_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShareUser item = dataList.get(position);
        holder.tvUser.setText(item.getPhone());
        Glide.with(context)
                .load(item.getAvatar())
                .placeholder(R.drawable.ic_user_default_portrait) // 加载中的占位图
                .error(R.drawable.ic_user_default_portrait)
                .into(holder.ivUser);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int shareStatus = AppSingleton.getInstance().getCurrentTerminal().getShareStatus();
                if(shareStatus == 2)
                    return;
                context.startActivity(new Intent(context, ShareUserActivity.class).putExtra("ShareUser",item));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser;
        ImageView ivUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvUser);
            ivUser = itemView.findViewById(R.id.ivUser);
        }
    }

}