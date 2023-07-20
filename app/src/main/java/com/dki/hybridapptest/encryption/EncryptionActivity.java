package com.dki.hybridapptest.encryption;

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

public class EncryptionActivity extends AppCompatActivity {
    private final String TAG = "TRUSTAPP";


    // https://192.168.0.35:8443/AppAdmin4.5/trustapp/

    // APPLICATION_ID는 앱 버전이 바뀔 때마다 새로 생성된 것을 받아야 한다.
    // 기존 버전과 동일하게 설정하면 두 앱의 해쉬가 다르므로 한 버전은 반드시 오류가 발생한다.
    private String APPLICATION_ID = "MIICyASCAcAwggG8BBh0cnVzdGFwcGFuZHJvaWRfMjAyMTA1MDgEG2NvbS5leGFtcGxlLnRydXN0YXBwYW5kcm9pZAQOMjAyMTA1MDgxODQ1NTAEL2h0dHBzOi8vMTkyLjE2OC4wLjM1Ojg0NDMvQXBwQWRtaW40LjUvdHJ1c3RhcHAvBIIBJjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBALQK3NVirIgkSck6twgXIxPVptNjlYOzwFcI13KTHDPPdIOwJ88uoepv1V4j7flUm5OnSIGs3ZvVSWoAn/h97vw019J60jTskjM4r++PvP+4cCDghTAG3W16cC4aeLwhWRMvmJApxjQ+d2bxnO0n0sCSlRHUVH8UbbvZGhKOeZACMGdNVqQU4lWAR3QAnUUziJGodnVgh20VRvh0vA1UByWircyXGcX4ipuv73MhIw41msy6Mf2tr6iuO8+b3zmfkZkD7eQKv5MY9yNyinT2EglbQnxkhqGaG/Z11moqHgPd3VbWVxXFynISnpgFeR8bh9CZytDEQLQzfeuvqX5VNwcCAwEAAQQYdHJ1c3RhcHBhbmRyb2lkXzIwMjEwNTA4BIIBAIiccCJDx9E/50wGow+53umxwU6iqwCxdp5yizzmQKckwocIko0RHWYAdtZg083P/kQnfqdokR8pWfODDuhulH4T/G3nWij07D+xZirHqZ8XEPVRiz34I+tuVkoaWxyW2lRzeaQ7mJv/B6j2RaVm7Y+IpCj2GS2ZLzVZY0XaJdqmrsO/mqpJaHeHc/h4WOOcsOOCgR1qiI/v/kih8+lmfJVvHmQOZTy/yit4ON7RUZPhU2G7H4xk/MCFLm7lr/h7/dTu7oOgTFNCimETDeb6vYf0SblZ+Hhzmj+cRnGV9pGvQ0gX4KtcqXW+L6ANHxz0JEXK9bJnQtG9OLNG1KSnxzA=";

    // 샘플용 서버 검증 URL
    private static final String SERVER_URL_VERIFY = "https://192.168.0.35:8443/AppAdmin4.5/trustapp_svr/trustapp_server_demo.jsp";

    // TARGET_CLIENT:[TRUSTAPP]
    // NAME:[com.example.trustappandroid],CODE=[F2C984EFA0C5C59755B4]
    private static final String TRUSTAPP_LIC_CODE = "F2C984EFA0C5C59755B4";

    // 서버로 전송하여 검증할 데이터
    private static String TRUSTAPP_TOKEN = "";
    private static String TRUSTAPP_CLIENT_VALUE = "";

    // Self Sign SSL 인증서 등록 : 정식 인증서일 경우 호출할 필요 없으나, 사설인증서일 경우 등록 필요
    private static String PEM_SSL_CERT = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDZTCCAk2gAwIBAgIEM0ZY7TANBgkqhkiG9w0BAQsFADBjMQswCQYDVQQGEwJL\n" +
            "UjEQMA4GA1UECBMHVW5rbm93bjEQMA4GA1UEBxMHVW5rbm93bjEQMA4GA1UEChMH\n" +
            "ZXhhbXBsZTEMMAoGA1UECxMDZGV2MRAwDgYDVQQDEwdzc2xjZXJ0MB4XDTIxMDUw\n" +
            "ODA5MjQwMloXDTMxMDUwNjA5MjQwMlowYzELMAkGA1UEBhMCS1IxEDAOBgNVBAgT\n" +
            "B1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24xEDAOBgNVBAoTB2V4YW1wbGUxDDAK\n" +
            "BgNVBAsTA2RldjEQMA4GA1UEAxMHc3NsY2VydDCCASIwDQYJKoZIhvcNAQEBBQAD\n" +
            "ggEPADCCAQoCggEBAKT5M5gkyVeWUClb9lq8+DorZBoju3Qu0Lld7YuLI0JVNiwP\n" +
            "Mkc25qwew47WnQ5X3fr/AZa+fP9sDXtR3mitkpvK7D2LMhubiPCT3veCg1AAkfOb\n" +
            "32P+TnkWLaeXfPqXK/xFCe0JsC059BM+IIPt24YJsERm85i2GVlIVFjYRqTK2iM5\n" +
            "YFpw1RCwE6eDLpblN4AT9bnZKo5sq9O702h06mzVzkDp6qQx80usWeHnHL+t0+Ru\n" +
            "ATgu/ah6JdP/aqzMqFomlt9gNy8f19dkVFHu8y+FHSPAZ9YG/F4SnrFsCDlZ8r4w\n" +
            "znfon2MCy70nvLYoTXO+JLSkuU1IYVcUp/aJTrkCAwEAAaMhMB8wHQYDVR0OBBYE\n" +
            "FFmAbqBodTYPFOuZP9UHVcoEdO5UMA0GCSqGSIb3DQEBCwUAA4IBAQCTCU1aR3UP\n" +
            "cZd5u57ul88vL21v+wCsGHOKgiQvLZsBkI0Pt6RciMo4z4N3PbBNFSqmLu4FEDAg\n" +
            "JOtwL44IoIV2UpqHK2amy1tXyjLi1DmtIAm4v2q0MBGZmJvefei0hhUSghaET5E2\n" +
            "ZT2/FPlKsrpIrelxlCjvmiy9dWVvhjXzTIT6nqgzF6/tC5EmE5sPzodIv0BX1HaM\n" +
            "S5KzZ3b9B1lpeIyVa9kbOAkNcB/UzrhjKJt47734myI1F2JBEd07xo0QojU1OUIM\n" +
            "yIDAmEs2z67LzfxOwY7zfM7C9UAv8kOPKaOxHkK/XMsSEWAWXJqsJ2QplFnpKYWt\n" +
            "9PlK2VBWmQca\n" +
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
        setContentView(R.layout.activity_encryption);

        // License / ApplicationID SET
        TrustAppUI.PACKAGE_LIC = TRUSTAPP_LIC_CODE;
        TrustAppToolkit.setApplicationID(APPLICATION_ID);
        TrustAppToolkit aap = new TrustAppToolkit();
        aap.setDebugMode(true);

        // Self Sign SSL 인증서 등록 : 정식 인증서일 경우 호출할 필요 없으나, 사설인증서일 경우 등록 필요
        TrustAppToolkit.AddTrustedCert(PEM_SSL_CERT);

        // 앱위변조 검증(해쉬계산을 먼저한다) : 속도 개선을 위해 처음에 넣어준다.
        // TrustAppToolkit.i(context, check_rooting, check_tegrak_app)
        TrustAppToolkit.i(this, false, false);

        ctx = EncryptionActivity.this;


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
                EncryptionActivity.this,        // UI를 위한 CONTEXT
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
