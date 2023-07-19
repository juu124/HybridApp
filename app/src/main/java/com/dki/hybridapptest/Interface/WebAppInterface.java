package com.dki.hybridapptest.Interface;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;

import com.dki.hybridapptest.dialog.InputDialog;
import com.dreamsecurity.magicvkeypad.MagicVKeypad;

public class WebAppInterface {
    private Context mContext;
    private WebView mWebView;
    private Intent mIntent;
    private InputDialog inputDialog;
    private String strfieldID;
    private String strViewMode;

    // 키보드
    boolean bUseDummyData = false;
    private MagicVKeypad magicVKeypad;
    private String mCallbackFuntion = "";


    private String RSAEncryptData = null;
    private String charResultData = "";
    private String AESEncData = "";
    private String AESDecData = "";

    public WebAppInterface(Context context, WebView webView) {
        mContext = context;
        mWebView = webView;
    }

//    Handler handler = new Handler();
//
//    // Say hello, hello, world 버튼 이벤트
//    @JavascriptInterface
//    public void showToast(String word) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                mWebView.loadUrl("javascript:nativeToWeb()");
//            }
//        });
//        if (TextUtils.equals(word, mContext.getString(R.string.hello))) {
//            mIntent = new Intent(mContext, HelloWorldActivity.class);
//            mContext.startActivity(mIntent);
//        } else if (TextUtils.equals(word, mContext.getString(R.string.world))) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//            builder.setTitle("Alert").setMessage(mContext.getString(R.string.hello_world));
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//        } else {
//            Toast.makeText(mContext, word, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    // show Dialog 버튼 이벤트
//    @JavascriptInterface
//    public void showDialog() {
//        inputDialog = new InputDialog(mContext, new InputDialogClickListener() {
//            @Override
//            public void onInputPositiveClick(String text) {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        GLog.d("text =======" + text);
//                        mWebView.loadUrl("javascript:nativeToWebWithMsg('" + text + "')");
//                    }
//                });
//            }
//
//            @Override
//            public void onInputNegativeClick() {
//
//            }
//        });
//        inputDialog.show();
//    }
//
//    // show User Information 선택
//    @JavascriptInterface
//    public void showUserList() {
//        GLog.d("user infomation을 선택했나요?");
//        Toast.makeText(mContext, "유저 리스트 액티비티", Toast.LENGTH_SHORT).show();
//        mIntent = new Intent(mContext, UserListActivity.class);
//        mContext.startActivity(mIntent);
//    }
//
//    // show User Information Certification 선택
//    @JavascriptInterface
//    public void showCertificationList() {
//        GLog.d("showCertificationList 클릭");
//        Toast.makeText(mContext, "showCertificationList 클릭", Toast.LENGTH_SHORT).show();
//        mIntent = new Intent(mContext, UserCertificationActivity.class);
//        mContext.startActivity(mIntent);
//    }
//
//    @JavascriptInterface
//    public void showKeyPad(String strJsonObject) {
////        Logger.d(TAG, "strJsonObject : " + strJsonObject);
//        magicVKeypad = new MagicVKeypad();
//
//        JSONObject jsonObject = null;
//        String callback = "";
//        try {
//            if (!strJsonObject.equals("")) {
//                jsonObject = new JSONObject(strJsonObject);
//
//                if (!jsonObject.isNull("callback")) {
//                    callback = jsonObject.getString("callback");
//                }
//                setCallbakckFuction(callback);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        //가상키보드 클래스 생성
//        // settingKeyPad();
//        //문자 키패드를 활성화
//        startCharKeyPad();
//    }
//
//    // callMagicVKeypad(inputFieldName, viewMode, keypadType, titleText, subTitleText, maskingType, maxLength, useReplace, portraitFixed)
//    @JavascriptInterface
//    public void showKeyBoard(final String fieldID,
//                             final String viewMode,
//                             final String keypadType,
//                             final String option) {
//
//        strfieldID = fieldID;
//        strViewMode = viewMode;
//
//        JSONObject options = null;
//        try {
//            options = new JSONObject(String.valueOf(option));
//            if (options.getBoolean("UseDummyData")) {
//                bUseDummyData = true;
//            } else {
//                bUseDummyData = false;
//            }
//
//            // 풀모드
//            if (viewMode.equals("FULL_TYPE")) {
//                if (keypadType.equals("CHAR_TYPE")) { // 풀모드 - 문자키패드
//
//                    // 입력 필드명 설정
//                    magicVKeypad.setFieldName(fieldID);
//
//                    // 마스킹 설정
//                    magicVKeypad.setMasking(options.getInt("MaskingType"));
//
//                    // 풀모드 설정
//                    magicVKeypad.setFullMode(true);
//
//                    // 입력 길이 제한
//                    magicVKeypad.setMaxLength(options.getInt("MaxLength"));
//
//                    // 화면 고정(DEFAULT: FALSE)
//                    if (options.getBoolean("PortraitFix"))
//                        magicVKeypad.setPortraitFixed(true);
//                    else
//                        magicVKeypad.setPortraitFixed(false);
//
//                    if (options.getBoolean("UseSpeaker"))
//                        magicVKeypad.setUseSpeaker(true);
//                    else
//                        magicVKeypad.setUseSpeaker(false);
//
//
//                    // 스크린샷 허용
//                    if (options.getBoolean("Screenshot"))
//                        magicVKeypad.setScreenshot(true);
//                    else {
//                        magicVKeypad.setScreenshot(false);
//                    }
//
//
//                    // 키패드 실행
//                    magicVKeypad.startCharKeypad(mOnClickInterface);
//
//                } else { // 풀모드 - 숫자키패드
//
//                    // 마스킹 타입 설정
//                    magicVKeypad.setMasking(options.getInt("MaskingType"));
//
//                    // 화면 세로 고정(DEFAULT: FALSE)
//                    if (options.getBoolean("PortraitFix"))
//                        magicVKeypad.setPortraitFixed(true);
//                    else
//                        magicVKeypad.setPortraitFixed(false);
//
//
//                    // 입력 필드명 설정
//                    magicVKeypad.setFieldName(fieldID);
//
//                    // 풀모드 설정
//                    magicVKeypad.setFullMode(true);
//
//                    // 키패드 재배치 허용(DEFAULT : FALSE)
//                    if (options.getBoolean("UseReplace"))
//                        magicVKeypad.setNumUseReplace(true);
//                    else
//                        magicVKeypad.setNumUseReplace(false);
//
//                    // 입력 길이 제한
//                    magicVKeypad.setMaxLength(options.getInt("MaxLength"));
//
//                    if (options.getBoolean("Screenshot"))
//                        magicVKeypad.setScreenshot(true);
//                    else {
//                        magicVKeypad.setScreenshot(false);
//                    }
//
//                    if (options.getBoolean("UseSpeaker"))
//                        magicVKeypad.setUseSpeaker(true);
//                    else
//                        magicVKeypad.setUseSpeaker(false);
//
//                    // 키패드 실행
//                    magicVKeypad.startNumKeypad(mOnClickInterface);
//                }
//
//                // 하프모드
//            } else if (viewMode.equals("HALF_TYPE")) {
//                if (keypadType.equals("CHAR_TYPE")) { // 하프모드 - 문자키패드
//                    if (magicVKeypad.isKeyboardOpen()) {
//                        magicVKeypad.closeKeypad();
//                    }
//
//                    // 입력 필드명 설정
//                    magicVKeypad.setFieldName(fieldID);
//
//                    // masking 설정
//                    magicVKeypad.setMasking(options.getInt("MaskingType"));
//
//                    // 하프모드 설정
//                    magicVKeypad.setFullMode(false);
//
//                    // 입력 길이 제한
//                    magicVKeypad.setMaxLength(options.getInt("MaxLength"));
//
//                    if (options.getBoolean("UseSpeaker"))
//                        magicVKeypad.setUseSpeaker(true);
//                    else
//                        magicVKeypad.setUseSpeaker(false);
//
//                    // 키패드 실행
//                    magicVKeypad.startCharKeypad(mOnClickInterface);
//
//                } else { // 하프모드 - 숫자키패드
//                    if (magicVKeypad.isKeyboardOpen()) {
//                        magicVKeypad.closeKeypad();
//                    }
//                    // 필드이름 지정
//                    magicVKeypad.setFieldName(fieldID);
//
//                    // masking 설정
//                    magicVKeypad.setMasking(options.getInt("MaskingType"));
//
//                    // 입력 길이 제한
//                    magicVKeypad.setMaxLength(options.getInt("MaxLength"));
//
//                    // 하프모드 설정
//                    magicVKeypad.setFullMode(false);
//
//                    // 화면 고정(DEFAULT: FALSE)
//                    if (options.getBoolean("PortraitFix"))
//                        magicVKeypad.setPortraitFixed(true);
//                    else
//                        magicVKeypad.setPortraitFixed(false);
//
//
//                    //재배치 설정
//                    if (options.getBoolean("UseReplace"))
//                        magicVKeypad.setNumUseReplace(true);
//                    else
//                        magicVKeypad.setNumUseReplace(false);
//
//                    if (options.getBoolean("UseSpeaker"))
//                        magicVKeypad.setUseSpeaker(true);
//                    else
//                        magicVKeypad.setUseSpeaker(false);
//
//                    magicVKeypad.setMultiClick(MagicVKeyPadSettings.bMultiClick);
//
//                    // 키패드 실행
//                    magicVKeypad.startNumKeypad(mOnClickInterface);
//
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    //콜백 함수 호출 공통으로 사용
//    public void callbackFunction(String callback, String result) {
//        GLog.d("callback : " + callback);
//        GLog.d("result : " + result);
//
//        if (callback == null || callback.equals("")) {
//            return;
//        }
//
//        try {
//            mWebView.post(new Runnable() {
//                @Override
//                public void run() {
//                    mWebView.loadUrl("javascript:" + callback + "('" + result + "')");
//                }
//            });
//        } catch (NullPointerException ne) {
//            ne.printStackTrace();
//        }
//    }
//
//    public String getCallbackFunction() {
//        return mCallbackFuntion;
//    }
//
//    private MagicVKeypadOnClickInterface mOnClickInterface = new MagicVKeypadOnClickInterface() {
//        @Override
//        public void onMagicVKeypadClick(MagicVKeypadResult magicVKeypadResult) {
//
//            int licenseResult = magicVKeypadResult.getLicenseResult();
//            if (licenseResult != 0) {
//                Log.d("TEST", "라이선스 검증 실패");
//            }
//
//            byte[] decData = null;
//
//            // 풀모드
//            if (magicVKeypad.isFullMode()) {
//                if (!MagicVKeyPadSettings.bUseE2E)
//                    decData = magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData());
//                if (magicVKeypad.getEncryptData() != null) {
//                    if (magicVKeypadResult.getEncryptData() != null)
//                        RSAEncryptData = new String(magicVKeypadResult.getEncryptData());
//                }
//
//                if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_OK_BUTTON) { // 확인 버튼 클릭
//                    magicVKeypad.closeKeypad();
//                    if (TextUtils.equals(magicVKeypadResult.getFieldName(), MagicVKeyPadSettings.numFieldName)) {
//                        if (!MagicVKeyPadSettings.bUseE2E) {
//                            if (decData != null) {
////                                insertNum.setText(new String(magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData())));
//                            }
//
//                        } else {
//                            if (magicVKeypad.getEncryptData() != null) {
//                                RSAEncryptData = new String(magicVKeypadResult.getEncryptData());
//                            }
//                        }
//                    } else if (TextUtils.equals(magicVKeypadResult.getFieldName(), MagicVKeyPadSettings.charFieldName)) {
//                        if (decData != null) {
//                            if (!MagicVKeyPadSettings.bUseE2E) {
////                                insertChar.setText(new String(magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData())));
////                                charResultData = new String(magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData()));
////                                insertChar.setText(charResultData);
//
//                                String randomedPassword = null;
//                                try {
//                                    randomedPassword = new String(magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData()));
//                                } catch (Exception e) {
//                                    magicVKeypad.closeKeypad();
//                                    return;
//                                }
////                    String randomedPassword = byteArrayToHex(magicVKeypadResult.getEncryptData());
//                                GLog.d("randomedPassword : " + randomedPassword);
//
//                                magicVKeypad.closeKeypad();
//
//                                JSONObject obj = new JSONObject();
//                                JSONObject data = new JSONObject();
//                                try {
//                                    obj.put("resultCode", "SUCCESS");
//                                    if (!"".equals(randomedPassword)) {
////                                        data.put("randomedPassword", randomedPassword);
//                                        //취약점 수정으로 인해 패스워드 암호화 하여 전송
//                                        data.put("randomedPassword", Base64.encodeToString(randomedPassword.getBytes(), Base64.NO_WRAP));
//                                    } else {
//                                        GLog.d("randomedPassword : " + randomedPassword);
//                                    }
//                                    obj.put("data", data);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                callbackFunction(getCallbackFunction(), obj.toString());
//                            } else {
//                                if (magicVKeypad.getEncryptData() != null)
//                                    RSAEncryptData = new String(magicVKeypadResult.getEncryptData());
//                            }
//
//                        }
//
//
//                        if (!MagicVKeyPadSettings.bUseE2E) {
//                            if (decData != null) {
//                                strPlaintext = "";
//
//                                for (int i = 0; i < decData.length; i++) {
//                                    strPlaintext = strPlaintext + "•";
//                                }
//                            }
//                            strScript += strfieldID + "','" + strPlaintext + "')";
//                            HybridResult.strScript = strScript;
//
//                            mWebView.loadUrl(strScript);
//
//
//                            if (magicVKeypadResult.getEncryptData() != null) {
//                                tv_AESEncryptData.setText("AES256 암호화 데이터 : " + byteArrayToHex(magicVKeypadResult.getEncryptData()));
//                                tv_AESDecryptData.setText("AES256 복호화 데이터 : " + new String(magicVKeypad.getDecryptData((magicVKeypadResult.getEncryptData()))));
//                            }
//                        } else {
//                            strScript += strfieldID + "','" + "" + "')";
//                            mWebView.loadUrl(strScript);
//                            if (magicVKeypadResult.getEncryptData() != null)
//                                tv_RSAEncryptData.setText("RSA 암호화 데이터 : " + new String(magicVKeypadResult.getEncryptData()));
//                        }
//                    }
//                }
//                // 하프모드
//                else {
//                    if (!MagicVKeyPadSettings.bUseE2E) {
//                        decData = magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData());
//                        if (decData != null) {
//                            AESDecData = byteArrayToHex(magicVKeypadResult.getEncryptData());
//                            AESEncData = new String(decData);
//                        }
//                    }
//                    if (magicVKeypad.getEncryptData() != null)
//                        RSAEncryptData = new String(magicVKeypad.getEncryptData());
//
//
//                    if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_CANCEL_BUTTON) {
//                        magicVKeypad.closeKeypad();
//                        strScript += strViewMode + "','" + strfieldID + "','" + "" + "')";
//                        HybridResult.strScript = strScript;
//                        mWebView.loadUrl(strScript);
//                    } else if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_OK_BUTTON) {
//                        if (magicVKeypadResult.getEncryptData() != null) {
//                            if (MagicVKeyPadSettings.bUseE2E == false) {
//                                tv_AESEncryptData.setText("AES256 암호화 데이터 : " + byteArrayToHex(magicVKeypadResult.getEncryptData()));
//                                tv_AESDecryptData.setText("AES256 복호화 데이터 : " + new String(magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData())));
//
//                            } else {
//                                RSAEncryptData = new String(magicVKeypad.getEncryptData());
//                                tv_RSAEncryptData.setText("RSA 암호화 데이터 : " + RSAEncryptData);
//
//                            }
//                        }
//
//                        magicVKeypad.closeKeypad();
//                    } else if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_CHAR_NUM_BUTTON) {
//                        if (MagicVKeyPadSettings.bUseE2E) {
//                            if (magicVKeypad.getEncryptData() != null)
//                                RSAEncryptData = new String(magicVKeypad.getEncryptData());
//                        } else {
//                            if (decData != null) {
//                                strPlaintext = "";
//
//                                for (int i = 0; i < decData.length; i++) {
//                                    strPlaintext = strPlaintext + "•";
//                                }
//                            }
//                            strScript += strfieldID + "','" + strPlaintext + "')";
//                            mWebView.loadUrl(strScript);
//
//                        }
//                    }
//                }
//            }
//        }
//
//    };
}