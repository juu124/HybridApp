package com.dki.hybridapptest.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.RecodePatientDTO;

import java.util.ArrayList;

public class RvRecodePatientListAdapter extends RecyclerView.Adapter<RvRecodePatientListAdapter.ViewHolder> {
    // 측정 기록 리스트
    private ArrayList<RecodePatientDTO> arrRecodePatient = new ArrayList<>();

    // 체크한 측정 기록 리스트
    private ArrayList<RecodePatientDTO> arrCheckedPatient = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView date;
        private TextView type;
        private TextView patientHealthInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            date = itemView.findViewById(R.id.date);
            type = itemView.findViewById(R.id.Log_type);
            patientHealthInfo = itemView.findViewById(R.id.patient_health_info);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        arrRecodePatient.get(getBindingAdapterPosition()).setChecked(true);
                        arrCheckedPatient.add(arrRecodePatient.get(getBindingAdapterPosition()));
                    } else {
                        arrCheckedPatient.remove(arrRecodePatient.get(getBindingAdapterPosition()));
                        arrRecodePatient.get(getBindingAdapterPosition()).setChecked(true);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_send_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.checkBox.setChecked(arrRecodePatient.get(position).isChecked());
        holder.date.setText(arrRecodePatient.get(position).getTime());
        holder.type.setText(arrRecodePatient.get(position).getType());
        holder.patientHealthInfo.setText(arrRecodePatient.get(position).getRecodePatient());
    }

    @Override
    public int getItemCount() {
        return arrRecodePatient.size();
    }

    public void addArrSendHistory(ArrayList<RecodePatientDTO> recodePatientList) {
        arrRecodePatient.addAll(recodePatientList);
    }

    // 체크한 기록
    public ArrayList<RecodePatientDTO> getCheckedList() {
        return arrCheckedPatient;
    }

//    public void addUser(PatientInfoDTO user) {
//        if (user != null) {
//            ArrPatientInfo.add(user);
//        }
//    }

}
