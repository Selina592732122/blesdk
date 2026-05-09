package com.shenghao.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.shenghao.R;
import com.shenghao.ui.ShareActivity;
import com.shenghao.utils.DensityUtil;

import java.util.List;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder> {
    private List<String> avatars;
    private int maxVisible;
    private Context context;
    
    public AvatarAdapter(Context context, List<String> avatars, int maxVisible) {
        this.context = context;
        this.avatars = avatars;
        this.maxVisible = maxVisible;
    }

    static class AvatarViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView avatar,overflow;
        RelativeLayout container;

        public AvatarViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.iv_avatar);
            overflow = itemView.findViewById(R.id.iv_overflow);
            container = itemView.findViewById(R.id.avatar_container);
        }
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_avatar, parent, false);
        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
        // 获取布局参数
        RecyclerView.LayoutParams layoutParams =
                (RecyclerView.LayoutParams) holder.container.getLayoutParams();
        
        // 设置重叠效果（第一个不重叠）
        if (position == 0) {
            layoutParams.setMarginStart(0);
        } else {
            layoutParams.setMarginStart(-DensityUtil.dip2px(context, 12));
        }
        holder.container.setLayoutParams(layoutParams);
        
        // 提升层级确保正确覆盖
        holder.container.setTranslationZ(position);
        
        // 处理最后一个位置的特殊情况
        if (position == getItemCount() - 1) {
//            holder.avatar.setVisibility(View.INVISIBLE);
            if(position == maxVisible -1){
                //满了也不显示加号
                holder.overflow.setVisibility(View.GONE);
            }else
                holder.overflow.setVisibility(View.VISIBLE);//显示加号
//            holder.overflowText.setText("+" + (avatars.size() - maxVisible + 1));
        } else {
            // 实际项目中用 Glide/Picasso 加载图片
            // Glide.with(context).load(avatars.get(position)).into(holder.avatar);
//            holder.avatar.setVisibility(View.VISIBLE);
            holder.overflow.setVisibility(View.GONE);
        }
        Glide.with(context).load(avatars.get(position)).into(holder.avatar);
        // 点击事件示例
        holder.container.setOnClickListener(v -> {
//            Toast.makeText(context, "Clicked position: " + position, Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, ShareActivity.class));
        });
    }

    @Override
    public int getItemCount() {
        return Math.min(avatars.size(), maxVisible);
    }
}