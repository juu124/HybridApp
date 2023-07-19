package com.dki.hybridapptest.bridge;

import static com.dream.magic.fido.authenticator.common.asm.authinfo.ASMInstallAuth.byteArrayToHex;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.dki.hybridapptest.HybridResult;
import com.dki.hybridapptest.Interface.InputDialogClickListener;
import com.dki.hybridapptest.ProcessCertificate;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.activities.HelloWorldActivity;
import com.dki.hybridapptest.activities.HybridModeActivity;
import com.dki.hybridapptest.activities.UserCertificationActivity;
import com.dki.hybridapptest.activities.UserListActivity;
import com.dki.hybridapptest.dialog.InputDialog;
import com.dki.hybridapptest.kfido.FIDOAuthentication;
import com.dki.hybridapptest.kfido.FIDODeRegistration;
import com.dki.hybridapptest.kfido.FIDORegistration;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.dki.hybridapptest.utils.MagicVKeyPadSettings;
import com.dki.hybridapptest.utils.PreferenceManager;
import com.dream.magic.fido.rpsdk.client.LOCAL_AUTH_TYPE;
import com.dream.magic.fido.rpsdk.client.MagicFIDOUtil;
import com.dream.magic.fido.rpsdk.util.KFidoUtil;
import com.dream.magic.fido.uaf.protocol.kfido.KCertificate;
import com.dreamsecurity.magicmrs.MagicMRS;
import com.dreamsecurity.magicvkeypad.MagicVKeypad;
import com.dreamsecurity.magicvkeypad.MagicVKeypadOnClickInterface;
import com.dreamsecurity.magicvkeypad.MagicVKeypadResult;
import com.dreamsecurity.magicvkeypad.MagicVKeypadType;
import com.dreamsecurity.magicxsign.MagicXSign;
import com.dreamsecurity.magicxsign.MagicXSign_Type;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

public class AndroidBridge {

    private WebView mWebView;
    private Activity mActivity;

    private MagicMRS mMagicMRS = null;
    private ProcessCertificate processCertificate = null;

    private String mCallbackFuntion = "";
    private String mAuthCode = "";
    private String mUserKey = "";
    private String mIssuedn = ""; //발급자
    private String mSubjdn = "";  //주체자
    private String mSn = "";      //sn
    private String mUcpidData = ""; //UCPID 원문

    private String color = "";

    private String RSAEncryptData = "";
    private String charResultData = "";
    private String AESEncData = "";
    private String AESDecData = "";

    private String TAG = "AndroidBridge";

    private MagicXSign pki;

    private FIDOAuthentication mAuth = null;
    private FIDORegistration reg = null;
    private FIDODeRegistration mDeregist = null;

    private final byte[] tobeData1 = "Hello".getBytes();

    private int failCnt;
    private String mCertType;

    private KCertificate kCertificate = null;

    private MagicFIDOUtil magicFIDOUtil = null;
    private Hashtable<String, Object> patternOption = null;

    private boolean mIsInvisible;

    private MagicVKeypad magicVKeypad;
    public final int ALERT_REQUEST = 0xA3;

    public static int iSignType = 2;
    public static int iLastError = 0;
    public static String sLastError = null;

    private JSONArray arrSignText;
//    private Context mContext;

    private Intent mIntent;
    private InputDialog inputDialog;
    private String strfieldID;
    private String strViewMode;
    Handler handler = new Handler(Looper.getMainLooper());

    public AndroidBridge(WebView webView, Activity activity) {
        Log.d(TAG, "AndroidBridge 입니다. 인증서 관련 처리 ==========================");
        this.mWebView = webView;
//        mContext = context;
        this.mActivity = activity;

        //인증서 관련 처리
        processCertificate = new ProcessCertificate(mActivity);
        pki = new MagicXSign();

        kCertificate = new KCertificate();
        MagicFIDOUtil.setSSLEnable(false);

        magicFIDOUtil = new MagicFIDOUtil(mActivity);
        patternOption = new Hashtable<String, Object>();

    }

    public AndroidBridge() {

    }

    public void setCallbakckFuction(String callbakckFuction) {
        mCallbackFuntion = callbakckFuction;
    }

    public String getCallbackFunction() {
        return mCallbackFuntion;
    }

    public void setAuthCode(String authCode) {
        mAuthCode = authCode;
    }

    public String getAuthCode() {
        return mAuthCode;
    }

    public void setUserKey(String userKey) {
        mUserKey = userKey;
    }

    public String getUserKey() {
        return mUserKey;
    }

    public void setIssuedn(String issuedn) {
        mIssuedn = issuedn;
    }

    public String getIssuedn() {
        return mIssuedn;
    }

    public void setSubjdn(String subjdn) {
        mSubjdn = subjdn;
    }

    public String getSubjdn() {
        return mSubjdn;
    }

    public void setSn(String sn) {
        mSn = sn;
    }

    public String getSn() {
        return mSn;
    }

    public void setUcpidData(String ucpidData) {
        mUcpidData = ucpidData;
    }

    public String getUcpidData() {
        return mUcpidData;
    }

    // HybridModeActivity 보안 키보드 버튼 클릭
    @JavascriptInterface
    public void showKeyboard() {
        GLog.d("showKeyboard 클릭");
        Toast.makeText(mActivity, "showKeyboard 클릭", Toast.LENGTH_SHORT).show();
        mIntent = new Intent(mActivity, HybridModeActivity.class);
        mActivity.startActivity(mIntent);
    }


    // Say hello, hello, world 버튼 이벤트
    @JavascriptInterface
    public void showToast(String word) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl("javascript:nativeToWeb()");
            }
        });
        if (TextUtils.equals(word, mActivity.getString(R.string.hello))) {
            mIntent = new Intent(mActivity, HelloWorldActivity.class);
            mActivity.startActivity(mIntent);
        } else if (TextUtils.equals(word, mActivity.getString(R.string.world))) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle("Alert").setMessage(mActivity.getString(R.string.hello_world));
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        } else {
            Toast.makeText(mActivity, word, Toast.LENGTH_SHORT).show();
        }
    }

    // show Dialog 버튼 이벤트
    @JavascriptInterface
    public void showDialog() {
        GLog.d("잘 들어왔습니다 =====");
        inputDialog = new InputDialog(mActivity, new InputDialogClickListener() {
            @Override
            public void onInputPositiveClick(String text) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        GLog.d("text =======" + text);
                        mWebView.loadUrl("javascript:nativeToWebWithMsg('" + text + "')");
                    }
                });
            }

            @Override
            public void onInputNegativeClick() {
            }
        });
        inputDialog.show();
    }

    // show User Information 선택
    @JavascriptInterface
    public void showUserList() {
        GLog.d("user infomation을 선택했나요?");
        Toast.makeText(mActivity, "유저 리스트 액티비티", Toast.LENGTH_SHORT).show();
        mIntent = new Intent(mActivity, UserListActivity.class);
        mActivity.startActivity(mIntent);
    }

    // show User Information Certification 선택
    @JavascriptInterface
    public void showCertificationList() {
        GLog.d("showCertificationList 클릭");
        Toast.makeText(mActivity, "showCertificationList 클릭", Toast.LENGTH_SHORT).show();
        mIntent = new Intent(mActivity, UserCertificationActivity.class);
        mActivity.startActivity(mIntent);
    }

    //외부 브라우저 호출
    @JavascriptInterface
    public void openBrowser(String strJsonObject) {
        JSONObject jsonObject = null;
        String mUrl;
        try {
            if (!strJsonObject.equals("")) {
                jsonObject = new JSONObject(strJsonObject);
                mUrl = jsonObject.getString("url");

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
                mActivity.startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //설정 페이지 호출
    @JavascriptInterface
    public void callSettings(String strJsonObject) {
        GLog.d("callSettings - strJsonObject : " + strJsonObject);

        JSONObject jsonObject = null;
        String setType = "";
        String callback = "";
        String setValue = "";

        try {
            if (!strJsonObject.equals("")) {
                jsonObject = new JSONObject(strJsonObject);

                if (!jsonObject.isNull("setType")) {
                    setType = jsonObject.getString("setType");
                }

                if (!jsonObject.isNull("setValue")) {
                    setValue = jsonObject.getString("setValue");
                }

                if (!jsonObject.isNull("callback")) {
                    callback = jsonObject.getString("callback");
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!setType.equals("null") || setType != null) {

            if (setType.equals("font")) {

                if (!setValue.equals("")) {
                    PreferenceManager.setString(mActivity, Constant.FONT_SIZE, setValue);
                }

                //폰트 설정 화면으로 이동
//                Intent intent = new Intent(mActivity, FontSizeActivity.class);
//                intent.putExtra("callback", callback);
//                mActivity.startActivity(intent);

            } else {

                PreferenceManager.setString(mActivity, Constant.CERT_TYPE, setValue);

                //인증타입 화면으로 이동
//                Intent intent = new Intent(mActivity, CertTypeActivity.class);
//                intent.putExtra("callback", callback);
//                mActivity.startActivity(intent);

            }
        }
    }


    //패턴 선 숨기기 설정
    public void setPattenLine() {
        mIsInvisible = PreferenceManager.getPattenLineBoolean(mActivity, Constant.PATTERN_INVISIBLE);
        GLog.d("mIsInvisible : " + mIsInvisible);

        if (magicFIDOUtil == null) {
            magicFIDOUtil = new MagicFIDOUtil(mActivity);
        }

        if (patternOption == null) {
            patternOption = new Hashtable<String, Object>();
        }

        //패턴 선 굵기
        patternOption.put(MagicFIDOUtil.KEY_LINE_WIDTH, 4.2f);

        //true : 선숨기기 ON, false: 선숨기기 OFF
        if (mIsInvisible) {
            patternOption.put(MagicFIDOUtil.KEY_LINE_COLOR, "#00000000");
            patternOption.put(MagicFIDOUtil.KEY_MISMATCH_LINE_COLOR, "#00000000");
            magicFIDOUtil.setAuthenticatorOptions(LOCAL_AUTH_TYPE.LOCAL_PATTERN_TYPE, patternOption);
        } else {
            patternOption.put(MagicFIDOUtil.KEY_LINE_COLOR, "#64b3ff");
            patternOption.put(MagicFIDOUtil.KEY_MISMATCH_LINE_COLOR, "#ff0000");
            magicFIDOUtil.setAuthenticatorOptions(LOCAL_AUTH_TYPE.LOCAL_PATTERN_TYPE, patternOption);
        }
    }

    //간편인증서(FaceID, 지문, 패턴)모둘 호출(단건)
    //인증요청
//    @JavascriptInterface
//    public void callSimpleAuth(String strJsonObject) {
//
//        GLog.d("callSimpleAuth - strJsonObject : " + strJsonObject);
//        JSONObject jsonObject = null;
//
//        String callback = "";
//        String issuedn = "";
//        String subjdn = "";
//        String sn = "";
//        String titleType = "";
//        String sSignText = "";
//        String ucpidText = "";
//
////        setAuthOption();
//        setPattenLine();
//
//        try {
//            if (!strJsonObject.equals("")) {
//                jsonObject = new JSONObject(strJsonObject);
//
//                if (!jsonObject.isNull("certType")) {
//                    mCertType = jsonObject.getString("certType");
//                }
//
//                if (!jsonObject.isNull("callback")) {
//                    callback = jsonObject.getString("callback");
//                    setCallbakckFuction(callback);
//                }
//
//                if (!jsonObject.isNull("issuedn")) {
//                    issuedn = jsonObject.getString("issuedn");
//                    setIssuedn(issuedn);
//                }
//
//                if (!jsonObject.isNull("subjdn")) {
//                    subjdn = jsonObject.getString("subjdn");
//                    setSubjdn(subjdn);
//                }
//
//                if (!jsonObject.isNull("sn")) {
//                    sn = jsonObject.getString("sn");
//                    setSn(sn);
//                }
//
//                if (!jsonObject.isNull("sSignText")) {
//                    sSignText = jsonObject.getString("sSignText");
//                }
//
//                if (!jsonObject.isNull("ucpidText")) {
//                    ucpidText = jsonObject.getString("ucpidText");
//                }
//
//                if (!jsonObject.isNull("titleType")) {
//                    titleType = jsonObject.getString("titleType");
//
//                    Hashtable<String, Object> authUICustom = new Hashtable<String, Object>();
//                    //“01” -> 로그인 시 지정값, 앱에서는 “간편하게 인증하세요”로 타이틀 표시
//                    // “02” -> 전자 서명 시 지정값, 앱에서는 “패턴”로 타이틀 표시
//                    if (titleType.equals("02")) {
////                        authUICustom.put("text1", mActivity.getResources().getString(R.string.pattern_title));
//                    } else {
////                        authUICustom.put("text1", mActivity.getResources().getString(R.string.pattern_login_title));
//                    }
//                    Hashtable<String, Object> settingValue = new Hashtable<String, Object>();
//                    settingValue.put("pattern", authUICustom);
//                    MagicFIDOUtil magicFIDOUtil = new MagicFIDOUtil(mActivity);
//                    magicFIDOUtil.setCustomUIValues(settingValue);
//                    magicFIDOUtil.setAuthenticationBackgroundCertInfoHidden(true);
//                } else {
//                    Hashtable<String, Object> authUICustom = new Hashtable<String, Object>();
//                    authUICustom.put("text1", "간편하게 로그인하세요.");
//                    Hashtable<String, Object> settingValue = new Hashtable<String, Object>();
//                    settingValue.put("pattern", authUICustom);
//                    MagicFIDOUtil magicFIDOUtil = new MagicFIDOUtil(mActivity);
//                    magicFIDOUtil.setCustomUIValues(settingValue);
//                    magicFIDOUtil.setAuthenticationBackgroundCertInfoHidden(true);
//                }
//
//                GLog.d("mCertType : " + mCertType + " , callback : " + getCallbackFunction() + " , issuedn : " + getIssuedn() + " ,subjdn : " + getSubjdn() + ", sn : " + getSn() + ", sSignText :" + sSignText + " ,ucpidText : " + ucpidText + " ,titleType : " + titleType);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        if (mCertType.equals("01")) {
//            //공동인증서 호출X
//
//        } else if (mCertType.equals("02")) {
//
//            GLog.d("Utils.isEnrolledFingerprint : " + Utils.isEnrolledFingerprint(mActivity));
//
//            failCnt = PreferenceManager.getInt(mActivity, Constant.FINGER);
//            //지문
////            mAuth = new FIDOAuthentication(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoAuthCallbackResult, Constant.FINGER, getSubjdn());
//
//            //지문 인증 요청
//            ArrayList<byte[]> tobeDatas = new ArrayList<byte[]>();
//            if ("".equals(sSignText)) {
//                tobeDatas.add(tobeData1);
//            } else {
//                byte[] mByteSignText = sSignText.getBytes();
//                tobeDatas.add(mByteSignText);
//            }
//
//            if (!"".equals(ucpidText)) {
//                try {
//                    pki = new MagicXSign();
//                    pki.Init(mActivity, MagicXSign_Type.XSIGN_DEBUG_LEVEL_1);
//                    byte[] encodeUcpidText = pki.BASE64_Decode(ucpidText);
//                    byte[] mByteUcpidText = encodeUcpidText;
//                    tobeDatas.add(mByteUcpidText);
//                    pki.Finish();
//                } catch (MagicXSign_Exception e) {
//                    e.printStackTrace();
//                    GLog.d("error: " + e.getErrorMessage());
//                }
//            }
//            mAuth.loginAuthFIDO(tobeDatas);
//
//        } else if (mCertType.equals("03")) {
//            //얼굴 호출 X
//
//        } else if (mCertType.equals("04")) {
//
//            failCnt = PreferenceManager.getInt(mActivity, Constant.PATTERN);
//
//            //패턴
////            mAuth = new FIDOAuthentication(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoAuthCallbackResult, Constant.PATTERN, subjdn);
//
//            //패턴 인증 요청
//            ArrayList<byte[]> tobeDatas = new ArrayList<byte[]>();
//            if ("".equals(sSignText)) {
//                tobeDatas.add(tobeData1);
//            } else {
//                byte[] mByteSignText = sSignText.getBytes();
//                tobeDatas.add(mByteSignText);
//            }
//
//            GLog.d("ucpidText : " + ucpidText);
//
//            if (!"".equals(ucpidText)) {
//                try {
//                    pki = new MagicXSign();
//                    pki.Init(mActivity, MagicXSign_Type.XSIGN_DEBUG_LEVEL_1);
//                    byte[] encodeUcpidText = pki.BASE64_Decode(ucpidText);
//                    byte[] mByteUcpidText = encodeUcpidText;
//                    tobeDatas.add(mByteUcpidText);
//                    pki.Finish();
//                } catch (MagicXSign_Exception e) {
//                    e.printStackTrace();
//                    GLog.d("error: " + e.getErrorMessage());
//                }
//            }
//            mAuth.loginAuthFIDO(tobeDatas);
//        }
//    }

    //간편인증서(FaceID, 지문, 패턴)모둘 호출(다건)
    //인증요청
//    @JavascriptInterface
//    public void callSimpleAuthMulti(String strJsonObject) {
//
//        GLog.d("callSimpleAuthMulti - strJsonObject : " + strJsonObject);
//        JSONObject jsonObject = null;
//
//        String callback = "";
//        String issuedn = "";
//        String subjdn = "";
//        String sn = "";
//        String titleType = "";
//        String sSignText = "";
//        String ucpidText = "";
//
//        setAuthOption();
//        setPattenLine();
//
//        try {
//            if (!strJsonObject.equals("")) {
//                jsonObject = new JSONObject(strJsonObject);
//
//                if (!jsonObject.isNull("certType")) {
//                    mCertType = jsonObject.getString("certType");
//                }
//
//                if (!jsonObject.isNull("callback")) {
//                    callback = jsonObject.getString("callback");
//                    setCallbakckFuction(callback);
//                }
//
//                if (!jsonObject.isNull("issuedn")) {
//                    issuedn = jsonObject.getString("issuedn");
//                    setIssuedn(issuedn);
//                }
//
//                if (!jsonObject.isNull("subjdn")) {
//                    subjdn = jsonObject.getString("subjdn");
//                    setSubjdn(subjdn);
//                }
//
//                if (!jsonObject.isNull("sn")) {
//                    sn = jsonObject.getString("sn");
//                    setSn(sn);
//                }
//
//                if (!jsonObject.isNull("sSignText")) {
//                    sSignText = jsonObject.getString("sSignText");
//                }
//
//                if (!jsonObject.isNull("ucpidText")) {
//                    ucpidText = jsonObject.getString("ucpidText");
//                }
//
//                if (!jsonObject.isNull("titleType")) {
//                    titleType = jsonObject.getString("titleType");
//
//                    Hashtable<String, Object> authUICustom = new Hashtable<String, Object>();
//                    //“01” -> 로그인 시 지정값, 앱에서는 “간편하게 인증하세요”로 타이틀 표시
//                    // “02” -> 전자 서명 시 지정값, 앱에서는 “패턴”로 타이틀 표시
//                    if (titleType.equals("02")) {
////                        authUICustom.put("text1", mActivity.getResources().getString(R.string.pattern_title));
//                    } else {
////                        authUICustom.put("text1", mActivity.getResources().getString(R.string.pattern_login_title));
//                    }
//                    Hashtable<String, Object> settingValue = new Hashtable<String, Object>();
//                    settingValue.put("pattern", authUICustom);
//                    MagicFIDOUtil magicFIDOUtil = new MagicFIDOUtil(mActivity);
//                    magicFIDOUtil.setCustomUIValues(settingValue);
//                    magicFIDOUtil.setAuthenticationBackgroundCertInfoHidden(true);
//                } else {
//                    Hashtable<String, Object> authUICustom = new Hashtable<String, Object>();
//                    authUICustom.put("text1", "간편하게 로그인하세요.");
//                    Hashtable<String, Object> settingValue = new Hashtable<String, Object>();
//                    settingValue.put("pattern", authUICustom);
//                    MagicFIDOUtil magicFIDOUtil = new MagicFIDOUtil(mActivity);
//                    magicFIDOUtil.setCustomUIValues(settingValue);
//                    magicFIDOUtil.setAuthenticationBackgroundCertInfoHidden(true);
//                }
//
//                GLog.d("mCertType : " + mCertType + " , callback : " + getCallbackFunction() + " , issuedn : " + getIssuedn() + " ,subjdn : " + getSubjdn() + ", sn : " + getSn() + ", sSignText :" + sSignText + " ,ucpidText : " + ucpidText + " ,titleType : " + titleType);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        if (mCertType.equals("01")) {
//            //공동인증서 호출X
//
//        } else if (mCertType.equals("02")) {
//
//            GLog.d("Utils.isEnrolledFingerprint : " + Utils.isEnrolledFingerprint(mActivity));
//
//            failCnt = PreferenceManager.getInt(mActivity, Constant.FINGER);
//            //지문
//            mAuth = new FIDOAuthentication(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoAuthCallbackResultMulti, Constant.FINGER, getSubjdn());
//
//            //지문 인증 요청
//            mAuth.loginAuthFIDO(getTobeDatas(sSignText, ucpidText));
//
//        } else if (mCertType.equals("03")) {
//            //얼굴 호출 X
//
//        } else if (mCertType.equals("04")) {
//
//            failCnt = PreferenceManager.getInt(mActivity, Constant.PATTERN);
//
//            //패턴
//            mAuth = new FIDOAuthentication(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoAuthCallbackResultMulti, Constant.PATTERN, subjdn);
//
//            //패턴 인증 요청
//            mAuth.loginAuthFIDO(getTobeDatas(sSignText, ucpidText));
//        }
//    }

    //인증요청
//    private ArrayList<byte[]> getTobeDatas(String sSignText, String ucpidText) {
//        ArrayList<byte[]> tobeDatas = new ArrayList<byte[]>();
//        if ("".equals(sSignText)) {
//            tobeDatas.add(tobeData1);
//        } else {
//            try {
//                arrSignText = new JSONArray(sSignText);
//                for (int i = 0; i < arrSignText.length(); i++) {
//                    GLog.d("arrSignText : " + arrSignText.getString(i));
//                    byte[] mByteSignText = arrSignText.getString(i).getBytes();
//                    tobeDatas.add(mByteSignText);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        GLog.d("ucpidText : " + ucpidText);
//
//        if (!"".equals(ucpidText)) {
//            try {
//                pki = new MagicXSign();
//                pki.Init(mActivity, MagicXSign_Type.XSIGN_DEBUG_LEVEL_1);
//                byte[] encodeUcpidText = pki.BASE64_Decode(ucpidText);
//                byte[] mByteUcpidText = encodeUcpidText;
//                tobeDatas.add(mByteUcpidText);
//                pki.Finish();
//            } catch (MagicXSign_Exception e) {
//                e.printStackTrace();
//                GLog.d("error: " + e.getErrorMessage());
//            }
//        }
//
//        GLog.d("tobeDatas size: " + tobeDatas.size());
//        return tobeDatas;
//    }

    //인증장치 옵션 설정
//    private void setAuthOption() {
//        if (magicFIDOUtil == null) {
//            magicFIDOUtil = new MagicFIDOUtil(mActivity);
//        }
//        Hashtable<String, Object> authOption = new Hashtable<String, Object>();
//        authOption.put(MagicFIDOUtil.KEY_RETRY_COUNT_TO_LOCK, 5);
//        authOption.put(MagicFIDOUtil.KEY_LOCK_TIME, 1);
//        authOption.put(MagicFIDOUtil.KEY_MAX_LOCK_COUNT, 1);
//        magicFIDOUtil.setAuthenticatorOptions(LOCAL_AUTH_TYPE.LOCAL_PATTERN_TYPE, authOption);
//    }


    //Fido 인증단건 콜백
//    private FIDOCallbackResult fidoAuthCallbackResult = new FIDOCallbackResult() {
//        @Override
//        public void onFIDOResult(int requestCode, boolean result, FidoResult fidoResult) {
//            switch (requestCode) {
//                case FIDORequestCode.REQUEST_CODE_AUTH: {
//                    GLog.d("fidoAuthCallbackResult : " + fidoResult.getErrorCode());
//
//                    JSONObject obj = new JSONObject();
//                    JSONObject data = new JSONObject();
//
//                    // FIDO Client에 전송하지 못 하였을 경우.
//                    if (fidoResult.getErrorCode() != FidoResult.RESULT_SUCCESS) {
//                        if (fidoResult.getErrorCode() != 999) {
//                            failCnt++;
//                        }
//
//                        String certData = "";
//                        try {
//                            byte[] dnData = mAuth.getDn();
//                            if (dnData != null) {
//                                certData = Base64.encodeToString(dnData, Base64.DEFAULT);
//                                GLog.d("certData : " + certData);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        try {
//                            obj.put("resultCode", "FAIL");
//                            data.put("signedData", "");
//                            data.put("certData", certData);
//                            data.put("errorCode", fidoResult.getErrorCode());
//                            data.put("issuedn", getIssuedn());
//                            data.put("subjdn", getSubjdn());
//                            data.put("sn", getSn());
//                            obj.put("data", data);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        if (mCertType.equals("02")) {
//                            PreferenceManager.setInt(mActivity, Constant.FINGER, failCnt);
//                        } else if (mCertType.equals("04")) {
//                            PreferenceManager.setInt(mActivity, Constant.PATTERN, failCnt);
//                        }
//
//                        showToastMessage(fidoResult.getDescription() + "(" + fidoResult.getErrorCode() + ")");
//
////                        Intent alertIntent = new Intent(mActivity, EmptyActivity.class);
////                        mActivity.startActivityForResult(alertIntent, ALERT_REQUEST);
//
//                    } else {
//                        try {
//                            showToastMessage("Auth Success");
//                            //등록된 인증서 가져와야 함
//                            ArrayList<byte[]> getSignedData = mAuth.getSignedData();
//                            if (!getSignedData.isEmpty()) {
//                                if (getSignedData.size() > 0) {
//
//                                    String signedData = "";
//                                    String ucpidData = "";
//
//                                    GLog.d("getSignedData.size() : " + getSignedData.size());
//
//                                    for (int i = 0; i < getSignedData.size(); i++) {
//                                        if (i == 0) {
//                                            signedData = Base64.encodeToString(getSignedData.get(0), Base64.DEFAULT);
//                                            GLog.d("signedData : " + signedData);
//                                        }
//                                        if (i == 1) {
//                                            ucpidData = Base64.encodeToString(getSignedData.get(1), Base64.DEFAULT);
//                                            GLog.d("ucpidData : " + ucpidData);
//                                        }
//                                    }
//
//                                    String certData = "";
//                                    try {
//                                        byte[] dnData = mAuth.getDn();
//                                        if (dnData != null) {
//                                            certData = Base64.encodeToString(dnData, Base64.DEFAULT);
//                                            GLog.d("certData : " + certData);
//                                        }
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    try {
//                                        obj.put("resultCode", "SUCCESS");
//                                        data.put("signedData", signedData.replaceAll("\\n", ""));
//                                        data.put("certData", certData);
//                                        data.put("errorCode", fidoResult.getErrorCode());
//                                        data.put("issuedn", getIssuedn());
//                                        data.put("subjdn", getSubjdn());
//                                        data.put("sn", getSn());
//
//                                        if (!"".equals(ucpidData)) {
//                                            data.put("ucpidData", ucpidData.replaceAll("\\n", ""));
//                                        }
//
//                                        obj.put("data", data);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    if (mCertType.equals("02")) {
//                                        PreferenceManager.setInt(mActivity, Constant.FINGER, 0);
//                                    } else if (mCertType.equals("04")) {
//                                        PreferenceManager.setInt(mActivity, Constant.PATTERN, 0);
//                                    }
//                                }
//                            }
//                        } catch (Exception e) {
//                            String dn = "";
//                            try {
//                                byte[] dnData = mAuth.getDn();
//                                if (dnData != null) {
//                                    dn = Base64.encodeToString(dnData, Base64.DEFAULT);
//                                    GLog.d("dn : " + dn);
//                                }
//
//                                obj.put("resultCode", "FAIL");
//                                data.put("signedData", "");
//                                data.put("errorCode", fidoResult.getErrorCode());
//                                data.put("failnCnt", String.valueOf(failCnt));
//                                data.put("issuedn", getIssuedn());
//                                data.put("subjdn", getSubjdn());
//                                data.put("sn", getSn());
//                                data.put("dn", dn);
//                                obj.put("data", data);
//
//                            } catch (Exception e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                    }
//
//                    callbackFunction(getCallbackFunction(), obj.toString());
//                }
//                break;
//            }
//        }
//    };

    //Fido 인증 콜백(다건인증)
//    private FIDOCallbackResult fidoAuthCallbackResultMulti = new FIDOCallbackResult() {
//        @Override
//        public void onFIDOResult(int requestCode, boolean result, FidoResult fidoResult) {
//            switch (requestCode) {
//                case FIDORequestCode.REQUEST_CODE_AUTH: {
//                    GLog.d("fidoAuthCallbackResult : " + fidoResult.getErrorCode());
//
//                    JSONObject obj = new JSONObject();
//                    JSONObject data = new JSONObject();
//
//                    // FIDO Client에 전송하지 못 하였을 경우.
//                    if (fidoResult.getErrorCode() != FidoResult.RESULT_SUCCESS) {
//                        if (fidoResult.getErrorCode() != 999) {
//                            failCnt++;
//                        }
//
//                        String certData = "";
//                        try {
//                            byte[] dnData = mAuth.getDn();
//                            if (dnData != null) {
//                                certData = Base64.encodeToString(dnData, Base64.DEFAULT);
//                                GLog.d("certData : " + certData);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        try {
//                            obj.put("resultCode", "FAIL");
//                            data.put("signedData", "");
//                            data.put("certData", certData);
//                            data.put("errorCode", fidoResult.getErrorCode());
//                            data.put("issuedn", getIssuedn());
//                            data.put("subjdn", getSubjdn());
//                            data.put("sn", getSn());
//                            obj.put("data", data);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        if (mCertType.equals("02")) {
//                            PreferenceManager.setInt(mActivity, Constant.FINGER, failCnt);
//                        } else if (mCertType.equals("04")) {
//                            PreferenceManager.setInt(mActivity, Constant.PATTERN, failCnt);
//                        }
//
////                        showToastMessage(fidoResult.getDescription() + "(" + fidoResult.getErrorCode() + ")");
//
//                    } else {
//                        try {
////                            showToastMessage("Auth Success");
//                            //등록된 인증서 가져와야 함
//                            ArrayList<byte[]> getSignedData = mAuth.getSignedData();
//                            if (!getSignedData.isEmpty()) {
//                                if (getSignedData.size() > 0) {
//
//                                    String signedData = "";
//                                    String ucpidData = "";
//
//                                    GLog.d("getSignedData.size() : " + getSignedData.size());
//                                    JSONArray arraySignedData = new JSONArray();
//
//                                    if (null == arrSignText) {
//                                        for (int i = 0; i < getSignedData.size(); i++) {
//                                            if (i == 0) {
//                                                signedData = Base64.encodeToString(getSignedData.get(0), Base64.DEFAULT);
//                                                signedData.replaceAll("\\n", "");
//                                                GLog.d("signedData : " + signedData);
//                                                arraySignedData.put(signedData);
//                                            }
//                                            if (i == 1) {
//                                                ucpidData = Base64.encodeToString(getSignedData.get(1), Base64.DEFAULT);
//                                                ucpidData.replaceAll("\\n", "");
//                                                GLog.d("ucpidData : " + ucpidData);
//                                            }
//                                        }
//                                    } else if (arrSignText.length() > 0) {
//                                        for (int i = 0; i < getSignedData.size(); i++) {
//                                            if (i < arrSignText.length()) {
//                                                signedData = Base64.encodeToString(getSignedData.get(i), Base64.DEFAULT);
//                                                signedData.replaceAll("\\n", "");
//                                                GLog.d("signedData : " + signedData);
//                                                arraySignedData.put(signedData);
//                                            } else {
//                                                ucpidData = Base64.encodeToString(getSignedData.get(i), Base64.DEFAULT);
//                                                ucpidData.replaceAll("\\n", "");
//                                                GLog.d("ucpidData : " + ucpidData);
//                                            }
//                                        }
//                                    }
//
//                                    String certData = "";
//                                    try {
//                                        byte[] dnData = mAuth.getDn();
//                                        if (dnData != null) {
//                                            certData = Base64.encodeToString(dnData, Base64.DEFAULT);
//                                            GLog.d("certData : " + certData);
//                                        }
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    try {
//                                        obj.put("resultCode", "SUCCESS");
//                                        data.put("signedData", arraySignedData);
//                                        data.put("certData", certData);
//                                        data.put("errorCode", fidoResult.getErrorCode());
//                                        data.put("issuedn", getIssuedn());
//                                        data.put("subjdn", getSubjdn());
//                                        data.put("sn", getSn());
//                                        if (!ucpidData.equals("")) {
//                                            data.put("ucpidData", ucpidData);
//                                        }
//                                        obj.put("data", data);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    if (mCertType.equals("02")) {
//                                        PreferenceManager.setInt(mActivity, Constant.FINGER, 0);
//                                    } else if (mCertType.equals("04")) {
//                                        PreferenceManager.setInt(mActivity, Constant.PATTERN, 0);
//                                    }
//                                }
//                            }
//                        } catch (Exception e) {
//                            String dn = "";
//                            try {
//                                byte[] dnData = mAuth.getDn();
//                                if (dnData != null) {
//                                    dn = Base64.encodeToString(dnData, Base64.DEFAULT);
//                                    GLog.d("dn : " + dn);
//                                }
//
//                                obj.put("resultCode", "FAIL");
//                                data.put("signedData", "");
//                                data.put("errorCode", fidoResult.getErrorCode());
//                                data.put("failnCnt", String.valueOf(failCnt));
//                                data.put("issuedn", getIssuedn());
//                                data.put("subjdn", getSubjdn());
//                                data.put("sn", getSn());
//                                data.put("dn", dn);
//                                obj.put("data", data);
//
//                            } catch (Exception e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                    }
//
//                    callbackFunction(getCallbackFunction(), obj.toString());
//                }
//                break;
//            }
//        }
//    };

    //간편인증서(FaceID, 지문, 패턴) 등록, 삭제, 재등록
    @JavascriptInterface
//    public void simpleAuthMgt(String strJsonObject) {
//
//        GLog.d("simpleAuthMgt - strJsonObject : " + strJsonObject);
//
//        setPattenLine();
//        JSONObject jsonObject = null;
//        mCertType = ""; //'02' /*지문*/ or '03' /*얼굴*/ or ' 04' /*패턴*/
//        String procType = ""; //'R'/*등록*/ or 'D'/*삭제*/
//        String userKey = ""; //공공인증서 DN값
//        String callback = "";
//
//        try {
//            if (!strJsonObject.equals("")) {
//                jsonObject = new JSONObject(strJsonObject);
//                if (!jsonObject.isNull("certType")) {
//                    mCertType = jsonObject.getString("certType");
//                }
//
//                if (!jsonObject.isNull("procType")) {
//                    procType = jsonObject.getString("procType");
//                }
//
//                if (!jsonObject.isNull("userKey")) {
//                    userKey = jsonObject.getString("userKey");
//                    setUserKey(userKey);
//                }
////                userKey = "cn=전자세금용001()00996802021031000007,ou=TEST,ou=KFTC,ou=xUse4Esero,o=yessign,c=kr"; //dream1004!
//                if (!jsonObject.isNull("callback")) {
//                    callback = jsonObject.getString("callback");
//                    setCallbakckFuction(callback);
//                }
//
//                if (procType.equals("R")) {//등록
//                    if (mCertType.equals("02")) {
//                        reg = new FIDORegistration(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoAuthMgtResult, Constant.FINGER);
//                        magicFIDOUtil.setAuthenticationBackgroundCertInfoHidden(false);
//                    } else if (mCertType.equals("04")) {
//
//                        Hashtable<String, Object> authUICustom = new Hashtable<String, Object>();
////                        authUICustom.put("text1", mActivity.getResources().getString(R.string.pt_t_regist));
//                        Hashtable<String, Object> settingValue = new Hashtable<String, Object>();
//                        settingValue.put("pattern", authUICustom);
//                        MagicFIDOUtil magicFIDOUtil = new MagicFIDOUtil(mActivity);
//                        magicFIDOUtil.setCustomUIValues(settingValue);
//                        magicFIDOUtil.setAuthenticationBackgroundCertInfoHidden(false);
//                        //리셋을 먼저 하고 리셋이 성공하면 패턴 등록을 요청한다.
//                        magicFIDOUtil.resetUserVerification(LOCAL_AUTH_TYPE.LOCAL_PATTERN_TYPE, fidoAuthMgtResult);
////                        reg = new FIDORegistration(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoResult, Constant.PATTERN);
//                        return;
//                    }
//                    fidoRegistration(userKey);
//                } else if (procType.equals("D")) {//삭제
//                    magicFIDOUtil.setAuthenticationBackgroundCertInfoHidden(false);
//                    if (mCertType.equals("02")) {
//                        mDeregist = new FIDODeRegistration(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoAuthMgtResult, Constant.FINGER, userKey);
//                    } else if (mCertType.equals("04")) {
//                        mDeregist = new FIDODeRegistration(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoAuthMgtResult, Constant.PATTERN, userKey);
//                    }
//                    mDeregist.deRegistFIDO();
//                }
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public void fidoRegistration(String userKey) {

        try {
            pki.Init(mActivity, MagicXSign_Type.XSIGN_DEBUG_LEVEL_1);

            int count = -1;
            pki.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER,
                    MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, "/sdcard");
            count = pki.MEDIA_GetCertCount();
            GLog.d("count : " + count);

            kCertificate.setDeliveryType(KCertificate.DELIVERY_TYPE_BINARY);

            int nMediaType[] = new int[1];

            for (int i = 0; i < count; i++) {
                byte[] binCert = pki.MEDIA_ReadCert(i, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, nMediaType);
                String subjectDn = pki.CERT_GetAttribute(binCert, MagicXSign_Type.XSIGN_CERT_ATTR_SUBJECT_DN, true);

                GLog.d("subjectDn : " + subjectDn);

                //전달 받은 dn과 값은 값을갖고 있는 지 확인
                if (userKey.equals(subjectDn)) {
                    byte[] signCert = pki.MEDIA_ReadCert(i, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, nMediaType);
                    byte[] signPri = pki.MEDIA_ReadPriKey(i, MagicXSign_Type.XSIGN_PKI_CERT_SIGN);

                    String signCert64 = KFidoUtil.getBase64URLToString(signCert);
                    String signKey64 = KFidoUtil.getBase64URLToString(signPri);

                    kCertificate.setSignCert(signCert64);
                    kCertificate.setSignPri(signKey64);
                }
            }
            pki.MEDIA_UnLoad();

        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<byte[]> tobeSignDatas = new ArrayList<>();
        tobeSignDatas.add(tobeData1);
        reg.registFIDO(kCertificate, tobeSignDatas);
    }

    //등록, 삭제 콜백메소드
//    private FIDOCallbackResult fidoAuthMgtResult = new FIDOCallbackResult() {
//        @Override
//        public void onFIDOResult(int requestCode, boolean result, FidoResult fidoResult) {
//            switch (requestCode) {
//                case FIDORequestCode.REQUEST_CODE_REGIST: { //등록
//                    JSONObject obj = new JSONObject();
//                    JSONObject data = new JSONObject();
//
//                    if (fidoResult.getErrorCode() != FidoResult.RESULT_SUCCESS) {
//                        showToastMessage(fidoResult.getDescription() + "(" + fidoResult.getErrorCode() + ")");
//                        try {
//                            obj.put("resultCode", "FAIL");
//                            data.put("errorCode", fidoResult.getErrorCode());
//                            data.put("signedData", "");
//                            obj.put("data", data);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    } else {
//
//                        try {
//                            //등록된 인증서 가져와야 함
//                            ArrayList<byte[]> getSignedData = reg.getSignedData();
//                            if (!getSignedData.isEmpty()) {
//                                if (getSignedData.size() > 0) {
//                                    showToastMessage("REG Success");
//                                    byte[] localData = getSignedData.get(0);
//                                    String signedData = Base64.encodeToString(localData, Base64.DEFAULT);
//                                    GLog.d("signedData : " + signedData);
//                                    try {
//                                        obj.put("resultCode", "SUCCESS");
//                                        data.put("signedData", signedData.replaceAll("\\n", ""));
//                                        data.put("errorCode", fidoResult.getErrorCode());
//                                        obj.put("data", data);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        } catch (Exception e) {
//                            showToastMessage("REG Fail");
//                            try {
//                                obj.put("resultCode", "SUCCESS");
//                                data.put("signedData", "");
//                                data.put("errorCode", fidoResult.getErrorCode());
//                                obj.put("data", data);
//                            } catch (JSONException e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                    }
//
//                    callbackFunction(getCallbackFunction(), obj.toString());
//                }
//                break;
//
//                case FIDORequestCode.REQUEST_CODE_DEREG: { //삭제
//
//                    JSONObject obj = new JSONObject();
//                    JSONObject data = new JSONObject();
//
//                    // FIDO Client에 전송하지 못 하였을 경우.
//                    if (fidoResult.getErrorCode() != FidoResult.RESULT_SUCCESS) {
//                        showToastMessage(fidoResult.getDescription() + "(" + fidoResult.getErrorCode() + ")");
//                        try {
//                            obj.put("resultCode", "FAIL");
//                            data.put("errorCode", fidoResult.getErrorCode());
//                            data.put("singedData", "");
//                            obj.put("data", data);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//
//                        try {
//                            //등록된 인증서 가져와야 함
//                            ArrayList<byte[]> getSignedData = reg.getSignedData();
//                            if (!getSignedData.isEmpty()) {
//                                if (getSignedData.size() > 0) {
//                                    showToastMessage("DEREG Success");
//                                    byte[] localData = getSignedData.get(0);
//                                    String signedData = Base64.encodeToString(localData, Base64.DEFAULT);
//                                    GLog.d("signedData : " + signedData);
//                                    try {
//                                        obj.put("resultCode", "SUCCESS");
//                                        data.put("signedData", signedData.replaceAll("\\n", ""));
//                                        data.put("errorCode", fidoResult.getErrorCode());
//                                        obj.put("data", data);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        } catch (Exception e) {
//                            showToastMessage("DEREG Fail");
//                            try {
//                                obj.put("resultCode", "SUCCESS");
//                                data.put("signedData", "");
//                                data.put("errorCode", fidoResult.getErrorCode());
//                                obj.put("data", data);
//                            } catch (JSONException e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                    }
//                    callbackFunction(getCallbackFunction(), obj.toString());
//                }
//                break;
//
//                case FIDORequestCode.REQUEST_LOCAL_RESET:
//                    GLog.d("fidoResetResult - errorCoode : " + fidoResult.getErrorCode());
//                    if (fidoResult.getErrorCode() == 0) {
//                        GLog.d("fidoResetResult - getUserKey : " + getUserKey());
//                        reg = new FIDORegistration(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoAuthMgtResult, Constant.PATTERN);
//                        fidoRegistration(getUserKey());
//                    }
//                    break;
//            }
//        }
//    };


    boolean bUseDummyData = false;

    @JavascriptInterface
    public void callMagicVKeypadAndroid(final String fieldID,
                                        final String viewMode,
                                        final String keypadType,
                                        final String option) {
        GLog.d();
        GLog.d("fieldID == " + fieldID + "\n" +
                "viewMode == " + viewMode + "\n" +
                "keypadType == " + keypadType + "\n" +
                "option == " + option);
        strfieldID = fieldID;
        strViewMode = viewMode;

        JSONObject options = null;
        try {
            options = new JSONObject(String.valueOf(option));
            if (options.getBoolean("UseDummyData")) {
                bUseDummyData = true;
            } else {
                bUseDummyData = false;
            }

            // 풀모드
            if (viewMode.equals("FULL_TYPE")) {
                GLog.d("풀모드");
                if (keypadType.equals("CHAR_TYPE")) { // 풀모드 - 문자키패드

                    // 입력 필드명 설정
                    magicVKeypad.setFieldName(fieldID);

                    // 마스킹 설정
                    magicVKeypad.setMasking(options.getInt("MaskingType"));

                    // 풀모드 설정
                    magicVKeypad.setFullMode(true);

                    // 입력 길이 제한
                    magicVKeypad.setMaxLength(options.getInt("MaxLength"));

                    // 화면 고정(DEFAULT: FALSE)
                    if (options.getBoolean("PortraitFix"))
                        magicVKeypad.setPortraitFixed(true);
                    else
                        magicVKeypad.setPortraitFixed(false);

                    if (options.getBoolean("UseSpeaker"))
                        magicVKeypad.setUseSpeaker(true);
                    else
                        magicVKeypad.setUseSpeaker(false);


                    // 스크린샷 허용
                    if (options.getBoolean("Screenshot"))
                        magicVKeypad.setScreenshot(true);
                    else {
                        magicVKeypad.setScreenshot(false);
                    }


                    // 키패드 실행
                    magicVKeypad.startCharKeypad(mOnClickInterface);

                } else { // 풀모드 - 숫자키패드

                    // 마스킹 타입 설정
                    magicVKeypad.setMasking(options.getInt("MaskingType"));

                    // 화면 세로 고정(DEFAULT: FALSE)
                    if (options.getBoolean("PortraitFix"))
                        magicVKeypad.setPortraitFixed(true);
                    else
                        magicVKeypad.setPortraitFixed(false);


                    // 입력 필드명 설정
                    magicVKeypad.setFieldName(fieldID);

                    // 풀모드 설정
                    magicVKeypad.setFullMode(true);

                    // 키패드 재배치 허용(DEFAULT : FALSE)
                    if (options.getBoolean("UseReplace"))
                        magicVKeypad.setNumUseReplace(true);
                    else
                        magicVKeypad.setNumUseReplace(false);

                    // 입력 길이 제한
                    magicVKeypad.setMaxLength(options.getInt("MaxLength"));

                    if (options.getBoolean("Screenshot"))
                        magicVKeypad.setScreenshot(true);
                    else {
                        magicVKeypad.setScreenshot(false);
                    }

                    if (options.getBoolean("UseSpeaker"))
                        magicVKeypad.setUseSpeaker(true);
                    else
                        magicVKeypad.setUseSpeaker(false);

                    // 키패드 실행
                    magicVKeypad.startNumKeypad(mOnClickInterface);
                }

                // 하프모드
            } else if (viewMode.equals("HALF_TYPE")) {
                GLog.d("하프모드");
                if (keypadType.equals("CHAR_TYPE")) { // 하프모드 - 문자키패드
                    if (magicVKeypad.isKeyboardOpen()) {
                        magicVKeypad.closeKeypad();
                    }
                    GLog.d();
                    // 입력 필드명 설정
                    magicVKeypad.setFieldName(fieldID);

                    // masking 설정
                    magicVKeypad.setMasking(options.getInt("MaskingType"));

                    // 하프모드 설정
                    magicVKeypad.setFullMode(false);

                    // 입력 길이 제한
                    magicVKeypad.setMaxLength(options.getInt("MaxLength"));

                    if (options.getBoolean("UseSpeaker"))
                        magicVKeypad.setUseSpeaker(true);
                    else
                        magicVKeypad.setUseSpeaker(false);

                    // 키패드 실행
                    magicVKeypad.startCharKeypad(mOnClickInterface);
                }
            } else { // 하프모드 - 숫자키패드
                if (magicVKeypad.isKeyboardOpen()) {
                    GLog.d("숫자키패드");
                    magicVKeypad.closeKeypad();
                }
                // 필드이름 지정
                magicVKeypad.setFieldName(fieldID);

                // masking 설정
                magicVKeypad.setMasking(options.getInt("MaskingType"));

                // 입력 길이 제한
                magicVKeypad.setMaxLength(options.getInt("MaxLength"));

                // 하프모드 설정
                magicVKeypad.setFullMode(false);

                // 화면 고정(DEFAULT: FALSE)
                if (options.getBoolean("PortraitFix"))
                    magicVKeypad.setPortraitFixed(true);
                else
                    magicVKeypad.setPortraitFixed(false);


                //재배치 설정
                if (options.getBoolean("UseReplace"))
                    magicVKeypad.setNumUseReplace(true);
                else
                    magicVKeypad.setNumUseReplace(false);

                if (options.getBoolean("UseSpeaker"))
                    magicVKeypad.setUseSpeaker(true);
                else
                    magicVKeypad.setUseSpeaker(false);

                magicVKeypad.setMultiClick(MagicVKeyPadSettings.bMultiClick);

                // 키패드 실행
                magicVKeypad.startNumKeypad(mOnClickInterface);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //보안 키패드 호출
//    @JavascriptInterface
//    public void showKeyPad(String strJsonObject) {
//        GLog.d("잘 들어왔다");
//        GLog.d("strJsonObject : " + strJsonObject);
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
//        settingKeyPad();
//        //문자 키패드를 활성화
//        startCharKeyPad();
//    }

    public void settingKeyPad() {
        GLog.d("============");
        magicVKeypad = new MagicVKeypad();

        // 키패드 라이선스 값 (드림시큐리티에게 패키지명 전달 후 받은 라이선스 값)
        String strLicense = MagicVKeyPadSettings.strLicense;

        GLog.d("============" + strLicense);
        boolean successLicense = magicVKeypad.initializeMagicVKeypad(mActivity, strLicense); // true : 검증 성공, false : 검증 실패
        GLog.d("============" + successLicense);
        if (successLicense) {
            GLog.d("============" + successLicense);
            if (MagicVKeyPadSettings.bUseE2E) {
                setPublickeyForE2E();
            }
        } else {
            Toast.makeText(mActivity, "라이선스 검증 실패로 인한 사용 불가", Toast.LENGTH_SHORT).show();
        }
    }

    //키패드 옵션 설정 및 키패드 열기
    public void startCharKeyPad() {
        GLog.d("==========");
        if (magicVKeypad.isKeyboardOpen()) {
            magicVKeypad.closeKeypad();
        }
        //풀모드 설정
        magicVKeypad.setFullMode(MagicVKeyPadSettings.bIsFullMode);
        // 세로고정 설정
        magicVKeypad.setPortraitFixed(MagicVKeyPadSettings.bIsPortFix);
        // masking 방식 지정
        magicVKeypad.setMasking(MagicVKeyPadSettings.maskingType);
        // 스크린 샷 X
        magicVKeypad.setScreenshot(MagicVKeyPadSettings.bAllowCapture);
        // 입력 글자수 제한
        magicVKeypad.setMaxLength(MagicVKeyPadSettings.maxLength);
        //사용할 키패드의 fieldName 설정(키패드 열기 전 무조건 설정 해야함)
        magicVKeypad.setFieldName(MagicVKeyPadSettings.charFieldName);
        // 외부스피커 설정
        magicVKeypad.setUseSpeaker(MagicVKeyPadSettings.bUseSpeaker);
        // 문자키패드 열기
        magicVKeypad.startCharKeypad(mOnClickInterface);
    }

    // E2E 를 위한 공개키 설정
    private void setPublickeyForE2E() {
        try {
            magicVKeypad.setPublickeyForE2E(MagicVKeyPadSettings.publicKey, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MagicVKeypadOnClickInterface mOnClickInterface = new MagicVKeypadOnClickInterface() {
        @Override
        public void onMagicVKeypadClick(MagicVKeypadResult magicVKeypadResult) {

            int licenseResult = magicVKeypadResult.getLicenseResult();
            Log.d(TAG, "licenseResult === " + licenseResult);
            if (licenseResult != 0) {
                Log.d("TEST", "라이선스 검증 실패");
            }

            byte[] decData = null;

            String strPlaintext = "";


            if (decData != null) {
                strPlaintext = new String(decData);
            }

            // js 로 리턴
            String strScript = "javascript:inputData('";

            // 풀모드
            if (magicVKeypad.isFullMode()) {
                GLog.d("풀 모드 ");
                if (!MagicVKeyPadSettings.bUseE2E)
                    decData = magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData());
                if (magicVKeypad.getEncryptData() != null) {
                    if (magicVKeypadResult.getEncryptData() != null)
                        RSAEncryptData = new String(magicVKeypadResult.getEncryptData());
                }

                if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_OK_BUTTON) { // 확인 버튼 클릭
                    if (!MagicVKeyPadSettings.bUseE2E) {
                        if (decData != null) {
                            strPlaintext = "";

                            for (int i = 0; i < decData.length; i++) {
                                strPlaintext = strPlaintext + "•";
                            }
                        }
                        strScript += strfieldID + "','" + strPlaintext + "')";
                        HybridResult.strScript = strScript;

                        mWebView.loadUrl(strScript);

                        if (magicVKeypadResult.getEncryptData() != null) {
//                            tv_AESEncryptData.setText("AES256 암호화 데이터 : " + byteArrayToHex(magicVKeypadResult.getEncryptData()));
//                            tv_AESDecryptData.setText("AES256 복호화 데이터 : " + new String(magicVKeypad.getDecryptData((magicVKeypadResult.getEncryptData()))));
                        }
                    } else {
                        strScript += strfieldID + "','" + "" + "')";
                        mWebView.loadUrl(strScript);
//                        if (magicVKeypadResult.getEncryptData() != null)
//                            tv_RSAEncryptData.setText("RSA 암호화 데이터 : " + new String(magicVKeypadResult.getEncryptData()));
                    }
                }
            }
            // 하프모드
            else {
                GLog.d("하프모드 ");
                if (!MagicVKeyPadSettings.bUseE2E) {
                    decData = magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData());
                    GLog.d("decData ===== " + decData);
                    if (decData != null) {
                        AESDecData = byteArrayToHex(magicVKeypadResult.getEncryptData());
                        AESEncData = new String(decData);
                        GLog.d("AESEncData ===== " + AESEncData);
                    }
                }
                if (magicVKeypad.getEncryptData() != null)
                    RSAEncryptData = new String(magicVKeypad.getEncryptData());

                if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_CANCEL_BUTTON) {
                    magicVKeypad.closeKeypad();
                    strScript += strViewMode + "','" + strfieldID + "','" + "" + "')";
                    HybridResult.strScript = strScript;
                    mWebView.loadUrl(strScript);
                } else if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_OK_BUTTON) { // 확인
                    if (magicVKeypadResult.getEncryptData() != null) {
                        GLog.d("확인 AESEncData ==== " + AESEncData);
                        if (MagicVKeyPadSettings.bUseE2E == false) {
//                            tv_AESEncryptData.setText("AES256 암호화 데이터 : " + byteArrayToHex(magicVKeypadResult.getEncryptData()));
//                            tv_AESDecryptData.setText("AES256 복호화 데이터 : " + new String(magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData())));

                        } else {
                            RSAEncryptData = new String(magicVKeypad.getEncryptData());
//                            tv_RSAEncryptData.setText("RSA 암호화 데이터 : " + RSAEncryptData);

                        }
                    }

                    magicVKeypad.closeKeypad();
                } else if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_CHAR_NUM_BUTTON) {
                    if (MagicVKeyPadSettings.bUseE2E) {
                        if (magicVKeypad.getEncryptData() != null)
                            RSAEncryptData = new String(magicVKeypad.getEncryptData());
                    } else {
                        if (decData != null) {
                            strPlaintext = "";

                            for (int i = 0; i < decData.length; i++) {
                                strPlaintext = strPlaintext + "•";
                            }
                        }
                        strScript += strfieldID + "','" + strPlaintext + "')";
                        mWebView.loadUrl(strScript);
                    }
                }
            }
        }
    };
}
