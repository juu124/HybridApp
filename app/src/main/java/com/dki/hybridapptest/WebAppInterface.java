package com.dki.hybridapptest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebAppInterface {
    private Context mContext;

    WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void showToast(String word) {
        if (word.equalsIgnoreCase("Hello JavaScriptInterface")) {
            Toast.makeText(mContext, word, Toast.LENGTH_SHORT).show();
        } else if (word.equalsIgnoreCase("world")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Alert").setMessage("Hello World");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            Intent intent = new Intent(mContext, HelloWorld.class);
            mContext.startActivity(intent);
        }
    }
}
