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
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.dki.hybridapptest.Interface.ProgressBarListener;
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

    // 프로그래스 바
    private ProgressBar mProgressBar;
    private ProgressBarListener progressBarListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.webview);
        mProgressBar = findViewById(R.id.indeterminate_progressbar);
        mWebSettings = mWebView.getSettings();
        mProgressBar.setVisibility(View.GONE);
        mWebSettings.setJavaScriptEnabled(true);

        androidBridge = new AndroidBridge(mWebView, MainActivity.this, new ProgressBarListener() {
            @Override
            public void showProgressBar() {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void unShownProgressBar() {
                mProgressBar.setVisibility(View.GONE);
            }
        });
        mWebView.addJavascriptInterface(androidBridge, "DKITec");
        mWebView.loadUrl(Constants.WEB_VIEW_MAIN_URL);
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
                        if ((checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)) {
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