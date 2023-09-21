package com.dki.hybridapptest.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.PatientDeviceDTO;
import com.dki.hybridapptest.ui.adapter.RvDeviceListAdapter;
import com.dki.hybridapptest.utils.GLog;

import java.util.ArrayList;

public class DeviceSettingsMainActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private Toolbar toolbar;
    private TextView mTitle;

    // 목록
    private RecyclerView connDevice;
    private RecyclerView saveDevice;
    private RvDeviceListAdapter rvDeviceListAdapter;
    private PatientDeviceDTO patientDevice;
    private ArrayList<PatientDeviceDTO> arrPatientDevice = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_settings_main);

        toolbar = findViewById(R.id.medical_tool_bar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        connDevice = findViewById(R.id.rv_connect_device);
        saveDevice = findViewById(R.id.rv_save_device);

        // 타이틀 UI displayHeader값 들어오기 전 초기화
        titleBarInit();

        // 테스트 샘플 데이터
        sampleData();

        connDevice.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        saveDevice.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvDeviceListAdapter = new RvDeviceListAdapter();
        rvDeviceListAdapter.addArrPatientDevice(arrPatientDevice);
        rvDeviceListAdapter.notifyDataSetChanged();
        connDevice.setAdapter(rvDeviceListAdapter);
        saveDevice.setAdapter(rvDeviceListAdapter);
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

    public void sampleData() {
        for (int i = 0; i < 20; i++) {
            patientDevice = new PatientDeviceDTO();
            if (i % 2 != 0) {
                patientDevice.setType("혈압");
                patientDevice.setDeviceName("AutoCheck");
            } else {
                patientDevice.setType("혈당");
                patientDevice.setDeviceName("CareSens");
            }
            arrPatientDevice.add(patientDevice);
        }
    }
}