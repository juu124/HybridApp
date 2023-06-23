package com.dki.hybridapptest.Interface;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.dki.hybridapptest.Activity.HelloWorldActivity;
import com.dki.hybridapptest.R;

public class WebAppInterface {
    private Context mContext;
    private WebView mWebView;

    public WebAppInterface(Context context, WebView webView) {
        mContext = context;
        mWebView = webView;
    }

    Handler handler = new Handler();

    @JavascriptInterface
    public void showToast(String word) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl("javascript:nativeToWeb()");
            }
        });
        if (TextUtils.equals(word, mContext.getString(R.string.hello))) {
            Intent intent = new Intent(mContext, HelloWorldActivity.class);
            mContext.startActivity(intent);
        } else if (TextUtils.equals(word, mContext.getString(R.string.world))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Alert").setMessage(mContext.getString(R.string.hello_world));
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            Toast.makeText(mContext, word, Toast.LENGTH_SHORT).show();
        }
    }
}
