package com.dki.hybridapptest.trustapp;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.dki.hybridapptest.Interface.CustomDialogClickListener;
import com.dki.hybridapptest.dialog.CustomDialog;
import com.dki.hybridapptest.ui.activity.IntroActivity;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.dki.hybridapptest.utils.Utils;
import com.dreamsecurity.trustapp.TrustAppToolkit;

// USE_TRUST_APP_DREAM
public class TrustAppManager {

    // APPLICATION_ID는 앱 버전이 바뀔 때마다 새로 생성된 것을 받아야 한다.
    // 기존 버전과 동일하게 설정하면 두 앱의 해쉬가 다르므로 한 버전은 반드시 오류가 발생한다.

    //개발 어플리케이션 아이디
//    private String APPLICATION_ID = "MIIC1gSCAc4wggHKBCFNWV9EQVRBX0FORFJPSURfVjEuMC4wX2Rldl8yMTA5MjkEFmtyLm9yLm15ZGF0YWNlbnRlci5wZHMEDjIwMjEwOTI5MTY1NjE5BDBodHRwczovL2RldnBkcy5teWRhdGFjZW50ZXIub3Iua3I6OTQ0My90cnVzdGFwcC8EggEmMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtArc1WKsiCRJyTq3CBcjE9Wm02OVg7PAVwjXcpMcM890g7Anzy6h6m/VXiPt+VSbk6dIgazdm9VJagCf+H3u/DTX0nrSNOySMziv74+8/7hwIOCFMAbdbXpwLhp4vCFZEy+YkCnGND53ZvGc7SfSwJKVEdRUfxRtu9kaEo55kAIwZ01WpBTiVYBHdACdRTOIkah2dWCHbRVG+HS8DVQHJaKtzJcZxfiKm6/vcyEjDjWazLox/a2vqK47z5vfOZ+RmQPt5Aq/kxj3I3KKdPYSCVtCfGSGoZob9nXWaioeA93dVtZXFcXKchKemAV5HxuH0JnK0MRAtDN966+pflU3BwIDAQABBCFNWV9EQVRBX0FORFJPSURfVjEuMC4wX2Rldl8yMTA5MjkEggEAcYieNhK/1Bc2/HlNAN0rF3g5JvQiOT2fsNGJIa5A3hLmV3ufp61p62RO0F6EqGlmJGkyXqz4HEgouIxjmgQjhTr88wCwAeB8bvzcUEL2yADMLcBJc6t4eybN7KEaybD8iQGByfgoUm07kDq5Dia3GS2QPorUkfdut1BumqLRKmIngbW1RB/K9BzpvZjFELuIlx4VRH477waHsioNesz9iZZgcEUdle/lf5VdlnwURozqumfyqoXfq3j7lITGRRSWRSJOMJJuN1gx4kCX44yLGHO/wNnfkbCYV+jrFJei1jyilXL0A2LfLbNi0BPefuagLGCTXnfPj4R2iLHW/7pkCQ==";

    //운영 어플리케이션 아이디
    private String APPLICATION_ID = "MIICuwSCAbMwggGvBBVhbmRyb2lkX3JlYWxfMjAyMzA1MjYEFmtyLm9yLm15ZGF0YWNlbnRlci5wZHMEDjIwMjMwNTI2MDkyOTQ5BC1odHRwczovL3Bkcy5teWRhdGFjZW50ZXIub3Iua3I6OTQ0My90cnVzdGFwcC8EggEmMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtArc1WKsiCRJyTq3CBcjE9Wm02OVg7PAVwjXcpMcM890g7Anzy6h6m/VXiPt+VSbk6dIgazdm9VJagCf+H3u/DTX0nrSNOySMziv74+8/7hwIOCFMAbdbXpwLhp4vCFZEy+YkCnGND53ZvGc7SfSwJKVEdRUfxRtu9kaEo55kAIwZ01WpBTiVYBHdACdRTOIkah2dWCHbRVG+HS8DVQHJaKtzJcZxfiKm6/vcyEjDjWazLox/a2vqK47z5vfOZ+RmQPt5Aq/kxj3I3KKdPYSCVtCfGSGoZob9nXWaioeA93dVtZXFcXKchKemAV5HxuH0JnK0MRAtDN966+pflU3BwIDAQABBBVhbmRyb2lkX3JlYWxfMjAyMzA1MjYEggEAc/7/5XpQ0GbfJ9D7UnmgsHk4/21CsQYPUFzp5KmXGrmngBmIspexah8i/ISGNwbM+hXof0DIyo0Us4lkouRd0KqZ9CBQxBWgwHZwJAM+6PgL7Z8TtFKad52uRMrJ6ymkoYE4mXcrac2Zd68aqF7jS30aCE00TklAw3z/jLScjd5haVbzPQtLmS1Tdz9ymsINGIhLVfvRNnoTln6ex/rPi+3KAXvMRXu+ZmREYDCx0Fgi2h6nYO5ivyFYPrIdskE8beIxOr/F7dK6SFFnQUonRMBLPTqGiJ2Ho6i6AKQVlUNVccKmY9tBcQfQK54vfHY4oqo5PTuhNLlfG92GaA9YkA==";

    // 샘플용 서버 검증 URL

    //    private static final String SERVER_URL_VERIFY = "http://125.133.65.225:8541/AppAdmin4.5/trustapp_svr/trustapp_server_demo.jsp";
    private String SERVER_URL_VERIFY = "";

    // TARGET_CLIENT:[TRUSTAPP]
    // NAME:[com.example.trustappandroid],CODE=[F2C984EFA0C5C59755B4]
    private static final String TRUSTAPP_LIC_CODE = "A0856470D84E4E194DDF";

    // 서버로 전송하여 검증할 데이터
    private static String TRUSTAPP_TOKEN = "";
    private static String TRUSTAPP_CLIENT_VALUE = "";

    // Self Sign SSL 인증서 등록 : 정식 인증서일 경우 호출할 필요 없으나, 사설인증서일 경우 등록 필요
    private static String PEM_SSL_CERT = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDhTCCAm2gAwIBAgIEc25gMjANBgkqhkiG9w0BAQsFADByMQswCQYDVQQGEwI4\n" +
            "MjEOMAwGA1UECBMFc2VvdWwxDjAMBgNVBAcTBXNlb3VsMRUwEwYDVQQKEwxteWRh\n" +
            "dGFjZW50ZXIxFTATBgNVBAsTDG15ZGF0YWNlbnRlcjEVMBMGA1UEAxMMbXlkYXRh\n" +
            "Y2VudGVyMCAXDTIxMDYyOTAxNDQzMFoYDzIxMjEwNjA1MDE0NDMwWjByMQswCQYD\n" +
            "VQQGEwI4MjEOMAwGA1UECBMFc2VvdWwxDjAMBgNVBAcTBXNlb3VsMRUwEwYDVQQK\n" +
            "EwxteWRhdGFjZW50ZXIxFTATBgNVBAsTDG15ZGF0YWNlbnRlcjEVMBMGA1UEAxMM\n" +
            "bXlkYXRhY2VudGVyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkn4Y\n" +
            "4+pzehrM2xEa0/WsZa29p1q+5yi9768L8ZMLFM1LQIlwr5wL/W3J9K9DGDhaT5i0\n" +
            "rAW8zALld9zm5NbjsxxQfKqViDQ/L8HeHmtTVnIfDcmfU7d9CH2avBBy3/KnfLDm\n" +
            "FL4AwIFk6DEnd3ZyXu2GAv8w4kvBefG649DBSouMFjBhP7Dz4wJzdTRWe7Y7jM2L\n" +
            "M00DE2bVcfO7J1rvJGYDag8l6VCNJZ/Oz473hgOOhzPGllRHjz7SOlAAlEP5GrSz\n" +
            "Fk9DlAZ/gPd9qzznHCV1t0CKWCHXiUDlr6mPKVYtjbF904NfCkDGSXrVtYnstLJ0\n" +
            "onTztpJb8PlePsAcdwIDAQABoyEwHzAdBgNVHQ4EFgQUXKDv+cAxKBaGkfRoVA7s\n" +
            "Qq7V+RgwDQYJKoZIhvcNAQELBQADggEBABzwylzUg6HPf5Onx4DYiIncxqvxT9qa\n" +
            "gm+afVQ5mTVhEhpya2pbxrn4PW6rZr/S7ockCF5YyNQtYRUrtmWPyzDRXtBtOcmX\n" +
            "CI8/+5kRD4VVKAgex5u8NevoySNjAaYSpzVoCNu81okuYZfWc5HjkOlABYorqMHC\n" +
            "EHyMESxNXTS1/9zYx2lHT/FcSFed//EvW79L68qaFC+F66S9nO21jI45drPnw5Cy\n" +
            "TP9BCj32WWVVWiJI7KVAOtakz1Q+H13p+5qsC+1Bxfhic4Q7HFtsI0SS7HVwnphK\n" +
            "mbbrWxDou+8y0fUzbOZ9v2GWMsywR4MCuNob2htlLLBjz77KsQcSdTQ=\n" +
            "-----END CERTIFICATE-----";


    static Context ctx = null;
    private static String txtData = "";
    private String TAG = "TrustAppManager";

    private static boolean isSuccess = false;

//    public static Handler handler = new Handler(Looper.getMainLooper()) {
//        public void handleMessage(Message msg) {
//            GLog.d("isSuccess : " + isSuccess);
//            if (msg.what == 0) {
//                GLog.d("txtData : " + txtData);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    if (IntroActivity.mContext != null)
//                        ((IntroActivity) IntroActivity.mContext).trustAppResult(isSuccess);
//                }
//            }
//        }
//    };

    public static Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            GLog.d("isSuccess : " + isSuccess);
            if (msg.what == 0) {
                GLog.d("txtData : " + txtData);
                ((IntroActivity) IntroActivity.mContext).trustAppResult(isSuccess);
                Toast.makeText(ctx, "앱 위변조 성공", Toast.LENGTH_SHORT).show();
            }
        }
    };


    public TrustAppManager(Context context) {
        this.ctx = context;

        // License / ApplicationID SET
        TrustAppUI.PACKAGE_LIC = TRUSTAPP_LIC_CODE;
        TrustAppToolkit.setApplicationID(APPLICATION_ID);
        TrustAppToolkit aap = new TrustAppToolkit();
        aap.setDebugMode(true);

        // Self Sign SSL 인증서 등록 : 정식 인증서일 경우 호출할 필요 없으나, 사설인증서일 경우 등록 필요
//        TrustAppToolkit.AddTrustedCert(PEM_SSL_CERT);

        // 앱위변조 검증(해쉬계산을 먼저한다) : 속도 개선을 위해 처음에 넣어준다.
        // TrustAppToolkit.i(context, check_rooting, check_tegrak_app)
        TrustAppToolkit.i(ctx, false, false);
    }

    /* AppDefence 앱위변조 검증 테스트 : 검증서버에 서버검증토큰을 요청하여 클라이언트검증값을 생성한다. */
    public void startTrustApp() {
        GLog.d("startTrustApp() start");

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

                    //네트워크 연결되지 않았을 경우

                    alert("ERROR", strError);
//                    isSuccess = false;
//                    handler.sendEmptyMessage(0);
                }
            }
        };

        // Callback 함수 Set
        TrustAppUI.setFinishCallback(callback);

        // 위변조검증 시작
        TrustAppUI.StartTrustApp_Start(
                ctx,        // UI를 위한 CONTEXT
                "MyDataCenter",        // App 명칭 : 이력 관리용
                null                    // 화면 출력용 문자열 : 없을 경우 default 문자열
        );
    }

    public static void alert(String title, String value) {
        if (!Utils.getNetworkStatus(ctx)) {
            CustomDialog customDialog = new CustomDialog(ctx, new CustomDialogClickListener() {
                @Override
                public void onPositiveClick(String text, boolean value) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }

                @Override
                public void onNegativeClick() {
                }
            }, "안내", "네트워크에 연결되지 않았습니다. \n네트워크 연결 상태를 확인 후 진행해 주세요.", Constant.ONE_BUTTON, true);
            customDialog.setCancelable(false);
            customDialog.show();
            customDialog.setOneButtonText("확인");
//            Display display = ((IntroActivity) ctx).getWindowManager().getDefaultDisplay();
//            Point size = new Point();
//            display.getSize(size);
//
//            Window window = customDialog.getWindow();
//            int x = (int) (size.x * 0.9f);
//            int y = (int) (size.y * 0.7f);
//
//            window.setLayout(x, y);
        } else {
            CustomDialog customDialog = new CustomDialog(ctx, new CustomDialogClickListener() {
                @Override
                public void onPositiveClick(String text, boolean value) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }

                @Override
                public void onNegativeClick() {
                }
            }, "안내", value, Constant.ONE_BUTTON, true);

            customDialog.setCancelable(false);
            customDialog.show();
            customDialog.setOneButtonText("확인");

//            Display display = ((IntroActivity) ctx).getWindowManager().getDefaultDisplay();
//            Point size = new Point();
//            display.getSize(size);
//
//            Window window = customDialog.getWindow();
//            int x = (int) (size.x * 0.9f);
//            int y = (int) (size.y * 0.7f);
//
//            window.setLayout(x, y);
            return;
        }
    }

    /*
    앱위변조 검증 테스트 : 샘플임!!
    서버검증토큰 과 클라이언트검증값을 서버에 전달하여 검증받고
    리턴된 서버검증값을 처리한다.
    */
    private void loginProcess() {
        GLog.d("loginProcess() start");

        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuffer sbLog = new StringBuffer();

                GLog.d("SERVER TOKEN : [" + TRUSTAPP_TOKEN + "]");
                GLog.d("CLIENT VALUE : [" + TRUSTAPP_CLIENT_VALUE + "]");

                // Send/Receive AppDefence Server
                String strPostData = ""; // SERVER_URL_VERIFY;
                strPostData = "TRUSTAPP_TOKEN=" + TRUSTAPP_TOKEN + "&TRUSTAPP_CLIENT_VALUE=" + TRUSTAPP_CLIENT_VALUE;

                SERVER_URL_VERIFY = Constant.SERVER_URL_VERITY;

                GLog.d("TEST URL : [" + SERVER_URL_VERIFY + "]");
                GLog.d("TEST DATA : [" + strPostData + "]");
                sbLog.append("TEST URL : [").append(SERVER_URL_VERIFY).append("]\n\n");
                sbLog.append("TEST DATA : [").append(strPostData).append("]\n\n");

                try {
                    // 서버검증토큰과 클라이언트검증값을 전달하고 앱위변조검증 결과값을 전달받는다. 전달받은 서버검증값은 API를 통해 set 해준다.
                    byte[] bt = sendServer(SERVER_URL_VERIFY, strPostData);
                    String serverValue = new String(bt);
                    serverValue = serverValue.replaceAll("((<|>|\r\n|\r|\n|\\p{Space}))", "");

                    GLog.d("serverValue : " + serverValue);
                    sbLog.append("SERVER VALUE : [").append(serverValue).append("]\n\n");

                    // Check ErrorMessage
                    if (serverValue.indexOf("[[[TRUSTAPPRESULT:OK]]]") > -1) {
                        GLog.d("APP위변조 검증결과 이상없음");
                        sbLog.append("APP위변조 검증결과 이상없음");

                        txtData = sbLog.toString();
                        isSuccess = true;
                        handler.sendEmptyMessage(0);

                    } else {

                        GLog.d("APP위변조 검증결과 오류");
                        sbLog.append("APP위변조 검증결과 오류");

                        isSuccess = false;
                        txtData = sbLog.toString();
                        handler.sendEmptyMessage(0);

                    }
                } catch (Exception e) {
                    GLog.d("ERROR : " + e.toString());
                    sbLog.append("Error : ").append(e.toString()).append("\n");
//                    txtResult.setText(sbLog.toString());
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
}
