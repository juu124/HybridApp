package com.dki.hybridapptest.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.PatientDeviceDTO;
import com.dki.hybridapptest.utils.GLog;

import java.util.ArrayList;

public class RvSettingDeviceListAdapter extends RecyclerView.Adapter<RvSettingDeviceListAdapter.ViewHolder> {
    private ArrayList<PatientDeviceDTO> arrPatientDeviceList = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView type;
        private TextView deviceName;
        private CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.device_type);
            deviceName = itemView.findViewById(R.id.device_name);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RvSettingDeviceListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvSettingDeviceListAdapter.ViewHolder holder, int position) {
//        GLog.d("position == " + position);
        holder.type.setText(arrPatientDeviceList.get(position).getType());
        holder.deviceName.setText(arrPatientDeviceList.get(position).getDeviceName());
    }

    @Override
    public int getItemCount() {
        return arrPatientDeviceList.size();
    }

    // 리스트 추가
    public void addArrPatientDevice(ArrayList<PatientDeviceDTO> deviceList) {
        GLog.d();
        arrPatientDeviceList.addAll(deviceList);
    }

    // 아이템 추가
    public void addItem(PatientDeviceDTO device) {
        GLog.d();
        if (device != null) {
            arrPatientDeviceList.add(device);
        }
    }
}