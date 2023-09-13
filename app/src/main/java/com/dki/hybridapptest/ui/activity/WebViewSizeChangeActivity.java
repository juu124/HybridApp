package com.dki.hybridapptest.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.utils.Constant;
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
        GLog.d();
        super.onCreate(savedInstanceState);
        // 화면 캡쳐 방지
        if (Constant.USE_SCREEN_SHOT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_full_web_view);

        mWebView = findViewById(R.id.webview);
        mWebSettings = mWebView.getSettings();
        mWebSettings.setDomStorageEnabled(true);
        displayMetrics = getApplicationContext().getResources().getDisplayMetrics();   // 해당 기기 화면 사이즈

        mIntent = getIntent();
        url = mIntent.getStringExtra("url");
        fullMode = mIntent.getBooleanExtra("isFullMode", true);
        displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
//        GLog.d("width === " + displayMetrics.widthPixels);
//        GLog.d("height === " + displayMetrics.heightPixels);

        if (!TextUtils.isEmpty(url)) {
            if (!fullMode) {
                GLog.d("isfullMode false");
                int width = (int) (displayMetrics.widthPixels * 0.7);
                int height = (int) (displayMetrics.heightPixels * 0.8);
                layoutParams.height = height;
                layoutParams.width = width;
                GLog.d("width === " + width);
                GLog.d("height === " + height);
                getWindow().setAttributes(layoutParams);
            }
        } else {
            Toast.makeText(this, "url을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
        GLog.d("url === " + url);
        mWebSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
    }
}