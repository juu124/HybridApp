package com.dki.hybridapptest.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
                    if (url.toLowerCase().contains("tel".toLowerCase())) {
                        if ((checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
                            GLog.d("CALL_PHONE PERMISSION_DENIED 입니다.");
                            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Constants.CALL_PHONE_REQUEST_CODE);
                        } else {
                            mAction = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                            startActivity(mAction);
                        }
                    } else if (url.toLowerCase().contains("mailto".toLowerCase())) {
                        mAction = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                        startActivity(mAction);
                    } else {
                        Toast.makeText(MainActivity.this, "없음", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.CALL_PHONE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한 수락", Toast.LENGTH_SHORT).show();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                    Toast.makeText(this, "권한 미수락", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "앱 설정 창에서 전화 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
                    mAction = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    mAction.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(mAction);
                }
            }
        }
    }
}