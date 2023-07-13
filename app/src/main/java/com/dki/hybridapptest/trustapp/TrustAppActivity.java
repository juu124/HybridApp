package com.dki.hybridapptest.trustapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dki.hybridapptest.R;
import com.dreamsecurity.trustapp.TrustAppToolkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class TrustAppActivity extends AppCompatActivity {
    private final String TAG = "TRUSTAPP";


    // http://192.168.0.35:8443/AppAdmin5.0/trustapp/

    // APPLICATION_ID는 앱 버전이 바뀔 때마다 새로 생성된 것을 받아야 한다.
    // 기존 버전과 동일하게 설정하면 두 앱의 해쉬가 다르므로 한 버전은 반드시 오류가 발생한다.

    // APK
    // AAB ecs.timeservice.org
    private String APPLICATION_ID = "MIICuQSCAbEwggGtBBBhbmRyb2lkX3Rlc3RfMDAyBBtjb20uZXhhbXBsZS50cnVzdGFwcGFuZHJvaWQEDjIwMjMwMzA2MTY0NDExBDBodHRwOi8vZWNzLnRpbWVzZXJ2aWNlLm9yZy9BcHBBZG1pbjUuMC90cnVzdGFwcC8EggEmMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtArc1WKsiCRJyTq3CBcjE9Wm02OVg7PAVwjXcpMcM890g7Anzy6h6m/VXiPt+VSbk6dIgazdm9VJagCf+H3u/DTX0nrSNOySMziv74+8/7hwIOCFMAbdbXpwLhp4vCFZEy+YkCnGND53ZvGc7SfSwJKVEdRUfxRtu9kaEo55kAIwZ01WpBTiVYBHdACdRTOIkah2dWCHbRVG+HS8DVQHJaKtzJcZxfiKm6/vcyEjDjWazLox/a2vqK47z5vfOZ+RmQPt5Aq/kxj3I3KKdPYSCVtCfGSGoZob9nXWaioeA93dVtZXFcXKchKemAV5HxuH0JnK0MRAtDN966+pflU3BwIDAQABBBBhbmRyb2lkX3Rlc3RfMDAyBIIBAKphX5DP9hZpC5laKMHVRYUEjyKi/itV8+xM7E7icSrLTftU/kvub9LXyjSX5CzibEO2Zzx7Caogd3PeaEb0JSg5E6kNEDgdPG3wTlWuwVOmSbnx6m62gTDWDILXq1dTDxpTkwRLbkVuIXvf+p+I5VL1dRzFVTgxvNTGvx0tsxcDz95F9wP4bEY7NAQjQOYZo9L4MXG3vl/EsPdqjHCRDVItBCzAkxeX6UqWLRcHV7ZKwnlShmhlFEzfmBvAQY9/tcA8F/kBMl4MsG4RZ3FZEgfHVPYxVQT4+SxqZQNY6ioYZtajmnXAB5mrhNIPglmhENGKV5pbbakpV4L/5m8+JyQ=";

    // 샘플용 서버 검증 URL
    private static final String SERVER_URL_VERIFY = "http://ecs.timeservice.org/AppAdmin5.0/trustapp_svr/trustapp_server_demo.jsp";

    // TARGET_CLIENT:[TRUSTAPP]
    // NAME:[com.example.trustappandroid],CODE=[F2C984EFA0C5C59755B4]
    private static final String TRUSTAPP_LIC_CODE = "F2C984EFA0C5C59755B4";

    // 서버로 전송하여 검증할 데이터
    private static String TRUSTAPP_TOKEN = "";
    private static String TRUSTAPP_CLIENT_VALUE = "";

    // Self Sign SSL 인증서 등록 : 정식 인증서일 경우 호출할 필요 없으나, 사설인증서일 경우 등록 필요
    private static String PEM_SSL_CERT = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDaDCCAlCgAwIBAgIJAOzVPBN1gwFiMA0GCSqGSIb3DQEBCwUAMGIxCzAJBgNV\n" +
            "BAYTAktSMQ4wDAYDVQQIEwVTZW91bDEOMAwGA1UEBxMFU2VvdWwxDDAKBgNVBAoT\n" +
            "A3R0YTEMMAoGA1UECxMDZGV2MRcwFQYDVQQDEw4yMTAuMTA0LjE4MS4zNDAeFw0y\n" +
            "MTA5MjcwODM2MDRaFw0yMTEyMjYwODM2MDRaMGIxCzAJBgNVBAYTAktSMQ4wDAYD\n" +
            "VQQIEwVTZW91bDEOMAwGA1UEBxMFU2VvdWwxDDAKBgNVBAoTA3R0YTEMMAoGA1UE\n" +
            "CxMDZGV2MRcwFQYDVQQDEw4yMTAuMTA0LjE4MS4zNDCCASIwDQYJKoZIhvcNAQEB\n" +
            "BQADggEPADCCAQoCggEBAKDjIwMjjHK/uBPy8dlPsfHjYfChCywqt/hxT82bPwFK\n" +
            "+FiYeMMapr+jG2cc3UOyg5nSqj9xrTPvs1XGsNXf6UieEf0WU13XQmQso6aC/V0e\n" +
            "pdl1+MSjawdVe1mzINeNSY0fbhD1rVUjfQdw0mc4RoOqIcWs9++vpeg5LG80oczv\n" +
            "a5ycGTqDfRVFzBC8WWVfAA6knPAGbE7yGNJkYLHmL8m2hpQzZfh2M42QR6jVD/gM\n" +
            "yvnq80o9/yXdOTroQa//szqSpv9RLT6SyTPBGFtni8WDcz7CB6XQsshDiSc8SbZU\n" +
            "5WVztB7Q8ARccM5jz5T9mYZWTM1HHS+mhWHQdvH1oMkCAwEAAaMhMB8wHQYDVR0O\n" +
            "BBYEFFjGnvpBhp69wPOO7TX97RlFBdxQMA0GCSqGSIb3DQEBCwUAA4IBAQB6muhL\n" +
            "P7S3HHotDaIUxsbJuhCxAn3n514/uXib/RBOUczXRkxu66GqVoqye8iTgFjyFnlD\n" +
            "rjx+H0ntWELTa5IDoGTDQwaQOidr4nmZKrUMsWiEgA9StXXU0RpYTjNg10ghKuxf\n" +
            "p7Jd7dpsbW197l20KVAhRlX3qWPGTW1iOwSWaO2TExp+PLbXu8Ym6HtqMmnaXYbw\n" +
            "FNUo5K7kNj2cstZqD2cZhPF2D78InTkC7Qvw4pEvGW8JtJtIWT8Mah3b3+Rrclbj\n" +
            "IqmAoq9vTZKSGwoe3W9S3YrYgaeb1+kzwLhM7V2INeGKkQvMvQYnoqqkpgpYlPIs\n" +
            "kDiF/eX0E4pxkda4\n" +
            "-----END CERTIFICATE-----";

    private static Context ctx;
    private Button btnStart;            // 앱위변조 검증 테스트 버튼
    private static TextView txtResult;            // 테스트 결과값 확인 필드
    private static String txtData = "";

    static final int READ_PHONE_STATE_PERMISSON = 1;

    public static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                txtResult.setText(txtData);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apkPath = getApplicationInfo().sourceDir;
        Log.d("Main", "111122228=" + apkPath);

        // License / ApplicationID SET
        TrustAppUI.PACKAGE_LIC = TRUSTAPP_LIC_CODE;
        TrustAppToolkit.setApplicationID(APPLICATION_ID);
        TrustAppToolkit aap = new TrustAppToolkit();
        aap.setDebugMode(true);

        // Self Sign SSL 인증서 등록 : 정식 인증서일 경우 호출할 필요 없으나, 사설인증서일 경우 등록 필요
        // TrustAppToolkit.AddTrustedCert(PEM_SSL_CERT);

        // 앱위변조 검증(해쉬계산을 먼저한다) : 속도 개선을 위해 처음에 넣어준다.
        // TrustAppToolkit.i(context, check_rooting, check_tegrak_app)
        TrustAppToolkit.i(this, false, false);

        ctx = TrustAppActivity.this;


        txtResult = (TextView) findViewById(R.id.txt_result);
        txtResult.setText("RESULT");

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 앱위변조 검증 테스트 시작
                startTrustApp();
            }
        });


        // 권한이 부여되어 있는지 확인
        // 샘플이며, Google 정책에 따라 바뀔 수 있음
        int permissonCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissonCheck == PackageManager.PERMISSION_GRANTED) {
            // Toast.makeText(getApplicationContext(), "READ_PHONE_STATE YES", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "READ_PHONE_STATE NO", Toast.LENGTH_SHORT).show();

            //권한설정 dialog에서 거부를 누르면
            //ActivityCompat.shouldShowRequestPermissionRationale 메소드의 반환값이 true가 된다.
            //단, 사용자가 "Don't ask again"을 체크한 경우
            //거부하더라도 false를 반환하여, 직접 사용자가 권한을 부여하지 않는 이상, 권한을 요청할 수 없게 된다.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                //이곳에 권한이 왜 필요한지 설명하는 Toast나 dialog를 띄워준 후, 다시 권한을 요청한다.
                Toast.makeText(getApplicationContext(), "READ_PHONE_STATE 권한이 필요합니다", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_PERMISSON);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_PERMISSON);
            }
        }
    }


    /* AppDefence 앱위변조 검증 테스트 : 검증서버에 서버검증토큰을 요청하여 클라이언트검증값을 생성한다. */
    private void startTrustApp() {
        Log.d(TAG, "startTrustApp() start");

        // Callback 함수 정의
        TrustAppUI.OnAAPlusFinish callback = new TrustAppUI.OnAAPlusFinish() {

            // 앱위변조 검증을 위해 검증서버로 서버검증토큰을 요청하고, 서버검증토큰으로 클라이언트 검증값을 생성한다.
            public void onAAPlusFinish(boolean result, String strToken, String strClientValue, String strError) {

                if (result == true) {
                    // 샘플 로그인 수행
                    TRUSTAPP_TOKEN = strToken;                  // 받은 토큰
                    TRUSTAPP_CLIENT_VALUE = strClientValue;     // 토큰으로 생성한 클라이언트검증값

                    loginProcess();
                } else {
                    /*
                    여기에서 에러가 발생하는 경우는
                    1. 토큰에 포함된 프로그램 해쉬값이 계산한 프로그램 해쉬값과 일치하지 않음
                    2. 토큰 이상 (통신 이상으로 인해 전체를 못 받은 경우)
                    3. 해당 ApplicationID 가 사용 중지 상태, 해당 DeviceID(폰마다 등록)가 사용 중지 상태
                    4. 서버 다운 또는 네트워크 오류
                       네트워크 오류(인터넷이 안되는 환경)는 프로그램에서 미리 체크해야 한다.
                     */
                    alert("ERROR", strError);
                }
            }
        };

        // Callback 함수 Set
        TrustAppUI.setFinishCallback(callback);

        // 위변조검증 시작
        TrustAppUI.StartTrustApp_Start(
                TrustAppActivity.this,        // UI를 위한 CONTEXT
                "SampleAndroid",        // App 명칭 : 이력 관리용
                null                    // 화면 출력용 문자열 : 없을 경우 default 문자열
        );
    }


    public static void alert(String title, String value) {
        AlertDialog successAlt = new AlertDialog.Builder(ctx)
                .setTitle(title)
                .setMessage(value)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked OK so do some stuff */
                        System.exit(0);
                    }
                })
                .create();
        successAlt.show();
    }


    /*
    앱위변조 검증 테스트 : 샘플임!!
    서버검증토큰 과 클라이언트검증값을 서버에 전달하여 검증받고
    리턴된 서버검증값을 처리한다.
    */
    private void loginProcess() {
        Log.d(TAG, "loginProcess() start");

        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuffer sbLog = new StringBuffer();

                Log.d(TAG, "SERVER TOKEN : [" + TRUSTAPP_TOKEN + "]");
                Log.d(TAG, "CLIENT VALUE : [" + TRUSTAPP_CLIENT_VALUE + "]");

                // Send/Receive AppDefence Server
                String strPostData = ""; // SERVER_URL_VERIFY;
                strPostData = "TRUSTAPP_TOKEN=" + TRUSTAPP_TOKEN + "&TRUSTAPP_CLIENT_VALUE=" + TRUSTAPP_CLIENT_VALUE;

                Log.d(TAG, "TEST URL : [" + SERVER_URL_VERIFY + "]");
                Log.d(TAG, "TEST DATA : [" + strPostData + "]");
                sbLog.append("TEST URL : [").append(SERVER_URL_VERIFY).append("]\n\n");
                sbLog.append("TEST DATA : [").append(strPostData).append("]\n\n");

                try {
                    // 서버검증토큰과 클라이언트검증값을 전달하고 앱위변조검증 결과값을 전달받는다. 전달받은 서버검증값은 API를 통해 set 해준다.
                    byte[] bt = sendServer(SERVER_URL_VERIFY, strPostData);
                    String serverValue = new String(bt);
                    serverValue = serverValue.replaceAll("((<|>|\r\n|\r|\n|\\p{Space}))", "");

                    Log.d(TAG, "serverValue : " + serverValue);
                    sbLog.append("SERVER VALUE : [").append(serverValue).append("]\n\n");

                    // Check ErrorMessage
                    if (serverValue.indexOf("[[[TRUSTAPPRESULT:OK]]]") > -1) {
                        Log.d(TAG, "APP위변조 검증결과 이상없음");
                        sbLog.append("APP위변조 검증결과 이상없음");

                        txtData = sbLog.toString();

                        handler.sendEmptyMessage(0);

                    } else {
                        Log.d(TAG, "APP위변조 검증결과 오류");
                        sbLog.append("APP위변조 검증결과 오류");

                        txtData = sbLog.toString();

                        handler.sendEmptyMessage(0);

                    }


                } catch (Exception e) {
                    Log.e(TAG, "ERROR : " + e.toString());
                    sbLog.append("Error : ").append(e.toString()).append("\n");
                    txtResult.setText(sbLog.toString());
                }
            }
        }).start();


    }

    private byte[] sendServer(String urlData, String strPostData) {
        try {
            // TLS 송수신 샘플함수 입니다. 테스트용으로 제작되어 안정성을 보장할 수 없으므로
            // 실 업무에서는 제거해 주세요.

            // public static byte[] SamplePostRequest
            // (String addr, byte[] btContent, String content_type, int nTimeOutSec)
            byte[] btResponse = TrustAppToolkit.SamplePostRequest(urlData, strPostData.getBytes(),
                    "application/x-www-form-urlencoded", 5);
            return btResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return "".getBytes();
        }
    }

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int grantResults[]) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_PHONE_STATE_PERMISSON:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "READ_PHONE_STATE 승인함", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "READ_PHONE_STATE 거부함", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}