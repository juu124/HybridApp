package com.dki.hybridapptest.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.SendLogDTO;
import com.dki.hybridapptest.utils.GLog;

import java.util.ArrayList;

public class RvSendLogAdapter extends RecyclerView.Adapter<RvSendLogAdapter.ViewHolder> {
    private ArrayList<SendLogDTO> arrSendHistory = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView name;
        private TextView id;
        private TextView patientHealthInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            name = itemView.findViewById(R.id.name);
            id = itemView.findViewById(R.id.id);
            patientHealthInfo = itemView.findViewById(R.id.health_info);

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
        holder.date.setText(arrSendHistory.get(position).getTime());
        holder.name.setText(arrSendHistory.get(position).getName());
        holder.id.setText(arrSendHistory.get(position).getId());
        holder.patientHealthInfo.setText(arrSendHistory.get(position).getSendLog());
//        patientHealthInfoTxt = arrSendHistory.get(position).getName()
//                + " ("
//                + arrSendHistory.get(position).getPatientId()
//                + ") "
//                + arrSendHistory.get(position).getSendLog()
//                + " 데이터 전송완료";
//        holder.patientHealthInfo.setText(patientHealthInfoTxt);
    }

    @Override
    public int getItemCount() {
        return arrSendHistory.size();
    }

    public void setArrSendHistory(ArrayList<SendLogDTO> sendHistoryList) {
        GLog.d("");
//        arrSendHistory.addAll(sendHistoryList);
        arrSendHistory = sendHistoryList;
    }

    public void addUser(SendLogDTO sendLog) {
        if (sendLog != null) {
            arrSendHistory.add(sendLog);
        }
    }
}
