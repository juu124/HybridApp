package com.dki.hybridapptest.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.dki.hybridapptest.Interface.WebAppInterface;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.constants.Constants;
import com.dki.hybridapptest.util.GLog;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    private WebSettings mWebSettings;
    private String mWebURL;
    private Intent mAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.webview);
        mWebSettings = mWebView.getSettings();
        mWebURL = "file:///android_asset/sample.html";

        mWebSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(this), "DKITec");
        mWebView.loadUrl(mWebURL);

        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (TextUtils.isEmpty(url)) {
                    GLog.d("url is null");
                } else {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        GLog.d("CALL_PHONE PERMISSION_DENIED 입니다.");
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Constants.CALL_PHONE_REQUEST_CODE);
                    } else {
                        if (url.toLowerCase().contains("tel".toLowerCase())) {
                            mAction = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                            startActivity(mAction);
                        }
                    }
                    if (url.toLowerCase().contains("mailto".toLowerCase())) {
                        mAction = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                        startActivity(mAction);
                    }
                }
                return true;
            }
        });
    }
}