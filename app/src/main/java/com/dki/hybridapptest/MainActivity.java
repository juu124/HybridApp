package com.dki.hybridapptest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    WebView myWebView;
    WebSettings myWebSettings;
    String webURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = (WebView) findViewById(R.id.webview);
        myWebSettings = myWebView.getSettings();
        webURL = "file:///android_asset/sample.html";

        myWebSettings.setJavaScriptEnabled(true);
//        myWebView.loadUrl("https://m.naver.com/");
        myWebView.loadUrl(webURL);
    }
}