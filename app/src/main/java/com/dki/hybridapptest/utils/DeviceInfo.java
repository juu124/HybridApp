package com.dki.hybridapptest.utils;

import android.os.Build;

public class DeviceInfo {

    // device 모델명 가져오기
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    // device Android OS 버전 가져오기
    public static String getDeviceOs() {
        return Build.VERSION.RELEASE;
    }
}
