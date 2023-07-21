package com.dki.hybridapptest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.bridge.AndroidBridge;
import com.dki.hybridapptest.utils.GLog;

public class FullWebViewActivity extends AppCompatActivity {
    private WebView mWebView;
    private WebSettings mWebSettings;
    private AndroidBridge androidBridge = null;
    private Intent mIntent;
    private String url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GLog.d();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_web_view);
        mWebView = findViewById(R.id.webview);
        mWebSettings = mWebView.getSettings();
        mIntent = getIntent();
        url = mIntent.getStringExtra("url");

        GLog.d("url === " + url);
        mWebSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
    }
}