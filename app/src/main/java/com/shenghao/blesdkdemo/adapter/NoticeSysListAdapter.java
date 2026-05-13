package com.shenghao.blesdkdemo.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.SysNotice;

import java.util.List;

public class NoticeSysListAdapter extends RecyclerView.Adapter<NoticeSysListAdapter.NoticeHolder> {
    private Context context;
    private List<SysNotice> noticeDataList;
    private OnItemClickListener onItemClickListener;

    public NoticeSysListAdapter(Context context, List<SysNotice> noticeDataList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.noticeDataList = noticeDataList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public NoticeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_item_notice_sys_list, parent, false);
        return new NoticeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeHolder holder, @SuppressLint("RecyclerView") int position) {
        SysNotice noticeData = noticeDataList.get(position);
        holder.itemNoticeTitleTv.setText(noticeData.getTitle());
        holder.itemNoticeContentTv.setText(noticeData.getContent());

        holder.itemNoticeTimeTv.setText(noticeData.getSysCreated());

        if(noticeData.getStatus() == 2){//状态（0-拒绝 1-同意 2-未操作）
            holder.llBtn.setVisibility(View.VISIBLE);
            holder.llResult.setVisibility(View.GONE);
        }else {
            holder.llBtn.setVisibility(View.GONE);
            holder.llResult.setVisibility(View.VISIBLE);
            if(noticeData.getStatus() == 1)
                holder.tvResult.setText(context.getString(R.string.has_agree));
            else
                holder.tvResult.setText(context.getString(R.string.has_reject));
        }

        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });

        holder.tvAgree.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onAgree(position);
            }
        });
        holder.tvReject.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onReject(position);
            }
        });
        holder.llDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(position);
                if (onItemClickListener != null) {
                    onItemClickListener.onItemLongClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return noticeDataList == null ? 0 : noticeDataList.size();
    }

    class NoticeHolder extends RecyclerView.ViewHolder {
        TextView itemNoticeTitleTv;
        TextView itemNoticeTimeTv;
        TextView itemNoticeContentTv;
        LinearLayout llDel;
        LinearLayout llItem,llBtn,llResult;
        TextView tvAgree,tvReject,tvResult;
        public NoticeHolder(@NonNull View itemView) {
            super(itemView);
            itemNoticeTitleTv = itemView.findViewById(R.id.itemNoticeTitleTv);
            itemNoticeTimeTv = itemView.findViewById(R.id.itemNoticeTimeTv);
            itemNoticeContentTv = itemView.findViewById(R.id.itemNoticeContentTv);
            tvReject = itemView.findViewById(R.id.tvReject);
            tvAgree = itemView.findViewById(R.id.tvAgree);
            tvResult = itemView.findViewById(R.id.tvResult);
            llDel = itemView.findViewById(R.id.llDel);
            llItem = itemView.findViewById(R.id.llItem);
            llBtn = itemView.findViewById(R.id.llBtn);
            llResult = itemView.findViewById(R.id.llResult);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onItemLongClick(int position);

        void onAgree(int pos);
        void onReject(int pos);
    }
}
