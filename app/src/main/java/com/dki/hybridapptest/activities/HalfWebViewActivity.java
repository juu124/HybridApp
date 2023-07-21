package com.dki.hybridapptest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.bridge.AndroidBridge;
import com.dki.hybridapptest.utils.GLog;

public class HalfWebViewActivity extends AppCompatActivity {
    private WebView mWebView;
    private WebSettings mWebSettings;
    private AndroidBridge androidBridge;
    private Intent mIntent;
    private String url = null;
    private DisplayMetrics displayMetrics;
    private int width;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_half_web_view);
        mWebView = findViewById(R.id.webview);
        mWebSettings = mWebView.getSettings();
        mIntent = getIntent();
        url = mIntent.getStringExtra("url");

        displayMetrics = getApplicationContext().getResources().getDisplayMetrics();

        width = (int) (displayMetrics.widthPixels * 0.7);
        height = (int) (displayMetrics.heightPixels * 0.8);
        GLog.d("화면 width === " + width + "\n" + " 화면 height === " + height);
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        GLog.d("url === " + url);
        mWebSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
    }
}