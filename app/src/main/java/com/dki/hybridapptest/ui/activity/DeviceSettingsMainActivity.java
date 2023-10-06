package com.dki.hybridapptest.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.PatientDeviceDTO;
import com.dki.hybridapptest.ui.adapter.RvSettingDeviceListAdapter;
import com.dki.hybridapptest.utils.GLog;

import java.util.ArrayList;

public class DeviceSettingsMainActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private Toolbar toolbar;
    private TextView mTitle;

    // 연결 가능 목록
    private RecyclerView connDevice;
    private RvSettingDeviceListAdapter rvSettingDeviceAdapter;
    private TextView rvSaveDeviceEmpty;

    // 등록된 기기 목록
    private RecyclerView saveDevice;
    private RvSettingDeviceListAdapter rvSaveDeviceAdapter;

    // 서버 설정
    private RadioButton serverSetting1;
    private RadioButton serverSetting2;
    private RadioGroup radioGroup;

    private PatientDeviceDTO patientDevice;
    private ArrayList<PatientDeviceDTO> arrPatientDevice = new ArrayList<>();
    private ArrayList<PatientDeviceDTO> arrSaveDevice = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_settings_main);

        toolbar = findViewById(R.id.medical_tool_bar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        connDevice = findViewById(R.id.rv_connect_device);
        saveDevice = findViewById(R.id.rv_save_device);
        serverSetting1 = findViewById(R.id.radioButton);
        serverSetting2 = findViewById(R.id.radioButton2);
        radioGroup = findViewById(R.id.radioGroup);
        rvSaveDeviceEmpty = findViewById(R.id.rv_save_device_empty);

        // 서버 수신 모드 디폴트
        radioGroup.check(serverSetting1.getId());

        // 타이틀 UI displayHeader값 들어오기 전 초기화
        titleBarInit();

        // 테스트 샘플 데이터
        sampleConnetData();
//        sampleSaveData();

        // 연결 가능 기기 목록
        connDevice.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvSettingDeviceAdapter = new RvSettingDeviceListAdapter();
        rvSettingDeviceAdapter.addArrPatientDevice(arrPatientDevice);
        connDevice.setAdapter(rvSettingDeviceAdapter);
        rvSettingDeviceAdapter.notifyDataSetChanged();
        GLog.d();

//        patientDevice = new PatientDeviceDTO();
//        patientDevice.setType("추가 연결 가능");
//        patientDevice.setDeviceName("add");
//        rvConnDeviceAdapter.addItem(patientDevice);
//        rvConnDeviceAdapter.notifyDataSetChanged();

        // 등록 기기 목록
        saveDevice.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvSaveDeviceAdapter = new RvSettingDeviceListAdapter();
        rvSaveDeviceAdapter.addArrPatientDevice(arrSaveDevice);
        saveDevice.setAdapter(rvSaveDeviceAdapter);
        rvSaveDeviceAdapter.notifyDataSetChanged();

//        patientDevice = new PatientDeviceDTO();
//        patientDevice.setType("추가 등록 기기");
//        patientDevice.setDeviceName("add add");
//        rvSaveDeviceAdapter.addItem(patientDevice);
//        rvSaveDeviceAdapter.notifyDataSetChanged();

        // 등록 기기 없을 시 노출 문구
        if (rvSaveDeviceAdapter.getItemCount() == 0) {
            rvSaveDeviceEmpty.setVisibility(View.VISIBLE);
            saveDevice.setVisibility(View.GONE);
        } else {
            rvSaveDeviceEmpty.setVisibility(View.GONE);
            saveDevice.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GLog.d();
    }

    // 웹 뷰에서 뒤로 가기
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // 타이틀 back 버튼
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    // 타이틀 UI 초기화
    public void titleBarInit() {
        GLog.d();
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
        String toolBarTitle = "기기 설정";
        mTitle.setText(toolBarTitle);
    }

    // 샘플 테스트 데이터 (연결 가능 기기 목록)
    public void sampleConnetData() {
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
    }

    // 샘플 테스트 데이터 (등록 기기 목록)
    public void sampleSaveData() {
        for (int i = 0; i < 1; i++) {
            patientDevice = new PatientDeviceDTO();
            if (i % 2 != 0) {
                patientDevice.setType("혈압");
                patientDevice.setDeviceName("AutoCheck");
            } else {
                patientDevice.setType("혈당");
                patientDevice.setDeviceName("CareSens");
            }
            arrSaveDevice.add(patientDevice);
        }
    }
}