package com.dki.hybridapptest.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.PatientDeviceDTO;
import com.dki.hybridapptest.utils.GLog;

import java.util.ArrayList;

public class RvDeviceListAdapter extends RecyclerView.Adapter<RvDeviceListAdapter.ViewHolder> {
    private ArrayList<PatientDeviceDTO> arrPatientDeviceList = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView type;
        private TextView deviceName;
        private CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type);
            deviceName = itemView.findViewById(R.id.device_name);
            checkBox = itemView.findViewById(R.id.device_check_box);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        arrPatientDeviceList.get(getBindingAdapterPosition()).setChecked(true);
                        checkBox.setChecked(true);
                        Toast.makeText(v.getContext(), "체크 되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        arrPatientDeviceList.get(getBindingAdapterPosition()).setChecked(false);
                        checkBox.setChecked(false);
                        Toast.makeText(v.getContext(), "체크가 해제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RvDeviceListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvDeviceListAdapter.ViewHolder holder, int position) {
//        GLog.d("position == " + position);
        holder.type.setText(arrPatientDeviceList.get(position).getType());
        holder.deviceName.setText(arrPatientDeviceList.get(position).getDeviceName());
        holder.checkBox.setChecked(arrPatientDeviceList.get(position).isChecked());
    }

    @Override
    public int getItemCount() {
        return arrPatientDeviceList.size();
    }

    public void addArrPatientDevice(ArrayList<PatientDeviceDTO> deviceList) {
        GLog.d();
        arrPatientDeviceList.addAll(deviceList);
    }

    public void getCheckItem() {
        GLog.d();
    }

    public void addItem(PatientDeviceDTO device) {
        GLog.d();
        if (device != null) {
            arrPatientDeviceList.add(device);
        }
    }
}