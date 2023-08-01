package com.dki.hybridapptest.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.dki.hybridapptest.Interface.CustomDialogClickListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dialog.CustomYesNoDialog;
import com.dki.hybridapptest.utils.GLog;

public class IntroActivity extends AppCompatActivity {
    private static final String[] permissionName = {Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS};
    private int permissionReqCode = 1000;
    private Intent intent;
    private ActivityResultLauncher<Intent> appSettingsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        GLog.d();
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(permissionName, permissionReqCode);
        }

        // 설정에서 돌아온 후 권한 확인
        appSettingsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            GLog.d("설정에서 다시 돌아와서 권한 확인하기 checkSomePermission == " + checkSomePermission());
            if (checkSomePermission()) {  // 권한이 허용되었을 경우에 대한 처리를 진행
                GLog.d("권한 허용");
                moveToMainActivity();
            } else {
                GLog.d("권한 비허용");
                Toast.makeText(this, "필수 권한을 설정해주세요.", Toast.LENGTH_SHORT).show();
                CustomYesNoDialog customDialog = new CustomYesNoDialog(this, new CustomDialogClickListener() {
                    @Override
                    public void onPositiveClick(String text) {
                        // 설정화면으로 이동
                        moveToPermissionSetting();
                    }

                    @Override
                    public void onNegativeClick() {
                        // 권한 설정 거부
                        finish();
                    }
                }, "권한 알림", this.getResources().getString(R.string.permission_app_message), true, "설정", "닫기");
                customDialog.setCancelable(false);
                customDialog.show();
            }
        });
    }

    // 권한 확인
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

            if (permissionGranted) {
                GLog.d("권한 허용");
                moveToMainActivity();
            } else {
                GLog.d("권한 비허용");
                moveToPermissionSetting();
            }
        }
    }

    // 메인 액티비티로 이동
    public void moveToMainActivity() {
        intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    // 권한 설정으로 이동
    public void moveToPermissionSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        try {
            intent.setData(Uri.parse("package:" + getPackageName()));
            appSettingsLauncher.launch(intent);
        } catch (Exception e) {
            GLog.d("usri parse 불가 == " + e);
        }
    }

    // 권한 여부
    private boolean checkSomePermission() {
        GLog.d();
        for (int i = 0; i < permissionName.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissionName[i]) != PackageManager.PERMISSION_GRANTED) { // 권한 부여 실패
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}