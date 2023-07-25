package com.dki.hybridapptest.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.bridge.AndroidBridge;
import com.dki.hybridapptest.retrofit.RetrofitApiManager;
import com.dki.hybridapptest.retrofit.RetrofitInterface;
import com.dki.hybridapptest.utils.Constants;
import com.dki.hybridapptest.utils.GLog;
import com.dreamsecurity.magicxsign.MagicXSign;
import com.dreamsecurity.magicxsign.MagicXSign_Type;
import com.dreamsecurity.xsignweb.XSignWebPlugin;

import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    private WebSettings mWebSettings;
    private Intent mAction;
    private SharedPreferences sharedPreferences;
    private AndroidBridge androidBridge = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.webview);
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);

        androidBridge = new AndroidBridge(mWebView, MainActivity.this);
        mWebView.addJavascriptInterface(androidBridge, "DKITec");
        mWebView.loadUrl(Constants.WEB_VIEW_URL);
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
                        if ((checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
                            GLog.d("CALL_PHONE PERMISSION_DENIED 입니다.");
                            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Constants.CALL_PHONE_REQUEST_CODE);
                        } else {
                            mAction = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                            startActivity(mAction);
                        }
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

    // web 플러그 인 초기화
    public void webPlugin_Init(Context c) {
        GLog.d("잘 들어왔습니다. =========" + c);
        int SDK_INT = Build.VERSION.SDK_INT;
        try {
            MagicXSign xSign = null;
            XSignWebPlugin xWeb = null;

            xWeb = new XSignWebPlugin(this, this, mWebView);
            xSign = new MagicXSign();
            GLog.d("0");

            xSign.Init(this, MagicXSign_Type.XSIGN_DEBUG_LEVEL_1);
            String mediaRootPath = Environment.getExternalStorageDirectory().getPath();
            GLog.d("1");
            xSign.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI | MagicXSign_Type.XSIGN_PKI_TYPE_GPKI | MagicXSign_Type.XSIGN_PKI_TYPE_PPKI | MagicXSign_Type.XSIGN_PKI_TYPE_MPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, mediaRootPath);

            GLog.d("2");
            MagicXSign pki = new MagicXSign();

            GLog.d("3");
            if (xWeb.setStorage(MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_DISK) == MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_DISK)
                System.out.println("----- DB 사용 -----");

            try {
                GLog.d("4");
                pki.Init(MainActivity.this, MagicXSign_Type.XSIGN_DEBUG_LEVEL_0);
                int count = -1;
                pki.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER,
                        MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_TYPE_ALL, "/sdcard");
                count = pki.MEDIA_GetCertCount();
                GLog.d("count : " + count);
                GLog.d("6");

                int nMediaType[] = new int[1];
                for (int i = 0; i < count; i++) {
                    GLog.d("7");
                    byte[] signCert = pki.MEDIA_ReadCert(i, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, nMediaType);
                    byte[] signPri = pki.MEDIA_ReadPriKey(i, MagicXSign_Type.XSIGN_PKI_CERT_SIGN);
                    pki.MEDIA_WriteCertAndPriKey(Base64.decode(signCert, Base64.DEFAULT), Base64.decode(signPri, Base64.DEFAULT), MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_DISK);
                }
                GLog.d("8");
                pki.MEDIA_UnLoad();
            } catch (Exception e) {
                GLog.d("webPlugin_Init ====== " + e);
            }
        } catch (Exception e) {
            GLog.d("webPlugin_Init ====== " + e);
        }
    }

    // 핸드폰 권한 확인 (전화)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        if (requestCode == Constants.CALL_PHONE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한 수락", Toast.LENGTH_SHORT).show();
            } else {
                if (sharedPreferences.getBoolean("FIRST_REQUEST", true)) {
                    GLog.d("첫 권한 요청 권한 미수락");
                    Toast.makeText(this, "권한 미수락", Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putBoolean("FIRST_REQUEST", false).apply();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                        Toast.makeText(this, "권한 미수락", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "앱 설정 창에서 전화 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
                        mAction = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        try {
                            mAction.setData(Uri.parse("package:" + getPackageName()));
                        } catch (Exception e) {
                            GLog.d("usri parse 불가 == " + e);
                            return;
                        }
                        startActivity(mAction);
                    }
                }
            }
        }
    }
}