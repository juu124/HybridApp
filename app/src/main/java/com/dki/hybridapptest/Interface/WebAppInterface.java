package com.dki.hybridapptest.Interface;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.dki.hybridapptest.Activity.HelloWorldActivity;

public class WebAppInterface {
    private Context mContext;

    public WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void showToast(String word) {
        if (!TextUtils.isEmpty(word)) {
            if (word.equalsIgnoreCase("hello")) {
                Intent intent = new Intent(mContext, HelloWorldActivity.class);
                mContext.startActivity(intent);
            } else if (word.equalsIgnoreCase("world")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Alert").setMessage("Hello World");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                Toast.makeText(mContext, word, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
