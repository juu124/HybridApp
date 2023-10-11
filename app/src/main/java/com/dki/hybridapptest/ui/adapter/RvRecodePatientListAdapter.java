package com.dki.hybridapptest.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.RecodePatientDTO;

import java.util.ArrayList;
import java.util.HashMap;

public class RvRecodePatientListAdapter extends RecyclerView.Adapter<RvRecodePatientListAdapter.ViewHolder> {
    // 측정 기록 리스트
    private ArrayList<RecodePatientDTO> arrRecodePatient = new ArrayList<>();

    // 체크한 측정 기록 리스트
    private ArrayList<RecodePatientDTO> arrCheckedPatient = new ArrayList<>();

    private HashMap<String, RecodePatientDTO> map = new HashMap<>();
    private RecodePatientDTO recodePatientBloodPressure, recodePatientBloodSugar;

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
            patientHealthInfo = itemView.findViewById(R.id.health_info);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {

                        recodePatientBloodPressure = map.get("혈압");
                        recodePatientBloodSugar = map.get("혈당");

                        // 혈압 중복 시 체크 방지
                        if (arrRecodePatient.get(getBindingAdapterPosition()) == recodePatientBloodPressure) {
                            arrCheckedPatient.add(arrRecodePatient.get(getBindingAdapterPosition()));
                        } else if (arrRecodePatient.get(getBindingAdapterPosition()) != recodePatientBloodPressure &&
                                TextUtils.equals(arrRecodePatient.get(getBindingAdapterPosition()).getType(), "혈압")) {
                            checkBox.setChecked(false);
                            arrRecodePatient.get(getBindingAdapterPosition()).setChecked(false);
                            Toast.makeText(v.getContext(), "최종 혈압 측정치만 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                        if (arrRecodePatient.get(getBindingAdapterPosition()) == recodePatientBloodSugar) {
                            arrCheckedPatient.add(arrRecodePatient.get(getBindingAdapterPosition()));
                        } else if (arrRecodePatient.get(getBindingAdapterPosition()) != recodePatientBloodSugar &&
                                TextUtils.equals(arrRecodePatient.get(getBindingAdapterPosition()).getType(), "혈당")) {
                            checkBox.setChecked(false);
                            arrRecodePatient.get(getBindingAdapterPosition()).setChecked(false);
                            Toast.makeText(v.getContext(), "최종 혈당 측정치만 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recode_patient, parent, false));
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

    // 최종 즉정치 (혈압, 혈당)
    public void duplicatedRecodePatient(HashMap<String, RecodePatientDTO> map) {
        this.map = map;
    }

//    public void addUser(PatientInfoDTO user) {
//        if (user != null) {
//            ArrPatientInfo.add(user);
//        }
//    }

}
