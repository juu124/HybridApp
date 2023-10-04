package com.dki.hybridapptest.ui.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.Interface.CustomDialogClickListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dialog.CustomDialog;
import com.dki.hybridapptest.dto.PatientDeviceDTO;
import com.dki.hybridapptest.dto.RecodePatientDTO;
import com.dki.hybridapptest.ui.adapter.RvDeviceListAdapter;
import com.dki.hybridapptest.ui.adapter.RvRecodePatientListAdapter;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PatientInfoActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private Toolbar toolbar;
    private TextView mTitle;
    private Intent mIntent;

    // 환자 정보
    private TextView patientName;
    private TextView patientId;

    // 기기 목록
    private RecyclerView mRvConnectDevice;
    private RvDeviceListAdapter rvDeviceListAdapter;
    private PatientDeviceDTO patientDevice;
    private ArrayList<PatientDeviceDTO> arrPatientDevice = new ArrayList<>();
    private CheckBox checkBox;

    // 측정 기록 목록
    private RecyclerView mRvRecodePatient;
    private SimpleDateFormat simpleDate;
    private String time;
    private RecodePatientDTO recodePatientDTO;
    private ArrayList<RecodePatientDTO> arrRecodePatient = new ArrayList<>();
    private RvRecodePatientListAdapter mRvRecodePatientListAdapter;
    private TextView tvRecodeEmpty;

    // 측정값 전송
    private Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        toolbar = findViewById(R.id.medical_tool_bar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        mRvConnectDevice = findViewById(R.id.rv_connect_device);
        mRvRecodePatient = findViewById(R.id.rv_recode_patient);
        patientName = findViewById(R.id.tv_patient_name);
        patientId = findViewById(R.id.tv_patient_id);
        checkBox = findViewById(R.id.device_check_box);  // 리스트 맨위 체크박스
        tvRecodeEmpty = findViewById(R.id.tv_recode_device_empty);
        sendBtn = findViewById(R.id.send_btn);

        // 타이틀 UI displayHeader값 들어오기 전 초기화
        titleBarInit();

        // 클릭한 환자 정보
        mIntent = getIntent();
        String name = mIntent.getStringExtra("name");
        String id = " (" + mIntent.getStringExtra("patientId") + ")";
        GLog.d("name == " + name);
        GLog.d("id == " + id);
        patientName.setText(name);
        patientId.setText(id);

        // 테스트 샘플 데이터 (기기목록)
        sampleDataDevice();

        // 테스트 샘플 데이터 (측정기록)
//        sampleDataPatient();

        // 기기목록
        mRvConnectDevice.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvDeviceListAdapter = new RvDeviceListAdapter();
        rvDeviceListAdapter.addArrPatientDevice(arrPatientDevice);
        rvDeviceListAdapter.notifyDataSetChanged();
        mRvConnectDevice.setAdapter(rvDeviceListAdapter);

        // 측정 기록
        mRvRecodePatient.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRvRecodePatientListAdapter = new RvRecodePatientListAdapter();
        mRvRecodePatientListAdapter.addArrSendHistory(arrRecodePatient);
        mRvRecodePatientListAdapter.notifyDataSetChanged();
        mRvRecodePatient.setAdapter(mRvRecodePatientListAdapter);

        // 기기 목록 전체 체크
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    rvDeviceListAdapter.getCheckItem(true);
                } else {
                    rvDeviceListAdapter.getCheckItem(false);
                }
            }
        });

        // 측정값 전송 버튼
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog(PatientInfoActivity.this, new CustomDialogClickListener() {
                    @Override
                    public void onPositiveClick(String text) {
                        if (mRvRecodePatientListAdapter.getItemCount() != 0) {
                            tvRecodeEmpty.setVisibility(View.GONE);
                            mRvRecodePatient.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                }, "안내", "혈압 값을 수신중 입니다.", Constant.ONE_BUTTON, true);
                customDialog.setCancelable(false);
                customDialog.show();
                customDialog.setOneButtonText("취소");
                setDisplay(customDialog);
            }
        });
    }

    // 웹 뷰에서 뒤로 가기
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // 타이틀 바
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // 백 버튼
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 타이틀 UI 초기화
    public void titleBarInit() {
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false); // 커스텀 타이틀을 사용하면 기본 타이틀은 사용하지 말아야한다.

        // 뒤로가기 버튼 이미지 불러오기
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_aos_new_24);
        String toolBarTitle = "건강 수치 측정";
        mTitle.setText(toolBarTitle);
    }

    // 커스텀 팝업의 사이즈를 조절
    public void setDisplay(CustomDialog customDialog) {
        Display display = (PatientInfoActivity.this).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Window window = customDialog.getWindow();
        int x = (int) (size.x * 0.3f);
        int y = (int) (size.y * 0.3f);

        window.setLayout(x, y);
    }

    // 테스트 샘플 데이터(기기 목록)
    public void sampleDataDevice() {
        for (int i = 0; i < 4; i++) {
            patientDevice = new PatientDeviceDTO();
            if (i == 1) {
                patientDevice.setType("혈압");
                patientDevice.setDeviceName("AutoCheck");
            } else if (i == 2) {
                patientDevice.setType("혈당");
                patientDevice.setDeviceName("CareSens");
            } else if (i == 3) {
                patientDevice.setType("혈당");
                patientDevice.setDeviceName("11073 Accuckeck");
            } else {
                patientDevice.setType("혈압");
                patientDevice.setDeviceName("11073 And");
            }
            arrPatientDevice.add(patientDevice);
        }

//        for (int i = 0; i < 20; i++) {
//            patientDevice = new PatientDeviceDTO();
//            if (i % 2 != 0) {
//                patientDevice.setType("혈압");
//                patientDevice.setDeviceName("AutoCheck");
//            } else {
//                patientDevice.setType("혈당");
//                patientDevice.setDeviceName("CareSens");
//            }
//            arrPatientDevice.add(patientDevice);
//        }
    }

    // 테스트 샘플 데이터(측정 기록)
    public void sampleDataPatient() {
        for (int i = 0; i < 20; i++) {
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            time = simpleDate.format(date);

            recodePatientDTO = new RecodePatientDTO("", "");
            recodePatientDTO.setTime(time);
            recodePatientDTO.setName("수축기");
            recodePatientDTO.setType("");

            if (i % 2 == 0) {
                recodePatientDTO.setType("혈압");
                recodePatientDTO.setBodyStatus("혈압, 혈당, 체중");
            } else {
                recodePatientDTO.setType("혈당");
                recodePatientDTO.setBodyStatus("혈당, 체중");
            }
            arrRecodePatient.add(recodePatientDTO);
        }
    }
}