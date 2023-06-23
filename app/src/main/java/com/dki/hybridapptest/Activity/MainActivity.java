package com.dki.hybridapptest.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.dki.hybridapptest.utils.Constants;
import com.dki.hybridapptest.utils.GLog;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    private WebSettings mWebSettings;
    private Intent mAction;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GLog.d("onCreate");
        mWebView = findViewById(R.id.webview);
        mWebSettings = mWebView.getSettings();

        mWebSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(this), "DKITec");
        mWebView.loadUrl(Constants.WEB_VIEW_URL);

        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (TextUtils.isEmpty(url)) {
                    GLog.d("url is null");
                } else {
                    if (url.startsWith("tel:")) {
                        if ((checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
                            GLog.d("CALL_PHONE PERMISSION_DENIED 입니다.");
                            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Constants.CALL_PHONE_REQUEST_CODE);
                        } else {
                            mAction = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                            startActivity(mAction);
                        }
                    } else if (url.startsWith("mailto:")) {
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
        sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        if (requestCode == Constants.CALL_PHONE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한 수락", Toast.LENGTH_SHORT).show();
            } else {
                if (sharedPreferences.getBoolean("FIRST_REQUEST", true)) {
                    GLog.d("첫 권한 요청 권한 미수락");
                    Toast.makeText(this, "권한 미수락", Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putBoolean("FIRST_REQUEST", false).apply();
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
}