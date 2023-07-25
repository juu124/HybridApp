package com.dki.hybridapptest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.utils.GLog;

public class WebViewSizeChangeActivity extends AppCompatActivity {
    private WebView mWebView;
    private WebSettings mWebSettings;
    private Intent mIntent;
    private String url = null;
    private boolean fullMode = true;

    // 화면 크기
    private DisplayMetrics displayMetrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_web_view);

        mWebView = findViewById(R.id.webview);
        mWebSettings = mWebView.getSettings();
        displayMetrics = getApplicationContext().getResources().getDisplayMetrics();   // 해당 기기 화면 사이즈

        mIntent = getIntent();
        url = mIntent.getStringExtra("url");
        fullMode = mIntent.getBooleanExtra("displaySize", true);

        // fullMode 값에 따른 화면 크기 조정
        if (!TextUtils.isEmpty(url)) {
            if (!fullMode) { // 하프 모드
                int width = (int) (displayMetrics.widthPixels * 0.7);
                int height = (int) (displayMetrics.heightPixels * 0.8);
                getWindow().getAttributes().width = width;
                getWindow().getAttributes().height = height;
            }
        } else {
            Toast.makeText(this, "url을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }

        GLog.d("url === " + url);
        mWebSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
    }
}