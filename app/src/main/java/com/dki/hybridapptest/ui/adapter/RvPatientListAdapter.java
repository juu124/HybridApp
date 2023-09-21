package com.dki.hybridapptest.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.PatientInfoDTO;
import com.dki.hybridapptest.ui.activity.PatientInfoActivity;
import com.dki.hybridapptest.utils.GLog;

import java.util.ArrayList;

public class RvPatientListAdapter extends RecyclerView.Adapter<RvPatientListAdapter.ViewHolder> {
    private ArrayList<PatientInfoDTO> arrPatientInfo = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView num;
        private TextView gender;
        private TextView name;
        private TextView id;
        private TextView bornYear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            num = itemView.findViewById(R.id.num);
            gender = itemView.findViewById(R.id.gender);
            name = itemView.findViewById(R.id.name);
            id = itemView.findViewById(R.id.patient_id);
            bornYear = itemView.findViewById(R.id.born_year);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GLog.d("아이템을 클릭했습니다.");
                    Intent intent = new Intent(v.getContext(), PatientInfoActivity.class);
                    String patientName = name.getText().toString();
                    String patientId = id.getText().toString();
                    intent.putExtra("name", patientName);
                    intent.putExtra("patientId", patientId);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_info, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.num.setText(arrPatientInfo.get(position).getNum());
        holder.gender.setText(arrPatientInfo.get(position).getGender());
        holder.name.setText(arrPatientInfo.get(position).getName());
        holder.id.setText(arrPatientInfo.get(position).getPatientId());
        holder.bornYear.setText(arrPatientInfo.get(position).getBornYear());
    }

    @Override
    public int getItemCount() {
        return arrPatientInfo.size();
    }

    public void addArrPatientInfo(ArrayList<PatientInfoDTO> userList) {
        GLog.d();
        arrPatientInfo.addAll(userList);
    }

//    public void addUser(PatientInfoDTO user) {
//        if (user != null) {
//            ArrPatientInfo.add(user);
//        }
//    }
}
