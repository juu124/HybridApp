package com.dki.hybridapptest;

import android.app.Application;

import com.dki.hybridapptest.utils.GLog;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        GLog.d();
        // Firebase 사용전 초기화
        FirebaseApp.initializeApp(MyApplication.this);
    }
}
