package com.shenghao.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.shenghao.R;
import com.shenghao.bean.StoreBean;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressHolder> {
    private Context context;
    private List<StoreBean> list;
    private OnItemClickListener listener ;

    public AddressAdapter(Context context, List<StoreBean> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_item_address, parent, false);
        return new AddressHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressHolder holder, @SuppressLint("RecyclerView") int position) {
        StoreBean storeBean = list.get(position);
        holder.tvAddr.setText(storeBean.getShopName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    public static class AddressHolder extends RecyclerView.ViewHolder {
        TextView tvAddr;
        public AddressHolder(@NonNull View itemView) {
            super(itemView);
            tvAddr = itemView.findViewById(R.id.tvAddr);
        }
    }

    public interface OnItemClickListener{
        void onClick(int pos);
    }

}
