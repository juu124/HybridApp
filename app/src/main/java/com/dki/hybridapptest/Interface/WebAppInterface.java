package com.dki.hybridapptest.Interface;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.activities.HelloWorldActivity;
import com.dki.hybridapptest.activities.UserListActivity;
import com.dki.hybridapptest.utils.GLog;

public class WebAppInterface {
    private Context mContext;
    private WebView mWebView;
    private Intent mIntent;

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
            mIntent = new Intent(mContext, HelloWorldActivity.class);
            mContext.startActivity(mIntent);
        } else if (TextUtils.equals(word, mContext.getString(R.string.world))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Alert").setMessage(mContext.getString(R.string.hello_world));
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            Toast.makeText(mContext, word, Toast.LENGTH_SHORT).show();
        }
    }

    @JavascriptInterface
    public void showDialog() {
        EditText editText = new EditText(mContext);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Alert")
                .setView(editText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "다이얼로그 OK 버튼 누름", Toast.LENGTH_SHORT).show();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mWebView.loadUrl("javascript:nativeToWebWithMsg('" + String.valueOf(editText.getText()) + "')");
                            }
                        });
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "다이얼로그 NO 버튼 누름", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @JavascriptInterface
    public void showUserList() {
        GLog.d("user infomation을 선택했나요?");
        Toast.makeText(mContext, "유저 리스트 액티비티", Toast.LENGTH_SHORT).show();
        mIntent = new Intent(mContext, UserListActivity.class);
        mContext.startActivity(mIntent);
    }
}
