package com.dki.hybridapptest.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.dki.hybridapptest.Interface.WebAppInterface;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.Util.GLog;

public class MainActivity extends AppCompatActivity {
    private WebView myWebView;
    private WebSettings myWebSettings;
    private String webURL;
    private Intent action;

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

        myWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.length() == 0 && url.equals("")) {
                    GLog.d("url is null");
                } else {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        GLog.d("CALL_PHONE PERMISSION_DENIED 입니다.");
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 10);
                    } else {
                        if (url.toLowerCase().contains("tel:010".toLowerCase())) {
                            action = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                            startActivity(action);
                        }
                    }
                    if (url.toLowerCase().contains("mailto:".toLowerCase())) {
                        action = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                        startActivity(action);
                    }
                }
                return true;
            }
        });
    }
}