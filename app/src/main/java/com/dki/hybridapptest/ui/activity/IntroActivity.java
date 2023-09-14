package com.dki.hybridapptest.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dki.hybridapptest.Interface.CustomDialogClickListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dialog.CustomDialog;
import com.dki.hybridapptest.trustapp.TrustAppManager;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.dki.hybridapptest.utils.Utils;
import com.dki.hybridapptest.vaccine.VaccineManager;

import org.json.JSONException;
import org.json.JSONObject;

import m.client.push.library.PushManager;
import m.client.push.library.common.PushConstants;
import m.client.push.library.common.PushLog;
import m.client.push.library.utils.PushUtils;

public class IntroActivity extends AppCompatActivity {
    public static Context mContext;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Intent intent;

    // 권한 코드
    private static final int permissionReqCode = 1000;

    // 앱 설정 화면
    private ActivityResultLauncher<Intent> appSettingsLauncher;

    // 백신
    private VaccineManager vaccineManager;
    public static boolean isRealTimeScanRunning = false;

    // 앱 위변조
    private TrustAppManager trustApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면 캡쳐 방지
        if (Constant.USE_SCREEN_SHOT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_intro);
        mContext = this;

        // 앱 위변조
        if (Constant.USE_TRUST_APP_DREAM) {
            trustApp = new TrustAppManager(IntroActivity.this);
        }

        // 모든 권한
        if (!Utils.isGrantedAllPermission(this)) {
            requestPermissions(Utils.getPermissionListAll().toArray(new String[0]), permissionReqCode);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startCheck();
                }
            }, 500);
        }

        // push 초기화 (앱 실행시마다 호출)
        // Manifest.xml 설정 파일에서 라이브러리를 초기화하기 위한 정보를 가져온다.
        // Parameters: context (Context) – 현재 Context
//        if (Constant.USE_PUSH_FIREBASE) {
//            PushManager.getInstance().initPushServer(this);
//        }
//        initPush("home", "work"); // Android Bridge에서 로그인 정보로 사용자 등록함 (자동 로그인 설정 눌러야함)

        // 설정에서 돌아온 후 권한 확인 (읽기 권한)
        appSettingsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (Utils.isGrantedAllPermission(this)) {  // 권한 허락 상태면 밖으로 빠지고, 권한 허락 못받았을 때 아래 구문 실행
                GLog.d("권한 허용");
                // 설정화면으로 이동
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startCheck();
                    }
                }, 500);
            } else {
                Toast.makeText(this, "필수 권한을 설정해주세요.", Toast.LENGTH_SHORT).show();
                CustomDialog customDialog = new CustomDialog(this, new CustomDialogClickListener() {
                    @Override
                    public void onPositiveClick(String text) {
                        moveToPermissionSetting();
                    }

                    @Override
                    public void onNegativeClick() {
                        // 권한 설정 거부
                        finish();
                    }
                }, "권한 알림", this.getResources().getString(R.string.permission_app_message), Constant.TWO_BUTTON, true);
                customDialog.setTwoButtonText("닫기", "설정");
                customDialog.setCancelable(false);
                customDialog.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Constant.USE_VACCINE_DREAM) {
            if (vaccineManager != null) {
                vaccineManager.stopVaccine();
            }
        }
    }

    // 백신 콜백 성공 후 메인 화면 이동
    public void vaccineResult(boolean isSuccess) {
        if (isSuccess) {
            moveToMainActivity();
        }
    }

    // 앱 위변조
    public void trustAppResult(boolean isSuccess) {
        GLog.d("trustAppresult : " + isSuccess);
        if (!isSuccess) {
//            Toast.makeText(IntroActivity.this, "APP위변조 검증결과 오류", Toast.LENGTH_SHORT).show();
            if (!IntroActivity.this.isFinishing()) {
                CustomDialog customDialog = new CustomDialog(IntroActivity.this, new CustomDialogClickListener() {
                    @Override
                    public void onPositiveClick(String text) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                }, "안내", getResources().getString(R.string.trust_app_error_message), Constant.ONE_BUTTON, true);
                customDialog.setCancelable(false);
                customDialog.show();
                customDialog.setOneButtonText("앱 종료");
            }
        } else {
            GLog.d("백신 작동");
            if (Constant.USE_VACCINE_DREAM) {
                VaccineAsyncTask vaccineAsyncTask = new VaccineAsyncTask();
                vaccineAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                moveToMainActivity();
            }
        }
    }

    public void setRealTimeScanRunning(boolean realTimeScanRunning) {
        isRealTimeScanRunning = realTimeScanRunning;
        GLog.d("setRealTimeScanRunning : isRealTimeScanRunning : " + isRealTimeScanRunning);
        if (!isRealTimeScanRunning) {
            handler.sendEmptyMessage(2000);
        }
    }

    public void startCheck() {
        GLog.d("startCheck");
        // 디버그 모드일때는 위변조 검사 하지 않음
        if (Constant.IS_DEBUG) {
            GLog.d("IS_DEBUG");
            if (Constant.USE_VACCINE_DREAM) {
                GLog.d("USE_VACCINE_DREAM");
                VaccineAsyncTask vaccineAsyncTask = new VaccineAsyncTask();
                vaccineAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                GLog.d("NONE_USE_VACCINE_DREAM");
                moveToMainActivity();
            }
        } else {
            if (Constant.USE_TRUST_APP_DREAM) {
                GLog.d("IS_NOT_DEBUG");
                TrustAppAsyncTask trustAppAsyncTask = new TrustAppAsyncTask();
                trustAppAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    // 위변조 앱 비동기 처리
    public class TrustAppAsyncTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            trustApp.startTrustApp(); //위변조 검사 시작
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    //백신 비동기 처리
    public class VaccineAsyncTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            GLog.d("백신 비동기 처리");
            if (Constant.USE_VACCINE_DREAM) {
                vaccineManager = new VaccineManager(IntroActivity.this); // 백신검사 시작
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    // 권한 확인
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionGranted = true;
        if (requestCode == permissionReqCode) {
            if (permissions != null) {
                for (int i = 0; i < permissions.length; i++) {
                    GLog.d("permission 권한 ===== " + permissions + " == " + grantResults[i]);
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) { // 권한 부여 실패
                        permissionGranted = false;
                        break;
                    }
                }
            }
            GLog.d("permissionGranted : " + permissionGranted);

            if (permissionGranted) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startCheck();
//                        moveToMainActivity();
                    }
                }, 700);
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

    // push 알림 권한 체크 android bridge에서 사용중
    private void initPush(String id, String cName) {
        GLog.d("push 알림 권한");
        final JSONObject params = new JSONObject();
        try {
            params.put(PushConstants.KEY_CUID, id);
            params.put(PushConstants.KEY_CNAME, cName);
            GLog.d("initPush ===== " + GLog.toJson(params));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (PushUtils.checkNetwork(this)) {
            GLog.d("push 권한 설정");
            PushManager.getInstance().registerServiceAndUser(IntroActivity.this, params);
        } else {
            PushLog.e("MainActivity", "network is not connected.");
            Toast.makeText(this, "[MainActivity] network is not connected.", Toast.LENGTH_SHORT).show();
        }
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
}