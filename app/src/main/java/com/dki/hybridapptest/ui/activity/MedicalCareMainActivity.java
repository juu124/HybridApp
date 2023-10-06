package com.dki.hybridapptest.ui.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.Interface.InputPatientInfoListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dialog.InputPatientDialog;
import com.dki.hybridapptest.dto.PatientInfoDTO;
import com.dki.hybridapptest.dto.SendLogDTO;
import com.dki.hybridapptest.ui.adapter.RvPatientListAdapter;
import com.dki.hybridapptest.ui.adapter.RvSendLogAdapter;
import com.dki.hybridapptest.utils.GLog;
import com.dki.hybridapptest.utils.SharedPreferencesAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private RvPatientListAdapter mRvPatientListAdapter;
    private PatientInfoDTO patientInfoDTO;
    private ArrayList<PatientInfoDTO> arrPatientInfo = new ArrayList<>();
    private TextView patientEmpty;

    // 전송 기록 목록
    private RecyclerView mRvSendLogList;
    private RvSendLogAdapter mRvSendLogAdapter;
    private SendLogDTO sendLogDTO;
    private ArrayList<SendLogDTO> arrSendLog = new ArrayList<>();
    private SimpleDateFormat simpleDate;
    private String time;
    private TextView sendLogEmpty;

    // 환자 추가 버튼
    private Button patientAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLog.d("");
        setContentView(R.layout.activity_medical_care_main);

        if (!TextUtils.isEmpty(SharedPreferencesAPI.getInstance(MedicalCareMainActivity.this).getRecodePatient())) {
            // null로 초기화하기
            SharedPreferencesAPI.getInstance(MedicalCareMainActivity.this).setRecodePatient("");
        }

        toolbar = findViewById(R.id.medical_tool_bar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        mRvPatientList = findViewById(R.id.rv_patient_list);
        mRvSendLogList = findViewById(R.id.rv_send_history);
        patientAddBtn = findViewById(R.id.btn_patient_add);
        patientEmpty = findViewById(R.id.tv_patient_empty);
        sendLogEmpty = findViewById(R.id.rv_send_history_empty);

        // 타이틀 UI displayHeader값 들어오기 전 초기화
        titleBarInit();

        // 테스트 샘플 데이터
        sampleData();

        // 환자 목록
        mRvPatientList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRvPatientListAdapter = new RvPatientListAdapter();
        mRvPatientListAdapter.addArrPatientInfo(arrPatientInfo);
        mRvPatientListAdapter.notifyDataSetChanged();
        mRvPatientList.setAdapter(mRvPatientListAdapter);
        mRvPatientList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRvPatientList.setSelected(true);
            }
        });

        // 환자 데이터 없을 시 노출 문구
        if (mRvPatientListAdapter.getItemCount() == 0) {
            patientEmpty.setVisibility(View.VISIBLE);
            mRvPatientList.setVisibility(View.GONE);
        } else {
            patientEmpty.setVisibility(View.GONE);
            mRvPatientList.setVisibility(View.VISIBLE);
        }

        // 환자 추가 버튼 클릭
        patientAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputPatientDialog inputPatientDialog = new InputPatientDialog(MedicalCareMainActivity.this, new InputPatientInfoListener() {
                    @Override
                    public void onInputPositiveClick(Object patientInfoDTO) {
                        mRvPatientListAdapter.addUser((PatientInfoDTO) patientInfoDTO);
                        mRvPatientListAdapter.notifyDataSetChanged();

                        if (mRvPatientListAdapter.getItemCount() != 0) {
                            patientEmpty.setVisibility(View.GONE);
                            mRvPatientList.setVisibility(View.VISIBLE);
                        }

                        // 환자 추가시 자동 스크롤
                        int position = mRvPatientListAdapter.getIndexUser((PatientInfoDTO) patientInfoDTO);
                        mRvPatientList.smoothScrollToPosition(position);
                    }

                    @Override
                    public void onInputNegativeClick() {

                    }
                });
                inputPatientDialog.show();
                setDialogsize(inputPatientDialog);   // 다이얼로그 사이즈 변경
                mRvSendLogList.setAdapter(mRvSendLogAdapter);
            }
        });

        // 전송 기록
        mRvSendLogList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRvSendLogAdapter = new RvSendLogAdapter();
//        getSendLogData();
//        mRvSendLogAdapter.addArrSendHistory(arrSendLog);
//        mRvSendLogAdapter.notifyDataSetChanged();
//        mRvSendLogList.setAdapter(mRvSendLogAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSendLogData();

        // 환자 데이터 없을 시 노출 문구
        if (mRvSendLogAdapter.getItemCount() == 0) {
            sendLogEmpty.setVisibility(View.VISIBLE);
            mRvSendLogList.setVisibility(View.GONE);
        } else {
            sendLogEmpty.setVisibility(View.GONE);
            mRvSendLogList.setVisibility(View.VISIBLE);
        }
    }

    // 기기 설정 버튼
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

    public void setDialogsize(InputPatientDialog inputPatientDialog) {
        // 다이얼로그 사이즈
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Window window = inputPatientDialog.getWindow();
        // 생년 numPicker일 때 화면 크기
//        int x = (int) (size.x * 0.35f);
//        int y = (int) (size.y * 0.6f);

        // 생년 edit일 때 화면 크기
        int x = (int) (size.x * 0.33f);
        int y = (int) (size.y * 0.5f);

        window.setLayout(x, y);
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
        String toolBarTitle = "유연의료 건강 측정";
        mTitle.setText(toolBarTitle);
    }

    // 전송 기록 테스트 데이터
    public void getSendLogData() {
        String sendLogStr = SharedPreferencesAPI.getInstance(MedicalCareMainActivity.this).getRecodePatient();
        GLog.d("sendLogStr == " + sendLogStr);

        if (!TextUtils.isEmpty(sendLogStr)) {
            try {
                JSONObject json = new JSONObject(sendLogStr);
                JSONArray itemJsonArray = new JSONArray(json.getString("item"));

                GLog.d("jsonArray length == " + itemJsonArray.length());

                for (int i = 0; i < itemJsonArray.length(); i++) {
                    GLog.d("itemJsonArray [" + i + "] == " + itemJsonArray.get(i));
//                                    GLog.d("item " + item);
                    JSONObject jsonObject = new JSONObject(itemJsonArray.get(i).toString());
//                    JSONObject sendLogJson = new JSONObject();

                    SendLogDTO sendLogDTO1 = new SendLogDTO(jsonObject.getString("time"), jsonObject.getString("name"), jsonObject.getString("id"), jsonObject.getString("sendLog"));
//                    sendLogJson.put("name", jsonObject.getString("name"));
//                    sendLogJson.put("id", jsonObject.getString("id"));
//                    sendLogJson.put("sendLog", jsonObject.getString("sendLog"));
//                    jArray.put(sendLogJson);
//                    GLog.d("jArray  == " + jArray);
                    arrSendLog.add(sendLogDTO1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mRvSendLogAdapter.setArrSendHistory(arrSendLog);
            mRvSendLogAdapter.notifyDataSetChanged();
        }
        GLog.d();
    }

    // 테스트 샘플 데이터
    public void sampleData() {
        // 환자 목록
//        for (int i = 0; i < 20; i++) {
//            int patientIdNum = 12345123 + i;
//            patientInfoDTO = new PatientInfoDTO("", patientIdNum);
//            patientInfoDTO.setNum(String.valueOf(i + 1));
//            if (i % 2 == 0) {
//                patientInfoDTO.setGender("여");
//            } else {
//                patientInfoDTO.setGender("남");
//            }
//            patientInfoDTO.setName("이지영" + i);
//            patientInfoDTO.setBornYear(String.valueOf(1953 + i));
//            arrPatientInfo.add(patientInfoDTO);
//        }

        // 전송 기록
//        for (int i = 0; i < 20; i++) {
//            long now = System.currentTimeMillis();
//            Date date = new Date(now);
//            simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//            time = simpleDate.format(date);
//
//            sendLogDTO = new SendLogDTO("", "");
//            sendLogDTO.setTime(time);
//            sendLogDTO.setName("이지영" + i);
//            if (i % 2 == 0) {
//                sendLogDTO.setSendLog("혈압, 혈당, 체중");
//            } else {
//                sendLogDTO.setSendLog("혈당, 체중");
//            }
//            arrSendLog.add(sendLogDTO);
//        }
    }

}