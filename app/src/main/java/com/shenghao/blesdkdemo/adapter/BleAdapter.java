//package com.shenghao.blesdkdemo.adapter;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.graphics.Color;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.clj.fastble.BleManager;
//import com.clj.fastble.data.BleDevice;
//import com.shenghao.blesdk.entity.BleSdkDevice;
//import com.shenghao.blesdkdemo.R;
//import com.shenghao.blesdk.enums.BluetoothStatus;
//import com.shenghao.blesdkdemo.ui.BleActivity;
//import com.shenghao.blesdkdemo.utils.LogUtils;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class BleAdapter extends RecyclerView.Adapter<BleAdapter.BleHolder> {
//    private Context context;
//    private List<BleSdkDevice> mList = new ArrayList<>();
//    private OnItemClickListener mOnItemClickListener;
//    private OnEditClickListener mOnEditClickListener;
//    private Map<String, BluetoothStatus> statusMap = new HashMap<>(); // 设备地址 -> 状态
//    public BleAdapter(Context context, List<BleSdkDevice> mList) {
//        this.context = context;
//        this.mList = mList;
//        // 初始化状态为未连接
//        for (BleSdkDevice device : mList) {
//            statusMap.put(device.getMac(), BluetoothStatus.DISCONNECTED);
//        }
//    }
//
//    @NonNull
//    @Override
//    public BleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context)
//                .inflate(R.layout.layout_item_ble, parent, false);
//        return new BleHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull BleHolder holder, @SuppressLint("RecyclerView") int position) {
//        BleDevice bleDevice = mList.get(position);
//        holder.tvName.setText(bleDevice.getName());
//        holder.tvAddr.setText(bleDevice.getMac());
//        BluetoothStatus status = statusMap.get(bleDevice.getMac());
//        updateTvStatus(status,holder);
//
////        if(isContains(BleManager.getInstance().getAllConnectedDevice(),bleDevice)){
////            holder.tvStatus.setText("已连接");
////            holder.llStatus.setBackground(context.getDrawable(R.drawable.bg_blue_ok_radius_shape));
////            holder.tvStatus.setTextColor(Color.WHITE);
////            holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right_white, 0);
////        }else {
////            holder.tvStatus.setText("连接");
////            holder.llStatus.setBackground(context.getDrawable(R.drawable.bg_gray_cancel_radius_shape));
////            holder.tvStatus.setTextColor(Color.parseColor("#99000000"));
////            holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
////        }
//        holder.tvName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if(mOnEditClickListener != null)
////                    mOnEditClickListener.onItemClick(position,v);
//            }
//        });
//        holder.tvStatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(BleManager.getInstance().isConnected(bleDevice) && isContains(BleManager.getInstance().getAllConnectedDevice(),bleDevice)){
//                    ((BleActivity)context).showSelectGearDialog(bleDevice);
//                    return;
//                }
//                if(mOnItemClickListener != null){
//                    mOnItemClickListener.onItemClick(position,holder.itemView);
//                }
//            }
//        });
//    }
//
//    private void updateTvStatus(BluetoothStatus status, BleHolder holder) {
//        TextView tvStatus = holder.tvStatus;
//        LinearLayout llStatus = holder.llStatus;
//        if(status == null){
//            tvStatus.setText("连接");
//            llStatus.setBackground(context.getDrawable(R.drawable.bg_gray_cancel_radius_shape));
//            tvStatus.setTextColor(Color.parseColor("#99000000"));
//            tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//            return;
//        }
//        switch (status) {
//            case CONNECTING:
//                tvStatus.setText("连接中...");
//                llStatus.setBackground(context.getDrawable(R.drawable.bg_gray_cancel_radius_shape));
//                tvStatus.setTextColor(Color.parseColor("#99000000"));
//                tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//                break;
//            case CONNECTED:
//                tvStatus.setText("已连接");
//                llStatus.setBackground(null);
//                tvStatus.setTextColor(Color.parseColor("#66000000"));
//                tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_enter_arrow, 0);
//                break;
//            case DISCONNECTED:
//                tvStatus.setText("连接");
//                llStatus.setBackground(context.getDrawable(R.drawable.bg_gray_cancel_radius_shape));
//                tvStatus.setTextColor(Color.parseColor("#99000000"));
//                tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//                break;
//            default:
//                tvStatus.setText("连接");
//                llStatus.setBackground(context.getDrawable(R.drawable.bg_gray_cancel_radius_shape));
//                tvStatus.setTextColor(Color.parseColor("#99000000"));
//                tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//                break;
//        }
//    }
//
//    // 更新设备状态
//    public void updateStatus(String deviceAddress, BluetoothStatus status) {
//        statusMap.put(deviceAddress, status);
//        notifyDataSetChanged(); // 刷新列表
//        // 或者使用更高效的方式刷新单个项
////        int position = getIndexOf(deviceAddress);
////        if (position != -1) {
////            notifyItemChanged(position);
////        }
//    }
//
//    private int getIndexOf(String deviceAddress) {
//        for (int i = 0; i < mList.size(); i++) {
//            BleSdkDevice bleDevice = mList.get(i);
//            if(bleDevice.getMac().equals(deviceAddress))
//                return i;
//        }
//        return -1;
//    }
//
//    public static boolean isContains(List<BleSdkDevice> allConnectedDevice, BleSdkDevice bleDevice) {
//        LogUtils.e("isContains","已连接蓝牙个数"+allConnectedDevice.size());
//        for (BleSdkDevice item :allConnectedDevice) {
//            if(TextUtils.isEmpty(item.getName())) {
//                return false;
//            }
//            LogUtils.e("isContains",item.getMac()+","+item.getName()+","+bleDevice.getName()+","+bleDevice.getMac());
//
//            if(item.getMac().equals(bleDevice.getMac())&&item.getName().equals(bleDevice.getName())){
//                LogUtils.e("isContains","true");
//                return true;
//            }
//        }
//        LogUtils.e("isContains","false");
//        return false;
//    }
//
//    @Override
//    public int getItemCount() {
//        return mList == null ? 0 : mList.size();
//    }
//
//    class BleHolder extends RecyclerView.ViewHolder {
//        TextView tvName;
//        TextView tvAddr;
//        TextView tvStatus;
//        LinearLayout llStatus;
//        public BleHolder(@NonNull View itemView) {
//            super(itemView);
//            tvName = itemView.findViewById(R.id.tvName);
//            tvAddr = itemView.findViewById(R.id.tvAddr);
//            tvStatus = itemView.findViewById(R.id.tvStatus);
//            llStatus = itemView.findViewById(R.id.llStatus);
//        }
//    }
//    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
//        this.mOnItemClickListener = onItemClickListener;
//    }
//    public void setOnEditClickListener(OnEditClickListener onEditClickListener){
//        this.mOnEditClickListener = onEditClickListener;
//    }
//    public interface OnItemClickListener{
//        void onItemClick(int pos,View view);
//    }
//    public interface OnEditClickListener{
//        void onItemClick(int pos,View view);
//    }
//}
