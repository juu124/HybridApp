package com.dki.hybridapptest.Activity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.dki.hybridapptest.Interface.WebAppInterface;
import com.dki.hybridapptest.R;

public class MainActivity extends AppCompatActivity {
    private WebView myWebView;
    private WebSettings myWebSettings;
    private String webURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = findViewById(R.id.webview);
        myWebSettings = myWebView.getSettings();
        webURL = "file:///android_asset/sample.html";

        myWebSettings.setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new WebAppInterface(this), "DKITec");
        myWebView.loadUrl(webURL);
    }
}