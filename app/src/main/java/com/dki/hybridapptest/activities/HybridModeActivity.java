package com.dki.hybridapptest.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dki.hybridapptest.HybridResult;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.bridge.AndroidBridge;
import com.dki.hybridapptest.utils.GLog;
import com.dki.hybridapptest.utils.MagicVKeyPadSettings;
import com.dreamsecurity.magicvkeypad.MagicVKeypad;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;


public class HybridModeActivity extends Activity {

    private WebView mWebview;
    private Button backButton = null;

    private TextView tv_AESEncryptData = null;
    private TextView tv_AESDecryptData = null;
    private TextView tv_RSAEncryptData = null;

    private static String strfieldID = null;
    private String strViewMode = null;

    private Button btn_sendE2E = null;

    boolean bUseDummyData = false;
    private AndroidBridge mAndroidBridge;

    String RSAEncryptData = null, AESDecData = null, AESEncData = null;
    MagicVKeypad magicVKeypad = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GLog.d();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hybrid_mode);

        backButton = findViewById(R.id.back_btn);

        mWebview = (WebView) findViewById(R.id.webview);
        settingKeypad(this);

        // 웹뷰 셋팅
        mWebview.getSettings().setJavaScriptEnabled(true);
        mAndroidBridge = new AndroidBridge(mWebview, HybridModeActivity.this);
        mWebview.addJavascriptInterface(mAndroidBridge, "MagicVKeypad");
        mWebview.setAccessibilityDelegate(new View.AccessibilityDelegate());
        mWebview.getSettings().setDomStorageEnabled(true);
        mAndroidBridge.settingKeyPad();

        JSONObject postData = new JSONObject();

        try {
            postData.put("PortraitFix", MagicVKeyPadSettings.bIsPortFix);
            postData.put("MaskingType", MagicVKeyPadSettings.maskingType);
            postData.put("MaxLength", MagicVKeyPadSettings.maxLength);
            postData.put("UseDummyData", MagicVKeyPadSettings.bUseDummydata);
            postData.put("UseReplace", MagicVKeyPadSettings.bUseReplace);
            postData.put("UseE2E", MagicVKeyPadSettings.bUseE2E);
            postData.put("UseSpeaker", MagicVKeyPadSettings.bUseSpeaker);
            postData.put("Screenshot", MagicVKeyPadSettings.bAllowCapture);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tv_AESEncryptData = findViewById(R.id.EncryptData);
        tv_AESDecryptData = findViewById(R.id.DecryptData);
        tv_RSAEncryptData = findViewById(R.id.RSAEncryptData);

        mWebview.loadUrl("file:///android_asset/MagicVKeypad_Sample.html?option=" + postData);

        if (!HybridResult.strScript.equals("")) {
            mWebview.loadUrl(HybridResult.strScript);
        }

//        mWebview.setWebViewClient(new MagicVKeypadWebCLient());

        btn_sendE2E = findViewById(R.id.Btn_E2EDataSend);


        if (!MagicVKeyPadSettings.bUseE2E) {
            btn_sendE2E.setVisibility(View.GONE);
            tv_RSAEncryptData.setVisibility(View.GONE);
            tv_AESDecryptData.setVisibility(View.VISIBLE);
            tv_AESEncryptData.setVisibility(View.VISIBLE);
        } else {
            btn_sendE2E.setVisibility(View.VISIBLE);
            tv_RSAEncryptData.setVisibility(View.VISIBLE);
            tv_AESDecryptData.setVisibility(View.GONE);
            tv_AESEncryptData.setVisibility(View.GONE);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HybridResult.RSAEncData = "";
                HybridResult.AESEncData = "";
                HybridResult.AESDecData = "";

                if (magicVKeypad.isKeyboardOpen()) {
                    // 키패드를 닫는다.
                    magicVKeypad.closeKeypad();
                }

                magicVKeypad.finalizeMagicVKeypad();
                finish();
            }
        });

        btn_sendE2E.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {

                            URL url = new URL(MagicVKeyPadSettings.E2ETestUrl); // 호출할 url
                            Map<String, Object> params = new LinkedHashMap<>(); // 파라미터 세팅
                            params.put("encKeypad", RSAEncryptData);

                            StringBuilder postData = new StringBuilder();
                            for (Map.Entry<String, Object> param : params.entrySet()) {
                                if (postData.length() != 0) postData.append('&');
                                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                                postData.append('=');
                                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                            }

                            byte[] postDataBytes = postData.toString().getBytes("UTF-8");


                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                            conn.setDoOutput(true);
                            conn.getOutputStream().write(postDataBytes); // POST 호출

                            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                            String inputLine;
                            while ((inputLine = in.readLine()) != null) { // response 출력
                                System.out.println(inputLine);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
        });

    }

    @Override
    public void onBackPressed() {

        if (magicVKeypad.isKeyboardOpen()) {
            // 키패드를 닫는다.
            magicVKeypad.closeKeypad();
        } else {
            super.onBackPressed();

            // 키패드와의 연결을 종료하고 모든 값을 초기화한다.
            magicVKeypad.finalizeMagicVKeypad();
            HybridResult.RSAEncData = "";
            HybridResult.AESEncData = "";
            HybridResult.AESDecData = "";

            finish();
        }

    }

    // 키패드 선언 및 라이선스 검증
    private void settingKeypad(Context context) {
        magicVKeypad = new MagicVKeypad();

        // 키패드 라이선스 값 (드림시큐리티에게 패키지명 전달 후 받은 라이선스 값)
        String strLicense = MagicVKeyPadSettings.strLicense;

        boolean successLicense = magicVKeypad.initializeMagicVKeypad(context, strLicense);

        if (!successLicense) {
            Toast.makeText(this, "라이선스 검증 실패", Toast.LENGTH_SHORT).show();
        }
        if (MagicVKeyPadSettings.bUseE2E) setPublickeyForE2E();
    }


    private void setPublickeyForE2E() {
        try {
            magicVKeypad.setPublickeyForE2E(MagicVKeyPadSettings.publicKey, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private class AndroidBridge {
//        @JavascriptInterface
//        public void callMagicVKeypadAndroid(final String fieldID,
//                                            final String viewMode,
//                                            final String keypadType,
//                                            final String option) {
//
//            Log.d(TAG, "fieldID == " + fieldID + "\n" +
//                    "viewMode == " + viewMode + "\n" +
//                    "keypadType == " + keypadType + "\n" +
//                    "option == " + option);
//            Log.d(TAG, "callMagicVKeypad 지나감");
//            strfieldID = fieldID;
//            strViewMode = viewMode;
//
//            JSONObject options = null;
//            try {
//                options = new JSONObject(String.valueOf(option));
//                if (options.getBoolean("UseDummyData")) {
//                    bUseDummyData = true;
//                } else {
//                    bUseDummyData = false;
//                }
//
//                // 풀모드
//                if (viewMode.equals("FULL_TYPE")) {
//                    if (keypadType.equals("CHAR_TYPE")) { // 풀모드 - 문자키패드
//
//                        // 입력 필드명 설정
//                        magicVKeypad.setFieldName(fieldID);
//
//                        // 마스킹 설정
//                        magicVKeypad.setMasking(options.getInt("MaskingType"));
//
//                        // 풀모드 설정
//                        magicVKeypad.setFullMode(true);
//
//                        // 입력 길이 제한
//                        magicVKeypad.setMaxLength(options.getInt("MaxLength"));
//
//                        // 화면 고정(DEFAULT: FALSE)
//                        if (options.getBoolean("PortraitFix"))
//                            magicVKeypad.setPortraitFixed(true);
//                        else
//                            magicVKeypad.setPortraitFixed(false);
//
//                        if (options.getBoolean("UseSpeaker"))
//                            magicVKeypad.setUseSpeaker(true);
//                        else
//                            magicVKeypad.setUseSpeaker(false);
//
//
//                        // 스크린샷 허용
//                        if (options.getBoolean("Screenshot"))
//                            magicVKeypad.setScreenshot(true);
//                        else {
//                            magicVKeypad.setScreenshot(false);
//                        }
//
//
//                        // 키패드 실행
//                        magicVKeypad.startCharKeypad(mOnClickInterface);
//
//                    } else { // 풀모드 - 숫자키패드
//
//                        // 마스킹 타입 설정
//                        magicVKeypad.setMasking(options.getInt("MaskingType"));
//
//                        // 화면 세로 고정(DEFAULT: FALSE)
//                        if (options.getBoolean("PortraitFix"))
//                            magicVKeypad.setPortraitFixed(true);
//                        else
//                            magicVKeypad.setPortraitFixed(false);
//
//
//                        // 입력 필드명 설정
//                        magicVKeypad.setFieldName(fieldID);
//
//                        // 풀모드 설정
//                        magicVKeypad.setFullMode(true);
//
//                        // 키패드 재배치 허용(DEFAULT : FALSE)
//                        if (options.getBoolean("UseReplace"))
//                            magicVKeypad.setNumUseReplace(true);
//                        else
//                            magicVKeypad.setNumUseReplace(false);
//
//                        // 입력 길이 제한
//                        magicVKeypad.setMaxLength(options.getInt("MaxLength"));
//
//                        if (options.getBoolean("Screenshot"))
//                            magicVKeypad.setScreenshot(true);
//                        else {
//                            magicVKeypad.setScreenshot(false);
//                        }
//
//                        if (options.getBoolean("UseSpeaker"))
//                            magicVKeypad.setUseSpeaker(true);
//                        else
//                            magicVKeypad.setUseSpeaker(false);
//
//                        // 키패드 실행
//                        magicVKeypad.startNumKeypad(mOnClickInterface);
//                    }
//
//                    // 하프모드
//                } else if (viewMode.equals("HALF_TYPE")) {
//                    if (keypadType.equals("CHAR_TYPE")) { // 하프모드 - 문자키패드
//                        if (magicVKeypad.isKeyboardOpen()) {
//                            magicVKeypad.closeKeypad();
//                        }
//
//                        // 입력 필드명 설정
//                        magicVKeypad.setFieldName(fieldID);
//
//                        // masking 설정
//                        magicVKeypad.setMasking(options.getInt("MaskingType"));
//
//                        // 하프모드 설정
//                        magicVKeypad.setFullMode(false);
//
//                        // 입력 길이 제한
//                        magicVKeypad.setMaxLength(options.getInt("MaxLength"));
//
//                        if (options.getBoolean("UseSpeaker"))
//                            magicVKeypad.setUseSpeaker(true);
//                        else
//                            magicVKeypad.setUseSpeaker(false);
//
//                        // 키패드 실행
//                        magicVKeypad.startCharKeypad(mOnClickInterface);
//
//                    } else { // 하프모드 - 숫자키패드
//                        if (magicVKeypad.isKeyboardOpen()) {
//                            magicVKeypad.closeKeypad();
//                        }
//                        // 필드이름 지정
//                        magicVKeypad.setFieldName(fieldID);
//
//                        // masking 설정
//                        magicVKeypad.setMasking(options.getInt("MaskingType"));
//
//                        // 입력 길이 제한
//                        magicVKeypad.setMaxLength(options.getInt("MaxLength"));
//
//                        // 하프모드 설정
//                        magicVKeypad.setFullMode(false);
//
//                        // 화면 고정(DEFAULT: FALSE)
//                        if (options.getBoolean("PortraitFix"))
//                            magicVKeypad.setPortraitFixed(true);
//                        else
//                            magicVKeypad.setPortraitFixed(false);
//
//
//                        //재배치 설정
//                        if (options.getBoolean("UseReplace"))
//                            magicVKeypad.setNumUseReplace(true);
//                        else
//                            magicVKeypad.setNumUseReplace(false);
//
//                        if (options.getBoolean("UseSpeaker"))
//                            magicVKeypad.setUseSpeaker(true);
//                        else
//                            magicVKeypad.setUseSpeaker(false);
//
//                        magicVKeypad.setMultiClick(MagicVKeyPadSettings.bMultiClick);
//
//                        // 키패드 실행
//                        magicVKeypad.startNumKeypad(mOnClickInterface);
//
//                    }
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    private class MagicVKeypadWebCLient extends WebViewClient {
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            return true;
//        }
//    }
//
//    // 키패드 종료
//    public void finishKeypad() {
//        if (magicVKeypad.isKeyboardOpen())
//            magicVKeypad.closeKeypad();
//
//        magicVKeypad.finalizeMagicVKeypad();
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        finishKeypad();
//    }
//
//    // 가로모드 세로모드 설정
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        magicVKeypad.configurationChanged(newConfig);
//        super.onConfigurationChanged(newConfig);
//    }
//
//    private MagicVKeypadOnClickInterface mOnClickInterface = new MagicVKeypadOnClickInterface() {
//        @Override
//        public void onMagicVKeypadClick(MagicVKeypadResult magicVKeypadResult) {
//
//            int licenseResult = magicVKeypadResult.getLicenseResult();
//            if (licenseResult != 0) {
//                Log.d("TEST", "라이선스 검증 실패");
//            }
//
//            byte[] decData = null;
//
//            String strPlaintext = "";
//
//
//            if (decData != null) {
//                strPlaintext = new String(decData);
//            }
//
//            // js 로 리턴
//            String strScript = "javascript:inputData('";
//
//            // 풀모드
//            if (magicVKeypad.isFullMode()) {
//                if (!MagicVKeyPadSettings.bUseE2E)
//                    decData = magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData());
//                if (magicVKeypad.getEncryptData() != null) {
//                    if (magicVKeypadResult.getEncryptData() != null)
//                        RSAEncryptData = new String(magicVKeypadResult.getEncryptData());
//                }
//
//                if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_OK_BUTTON) { // 확인 버튼 클릭
//                    if (!MagicVKeyPadSettings.bUseE2E) {
//                        if (decData != null) {
//                            strPlaintext = "";
//
//                            for (int i = 0; i < decData.length; i++) {
//                                strPlaintext = strPlaintext + "•";
//                            }
//                        }
//                        strScript += strfieldID + "','" + strPlaintext + "')";
//                        HybridResult.strScript = strScript;
//
//                        mWebview.loadUrl(strScript);
//
//
//                        if (magicVKeypadResult.getEncryptData() != null) {
//                            tv_AESEncryptData.setText("AES256 암호화 데이터 : " + byteArrayToHex(magicVKeypadResult.getEncryptData()));
//                            tv_AESDecryptData.setText("AES256 복호화 데이터 : " + new String(magicVKeypad.getDecryptData((magicVKeypadResult.getEncryptData()))));
//                        }
//                    } else {
//                        strScript += strfieldID + "','" + "" + "')";
//                        mWebview.loadUrl(strScript);
//                        if (magicVKeypadResult.getEncryptData() != null)
//                            tv_RSAEncryptData.setText("RSA 암호화 데이터 : " + new String(magicVKeypadResult.getEncryptData()));
//                    }
//                }
//            }
//            // 하프모드
//            else {
//                if (!MagicVKeyPadSettings.bUseE2E) {
//                    decData = magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData());
//                    if (decData != null) {
//                        AESDecData = byteArrayToHex(magicVKeypadResult.getEncryptData());
//                        AESEncData = new String(decData);
//                    }
//                }
//                if (magicVKeypad.getEncryptData() != null)
//                    RSAEncryptData = new String(magicVKeypad.getEncryptData());
//
//
//                if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_CANCEL_BUTTON) {
//                    magicVKeypad.closeKeypad();
//                    strScript += strViewMode + "','" + strfieldID + "','" + "" + "')";
//                    HybridResult.strScript = strScript;
//                    mWebview.loadUrl(strScript);
//                } else if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_OK_BUTTON) {
//                    if (magicVKeypadResult.getEncryptData() != null) {
//                        if (MagicVKeyPadSettings.bUseE2E == false) {
//                            tv_AESEncryptData.setText("AES256 암호화 데이터 : " + byteArrayToHex(magicVKeypadResult.getEncryptData()));
//                            tv_AESDecryptData.setText("AES256 복호화 데이터 : " + new String(magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData())));
//
//                        } else {
//                            RSAEncryptData = new String(magicVKeypad.getEncryptData());
//                            tv_RSAEncryptData.setText("RSA 암호화 데이터 : " + RSAEncryptData);
//
//                        }
//                    }
//
//                    magicVKeypad.closeKeypad();
//                } else if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_CHAR_NUM_BUTTON) {
//                    if (MagicVKeyPadSettings.bUseE2E) {
//                        if (magicVKeypad.getEncryptData() != null)
//                            RSAEncryptData = new String(magicVKeypad.getEncryptData());
//                    } else {
//                        if (decData != null) {
//                            strPlaintext = "";
//
//                            for (int i = 0; i < decData.length; i++) {
//                                strPlaintext = strPlaintext + "•";
//                            }
//                        }
//                        strScript += strfieldID + "','" + strPlaintext + "')";
//                        mWebview.loadUrl(strScript);
//
//                    }
//                }
//            }
//        }
//    };
//
//    String byteArrayToHex(byte[] a) {
//        StringBuilder sb = new StringBuilder();
//        for (final byte b : a)
//            sb.append(String.format("%02x", b & 0xff));
//        return sb.toString();
//    }
//

}
