package com.dki.hybridapptest.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

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

    public void moveToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean permissionDenied = true;
        if (requestCode == permissionReqCode) {
            if (permissions != null) {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        permissionDenied = false;
                        break;
                    }
                }
            }

            GLog.d("permisisonDenied : " + permissionDenied);

            if (permissionDenied) {
                moveToMainActivity();
            }
        }
    }
}