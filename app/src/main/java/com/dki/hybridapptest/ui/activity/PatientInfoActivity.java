package com.dki.hybridapptest.ui.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.Interface.AllCheckListener;
import com.dki.hybridapptest.Interface.InputRecodePatientInfoListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dialog.CustomDialog;
import com.dki.hybridapptest.dto.PatientDeviceDTO;
import com.dki.hybridapptest.dto.RecodePatientDTO;
import com.dki.hybridapptest.ui.adapter.RvDeviceListAdapter;
import com.dki.hybridapptest.ui.adapter.RvRecodePatientListAdapter;
import com.dki.hybridapptest.utils.GLog;
import com.dki.hybridapptest.utils.SharedPreferencesAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class PatientInfoActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private Toolbar toolbar;
    private TextView mTitle;
    private Intent mIntent;

    // 환자 정보
    private TextView patientName;
    private TextView patientId;
    private String name;
    private String id;

    // 기기 목록
    private TextView tvDeviceEmpty;
    private RecyclerView mRvConnectDevice;
    private RvDeviceListAdapter mRvDeviceListAdapter;
    private PatientDeviceDTO patientDevice;
    private ArrayList<PatientDeviceDTO> arrPatientDevice = new ArrayList<>();

    // 측정 기록 목록
    private TextView tvRecodeEmpty;
    private RecyclerView mRvRecodePatient;
    private RvRecodePatientListAdapter mRvRecodePatientListAdapter;
    private RecodePatientDTO recodePatientDTO;
    private ArrayList<RecodePatientDTO> arrRecodePatient;
    private ArrayList<RecodePatientDTO> arrCheckRecodePatient;
    private ArrayList<RecodePatientDTO> arrSetCheckList;
    private CheckBox allCheck;
    private String dataType;
    private int beforeCount;

    private RecodePatientDTO checkBloodPressureResult;
    private RecodePatientDTO checkBloodSugarResult;

    // 측정 기록 추가
    private InputRecodePatientInfoListener inputRecodePatientInfoListener;
    private JSONObject obj = new JSONObject();
    private HashMap<String, RecodePatientDTO> checkMap = new HashMap<>(); // 측정 기록 체크
    private SimpleDateFormat simpleDate;                                  // 측정 기록 날짜
    private String time;                                                  // 측정 기록 날짜
    private AllCheckListener allCheckListener;

    // 측정값 전송
    private Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        // null로 초기화하기 (환자 > 메인화면 이동시 전송 리스트 초기화)
        SharedPreferencesAPI.getInstance(PatientInfoActivity.this).setRecodePatient("");

        toolbar = findViewById(R.id.medical_tool_bar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        mRvConnectDevice = findViewById(R.id.rv_conn_device);
        mRvRecodePatient = findViewById(R.id.rv_recode_patient);
        patientName = findViewById(R.id.tv_patient_name);
        patientId = findViewById(R.id.tv_patient_id);
        sendBtn = findViewById(R.id.send_btn);
        tvDeviceEmpty = findViewById(R.id.tv_conn_device_empty);
        tvRecodeEmpty = findViewById(R.id.tv_recode_device_empty);
        allCheck = findViewById(R.id.recode_check_box);

        // 타이틀 UI displayHeader값 들어오기 전 초기화
        titleBarInit();

        // 클릭한 환자 정보
        mIntent = getIntent();
        name = mIntent.getStringExtra("name");
        id = mIntent.getStringExtra("patientId");
//        GLog.d("name == " + name);
//        GLog.d("id == " + id);
        patientName.setText(name);
        patientId.setText(id);

        // 테스트 샘플 데이터 (기기목록)
        sampleDataDevice();

        // 선택 기기 종류에 맞는 측정 기록 데이터 추가 리스너
        inputRecodePatientInfoListener = new InputRecodePatientInfoListener() {
            @Override
            public void onInputPositiveClick(String patientDeviceType, boolean isChecked) {

                // 기기 종류에 따른 샘플 데이터 추가
                addTestRecodeData(patientDeviceType, isChecked);

                // 환자 데이터 없을 시 노출 문구
                if (mRvRecodePatientListAdapter.getItemCount() == 0) {
                    tvRecodeEmpty.setVisibility(View.VISIBLE);
                    mRvRecodePatient.setVisibility(View.GONE);
                } else {
                    tvRecodeEmpty.setVisibility(View.GONE);
                    mRvRecodePatient.setVisibility(View.VISIBLE);

                    // 환자 추가시 자동 스크롤
                    int position = mRvRecodePatientListAdapter.getIndexUser(recodePatientDTO);
                    mRvRecodePatient.smoothScrollToPosition(position);

                }
            }

            @Override
            public void onInputNegativeClick() {

            }
        };

        // 전체 체크 선택
        allCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAllRecode();
            }
        });

        // 기기목록
        mRvConnectDevice.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRvDeviceListAdapter = new RvDeviceListAdapter(inputRecodePatientInfoListener);
        mRvDeviceListAdapter.addArrPatientDevice(arrPatientDevice);
        mRvDeviceListAdapter.notifyDataSetChanged();
        mRvConnectDevice.setAdapter(mRvDeviceListAdapter);


        // 환자 데이터 없을 시 노출 문구
        if (mRvDeviceListAdapter.getItemCount() == 0) {
            tvDeviceEmpty.setVisibility(View.VISIBLE);
            mRvConnectDevice.setVisibility(View.GONE);
        } else {
            tvDeviceEmpty.setVisibility(View.GONE);
            mRvConnectDevice.setVisibility(View.VISIBLE);
        }

        // 측정 기록
        mRvRecodePatient.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRvRecodePatientListAdapter = new RvRecodePatientListAdapter();
//        mRvRecodePatientListAdapter = new RvRecodePatientListAdapter();
//        mRvRecodePatientListAdapter.addArrSendHistory(arrRecodePatient);
//        mRvRecodePatientListAdapter.notifyDataSetChanged();
        mRvRecodePatient.setAdapter(mRvRecodePatientListAdapter);

        // 테스트 샘플 데이터 (측정기록)
//        sampleDataPatient();

        // 측정값 전송 버튼
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRvRecodePatientListAdapter.getItemCount() != 0) {
                    if (mRvRecodePatientListAdapter.getCheckedList().size() != 0) {
                        // 측정 기록 type (혈압, 혈당 등)
                        arrRecodePatient = mRvRecodePatientListAdapter.getCheckedList();

                        // SharedPreferences에 저장된 측정 기록
                        String sendLogList = SharedPreferencesAPI.getInstance(PatientInfoActivity.this).getRecodePatient();

                        // 측정 기록 배열 add (JSON 형태)
                        sendLogParser(arrRecodePatient, sendLogList);

                        Toast.makeText(PatientInfoActivity.this, "전송 완료", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(PatientInfoActivity.this, "측정값을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // 전체 체크를 한뒤 check목록에서  기존 숫자보다 낮아지면
        if (mRvRecodePatientListAdapter.getCheckedList().size() == 0) {
        }

    }

    // 기기 종류에 따른 샘플 데이터 추가
    public void addTestRecodeData(String patientDeviceType, boolean isChecked) {
        arrRecodePatient = new ArrayList<>();
        dataType = patientDeviceType;

        if (!TextUtils.isEmpty(patientDeviceType)) {
            // 현재 시간
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            time = simpleDate.format(date);

            // 종류에 따른 샘플 데이터 추가
            recodePatientDTO = new RecodePatientDTO();
            recodePatientDTO.setTime(time);

            // check 초기화
            recodePatientDTO.setChecked(isChecked);

            if (TextUtils.equals(patientDeviceType, "혈압")) {
                GLog.d("혈압");
                recodePatientDTO.setType("혈압");
                recodePatientDTO.setRecodePatient("수축기 (156.0), 이완기 (87.3), 맥박수 (80.0)");
            } else if (TextUtils.equals(patientDeviceType, "혈당")) {
                GLog.d("혈당");
                recodePatientDTO.setType("혈당");
                recodePatientDTO.setRecodePatient("-");
            } else {
                GLog.d("체중");
                recodePatientDTO.setType("체중");
                recodePatientDTO.setRecodePatient("-");
            }

            checkMap.put(patientDeviceType, recodePatientDTO);
            mRvRecodePatientListAdapter.duplicatedRecodePatient(checkMap);

            ArrayList<RecodePatientDTO> arrRecode = mRvRecodePatientListAdapter.getList();
            for (int i = 0; i < mRvRecodePatientListAdapter.getItemCount(); i++) {
                arrRecode.get(i).setChecked(false);
            }
            mRvRecodePatientListAdapter.getCheckedList().clear();
            if (mRvRecodePatientListAdapter.getCheckedList().size() == 0) {
                allCheck.setChecked(false);
            }
        }
        mRvRecodePatientListAdapter.addSendLog(recodePatientDTO);
        mRvRecodePatientListAdapter.notifyDataSetChanged();
    }

    // 측정 기록 배열 add (JSON 형태)
    public void sendLogParser(ArrayList<RecodePatientDTO> arrRecodePatient, String sendLogList) {
        // SharePreference에 측정 type이 있을 때
        try {
            if (!TextUtils.isEmpty(sendLogList)) {
                JSONObject json = new JSONObject(sendLogList);
                JSONArray itemJsonArray = new JSONArray(json.getString("item"));
                JSONArray jArray = new JSONArray();
                GLog.d("jsonArray length == " + itemJsonArray.length());

                for (int i = 0; i < itemJsonArray.length(); i++) {
                    GLog.d("itemJsonArray [" + i + "] == " + itemJsonArray.get(i));
                    JSONObject jsonObject = new JSONObject(itemJsonArray.get(i).toString());
                    JSONObject sendLogJson = new JSONObject();
                    sendLogJson.put("time", jsonObject.getString("time"));
                    sendLogJson.put("name", jsonObject.getString("name"));
                    sendLogJson.put("id", jsonObject.getString("id"));
                    sendLogJson.put("sendLog", jsonObject.getString("sendLog"));
                    jArray.put(sendLogJson);
                    GLog.d("jArray  == " + jArray);

                }
                String sendLog = null;
                JSONObject sObject = new JSONObject();

                // 측정 종류 (혈압, 혈당 등)
                for (int i = 0; i < arrRecodePatient.size(); i++) {
                    GLog.d("arrRecodePatient.size() " + arrRecodePatient.size());
                    if (sendLog != null) {
                        sendLog += arrRecodePatient.get(i).getType();
                    } else {
                        sendLog = arrRecodePatient.get(i).getType();
                    }
                }

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                time = simpleDate.format(date);

                sObject.put("time", time);
                sObject.put("name", name);
                sObject.put("id", id);
                sObject.put("sendLog", sendLog);
                jArray.put(sObject);

                obj.put("item", jArray); //배열을 넣음
                GLog.d("obj 3 == " + obj);
                SharedPreferencesAPI.getInstance(PatientInfoActivity.this).setRecodePatient(String.valueOf(obj));
                GLog.d("preferences value == " + SharedPreferencesAPI.getInstance(PatientInfoActivity.this).getRecodePatient());

            } else {
                // SharePreference에 측정 type이 없을 때
                JSONArray jArray = new JSONArray();
                String sendLog = null;
                JSONObject sObject = new JSONObject();

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                time = simpleDate.format(date);

                for (int i = 0; i < arrRecodePatient.size(); i++) {
                    if (sendLog != null) {
                        sendLog = sendLog + ", " + arrRecodePatient.get(i).getType();
                    } else {
                        sendLog = arrRecodePatient.get(i).getType();
                    }
                }

                sObject.put("time", time);
                sObject.put("name", name);
                sObject.put("id", id);
                sObject.put("sendLog", sendLog);
                jArray.put(sObject);

                obj.put("item", jArray);
                GLog.d("obj == " + obj);

                // SharedPreferences에 저장
                SharedPreferencesAPI.getInstance(PatientInfoActivity.this).setRecodePatient(String.valueOf(obj));
                GLog.d("preferences value == " + SharedPreferencesAPI.getInstance(PatientInfoActivity.this).getRecodePatient());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public void newInsertCheck() {
        GLog.d("beforeCount == " + beforeCount);
        GLog.d("mRvRecodePatientListAdapter.getItemCount() == " + mRvRecodePatientListAdapter.getItemCount());
        if (beforeCount != mRvRecodePatientListAdapter.getItemCount()) {
            mRvRecodePatientListAdapter.newDataCheck(true);
        } else {
            mRvRecodePatientListAdapter.newDataCheck(false);
        }
        mRvRecodePatientListAdapter.setArrSendHistory(arrCheckRecodePatient);
        mRvRecodePatientListAdapter.notifyDataSetChanged();
    }

    // checkBox 전체 선택
    public void checkAllRecode() {
        arrSetCheckList = mRvRecodePatientListAdapter.getCheckedList();
        if (allCheck.isChecked()) {
            arrCheckRecodePatient = mRvRecodePatientListAdapter.getList();
            checkBloodPressureResult = (RecodePatientDTO) checkMap.get("혈압");
            checkBloodSugarResult = (RecodePatientDTO) checkMap.get("혈당");

            String pressureTime = null;
            String sugarTime = null;

            if (checkBloodPressureResult != null) {
                pressureTime = checkBloodPressureResult.getTime();
            }
            if (checkBloodSugarResult != null) {
                sugarTime = checkBloodSugarResult.getTime();
            }

            for (int i = 0; i < arrCheckRecodePatient.size(); i++) {
                GLog.d("checkMap.size == " + checkMap.size());
//                GLog.d("arrCheckRecodePatient [" + i + "]" + " = " + checkBloodPressureResult.getTime());
//                GLog.d("checkBloodPressureResult time == " + checkBloodPressureResult.getTime());
                if (TextUtils.equals(arrCheckRecodePatient.get(i).getTime(), pressureTime)) {
                    arrCheckRecodePatient.get(i).setChecked(true);
                } else if (TextUtils.equals(arrCheckRecodePatient.get(i).getTime(), sugarTime)) {
                    arrCheckRecodePatient.get(i).setChecked(true);
                } else {
                    arrCheckRecodePatient.get(i).setChecked(false);
                }
                mRvRecodePatientListAdapter.setArrSendHistory(arrCheckRecodePatient);
                mRvRecodePatientListAdapter.notifyDataSetChanged();
            }
        } else {
            for (int i = 0; i < arrCheckRecodePatient.size(); i++) {
                arrCheckRecodePatient.get(i).setChecked(false);
            }
            arrSetCheckList.clear();
//            mRvRecodePatientListAdapter.setArrCheckList(arrRecodePatient);
            mRvRecodePatientListAdapter.setArrSendHistory(arrCheckRecodePatient);
            mRvRecodePatientListAdapter.notifyDataSetChanged();
        }
    }

    // 타이틀 UI 초기화
    public void titleBarInit() {
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
            if (i == 0) {
                patientDevice.setType("혈압");
                patientDevice.setDeviceName("AutoCheck");
            } else if (i == 1) {
                patientDevice.setType("혈당");
                patientDevice.setDeviceName("CareSens");
            } else if (i == 2) {
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
//        for (int i = 0; i < 3; i++) {
//            long now = System.currentTimeMillis();
//            Date date = new Date(now);
//            simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//            time = simpleDate.format(date);
//
//            recodePatientDTO = new RecodePatientDTO("", "");
//            recodePatientDTO.setTime(time);
//
//            if (i == 0) {
//                recodePatientDTO.setType("혈압");
//                recodePatientDTO.setRecodePatient("수축기 (156.0), 이완기 (87.3), 맥박수 (80.0)");
//            } else if (i == 1) {
//                recodePatientDTO.setType("혈당");
//                recodePatientDTO.setRecodePatient("-");
//            } else {
//                recodePatientDTO.setType("체중");
//                recodePatientDTO.setRecodePatient("-");
//            }
//            arrRecodePatient.add(recodePatientDTO);
//
        //  리스트 체크 중복 현상을 위한 데이터
////            if (i % 2 == 0) {
////                recodePatientDTO.setType("혈압");
////                recodePatientDTO.setRecodePatient("혈압, 혈당, 체중");
////            } else {
////                recodePatientDTO.setType("혈당");
////                recodePatientDTO.setRecodePatient("혈당, 체중");
////            }
////            arrRecodePatient.add(recodePatientDTO);
//        }
    }
}