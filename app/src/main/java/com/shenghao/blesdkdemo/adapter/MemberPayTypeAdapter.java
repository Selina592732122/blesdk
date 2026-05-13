package com.shenghao.blesdkdemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.bean.MemberPayType;

import java.util.List;

/**
 * 会员套餐adapter
 */
public class MemberPayTypeAdapter extends RecyclerView.Adapter<MemberPayTypeAdapter.MemberPayTypeHolder> {
    private Context context;
    private List<MemberPayType> memberPayTypeList;
    private OnItemClickListener onItemClickListener;

    public MemberPayTypeAdapter(Context context, List<MemberPayType> memberPayTypeList) {
        this.context = context;
        this.memberPayTypeList = memberPayTypeList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MemberPayTypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_item_member_pay_type, parent, false);
        return new MemberPayTypeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberPayTypeHolder holder, @SuppressLint("RecyclerView") int position) {
        MemberPayType memberPayType = memberPayTypeList.get(position);
        holder.payTimeTv.setText(memberPayType.getDesc());
        holder.payAmountTv.setText(memberPayType.getDisplayAmount());
        holder.payOriginalAmountTv.setText("原价 ¥"+memberPayType.getDisplayOriginalAmount());
        holder.payOriginalAmountTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        if (memberPayType.isSelected()) {
            holder.payAmountYuanTv.setTextColor(Color.parseColor("#FFE48D20"));
            holder.payAmountTv.setTextColor(Color.parseColor("#FFE48D20"));
            holder.payItemLayout.setSelected(true);
        } else {
            holder.payAmountYuanTv.setTextColor(Color.parseColor("#FF181818"));
            holder.payAmountTv.setTextColor(Color.parseColor("#FF181818"));
            holder.payItemLayout.setSelected(false);
        }
        if(position == 2){
            holder.tvTop.setVisibility(View.VISIBLE);
        }else {
            holder.tvTop.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberPayTypeList == null ? 0 : memberPayTypeList.size();
    }

    class MemberPayTypeHolder extends RecyclerView.ViewHolder {
        ViewGroup payItemLayout;
        TextView payTimeTv;
        TextView payAmountYuanTv;
        TextView payAmountTv;
        TextView payOriginalAmountTv;
        TextView tvTop;//限時優惠
        public MemberPayTypeHolder(@NonNull View itemView) {
            super(itemView);
            payItemLayout = itemView.findViewById(R.id.payItemLayout);
            payTimeTv = itemView.findViewById(R.id.payTimeTv);
            payAmountYuanTv = itemView.findViewById(R.id.payAmountYuanTv);
            payAmountTv = itemView.findViewById(R.id.payAmountTv);
            payOriginalAmountTv = itemView.findViewById(R.id.payOriginalAmountTv);
            tvTop = itemView.findViewById(R.id.tvTop);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
