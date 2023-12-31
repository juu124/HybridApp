package com.dki.hybridapptest.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.ui.activity.bridge.AndroidBridge;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.dki.hybridapptest.utils.HybridResult;
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
    private Button btn_sendE2E = null;

    private TextView tv_AESEncryptData = null;
    private TextView tv_AESDecryptData = null;
    private TextView tv_RSAEncryptData = null;

    private AndroidBridge mAndroidBridge;

    // 보안 키보드 입력 값
    String RSAEncryptData = null;
    MagicVKeypad magicVKeypad = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GLog.d();
        super.onCreate(savedInstanceState);
        // 화면 캡쳐 방지
        if (Constant.USE_SCREEN_SHOT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_hybrid_mode);

        backButton = findViewById(R.id.back_btn);
        mWebview = (WebView) findViewById(R.id.webview);
        // 보안 키보드 셋팅
        if (Constant.USE_MAGIC_KEYPAD_DREAM) {
            settingKeypad(magicVKeypad);
            mAndroidBridge = new AndroidBridge(mWebview, HybridModeActivity.this);
            mAndroidBridge.licenseAuth(magicVKeypad);

            // 웹뷰 셋팅
            mWebview.getSettings().setJavaScriptEnabled(true);
            mWebview.addJavascriptInterface(mAndroidBridge, "MagicVKeypad");
            mWebview.setAccessibilityDelegate(new View.AccessibilityDelegate());
            mWebview.getSettings().setDomStorageEnabled(true);

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

            mWebview.loadUrl(Constant.WEB_MAGIC_KEYPAD_URL + postData);

            if (!HybridResult.strScript.equals("")) {
                mWebview.loadUrl(HybridResult.strScript);
            }

            mWebview.setWebViewClient(new MagicVKeypadWebCLient());

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
                                GLog.d("RSAEncryptData == " + RSAEncryptData);

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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GLog.d();
        if (Constant.USE_MAGIC_KEYPAD_DREAM) {
            if (magicVKeypad.isKeyboardOpen()) {
                // 키패드를 닫는다.
                magicVKeypad.closeKeypad();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 키패드와의 연결을 종료하고 모든 값을 초기화한다.
        magicVKeypad.finalizeMagicVKeypad();
        HybridResult.RSAEncData = "";
        HybridResult.AESEncData = "";
        HybridResult.AESDecData = "";
    }

    private class MagicVKeypadWebCLient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    // 키패드 선언 및 null 체크
    private void settingKeypad(MagicVKeypad magicVKeypad) {
        if (magicVKeypad != null) {
            this.magicVKeypad = magicVKeypad;
        } else {
            GLog.d("키보드 생성");
            this.magicVKeypad = new MagicVKeypad();
        }
    }
}