package com.dki.hybridapptest.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dki.hybridapptest.DeviceInfo;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.utils.GLog;

public class SWInfoActivity extends AppCompatActivity {
    private TextView tvOSInfo;
    private TextView tvModelInfo;
    private TextView tvBiometricInfo;
    private TextView tvAppVersionInfo;
    private TextView tvFirstLaunchInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swinfo);

        tvOSInfo = findViewById(R.id.tv_OS_info);
        tvModelInfo = findViewById(R.id.tv_model_info);
        tvBiometricInfo = findViewById(R.id.tv_biometric_info);
        tvAppVersionInfo = findViewById(R.id.tv_app_version_info);
        tvFirstLaunchInfo = findViewById(R.id.tv_fist_launch_info);
        GLog.d("getDeviceOs === " + DeviceInfo.getDeviceOs());
        GLog.d("getDeviceModel === " + DeviceInfo.getDeviceModel());
    }
}