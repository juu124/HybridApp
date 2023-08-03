package com.dki.hybridapptest.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dki.hybridapptest.Interface.ProgressBarListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.bridge.AndroidBridge;
import com.dki.hybridapptest.retrofit.RetrofitApiManager;
import com.dki.hybridapptest.retrofit.RetrofitInterface;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.dreamsecurity.magicxsign.MagicXSign;
import com.dreamsecurity.magicxsign.MagicXSign_Type;
import com.dreamsecurity.xsignweb.XSignWebPlugin;

import org.json.JSONException;
import org.json.JSONObject;

import m.client.push.library.PushHandler;
import m.client.push.library.common.Logger;
import m.client.push.library.common.PushConstants;
import m.client.push.library.common.PushConstantsEx;
import m.client.push.library.utils.PushUtils;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    private WebSettings mWebSettings;
    private Intent mAction;
    private AndroidBridge androidBridge = null;
    private Handler handler = new Handler(Looper.getMainLooper());

    // push
    private BroadcastReceiver mMainBroadcastReceiver;

    // 프로그래스 바
    private RelativeLayout mProgressBar;

    // 웹 뷰에서 뒤로 가기
    @Override
    public void onBackPressed() {
        if (TextUtils.equals(mWebView.getUrl(), Constant.WEB_VIEW_MAIN_URL)) {
            super.onBackPressed();
        } else if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.webview);
        mProgressBar = findViewById(R.id.dialog_user_info_progressbar);
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);


        // 로그인 화면 프로그래스 바
        androidBridge = new AndroidBridge(mWebView, MainActivity.this, new ProgressBarListener() {
            @Override
            public void showProgressBar() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void unShownProgressBar() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
        mWebView.addJavascriptInterface(androidBridge, "DKITec");
        mWebView.loadUrl(Constant.WEB_VIEW_MAIN_URL);
        webPlugin_Init(MainActivity.this);

        RetrofitApiManager.getInstance().requestPostUser(new RetrofitInterface() {
            @Override
            public void onResponse(Response response) {
            }

            @Override
            public void onFailure(Throwable t) {
                GLog.d("오류 메세지 == " + t.toString());
            }
        });

        // webView 화면 (전화, e-mail, 외부 url 링크)
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (TextUtils.isEmpty(url)) {
                    GLog.d("url is null");
                } else {
                    // 전화
                    if (url.startsWith("tel:")) {
                        mAction = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                        startActivity(mAction);
                    }
                    // 이메일
                    else if (url.startsWith("mailto:")) {
                        mAction = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                        startActivity(mAction);
                    }
                    // 외부 url 링크
                    else if (url.startsWith("https:")) {
                        mAction = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(mAction);
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    public void registerReceiver() {
        if (mMainBroadcastReceiver != null) {
            return;
        }

        GLog.d("registerReceiver ===== ");

        // 화면에서 서비스 등록 결과를 받기 위한 리시버 등록 - 패키지명.ACTION_COMPLETED
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.this.getPackageName() + PushConstantsEx.ACTION_COMPLETED);

        mMainBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // 수신된 인텐트(결과값 응답) 의 정상 데이터 여부 체크 (푸시 타입/액션 타입) - 반드시 구현
                if (!PushUtils.checkValidationOfCompleted(intent, context)) {
                    return;
                }

                //push 타입 판단
                JSONObject result_obj = null; // 수신된 오브젝트
                String result_code = ""; // 결과 코드
                String result_msg = ""; // 결과 메세지
                try {
                    result_obj = new JSONObject(intent.getExtras().getString(PushConstants.KEY_RESULT));
                    result_code = result_obj.getString(PushConstants.KEY_RESULT_CODE);
                    result_msg = result_obj.getString(PushConstants.KEY_RESULT_MSG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 액션 타입
                String bundle = intent.getStringExtra(PushConstantsEx.KEY_BUNDLE);

                //액션에 따라 분기 (이미 서비스 등록이 완료된 상태인 경우 다음 process 이동)
                if (bundle.equals(PushConstantsEx.COMPLETE_BUNDLE.IS_REGISTERED_SERVICE)) {
                    //mProgressDialog.dismiss();
                    Logger.e(result_code);
                    String resultPushType = intent.getExtras().getString(PushConstants.KEY_PUSH_TYPE);
                    if (resultPushType.equals(PushHandler.getInstance().getPushConfigInfo(getApplicationContext()).getPushType())) {
                        if (result_code.equals(PushConstants.RESULTCODE_OK)) {
                            mWebView.loadUrl(Constant.WEB_VIEW_LOGIN_URL);
//                            Intent pageIntent = new Intent(this, LoginActivity.class);
//                            startActivity(pageIntent);
//                            finish();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, result_msg, Toast.LENGTH_LONG).show();
                    }

                    // 최초 서비스 등록이 완료된 경우 다음 process 이동
                } else if (bundle.equals(PushConstantsEx.COMPLETE_BUNDLE.REG_PUSHSERVICE)) {

                    // 등록 성공
                    if (result_code.equals(PushConstants.RESULTCODE_OK)) {
                        Toast.makeText(context, "등록 성공!", Toast.LENGTH_SHORT).show();
                        mWebView.loadUrl(Constant.WEB_VIEW_LOGIN_URL);
//                        Intent pageIntent = new Intent(this, LoginActivity.class);
//                        startActivity(pageIntent);
//                        finish();
                    }
                    // 통신 에러
                    else if (result_code.equals(PushConstants.RESULTCODE_HTTP_ERR)) {
                        Toast.makeText(context, "[MainActivity] error code: " + result_code + " msg: " + result_msg, Toast.LENGTH_SHORT).show();
                    }
                    // 인증키 오류
                    else if (result_code.equals(PushConstants.RESULTCODE_AUTHKEY_ERR)) {
                        Toast.makeText(context, "[MainActivity] error code: " + result_code + " msg: " + result_msg, Toast.LENGTH_SHORT).show();
                    }
                    // 서버 응답 에러
                    else if (result_code.equals(PushConstants.RESULTCODE_RESPONSE_ERR)) {
                        Toast.makeText(context, "[MainActivity] error code: " + result_code + " msg: " + result_msg, Toast.LENGTH_SHORT).show();
                    }
                    // 라이브러리 에러 - 파싱 에러
                    else if (result_code.equals(PushConstants.RESULTCODE_INTERNAL_ERR)) {
                        Toast.makeText(context, "[MainActivity] error code: " + result_code + " msg: " + result_msg, Toast.LENGTH_SHORT).show();
                    }
                    // 기타
                    else {
                        Toast.makeText(context, "[MainActivity] error code: " + result_code + " msg: " + result_msg, Toast.LENGTH_SHORT).show();
                        //System.exit(0);
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMainBroadcastReceiver, intentFilter);
    }

    public void unregisterReceiver() {
        GLog.d("unregisterReceiver ===== ");
        if (mMainBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMainBroadcastReceiver);
            mMainBroadcastReceiver = null;
        }
    }

    // web 플러그 인 초기화
    public void webPlugin_Init(Context c) {
        GLog.d("잘 들어왔습니다. =========" + c);
        int SDK_INT = Build.VERSION.SDK_INT;
        try {
            MagicXSign xSign = null;
            XSignWebPlugin xWeb = null;

            xWeb = new XSignWebPlugin(this, this, mWebView);
            xSign = new MagicXSign();

            xSign.Init(this, MagicXSign_Type.XSIGN_DEBUG_LEVEL_1);
            String mediaRootPath = Environment.getExternalStorageDirectory().getPath();
            xSign.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI | MagicXSign_Type.XSIGN_PKI_TYPE_GPKI | MagicXSign_Type.XSIGN_PKI_TYPE_PPKI | MagicXSign_Type.XSIGN_PKI_TYPE_MPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, mediaRootPath);

            MagicXSign pki = new MagicXSign();

            if (xWeb.setStorage(MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_DISK) == MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_DISK)
                System.out.println("----- DB 사용 -----");

            try {
                pki.Init(MainActivity.this, MagicXSign_Type.XSIGN_DEBUG_LEVEL_0);
                int count = -1;
                pki.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER,
                        MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_TYPE_ALL, "/sdcard");
                count = pki.MEDIA_GetCertCount();
                GLog.d("count : " + count);

                int nMediaType[] = new int[1];
                for (int i = 0; i < count; i++) {
                    byte[] signCert = pki.MEDIA_ReadCert(i, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, nMediaType);
                    byte[] signPri = pki.MEDIA_ReadPriKey(i, MagicXSign_Type.XSIGN_PKI_CERT_SIGN);
                    pki.MEDIA_WriteCertAndPriKey(Base64.decode(signCert, Base64.DEFAULT), Base64.decode(signPri, Base64.DEFAULT), MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_DISK);
                }
                pki.MEDIA_UnLoad();
            } catch (Exception e) {
                GLog.d("webPlugin_Init ====== " + e);
            }
        } catch (Exception e) {
            GLog.d("webPlugin_Init ====== " + e);
        }
    }
}