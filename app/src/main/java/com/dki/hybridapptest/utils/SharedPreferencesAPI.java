package com.dki.hybridapptest.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.internal.Primitives;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPreferencesAPI {
    private static SharedPreferencesAPI mInstance;
    private final SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    // SharedPreferences Name
    public static final String SP_NAME = "HybridAppTestSharedPrefs";       // Preference Name 수정필요

    public SharedPreferencesAPI(Context mContext) {
        mPref = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        mEditor = mPref.edit();
    }

    public static SharedPreferencesAPI getInstance(Context mContext) {
        if (mInstance == null) {
            synchronized (SharedPreferencesAPI.class) {

                if (mInstance == null) {
                    mInstance = new SharedPreferencesAPI(mContext);
                }
            }
        }
        return mInstance;
    }

    // SharedPreferences Key

    private static final String PREF_KEY_TEST = "test";
    private static final String PREF_KEY_OBJECT_TEST = "object.test";
    private static final String PREF_KEY_LOGIN_ID = "PREF_KEY_LOGIN_ID";
    private static final String PREF_KEY_LOGIN_PW = "PREF_KEY_LOGIN_PW";
    private static final String PREF_KEY_AUTO_LOGIN_CHECK = "PREF_KEY_AUTO_LOGIN_CHECK";
    private static final String PREF_KEY_SEARCH_URL = "PREF_KEY_SEARCH_URL";
    private static final String PREF_KEY_DISPLAY_FULL_MODE = "PREF_KEY_DISPLAY_FULL_MODE";
    private static final String PREF_KEY_HEALTH_RECODE_PATIENT = "PREF_KEY_HEALTH_RECODE_PATIENT";
    private static final String PREF_KEY_HEALTH_DEVICE_TYPE = "PREF_KEY_HEALTH_DEVICE_TYPE";

    /**
     * Get Template Function
     */
    public String getUrl() {
        return getString(PREF_KEY_SEARCH_URL);
    }

    public String getTest() {
        return getString(PREF_KEY_TEST);
    }

    public String getLoginId() {
        return getString(PREF_KEY_LOGIN_ID);
    }

    public String getLoginPw() {
        return getString(PREF_KEY_LOGIN_PW);
    }

    public boolean getAutoLogin() {
        return getBoolean(PREF_KEY_AUTO_LOGIN_CHECK);
    }

    public boolean getDisplayFullMode() {
        return getBoolean(PREF_KEY_DISPLAY_FULL_MODE);
    }

    public String getRecodePatient() {
        return getString(PREF_KEY_HEALTH_RECODE_PATIENT);
    }

    public String getDeviceType() {
        return getString(PREF_KEY_HEALTH_DEVICE_TYPE);
    }

    /**
     * Set Template Function
     */
    public void setUrl(String value) {
        set(PREF_KEY_SEARCH_URL, value);
    }

    public void setTest(String value) {
        set(PREF_KEY_TEST, value);
    }

    public void setLoginPw(String value) {
        set(PREF_KEY_LOGIN_PW, value);
    }

    public void setLoginId(String value) {
        set(PREF_KEY_LOGIN_ID, value);
    }

    public void setAutoLogin(boolean value) {
        set(PREF_KEY_AUTO_LOGIN_CHECK, value);
    }

    public void setDisplayFullMode(boolean value) {
        set(PREF_KEY_DISPLAY_FULL_MODE, value);
    }

    public void setRecodePatient(String value) {
        set(PREF_KEY_HEALTH_RECODE_PATIENT, value);
    }

    public void setDeviceType(String value) {
        set(PREF_KEY_HEALTH_DEVICE_TYPE, value);
    }

    public <T> T getTest(Class<T> classOfT) {
        return getObject(PREF_KEY_OBJECT_TEST, classOfT);
    }

    public <T> void setTest(T value) {
        set(PREF_KEY_OBJECT_TEST, value);
    }

    /////////////////////////////////////////////////////////////////////
    // 기본 SharedPreferences Api
    private boolean set(String key, boolean value) {
        boolean result;
        mEditor.putBoolean(key, value);
        result = mEditor.commit();
        return result;
    }

    private boolean set(String key, int value) {

        boolean result;
        mEditor.putInt(key, value);
        result = mEditor.commit();
        return result;
    }

    private boolean set(String key, long value) {
        boolean result;
        mEditor.putLong(key, value);
        result = mEditor.commit();
        return result;
    }

    private boolean set(String key, String value) {
        boolean result;
        mEditor.putString(key, value);
        result = mEditor.commit();
        return result;
    }

    private <T> boolean set(String key, T value) {
        boolean result;

        mEditor.putString(key, new Gson().toJson(value));
        result = mEditor.commit();

        return result;
    }

    private boolean setStringList(String key, ArrayList<String> valueList) {
        boolean result;
        for (int i = 0; i < valueList.size(); i++) {
            mEditor.putString(key + i, valueList.get(i));
        }

        result = mEditor.commit();
        return result;
    }

    private boolean getBoolean(String key) {
        return mPref.getBoolean(key, false);
    }

    private int getInt(String key) {
        return mPref.getInt(key, 0);
    }

    private long getLong(String key) {
        return mPref.getLong(key, 0L);
    }

    private String getString(String key) {
        return mPref.getString(key, "");
    }

    private ArrayList<String> getStringArrayList(String key, String strDefault) {
        ArrayList<String> strList = new ArrayList<>();

        int i = 0;
        while (!TextUtils.isEmpty(mPref.getString(key + i, strDefault))) {
            strList.add(mPref.getString(key + i, strDefault));
            i++;
        }

        return strList;
    }

    public <T> T getObject(String key, Class<T> classOfT) {
        String json = mPref.getString(key, "");
        Object object = new Gson().fromJson(json, (Type) classOfT);

        return Primitives.wrap(classOfT).cast(object);
    }

    //// Listener ////
    public void setOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener l) {
        mPref.registerOnSharedPreferenceChangeListener(l);
    }

    public void releaseOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener l) {
        mPref.unregisterOnSharedPreferenceChangeListener(l);
    }


}
