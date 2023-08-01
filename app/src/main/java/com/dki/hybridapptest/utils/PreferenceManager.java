//package com.dki.hybridapptest.utils;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//
//public class PreferenceManager {
//
//    public static final String PREFERENCES_NAME = "mydatacenter";
//    private static final String DEFAULT_VALUE_STRING = "";
//    private static final boolean DEFAULT_VALUE_BOOLEAN = true;
//    private static final boolean DEFAULT_VALUE_PATTEN_LINE_BOOLEAN = false;
//    private static final int DEFAULT_VALUE_INT = -1;
//
//    private static SharedPreferences getPreferences(Context context) {
//        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
//    }
//
//    /**
//     * String 값 저장
//     *
//     * @param context
//     * @param key
//     * @param value
//     */
//
//    public static void setString(Context context, String key, String value) {
//        SharedPreferences prefs = getPreferences(context);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(key, value);
//        editor.commit();
//    }
//
//    /**
//     * boolean 값 저장
//     *
//     * @param context
//     * @param key
//     * @param value
//     */
//
//    public static void setBoolean(Context context, String key, boolean value) {
//        SharedPreferences prefs = getPreferences(context);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putBoolean(key, value);
//        editor.commit();
//    }
//
//    /**
//     * int 값 저장
//     *
//     * @param context
//     * @param key
//     * @param value
//     */
//
//    public static void setInt(Context context, String key, int value) {
//        SharedPreferences prefs = getPreferences(context);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putInt(key, value);
//        editor.commit();
//    }
//
//    /**
//     * String 값 로드
//     *
//     * @param context
//     * @param key
//     * @return
//     */
//
//    public static String getString(Context context, String key) {
//        SharedPreferences prefs = getPreferences(context);
//        String value = prefs.getString(key, DEFAULT_VALUE_STRING);
//        return value;
//    }
//
//    /**
//     * boolean 값 로드
//     *
//     * @param context
//     * @param key
//     * @return
//     */
//    public static boolean getBoolean(Context context, String key) {
//        SharedPreferences prefs = getPreferences(context);
//        boolean value = prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN);
//        return value;
//    }
//
//    /**
//     * boolean 값 로드
//     *
//     * @param context
//     * @param key
//     * @return
//     */
//    public static boolean getPattenLineBoolean(Context context, String key) {
//        SharedPreferences prefs = getPreferences(context);
//        boolean value = prefs.getBoolean(key, DEFAULT_VALUE_PATTEN_LINE_BOOLEAN);
//        return value;
//    }
//
//
//    /**
//     * int 값 로드
//     *
//     * @param context
//     * @param key
//     * @return
//     */
//
//    public static int getInt(Context context, String key) {
//        SharedPreferences prefs = getPreferences(context);
//        int value = prefs.getInt(key, DEFAULT_VALUE_INT);
//        return value;
//    }
//
//    /**
//     * 키 값 삭제
//     *
//     * @param context
//     * @param key
//     */
//
//    public static void removeKey(Context context, String key) {
//        SharedPreferences prefs = getPreferences(context);
//        SharedPreferences.Editor edit = prefs.edit();
//        edit.remove(key);
//        edit.commit();
//
//    }
//
//    /**
//     * 모든 저장 데이터 삭제
//     *
//     * @param context
//     */
//
//    public static void clear(Context context) {
//        SharedPreferences prefs = getPreferences(context);
//        SharedPreferences.Editor edit = prefs.edit();
//        edit.clear();
//        edit.commit();
//    }
//}
