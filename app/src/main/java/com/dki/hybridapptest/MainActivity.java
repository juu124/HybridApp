package com.dki.hybridapptest;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WebView myWebView;
    private WebSettings myWebSettings;
    private String webURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = (WebView) findViewById(R.id.webview);
        myWebSettings = myWebView.getSettings();
        webURL = "file:///android_asset/sample.html";

        myWebSettings.setJavaScriptEnabled(true);
//        myWebView.loadUrl("https://m.naver.com/");
        myWebView.addJavascriptInterface(new WebAppInterface(this), "DKITec");
        myWebView.loadUrl(webURL);
    }
}