//package com.dki.hybridapptest.activities;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.dki.hybridapptest.BuildConfig;
//import com.dki.hybridapptest.utils.DeviceInfo;
//import com.dki.hybridapptest.R;
//
//public class SWInfoActivity extends AppCompatActivity {
//    private DeviceInfo mDeviceInfo;
//    private TextView tvOSInfo; // 단말 OS 정보
//    private TextView tvModelInfo; // 단말 모델명(기종) 정보
//    private TextView tvBiometricInfo; // 단말 사용 가능한 생체 인증 목록 정보
//
//    // 앱 버전 정보
//    private TextView tvAppVersionInfo;
//    private String appVersion;
//
//    // 앱 최초 실행 여부
//    private TextView tvFirstLaunchInfo;
//    private SharedPreferences sharedPreferences;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_swinfo);
//
//        mDeviceInfo = new DeviceInfo();
//
//        tvOSInfo = findViewById(R.id.tv_OS_info);
//        tvModelInfo = findViewById(R.id.tv_model_info);
//        tvBiometricInfo = findViewById(R.id.tv_biometric_info);
//        tvAppVersionInfo = findViewById(R.id.tv_app_version_info);
//        tvFirstLaunchInfo = findViewById(R.id.tv_fist_launch_info);
//        appVersion = BuildConfig.VERSION_NAME;
//
//        tvOSInfo.setText(mDeviceInfo.getDeviceOs());
////        tvModelInfo.setText(mDeviceInfo.getDeviceBrand() + " " + mDeviceInfo.getDeviceModel());
////        tvBiometricInfo.setText(DeviceInfo.isFingerprintSensorAvailable(getApplicationContext()));
//        tvBiometricInfo.setText(mDeviceInfo.getDeviceInfo(this));
//        tvAppVersionInfo.setText(appVersion);
//
//        // 앱 최초 실행 여부
//        sharedPreferences = getSharedPreferences("mPreferences", Context.MODE_PRIVATE);
//        boolean isFirstRun = sharedPreferences.getBoolean("firstRun", true);
//
//        if (isFirstRun) {
//            tvFirstLaunchInfo.setText(String.valueOf(isFirstRun));
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("firstRun", false);
//            editor.apply();
//        } else {
//            tvFirstLaunchInfo.setText(String.valueOf(isFirstRun));
//        }
//    }
//}