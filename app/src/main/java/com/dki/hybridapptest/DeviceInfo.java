package com.dki.hybridapptest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.dki.hybridapptest.utils.PreferenceManager;
import com.dream.magic.fido.rpsdk.client.LOCAL_AUTH_TYPE;
import com.dream.magic.fido.rpsdk.client.MagicFIDOUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceInfo {

//    // device id 가져오기
//    public String getDeviceId(@NonNull Context context) {
//        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//    }

//    // device 제조사 가져오기
//    public String getManufacturer() {
//        return Build.MANUFACTURER;
//    }

    // device 브랜드 가져오기
    public String getDeviceBrand() {
        return Build.BRAND;
    }

    // device 모델명 가져오기
    public String getDeviceModel() {
        return Build.MODEL;
    }

    // device Android OS 버전 가져오기
    public String getDeviceOs() {
        return Build.VERSION.RELEASE;
    }

    // 단말 지문 인식 기능 지원 여부 확인
//    public static String isFingerprintSensorAvailable(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
////            fingerprintManager != null && fingerprintManager.isHardwareDetected();
//            return "지문";
//        }
//        return "";
//    }

    //디바이스 OS에서 제공 가능한 생체인증 유형
    public String getDeviceInfo(Activity mActivity) {
        GLog.d("getDeviceInfo=============");
        String fingerPrint = "지문";

        MagicFIDOUtil magicFIDOUtil = new MagicFIDOUtil(mActivity);
        boolean isAvailableFingerPrint = magicFIDOUtil.isAvailableFIDO(LOCAL_AUTH_TYPE.LOCAL_FINGERPRINT_TYPE);
//        JSONObject obj = new JSONObject();
        JSONObject data = new JSONObject();

        SharedPreferences prefs = mActivity.getSharedPreferences(PreferenceManager.PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean isInvisiblePattern = prefs.getBoolean(Constant.PATTERN_INVISIBLE, false);
        try {
            data.put("faceid", "N");
            if (isAvailableFingerPrint) {
                data.put("fingerprint", "Y");
                GLog.d("지문인식 가능 ========");
                return fingerPrint;
            } else {
                data.put("fingerprint", "N");
                GLog.d("지문인식 불가능 =========");
                return "";
            }
//            data.put("appVersion", BuildConfig.VERSION_NAME);
//            data.put("isInvisiblePattern", isInvisiblePattern);
//            obj.put("resultCode", "SUCCESS");
//            obj.put("data", data);

        } catch (JSONException e) {
            e.printStackTrace();
            try {
                data.put("faceid", "N");
                if (isAvailableFingerPrint) {
                    data.put("fingerprint", "Y");
                    GLog.d("지문인식 가능============");
                    return fingerPrint;
                } else {
                    data.put("fingerprint", "N");
                    GLog.d("지문인식 불가능==============");
                    return "";
                }
//                data.put("appVersion", BuildConfig.VERSION_NAME);
//                obj.put("resultCode", "FAIL");
//                obj.put("data", data);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return "";
    }
}
