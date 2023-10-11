package com.dki.hybridapptest.ui.adapter;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.Interface.CustomDialogClickListener;
import com.dki.hybridapptest.Interface.InputRecodePatientInfoListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dialog.CustomDialog;
import com.dki.hybridapptest.dto.PatientDeviceDTO;
import com.dki.hybridapptest.dto.RecodePatientDTO;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RvDeviceListAdapter extends RecyclerView.Adapter<RvDeviceListAdapter.ViewHolder> {
    private ArrayList<PatientDeviceDTO> arrPatientDeviceList = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean recode;
    private SimpleDateFormat simpleDate;
    private String time;
    private String typeTxt;
    private RecodePatientDTO recodePatientDTO;
    private InputRecodePatientInfoListener inputRecodePatientInfoListener;
    private CustomDialog customDialog;

    public RvDeviceListAdapter(InputRecodePatientInfoListener inputRecodePatientInfoListener) {
        GLog.d("리스너 생성 되었습니다.");
        this.inputRecodePatientInfoListener = inputRecodePatientInfoListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView type;
        private TextView deviceName;
        private CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type);
            deviceName = itemView.findViewById(R.id.device_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    recode = true;
                    typeTxt = arrPatientDeviceList.get(getBindingAdapterPosition()).getType();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            inputRecodePatientInfoListener.onInputPositiveClick(arrPatientDeviceList.get(getBindingAdapterPosition()).getType());
                            customDialog.dismiss();
                        }
                    }, 1000);

                    customDialog = new CustomDialog(v.getContext(), new CustomDialogClickListener() {
                        @Override
                        public void onPositiveClick(String text) {
                            customDialog.dismiss();
                            handler.removeCallbacksAndMessages(null);
//                            inputRecodePatientInfoListener.onInputPositiveClick(arrPatientDeviceList.get(getBindingAdapterPosition()).getType());
                        }

                        @Override
                        public void onNegativeClick() {
                        }
                    }, "안내", typeTxt + " 값을 수신중 입니다.", Constant.ONE_BUTTON, true);
                    customDialog.setCancelable(false);
                    customDialog.show();
                    customDialog.setOneButtonText("취소");
//                    setDisplay(customDialog);
//                    GLog.d("클릭 후 recode == " + recode);
                }

            });
//            checkBox = itemView.findViewById(R.id.device_check_box);

//            checkBox.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (checkBox.isChecked()) {
//                        arrPatientDeviceList.get(getBindingAdapterPosition()).setChecked(true);
//                        GLog.d("체크 종류 : " + arrPatientDeviceList.get(getBindingAdapterPosition()).getType() + "기기명 : " + arrPatientDeviceList.get(getBindingAdapterPosition()).getDeviceName());
//                    } else {
//                        arrPatientDeviceList.get(getBindingAdapterPosition()).setChecked(false);
//                    }
//                }
//            });
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

    // 전체 체크 및 해제
//    public void getCheckItem(boolean isChecked) {
//        if (isChecked) {
//            for (int i = 0; i < arrPatientDeviceList.size(); i++) {
//                arrPatientDeviceList.get(i).setChecked(true);
//            }
//        } else {
//            for (int i = 0; i < arrPatientDeviceList.size(); i++) {
//                arrPatientDeviceList.get(i).setChecked(false);
//            }
//        }
//        notifyDataSetChanged();
//    }

    // 커스텀 팝업의 사이즈를 조절
//    public void setDisplay(CustomDialog customDialog) {
//        Display display = (PatientInfoActivity.class).getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//
//        Window window = customDialog.getWindow();
//        int x = (int) (size.x * 0.3f);
//        int y = (int) (size.y * 0.3f);
//
//        window.setLayout(x, y);
//    }

    // 기기 연결 후 측정 기록 추가
    public RecodePatientDTO addRecodePatient() {
        GLog.d("recode == " + recode);
        if (recode) {
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            time = simpleDate.format(date);

            recodePatientDTO = new RecodePatientDTO();
            recodePatientDTO.setTime(time);

            if (TextUtils.equals(typeTxt, "혈압")) {
                recodePatientDTO.setType("혈압");
                recodePatientDTO.setRecodePatient("수축기 (156.0), 이완기 (87.3), 맥박수 (80.0)");
            } else if (TextUtils.equals(typeTxt, "혈당")) {
                recodePatientDTO.setType("혈당");
                recodePatientDTO.setRecodePatient("-");
            } else {
                recodePatientDTO.setType("체중");
                recodePatientDTO.setRecodePatient("-");
            }
//        GLog.d("recodePatientDTO getType == " + recodePatientDTO.getType());
        }
        return recodePatientDTO;
    }

    // 아이템 추가
    public void addItem(PatientDeviceDTO device) {
        GLog.d();
        if (device != null) {
            arrPatientDeviceList.add(device);
        }
    }
}