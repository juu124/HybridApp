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
import com.dki.hybridapptest.utils.GLog;

import java.util.ArrayList;
import java.util.HashMap;

public class RvRecodePatientListAdapter extends RecyclerView.Adapter<RvRecodePatientListAdapter.ViewHolder> {
    // 측정 기록 리스트
    private ArrayList<RecodePatientDTO> arrRecodePatient = new ArrayList<>();

    // 체크한 측정 기록 리스트
    private ArrayList<RecodePatientDTO> arrCheckedPatient = new ArrayList<>();

    private HashMap<String, RecodePatientDTO> map = new HashMap<>();
    private RecodePatientDTO recodePatientBloodPressure, recodePatientBloodSugar;
    private int size;
    private boolean isNewData;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView date;
        private TextView type;
        private TextView patientHealthInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            GLog.d("");
            checkBox = itemView.findViewById(R.id.checkbox);
            date = itemView.findViewById(R.id.date);
            type = itemView.findViewById(R.id.Log_type);
            patientHealthInfo = itemView.findViewById(R.id.health_info);

            recodePatientBloodPressure = map.get("혈압");
            recodePatientBloodSugar = map.get("혈당");

            size = getItemCount();

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 혈압 중복 시 체크 방지
                    if (arrRecodePatient.get(getBindingAdapterPosition()) == recodePatientBloodPressure) {
                        if (!checkBox.isChecked()) {
                            GLog.d("체크 해제 1");
                            GLog.d("체크 해제 번호 == " + getBindingAdapterPosition());
                            for (int i = 0; i < arrCheckedPatient.size(); i++) {
                                GLog.d("check type = == " + arrCheckedPatient.get(i).getType() + " || 체크 됨 == " + arrCheckedPatient.get(i).isChecked());
                                if (arrCheckedPatient.get(i) == recodePatientBloodPressure) {
                                    arrCheckedPatient.remove(recodePatientBloodPressure);
                                    arrRecodePatient.get(getBindingAdapterPosition()).setChecked(false);
                                }
                            }
                        } else {
                            GLog.d("1");
                            arrRecodePatient.get(getBindingAdapterPosition()).setChecked(true);
                            arrCheckedPatient.add(arrRecodePatient.get(getBindingAdapterPosition()));
                        }

                    } else if (arrRecodePatient.get(getBindingAdapterPosition()) != recodePatientBloodPressure &&
                            TextUtils.equals(arrRecodePatient.get(getBindingAdapterPosition()).getType(), "혈압")) {
                        GLog.d("2");
                        checkBox.setChecked(false);
                        arrRecodePatient.get(getBindingAdapterPosition()).setChecked(false);
                        Toast.makeText(v.getContext(), "최종 혈압 측정치만 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                    }
                    if (arrRecodePatient.get(getBindingAdapterPosition()) == recodePatientBloodSugar) {
                        if (!checkBox.isChecked()) {
                            GLog.d("체크 해제 3");
                            GLog.d("체크 해제 번호 == " + getBindingAdapterPosition());
                            for (int i = 0; i < arrCheckedPatient.size(); i++) {
                                GLog.d("check type = == " + arrCheckedPatient.get(i).getType() + " || 체크 됨 == " + arrCheckedPatient.get(i).isChecked());
                                if (arrCheckedPatient.get(i) == recodePatientBloodSugar) {
                                    arrCheckedPatient.remove(recodePatientBloodPressure);
                                    arrRecodePatient.get(getBindingAdapterPosition()).setChecked(false);
                                }
                            }
                        } else {
                            GLog.d("3");
                            arrRecodePatient.get(getBindingAdapterPosition()).setChecked(true);
                            arrCheckedPatient.add(arrRecodePatient.get(getBindingAdapterPosition()));
                        }
                    } else if (arrRecodePatient.get(getBindingAdapterPosition()) != recodePatientBloodSugar &&
                            TextUtils.equals(arrRecodePatient.get(getBindingAdapterPosition()).getType(), "혈당")) {
                        GLog.d("4");
                        checkBox.setChecked(false);
                        arrRecodePatient.get(getBindingAdapterPosition()).setChecked(false);
                        Toast.makeText(v.getContext(), "최종 혈당 측정치만 선택 가능합니다.", Toast.LENGTH_SHORT).show();
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
        GLog.d("");
        GLog.d("position == " + position);
        GLog.d("arrRecodePatient.get(position).isChecked() == " + arrRecodePatient.get(position).isChecked());

        if (arrRecodePatient.get(position).isChecked()) {
            holder.checkBox.setChecked(true);
            arrCheckedPatient.add(arrRecodePatient.get(position));
        } else {
            holder.checkBox.setChecked(false);
        }


        holder.date.setText(arrRecodePatient.get(position).getTime());
        holder.type.setText(arrRecodePatient.get(position).getType());
        holder.patientHealthInfo.setText(arrRecodePatient.get(position).getRecodePatient());
    }

    // 측정 기록 사이즈
    public int getItemCount() {
        return arrRecodePatient.size();
    }

//    public void addArrSendHistory(ArrayList<RecodePatientDTO> recodePatientList) {
//        arrRecodePatient.addAll(recodePatientList);
//    }

    public void newDataCheck(boolean isNewData) {
        if (isNewData) {
            for (int i = 0; i < getItemCount(); i++) {
                arrRecodePatient.get(i).setChecked(false);
                arrCheckedPatient.clear();
            }
        }
    }

    public void setArrSendHistory(ArrayList<RecodePatientDTO> recodePatientList) {
        arrRecodePatient = recodePatientList;
    }

    public void setArrCheckList(ArrayList<RecodePatientDTO> recodePatientList) {
        arrCheckedPatient = recodePatientList;
    }

    public void addSendLog(RecodePatientDTO recodePatientDTO) {
        arrRecodePatient.add(recodePatientDTO);
    }


    // 체크한 기록
    public ArrayList<RecodePatientDTO> getCheckedList() {
        return arrCheckedPatient;
    }

    public ArrayList<RecodePatientDTO> getList() {
        return arrRecodePatient;
    }

    // 최종 즉정치 (혈압, 혈당)
    public void duplicatedRecodePatient(HashMap<String, RecodePatientDTO> map) {
        this.map = map;
    }

    // 아이템 추가시 자동 스크롤
    public int getIndexUser(RecodePatientDTO user) {
        for (int i = 0; i < arrRecodePatient.size(); i++) {
//            GLog.d("getIndexUser == " + arrPatientInfo.get(i));
            if (user == arrRecodePatient.get(i)) {
                return i;
            }
        }
        return 0;
    }

//    // 전체 선택
//    public void getAllCheck(boolean isAllChecked) {
//        GLog.d("");
//        this.isAllChecked = isAllChecked;
//        if (isAllChecked) {
//            allCheckListener.allCheck(true);
//        } else {
//            allCheckListener.allCheck(false);
//        }
//    }
}
