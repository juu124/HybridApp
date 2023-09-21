package com.dki.hybridapptest.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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
import com.dki.hybridapptest.dto.PatientInfoDTO;
import com.dki.hybridapptest.dto.SendHistoryDTO;
import com.dki.hybridapptest.ui.adapter.RvPatientListAdapter;
import com.dki.hybridapptest.ui.adapter.RvSendHistoryListAdapter;
import com.dki.hybridapptest.utils.GLog;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MedicalCareMainActivity extends AppCompatActivity {
    private Intent mIntent;

    // top tool bar
    private ActionBar actionBar;
    private Toolbar toolbar;
    private TextView mTitle;

    // 환자 목록
    private RecyclerView mRvPatientList;
    private RecyclerView mRvSendHistoryList;
    private RvPatientListAdapter mRvPatientListAdapter;
    private RvSendHistoryListAdapter mRvSendHistoryListAdapter;
    private PatientInfoDTO patientInfoDTO;
    private SendHistoryDTO sendHistoryDTO;
    private ArrayList<SendHistoryDTO> arrSendHistory = new ArrayList<>();
    private ArrayList<PatientInfoDTO> arrPatientInfo = new ArrayList<>();

    // 전송 기록 목록
    private SimpleDateFormat simpleDate;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_care_main);

        toolbar = findViewById(R.id.medical_tool_bar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        mRvPatientList = findViewById(R.id.rv_patient_list);
        mRvSendHistoryList = findViewById(R.id.rv_send_history);

        // 테스트 샘플 데이터
        sampleData();

        mRvPatientList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRvPatientListAdapter = new RvPatientListAdapter();
        mRvPatientListAdapter.addArrPatientInfo(arrPatientInfo);
        mRvPatientListAdapter.notifyDataSetChanged();
        mRvPatientList.setAdapter(mRvPatientListAdapter);

        mRvSendHistoryList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRvSendHistoryListAdapter = new RvSendHistoryListAdapter();
        mRvSendHistoryListAdapter.addArrSendHistory(arrSendHistory);
        mRvSendHistoryListAdapter.notifyDataSetChanged();
        mRvSendHistoryList.setAdapter(mRvSendHistoryListAdapter);

        // 타이틀 UI displayHeader값 들어오기 전 초기화
        titleBarInit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.medical_menu, menu);
        return true;
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
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.device_settings:
                GLog.d("onNavigationItemSelected device_settings");
                mIntent = new Intent(this, DeviceSettingsMainActivity.class);
                startActivity(mIntent);
                break;
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
        String toolBarTitle = "유연의료 건강 측정";
        mTitle.setText(toolBarTitle);
    }

    // 테스트 샘플 데이터
    public void sampleData() {
        // 환자 목록
        for (int i = 0; i < 20; i++) {
            patientInfoDTO = new PatientInfoDTO("", "12345123");
            patientInfoDTO.setNum(String.valueOf(i + 1));
            if (i % 2 == 0) {
                patientInfoDTO.setGender("여");
            } else {
                patientInfoDTO.setGender("남");
            }
            patientInfoDTO.setName("이지영" + i);
            patientInfoDTO.setBornYear("1953" + i);
            GLog.d("num == " + patientInfoDTO.getNum());
            GLog.d("gender == " + patientInfoDTO.getGender());
            GLog.d("name == " + patientInfoDTO.getName());
            GLog.d("id == " + patientInfoDTO.getPatientId());
            GLog.d("bornyear == " + patientInfoDTO.getBornYear());
            arrPatientInfo.add(patientInfoDTO);
        }

        // 전송 기록
        for (int i = 0; i < 20; i++) {
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            time = simpleDate.format(date);

            sendHistoryDTO = new SendHistoryDTO("", "");
            sendHistoryDTO.setTime(time);
            sendHistoryDTO.setName("이지영" + i);
            if (i % 2 == 0) {
                sendHistoryDTO.setBodyStatus("혈압, 혈당, 체중");
            } else {
                sendHistoryDTO.setBodyStatus("혈당, 체중");
            }
            arrSendHistory.add(sendHistoryDTO);
        }
    }
}