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
}
