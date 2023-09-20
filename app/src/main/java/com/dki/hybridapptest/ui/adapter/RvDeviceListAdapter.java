package com.dki.hybridapptest.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.PatientDeviceDTO;
import com.dki.hybridapptest.utils.GLog;

import java.util.ArrayList;

public class RvDeviceListAdapter extends RecyclerView.Adapter<RvDeviceListAdapter.ViewHolder> {
    private PatientDeviceDTO patientDeviceDTO;
    private ArrayList<PatientDeviceDTO> arrPatientDeviceList = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView type;
        private TextView deviceName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type);
            deviceName = itemView.findViewById(R.id.device_name);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RvDeviceListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvDeviceListAdapter.ViewHolder holder, int position) {
        holder.type.setText(arrPatientDeviceList.get(position).getType());
        holder.deviceName.setText(arrPatientDeviceList.get(position).getDeviceName());
    }

    @Override
    public int getItemCount() {
        return arrPatientDeviceList.size();
    }

    public void addArrPatientDevice(ArrayList<PatientDeviceDTO> deviceList) {
        GLog.d();
        arrPatientDeviceList.addAll(deviceList);
    }
}
