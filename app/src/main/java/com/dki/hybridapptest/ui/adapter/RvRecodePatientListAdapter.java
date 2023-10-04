package com.dki.hybridapptest.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.RecodePatientDTO;
import com.dki.hybridapptest.utils.GLog;

import java.util.ArrayList;

public class RvRecodePatientListAdapter extends RecyclerView.Adapter<RvRecodePatientListAdapter.ViewHolder> {
    private ArrayList<RecodePatientDTO> arrRecodePatient = new ArrayList<>();
    private String patientHealthInfoTxt;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView patientHealthInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            patientHealthInfo = itemView.findViewById(R.id.patient_health_info);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GLog.d("아이템을 클릭했습니다.");
                    Toast.makeText(v.getContext(), "item onclick", Toast.LENGTH_SHORT).show();
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
        GLog.d("");
        holder.date.setText(arrRecodePatient.get(position).getTime());
        patientHealthInfoTxt = arrRecodePatient.get(position).getName()
                + " ("
                + arrRecodePatient.get(position).getPatientId()
                + ") "
                + arrRecodePatient.get(position).getBodyStatus();
        holder.patientHealthInfo.setText(patientHealthInfoTxt);

        GLog.d("date = " + arrRecodePatient.get(position).getTime());
        GLog.d("patientHealthInfoTxt = " + patientHealthInfoTxt);
    }

    @Override
    public int getItemCount() {
        return arrRecodePatient.size();
    }

    public void addArrSendHistory(ArrayList<RecodePatientDTO> recodePatientList) {
        GLog.d();
        arrRecodePatient.addAll(recodePatientList);
    }

//    public void addUser(PatientInfoDTO user) {
//        if (user != null) {
//            ArrPatientInfo.add(user);
//        }
//    }
}
