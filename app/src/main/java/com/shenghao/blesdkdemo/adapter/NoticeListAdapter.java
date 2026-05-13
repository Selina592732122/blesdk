package com.shenghao.blesdkdemo.adapter;


import static com.shenghao.blesdkdemo.utils.TimeUtils.PATTERN_04;
import static com.shenghao.blesdkdemo.utils.TimeUtils.PATTERN_08;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.NoticeData;
import com.shenghao.blesdkdemo.constant.Const;
import com.shenghao.blesdkdemo.utils.TimeUtils;

import java.util.List;

public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.NoticeHolder> {
    private Context context;
    private List<NoticeData> noticeDataList;
    private OnItemClickListener onItemClickListener;

    public NoticeListAdapter(Context context, List<NoticeData> noticeDataList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.noticeDataList = noticeDataList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public NoticeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_item_notice_list, parent, false);
        return new NoticeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeHolder holder, @SuppressLint("RecyclerView") int position) {
        NoticeData noticeData = noticeDataList.get(position);
        holder.itemNoticeTitleTv.setText(noticeData.getTitle());
        holder.itemNoticeContentTv.setText(noticeData.getContent());

        holder.itemNoticeDateTv.setVisibility(View.VISIBLE);
        if (position > 0 && TextUtils.equals(TimeUtils.getDateToString(noticeData.getNoticeTimestamp(), PATTERN_04),
                TimeUtils.getDateToString(noticeDataList.get(position - 1).getNoticeTimestamp(), PATTERN_04))) {
            holder.itemNoticeDateTv.setVisibility(View.GONE);
        } else {
            holder.itemNoticeDateTv.setText(TimeUtils.getDateToString(noticeData.getNoticeTimestamp(), PATTERN_04));
            holder.itemNoticeDateTv.setVisibility(View.GONE);
        }

        holder.itemNoticeTimeTv.setText(TimeUtils.getDateToString(noticeData.getNoticeTimestamp(), PATTERN_08));
        if (noticeData.hasRead()) {
            holder.itemNoticeRedDot.setVisibility(View.GONE);
        } else {
            holder.itemNoticeRedDot.setVisibility(View.VISIBLE);
        }

        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });

//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (onItemClickListener != null) {
//                    onItemClickListener.onItemLongClick(position);
//                }
//                return false;
//            }
//        });
        holder.llDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(position);
                if (onItemClickListener != null) {
                    onItemClickListener.onItemLongClick(position);
                }
            }
        });

        if (TextUtils.equals(noticeData.getType(), Const.NOTICE_TYPE_MOVE_ALARM)) { //震动告警
            holder.itemNoticeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_notify_power_shake));
        } else if (TextUtils.equals(noticeData.getType(), Const.NOTICE_TYPE_EXTERNAL_POWER_ALARM)) {    //电瓶被拆告警
            holder.itemNoticeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_notify_power_remove));
        } else if (TextUtils.equals(noticeData.getType(), Const.NOTICE_TYPE_LOW_VOLTAGE_ALARM)) {   //低电量告警
            holder.itemNoticeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_notify_power_low));
        } else if (TextUtils.equals(noticeData.getType(), Const.NOTICE_TYPE_HIGH_VOLTAGE_ALARM)) {  //高电压告警
            holder.itemNoticeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_notify_power_max));
        } else {    //其他告警（电子围栏告警）
            holder.itemNoticeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_notify_msg_default));
        }
    }

    @Override
    public int getItemCount() {
        return noticeDataList == null ? 0 : noticeDataList.size();
    }

    class NoticeHolder extends RecyclerView.ViewHolder {
        TextView itemNoticeTitleTv;
        TextView itemNoticeDateTv;
        TextView itemNoticeTimeTv;
        TextView itemNoticeContentTv;
        View itemNoticeRedDot;
        ImageView itemNoticeIcon;
        LinearLayout llDel;
        LinearLayout llItem;

        public NoticeHolder(@NonNull View itemView) {
            super(itemView);
            itemNoticeTitleTv = itemView.findViewById(R.id.itemNoticeTitleTv);
            itemNoticeDateTv = itemView.findViewById(R.id.itemNoticeDateTv);
            itemNoticeTimeTv = itemView.findViewById(R.id.itemNoticeTimeTv);
            itemNoticeContentTv = itemView.findViewById(R.id.itemNoticeContentTv);
            itemNoticeRedDot = itemView.findViewById(R.id.itemNoticeRedDot);
            itemNoticeIcon = itemView.findViewById(R.id.itemNoticeIcon);
            llDel = itemView.findViewById(R.id.llDel);
            llItem = itemView.findViewById(R.id.llItem);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onItemLongClick(int position);
    }
}
