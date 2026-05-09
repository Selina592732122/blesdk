package com.shenghao.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.shenghao.R;
import com.shenghao.bean.AlarmSettingBean;

import java.util.List;

public class AlarmSettingAdapter extends RecyclerView.Adapter<AlarmSettingAdapter.AlarmHolder> {
    private Context context;
    private List<AlarmSettingBean> alarmList;
    private onSwitchCheckedListener onSwitchCheckedListener;

    public AlarmSettingAdapter(Context context, List<AlarmSettingBean> alarmList, onSwitchCheckedListener onSwitchCheckedListener) {
        this.context = context;
        this.alarmList = alarmList;
        this.onSwitchCheckedListener = onSwitchCheckedListener;
    }

    @NonNull
    @Override
    public AlarmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_item_alarm_setting, parent, false);
        return new AlarmHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmHolder holder, @SuppressLint("RecyclerView") int position) {
        AlarmSettingBean alarmSetting = alarmList.get(position);
        holder.itemAlarmTv.setText(alarmSetting.getDesc());
        holder.itemAlarmSwitch.setChecked(alarmSetting.isChecked());
//        holder.itemAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (onSwitchCheckedListener != null) {
//                    onSwitchCheckedListener.onCheckedChanged(position, isChecked);
//                }
//            }
//        });
        holder.itemAlarmSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSwitchCheckedListener != null) {
                    onSwitchCheckedListener.onCheckedChanged(position, ((Switch)v).isChecked());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmList == null ? 0 : alarmList.size();
    }

    class AlarmHolder extends RecyclerView.ViewHolder {
        TextView itemAlarmTv;
        Switch itemAlarmSwitch;

        public AlarmHolder(@NonNull View itemView) {
            super(itemView);
            itemAlarmTv = itemView.findViewById(R.id.itemAlarmTv);
            itemAlarmSwitch = itemView.findViewById(R.id.itemAlarmSwitch);
        }
    }

    public interface onSwitchCheckedListener {
        void onCheckedChanged(int position, boolean isChecked);
    }
}
