package com.dki.hybridapptest.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.utils.GLog;

public class IntroActivity extends AppCompatActivity {
    private static final String[] permissionName = {Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS};
    private int permissionReqCode = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        GLog.d();
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(permissionName, permissionReqCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionGranted = true;
        if (requestCode == permissionReqCode) {
            if (permissions != null) {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) { // 권한 부여 실패
                        permissionGranted = false;
                        break;
                    }
                }
            }

            GLog.d("permissionGranted : " + permissionGranted);

            if (permissionGranted) {// 외부 터치 무시
                moveToMainActivity();
            } else {
                moveToPermissionSetting();
            }
        }
    }

    public void moveToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    public void moveToPermissionSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        try {
            intent.setData(Uri.parse("package:" + getPackageName()));
            finish();
        } catch (Exception e) {
            GLog.d("usri parse 불가 == " + e);
            return;
        }
        startActivity(intent);
        finish();
    }
}