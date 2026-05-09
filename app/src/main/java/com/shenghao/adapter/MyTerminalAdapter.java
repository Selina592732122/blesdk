package com.shenghao.adapter;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.shenghao.R;
import com.shenghao.bean.TerminalBean;
import com.shenghao.utils.SizeUtils;
import com.shenghao.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class MyTerminalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<TerminalBean> terminalList;
    private List<TerminalBean> allTerminalList;
    private OnTerminalItemClickListener onTerminalItemClickListener;
    private static final int WIDTH_ITEM_LAYOUT = 300;

    public MyTerminalAdapter(Context context, List<TerminalBean> terminalList, OnTerminalItemClickListener onTerminalItemClickListener) {
        this.context = context;
        this.terminalList = terminalList;
        this.allTerminalList = new ArrayList<>(terminalList); // 初始化完整列表
        this.onTerminalItemClickListener = onTerminalItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.layout_item_my_terminal, parent, false);
        return new MyTerminalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TerminalBean terminalBean = terminalList.get(position);
        MyTerminalViewHolder terminalViewHolder = (MyTerminalViewHolder) holder;

        if (TextUtils.isEmpty(terminalBean.getName())) {
            terminalViewHolder.terminalNameTv.setVisibility(View.VISIBLE);
            terminalViewHolder.terminalNameTv.setText(terminalBean.getCarName());
        } else {
            terminalViewHolder.terminalNameTv.setText(terminalBean.getName());
            terminalViewHolder.terminalNameTv.setVisibility(View.VISIBLE);
        }
        terminalViewHolder.terminalNoTv.setText("No: " + terminalBean.getTerminalNo());
        terminalViewHolder.currentFlagTv.setVisibility(terminalBean.isSelected() ? View.VISIBLE : View.GONE);
        if(terminalBean.getShareStatus() == 2)
            terminalViewHolder.tvShare.setVisibility(View.VISIBLE);
        else
            terminalViewHolder.tvShare.setVisibility(View.GONE);//是否共享车辆
        Glide.with(context).load(terminalBean.getCarPicture())
                .placeholder(R.drawable.ic_item_vehicle) // 加载中的占位图
                .error(R.drawable.ic_item_vehicle)
                .into(terminalViewHolder.ivVehicle);
        terminalViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTerminalItemClickListener != null) {
                    onTerminalItemClickListener.onItemClick(position);
                }
            }
        });
        terminalViewHolder.terminalNoTv.setOnClickListener(v -> {
            // 复制逻辑
            String text = terminalViewHolder.terminalNoTv.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", text);

            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                ToastUtils.showShort(context,"文本已复制");
            }
        });
    }

    @Override
    public int getItemCount() {
        return terminalList == null ? 0 : terminalList.size();
    }

    private void setViewWidth(View view, int width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width;
        view.setLayoutParams(layoutParams);
    }
    public void updateAllTerminalList(List<TerminalBean> newList,String query) {
        allTerminalList.clear();
        allTerminalList.addAll(newList);
        filter(query); // 重置过滤
    }
    public void filter(String query) {
        query = query.toLowerCase().trim();

        if (query.isEmpty()) {
            // 如果搜索条件为空，显示所有设备
            terminalList.clear();
            terminalList.addAll(allTerminalList);
        } else {
            // 根据设备号过滤
            List<TerminalBean> filteredList = new ArrayList<>();
            for (TerminalBean terminal : allTerminalList) {
                if (terminal.getTerminalNo().toLowerCase().contains(query) || terminal.getCarName().contains(query) || terminal.getName().contains(query)) {
                    filteredList.add(terminal);
                }
            }
            terminalList.clear();
            terminalList.addAll(filteredList);
        }
        notifyDataSetChanged(); // 通知数据变化，更新UI
    }

    static class MyTerminalViewHolder extends RecyclerView.ViewHolder {
        View itemTerminalLayout;
        TextView terminalNameTv;
        TextView terminalNoTv;
        TextView currentFlagTv;
        ImageView ivVehicle;
        TextView tvShare;
        public MyTerminalViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTerminalLayout = itemView.findViewById(R.id.itemTerminalLayout);
            terminalNameTv = itemView.findViewById(R.id.terminalNameTv);
            terminalNoTv = itemView.findViewById(R.id.terminalNoTv);
            currentFlagTv = itemView.findViewById(R.id.currentFlagTv);
            ivVehicle = itemView.findViewById(R.id.ivVehicle);
            tvShare = itemView.findViewById(R.id.tvShare);
            // 设置项目间距
            int margin = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    4,
                    itemView.getResources().getDisplayMetrics()
            );

            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.setMargins(margin, 0, margin, 0);
                itemView.setLayoutParams(layoutParams);
            }
        }
    }

    public interface OnTerminalItemClickListener {
        void onItemClick(int position);
    }

}
