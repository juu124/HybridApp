package com.dki.hybridapptest.bridge;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.dki.hybridapptest.BuildConfig;
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
import com.dki.hybridapptest.utils.Utils;
import com.dream.magic.fido.rpsdk.callback.FIDOCallbackResult;
import com.dream.magic.fido.rpsdk.client.FIDORequestCode;
import com.dream.magic.fido.rpsdk.client.FidoResult;
import com.dream.magic.fido.rpsdk.client.LOCAL_AUTH_TYPE;
import com.dream.magic.fido.rpsdk.client.MagicFIDOUtil;
import com.dream.magic.fido.rpsdk.util.KFidoUtil;
import com.dream.magic.fido.rpsdk.util.MAGIC_FIDO_TYPE;
import com.dream.magic.fido.uaf.protocol.kfido.KCertificate;
import com.dreamsecurity.magicmrs.MRSCertificate;
import com.dreamsecurity.magicmrs.MRSDisplayInfo;
import com.dreamsecurity.magicmrs.MagicMRS;
import com.dreamsecurity.magicmrs.MagicMRSCallback;
import com.dreamsecurity.magicmrs.MagicMRSExportCallback;
import com.dreamsecurity.magicmrs.MagicMRSResult;
import com.dreamsecurity.magicmrs.etc.MagicMRSConfig;
import com.dreamsecurity.magicvkeypad.MagicVKeypad;
import com.dreamsecurity.magicvkeypad.MagicVKeypadOnClickInterface;
import com.dreamsecurity.magicvkeypad.MagicVKeypadResult;
import com.dreamsecurity.magicvkeypad.MagicVKeypadType;
import com.dreamsecurity.magicxsign.MagicXSign;
import com.dreamsecurity.magicxsign.MagicXSign_Exception;
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
                startCharKeyPad();
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
    @JavascriptInterface
    public void callSimpleAuth(String strJsonObject) {

        GLog.d("callSimpleAuth - strJsonObject : " + strJsonObject);
        JSONObject jsonObject = null;

        String callback = "";
        String issuedn = "";
        String subjdn = "";
        String sn = "";
        String titleType = "";
        String sSignText = "";
        String ucpidText = "";

        setAuthOption();
        setPattenLine();

        try {
            if (!strJsonObject.equals("")) {
                jsonObject = new JSONObject(strJsonObject);

                if (!jsonObject.isNull("certType")) {
                    mCertType = jsonObject.getString("certType");
                }

                if (!jsonObject.isNull("callback")) {
                    callback = jsonObject.getString("callback");
                    setCallbakckFuction(callback);
                }

                if (!jsonObject.isNull("issuedn")) {
                    issuedn = jsonObject.getString("issuedn");
                    setIssuedn(issuedn);
                }

                if (!jsonObject.isNull("subjdn")) {
                    subjdn = jsonObject.getString("subjdn");
                    setSubjdn(subjdn);
                }

                if (!jsonObject.isNull("sn")) {
                    sn = jsonObject.getString("sn");
                    setSn(sn);
                }

                if (!jsonObject.isNull("sSignText")) {
                    sSignText = jsonObject.getString("sSignText");
                }

                if (!jsonObject.isNull("ucpidText")) {
                    ucpidText = jsonObject.getString("ucpidText");
                }

                if (!jsonObject.isNull("titleType")) {
                    titleType = jsonObject.getString("titleType");

                    Hashtable<String, Object> authUICustom = new Hashtable<String, Object>();
                    //“01” -> 로그인 시 지정값, 앱에서는 “간편하게 인증하세요”로 타이틀 표시
                    // “02” -> 전자 서명 시 지정값, 앱에서는 “패턴”로 타이틀 표시
                    if (titleType.equals("02")) {
//                        authUICustom.put("text1", mActivity.getResources().getString(R.string.pattern_title));
                    } else {
//                        authUICustom.put("text1", mActivity.getResources().getString(R.string.pattern_login_title));
                    }
                    Hashtable<String, Object> settingValue = new Hashtable<String, Object>();
                    settingValue.put("pattern", authUICustom);
                    MagicFIDOUtil magicFIDOUtil = new MagicFIDOUtil(mActivity);
                    magicFIDOUtil.setCustomUIValues(settingValue);
                    magicFIDOUtil.setAuthenticationBackgroundCertInfoHidden(true);
                } else {
                    Hashtable<String, Object> authUICustom = new Hashtable<String, Object>();
                    authUICustom.put("text1", "간편하게 로그인하세요.");
                    Hashtable<String, Object> settingValue = new Hashtable<String, Object>();
                    settingValue.put("pattern", authUICustom);
                    MagicFIDOUtil magicFIDOUtil = new MagicFIDOUtil(mActivity);
                    magicFIDOUtil.setCustomUIValues(settingValue);
                    magicFIDOUtil.setAuthenticationBackgroundCertInfoHidden(true);
                }

                GLog.d("mCertType : " + mCertType + " , callback : " + getCallbackFunction() + " , issuedn : " + getIssuedn() + " ,subjdn : " + getSubjdn() + ", sn : " + getSn() + ", sSignText :" + sSignText + " ,ucpidText : " + ucpidText + " ,titleType : " + titleType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mCertType.equals("01")) {
            //공동인증서 호출X

        } else if (mCertType.equals("02")) {

            GLog.d("Utils.isEnrolledFingerprint : " + Utils.isEnrolledFingerprint(mActivity));

            failCnt = PreferenceManager.getInt(mActivity, Constant.FINGER);
            //지문
            mAuth = new FIDOAuthentication(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoAuthCallbackResult, Constant.FINGER, getSubjdn());

            //지문 인증 요청
            ArrayList<byte[]> tobeDatas = new ArrayList<byte[]>();
            if ("".equals(sSignText)) {
                tobeDatas.add(tobeData1);
            } else {
                byte[] mByteSignText = sSignText.getBytes();
                tobeDatas.add(mByteSignText);
            }

            if (!"".equals(ucpidText)) {
                try {
                    pki = new MagicXSign();
                    pki.Init(mActivity, MagicXSign_Type.XSIGN_DEBUG_LEVEL_1);
                    byte[] encodeUcpidText = pki.BASE64_Decode(ucpidText);
                    byte[] mByteUcpidText = encodeUcpidText;
                    tobeDatas.add(mByteUcpidText);
                    pki.Finish();
                } catch (MagicXSign_Exception e) {
                    e.printStackTrace();
                    GLog.d("error: " + e.getErrorMessage());
                }
            }
            mAuth.loginAuthFIDO(tobeDatas);

        } else if (mCertType.equals("03")) {
            //얼굴 호출 X

        } else if (mCertType.equals("04")) {

            failCnt = PreferenceManager.getInt(mActivity, Constant.PATTERN);

            //패턴
            mAuth = new FIDOAuthentication(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoAuthCallbackResult, Constant.PATTERN, subjdn);

            //패턴 인증 요청
            ArrayList<byte[]> tobeDatas = new ArrayList<byte[]>();
            if ("".equals(sSignText)) {
                tobeDatas.add(tobeData1);
            } else {
                byte[] mByteSignText = sSignText.getBytes();
                tobeDatas.add(mByteSignText);
            }

            GLog.d("ucpidText : " + ucpidText);

            if (!"".equals(ucpidText)) {
                try {
                    pki = new MagicXSign();
                    pki.Init(mActivity, MagicXSign_Type.XSIGN_DEBUG_LEVEL_1);
                    byte[] encodeUcpidText = pki.BASE64_Decode(ucpidText);
                    byte[] mByteUcpidText = encodeUcpidText;
                    tobeDatas.add(mByteUcpidText);
                    pki.Finish();
                } catch (MagicXSign_Exception e) {
                    e.printStackTrace();
                    GLog.d("error: " + e.getErrorMessage());
                }
            }
            mAuth.loginAuthFIDO(tobeDatas);
        }
    }

    //간편인증서(FaceID, 지문, 패턴)모둘 호출(다건)
    //인증요청
    @JavascriptInterface
    public void callSimpleAuthMulti(String strJsonObject) {

        GLog.d("callSimpleAuthMulti - strJsonObject : " + strJsonObject);
        JSONObject jsonObject = null;

        String callback = "";
        String issuedn = "";
        String subjdn = "";
        String sn = "";
        String titleType = "";
        String sSignText = "";
        String ucpidText = "";

        setAuthOption();
        setPattenLine();

        try {
            if (!strJsonObject.equals("")) {
                jsonObject = new JSONObject(strJsonObject);

                if (!jsonObject.isNull("certType")) {
                    mCertType = jsonObject.getString("certType");
                }

                if (!jsonObject.isNull("callback")) {
                    callback = jsonObject.getString("callback");
                    setCallbakckFuction(callback);
                }

                if (!jsonObject.isNull("issuedn")) {
                    issuedn = jsonObject.getString("issuedn");
                    setIssuedn(issuedn);
                }

                if (!jsonObject.isNull("subjdn")) {
                    subjdn = jsonObject.getString("subjdn");
                    setSubjdn(subjdn);
                }

                if (!jsonObject.isNull("sn")) {
                    sn = jsonObject.getString("sn");
                    setSn(sn);
                }

                if (!jsonObject.isNull("sSignText")) {
                    sSignText = jsonObject.getString("sSignText");
                }

                if (!jsonObject.isNull("ucpidText")) {
                    ucpidText = jsonObject.getString("ucpidText");
                }

                if (!jsonObject.isNull("titleType")) {
                    titleType = jsonObject.getString("titleType");

                    Hashtable<String, Object> authUICustom = new Hashtable<String, Object>();
                    //“01” -> 로그인 시 지정값, 앱에서는 “간편하게 인증하세요”로 타이틀 표시
                    // “02” -> 전자 서명 시 지정값, 앱에서는 “패턴”로 타이틀 표시
                    if (titleType.equals("02")) {
//                        authUICustom.put("text1", mActivity.getResources().getString(R.string.pattern_title));
                    } else {
//                        authUICustom.put("text1", mActivity.getResources().getString(R.string.pattern_login_title));
                    }
                    Hashtable<String, Object> settingValue = new Hashtable<String, Object>();
                    settingValue.put("pattern", authUICustom);
                    MagicFIDOUtil magicFIDOUtil = new MagicFIDOUtil(mActivity);
                    magicFIDOUtil.setCustomUIValues(settingValue);
                    magicFIDOUtil.setAuthenticationBackgroundCertInfoHidden(true);
                } else {
                    Hashtable<String, Object> authUICustom = new Hashtable<String, Object>();
                    authUICustom.put("text1", "간편하게 로그인하세요.");
                    Hashtable<String, Object> settingValue = new Hashtable<String, Object>();
                    settingValue.put("pattern", authUICustom);
                    MagicFIDOUtil magicFIDOUtil = new MagicFIDOUtil(mActivity);
                    magicFIDOUtil.setCustomUIValues(settingValue);
                    magicFIDOUtil.setAuthenticationBackgroundCertInfoHidden(true);
                }

                GLog.d("mCertType : " + mCertType + " , callback : " + getCallbackFunction() + " , issuedn : " + getIssuedn() + " ,subjdn : " + getSubjdn() + ", sn : " + getSn() + ", sSignText :" + sSignText + " ,ucpidText : " + ucpidText + " ,titleType : " + titleType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mCertType.equals("01")) {
            //공동인증서 호출X

        } else if (mCertType.equals("02")) {

            GLog.d("Utils.isEnrolledFingerprint : " + Utils.isEnrolledFingerprint(mActivity));

            failCnt = PreferenceManager.getInt(mActivity, Constant.FINGER);
            //지문
            mAuth = new FIDOAuthentication(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoAuthCallbackResultMulti, Constant.FINGER, getSubjdn());

            //지문 인증 요청
            mAuth.loginAuthFIDO(getTobeDatas(sSignText, ucpidText));

        } else if (mCertType.equals("03")) {
            //얼굴 호출 X

        } else if (mCertType.equals("04")) {

            failCnt = PreferenceManager.getInt(mActivity, Constant.PATTERN);

            //패턴
            mAuth = new FIDOAuthentication(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoAuthCallbackResultMulti, Constant.PATTERN, subjdn);

            //패턴 인증 요청
            mAuth.loginAuthFIDO(getTobeDatas(sSignText, ucpidText));
        }
    }

    //인증요청
    private ArrayList<byte[]> getTobeDatas(String sSignText, String ucpidText) {
        ArrayList<byte[]> tobeDatas = new ArrayList<byte[]>();
        if ("".equals(sSignText)) {
            tobeDatas.add(tobeData1);
        } else {
            try {
                arrSignText = new JSONArray(sSignText);
                for (int i = 0; i < arrSignText.length(); i++) {
                    GLog.d("arrSignText : " + arrSignText.getString(i));
                    byte[] mByteSignText = arrSignText.getString(i).getBytes();
                    tobeDatas.add(mByteSignText);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        GLog.d("ucpidText : " + ucpidText);

        if (!"".equals(ucpidText)) {
            try {
                pki = new MagicXSign();
                pki.Init(mActivity, MagicXSign_Type.XSIGN_DEBUG_LEVEL_1);
                byte[] encodeUcpidText = pki.BASE64_Decode(ucpidText);
                byte[] mByteUcpidText = encodeUcpidText;
                tobeDatas.add(mByteUcpidText);
                pki.Finish();
            } catch (MagicXSign_Exception e) {
                e.printStackTrace();
                GLog.d("error: " + e.getErrorMessage());
            }
        }

        GLog.d("tobeDatas size: " + tobeDatas.size());
        return tobeDatas;
    }

    //인증장치 옵션 설정
    private void setAuthOption() {
        if (magicFIDOUtil == null) {
            magicFIDOUtil = new MagicFIDOUtil(mActivity);
        }
        Hashtable<String, Object> authOption = new Hashtable<String, Object>();
        authOption.put(MagicFIDOUtil.KEY_RETRY_COUNT_TO_LOCK, 5);
        authOption.put(MagicFIDOUtil.KEY_LOCK_TIME, 1);
        authOption.put(MagicFIDOUtil.KEY_MAX_LOCK_COUNT, 1);
        magicFIDOUtil.setAuthenticatorOptions(LOCAL_AUTH_TYPE.LOCAL_PATTERN_TYPE, authOption);
    }


    //Fido 인증단건 콜백
    private FIDOCallbackResult fidoAuthCallbackResult = new FIDOCallbackResult() {
        @Override
        public void onFIDOResult(int requestCode, boolean result, FidoResult fidoResult) {
            switch (requestCode) {
                case FIDORequestCode.REQUEST_CODE_AUTH: {
                    GLog.d("fidoAuthCallbackResult : " + fidoResult.getErrorCode());

                    JSONObject obj = new JSONObject();
                    JSONObject data = new JSONObject();

                    // FIDO Client에 전송하지 못 하였을 경우.
                    if (fidoResult.getErrorCode() != FidoResult.RESULT_SUCCESS) {
                        if (fidoResult.getErrorCode() != 999) {
                            failCnt++;
                        }

                        String certData = "";
                        try {
                            byte[] dnData = mAuth.getDn();
                            if (dnData != null) {
                                certData = Base64.encodeToString(dnData, Base64.DEFAULT);
                                GLog.d("certData : " + certData);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            obj.put("resultCode", "FAIL");
                            data.put("signedData", "");
                            data.put("certData", certData);
                            data.put("errorCode", fidoResult.getErrorCode());
                            data.put("issuedn", getIssuedn());
                            data.put("subjdn", getSubjdn());
                            data.put("sn", getSn());
                            obj.put("data", data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (mCertType.equals("02")) {
                            PreferenceManager.setInt(mActivity, Constant.FINGER, failCnt);
                        } else if (mCertType.equals("04")) {
                            PreferenceManager.setInt(mActivity, Constant.PATTERN, failCnt);
                        }

                        showToastMessage(fidoResult.getDescription() + "(" + fidoResult.getErrorCode() + ")");

//                        Intent alertIntent = new Intent(mActivity, EmptyActivity.class);
//                        mActivity.startActivityForResult(alertIntent, ALERT_REQUEST);

                    } else {
                        try {
                            showToastMessage("Auth Success");
                            //등록된 인증서 가져와야 함
                            ArrayList<byte[]> getSignedData = mAuth.getSignedData();
                            if (!getSignedData.isEmpty()) {
                                if (getSignedData.size() > 0) {

                                    String signedData = "";
                                    String ucpidData = "";

                                    GLog.d("getSignedData.size() : " + getSignedData.size());

                                    for (int i = 0; i < getSignedData.size(); i++) {
                                        if (i == 0) {
                                            signedData = Base64.encodeToString(getSignedData.get(0), Base64.DEFAULT);
                                            GLog.d("signedData : " + signedData);
                                        }
                                        if (i == 1) {
                                            ucpidData = Base64.encodeToString(getSignedData.get(1), Base64.DEFAULT);
                                            GLog.d("ucpidData : " + ucpidData);
                                        }
                                    }

                                    String certData = "";
                                    try {
                                        byte[] dnData = mAuth.getDn();
                                        if (dnData != null) {
                                            certData = Base64.encodeToString(dnData, Base64.DEFAULT);
                                            GLog.d("certData : " + certData);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        obj.put("resultCode", "SUCCESS");
                                        data.put("signedData", signedData.replaceAll("\\n", ""));
                                        data.put("certData", certData);
                                        data.put("errorCode", fidoResult.getErrorCode());
                                        data.put("issuedn", getIssuedn());
                                        data.put("subjdn", getSubjdn());
                                        data.put("sn", getSn());

                                        if (!"".equals(ucpidData)) {
                                            data.put("ucpidData", ucpidData.replaceAll("\\n", ""));
                                        }

                                        obj.put("data", data);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (mCertType.equals("02")) {
                                        PreferenceManager.setInt(mActivity, Constant.FINGER, 0);
                                    } else if (mCertType.equals("04")) {
                                        PreferenceManager.setInt(mActivity, Constant.PATTERN, 0);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            String dn = "";
                            try {
                                byte[] dnData = mAuth.getDn();
                                if (dnData != null) {
                                    dn = Base64.encodeToString(dnData, Base64.DEFAULT);
                                    GLog.d("dn : " + dn);
                                }

                                obj.put("resultCode", "FAIL");
                                data.put("signedData", "");
                                data.put("errorCode", fidoResult.getErrorCode());
                                data.put("failnCnt", String.valueOf(failCnt));
                                data.put("issuedn", getIssuedn());
                                data.put("subjdn", getSubjdn());
                                data.put("sn", getSn());
                                data.put("dn", dn);
                                obj.put("data", data);

                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    callbackFunction(getCallbackFunction(), obj.toString());
                }
                break;
            }
        }
    };

    //Fido 인증 콜백(다건인증)
    private FIDOCallbackResult fidoAuthCallbackResultMulti = new FIDOCallbackResult() {
        @Override
        public void onFIDOResult(int requestCode, boolean result, FidoResult fidoResult) {
            switch (requestCode) {
                case FIDORequestCode.REQUEST_CODE_AUTH: {
                    GLog.d("fidoAuthCallbackResult : " + fidoResult.getErrorCode());

                    JSONObject obj = new JSONObject();
                    JSONObject data = new JSONObject();

                    // FIDO Client에 전송하지 못 하였을 경우.
                    if (fidoResult.getErrorCode() != FidoResult.RESULT_SUCCESS) {
                        if (fidoResult.getErrorCode() != 999) {
                            failCnt++;
                        }

                        String certData = "";
                        try {
                            byte[] dnData = mAuth.getDn();
                            if (dnData != null) {
                                certData = Base64.encodeToString(dnData, Base64.DEFAULT);
                                GLog.d("certData : " + certData);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            obj.put("resultCode", "FAIL");
                            data.put("signedData", "");
                            data.put("certData", certData);
                            data.put("errorCode", fidoResult.getErrorCode());
                            data.put("issuedn", getIssuedn());
                            data.put("subjdn", getSubjdn());
                            data.put("sn", getSn());
                            obj.put("data", data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (mCertType.equals("02")) {
                            PreferenceManager.setInt(mActivity, Constant.FINGER, failCnt);
                        } else if (mCertType.equals("04")) {
                            PreferenceManager.setInt(mActivity, Constant.PATTERN, failCnt);
                        }

                        showToastMessage(fidoResult.getDescription() + "(" + fidoResult.getErrorCode() + ")");

                    } else {
                        try {
                            showToastMessage("Auth Success");
                            //등록된 인증서 가져와야 함
                            ArrayList<byte[]> getSignedData = mAuth.getSignedData();
                            if (!getSignedData.isEmpty()) {
                                if (getSignedData.size() > 0) {

                                    String signedData = "";
                                    String ucpidData = "";

                                    GLog.d("getSignedData.size() : " + getSignedData.size());
                                    JSONArray arraySignedData = new JSONArray();

                                    if (null == arrSignText) {
                                        for (int i = 0; i < getSignedData.size(); i++) {
                                            if (i == 0) {
                                                signedData = Base64.encodeToString(getSignedData.get(0), Base64.DEFAULT);
                                                signedData.replaceAll("\\n", "");
                                                GLog.d("signedData : " + signedData);
                                                arraySignedData.put(signedData);
                                            }
                                            if (i == 1) {
                                                ucpidData = Base64.encodeToString(getSignedData.get(1), Base64.DEFAULT);
                                                ucpidData.replaceAll("\\n", "");
                                                GLog.d("ucpidData : " + ucpidData);
                                            }
                                        }
                                    } else if (arrSignText.length() > 0) {
                                        for (int i = 0; i < getSignedData.size(); i++) {
                                            if (i < arrSignText.length()) {
                                                signedData = Base64.encodeToString(getSignedData.get(i), Base64.DEFAULT);
                                                signedData.replaceAll("\\n", "");
                                                GLog.d("signedData : " + signedData);
                                                arraySignedData.put(signedData);
                                            } else {
                                                ucpidData = Base64.encodeToString(getSignedData.get(i), Base64.DEFAULT);
                                                ucpidData.replaceAll("\\n", "");
                                                GLog.d("ucpidData : " + ucpidData);
                                            }
                                        }
                                    }

                                    String certData = "";
                                    try {
                                        byte[] dnData = mAuth.getDn();
                                        if (dnData != null) {
                                            certData = Base64.encodeToString(dnData, Base64.DEFAULT);
                                            GLog.d("certData : " + certData);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        obj.put("resultCode", "SUCCESS");
                                        data.put("signedData", arraySignedData);
                                        data.put("certData", certData);
                                        data.put("errorCode", fidoResult.getErrorCode());
                                        data.put("issuedn", getIssuedn());
                                        data.put("subjdn", getSubjdn());
                                        data.put("sn", getSn());
                                        if (!ucpidData.equals("")) {
                                            data.put("ucpidData", ucpidData);
                                        }
                                        obj.put("data", data);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (mCertType.equals("02")) {
                                        PreferenceManager.setInt(mActivity, Constant.FINGER, 0);
                                    } else if (mCertType.equals("04")) {
                                        PreferenceManager.setInt(mActivity, Constant.PATTERN, 0);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            String dn = "";
                            try {
                                byte[] dnData = mAuth.getDn();
                                if (dnData != null) {
                                    dn = Base64.encodeToString(dnData, Base64.DEFAULT);
                                    GLog.d("dn : " + dn);
                                }

                                obj.put("resultCode", "FAIL");
                                data.put("signedData", "");
                                data.put("errorCode", fidoResult.getErrorCode());
                                data.put("failnCnt", String.valueOf(failCnt));
                                data.put("issuedn", getIssuedn());
                                data.put("subjdn", getSubjdn());
                                data.put("sn", getSn());
                                data.put("dn", dn);
                                obj.put("data", data);

                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    callbackFunction(getCallbackFunction(), obj.toString());
                }
                break;
            }
        }
    };

    //간편인증서(FaceID, 지문, 패턴) 등록, 삭제, 재등록
    @JavascriptInterface
    public void simpleAuthMgt(String strJsonObject) {

        GLog.d("simpleAuthMgt - strJsonObject : " + strJsonObject);

        setPattenLine();
        JSONObject jsonObject = null;
        mCertType = ""; //'02' /*지문*/ or '03' /*얼굴*/ or ' 04' /*패턴*/
        String procType = ""; //'R'/*등록*/ or 'D'/*삭제*/
        String userKey = ""; //공공인증서 DN값
        String callback = "";

        try {
            if (!strJsonObject.equals("")) {
                jsonObject = new JSONObject(strJsonObject);
                if (!jsonObject.isNull("certType")) {
                    mCertType = jsonObject.getString("certType");
                }

                if (!jsonObject.isNull("procType")) {
                    procType = jsonObject.getString("procType");
                }

                if (!jsonObject.isNull("userKey")) {
                    userKey = jsonObject.getString("userKey");
                    setUserKey(userKey);
                }
//                userKey = "cn=전자세금용001()00996802021031000007,ou=TEST,ou=KFTC,ou=xUse4Esero,o=yessign,c=kr"; //dream1004!
                if (!jsonObject.isNull("callback")) {
                    callback = jsonObject.getString("callback");
                    setCallbakckFuction(callback);
                }

                if (procType.equals("R")) {//등록
                    if (mCertType.equals("02")) {
                        reg = new FIDORegistration(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoAuthMgtResult, Constant.FINGER);
                        magicFIDOUtil.setAuthenticationBackgroundCertInfoHidden(false);
                    } else if (mCertType.equals("04")) {

                        Hashtable<String, Object> authUICustom = new Hashtable<String, Object>();
//                        authUICustom.put("text1", mActivity.getResources().getString(R.string.pt_t_regist));
                        Hashtable<String, Object> settingValue = new Hashtable<String, Object>();
                        settingValue.put("pattern", authUICustom);
                        MagicFIDOUtil magicFIDOUtil = new MagicFIDOUtil(mActivity);
                        magicFIDOUtil.setCustomUIValues(settingValue);
                        magicFIDOUtil.setAuthenticationBackgroundCertInfoHidden(false);
                        //리셋을 먼저 하고 리셋이 성공하면 패턴 등록을 요청한다.
                        magicFIDOUtil.resetUserVerification(LOCAL_AUTH_TYPE.LOCAL_PATTERN_TYPE, fidoAuthMgtResult);
//                        reg = new FIDORegistration(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoResult, Constant.PATTERN);
                        return;
                    }
                    fidoRegistration(userKey);
                } else if (procType.equals("D")) {//삭제
                    magicFIDOUtil.setAuthenticationBackgroundCertInfoHidden(false);
                    if (mCertType.equals("02")) {
                        mDeregist = new FIDODeRegistration(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoAuthMgtResult, Constant.FINGER, userKey);
                    } else if (mCertType.equals("04")) {
                        mDeregist = new FIDODeRegistration(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoAuthMgtResult, Constant.PATTERN, userKey);
                    }
                    mDeregist.deRegistFIDO();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
    private FIDOCallbackResult fidoAuthMgtResult = new FIDOCallbackResult() {
        @Override
        public void onFIDOResult(int requestCode, boolean result, FidoResult fidoResult) {
            switch (requestCode) {
                case FIDORequestCode.REQUEST_CODE_REGIST: { //등록
                    JSONObject obj = new JSONObject();
                    JSONObject data = new JSONObject();

                    if (fidoResult.getErrorCode() != FidoResult.RESULT_SUCCESS) {
                        showToastMessage(fidoResult.getDescription() + "(" + fidoResult.getErrorCode() + ")");
                        try {
                            obj.put("resultCode", "FAIL");
                            data.put("errorCode", fidoResult.getErrorCode());
                            data.put("signedData", "");
                            obj.put("data", data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {

                        try {
                            //등록된 인증서 가져와야 함
                            ArrayList<byte[]> getSignedData = reg.getSignedData();
                            if (!getSignedData.isEmpty()) {
                                if (getSignedData.size() > 0) {
                                    showToastMessage("REG Success");
                                    byte[] localData = getSignedData.get(0);
                                    String signedData = Base64.encodeToString(localData, Base64.DEFAULT);
                                    GLog.d("signedData : " + signedData);
                                    try {
                                        obj.put("resultCode", "SUCCESS");
                                        data.put("signedData", signedData.replaceAll("\\n", ""));
                                        data.put("errorCode", fidoResult.getErrorCode());
                                        obj.put("data", data);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            showToastMessage("REG Fail");
                            try {
                                obj.put("resultCode", "SUCCESS");
                                data.put("signedData", "");
                                data.put("errorCode", fidoResult.getErrorCode());
                                obj.put("data", data);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    callbackFunction(getCallbackFunction(), obj.toString());
                }
                break;

                case FIDORequestCode.REQUEST_CODE_DEREG: { //삭제

                    JSONObject obj = new JSONObject();
                    JSONObject data = new JSONObject();

                    // FIDO Client에 전송하지 못 하였을 경우.
                    if (fidoResult.getErrorCode() != FidoResult.RESULT_SUCCESS) {
                        showToastMessage(fidoResult.getDescription() + "(" + fidoResult.getErrorCode() + ")");
                        try {
                            obj.put("resultCode", "FAIL");
                            data.put("errorCode", fidoResult.getErrorCode());
                            data.put("singedData", "");
                            obj.put("data", data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                        try {
                            //등록된 인증서 가져와야 함
                            ArrayList<byte[]> getSignedData = reg.getSignedData();
                            if (!getSignedData.isEmpty()) {
                                if (getSignedData.size() > 0) {
                                    showToastMessage("DEREG Success");
                                    byte[] localData = getSignedData.get(0);
                                    String signedData = Base64.encodeToString(localData, Base64.DEFAULT);
                                    GLog.d("signedData : " + signedData);
                                    try {
                                        obj.put("resultCode", "SUCCESS");
                                        data.put("signedData", signedData.replaceAll("\\n", ""));
                                        data.put("errorCode", fidoResult.getErrorCode());
                                        obj.put("data", data);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            showToastMessage("DEREG Fail");
                            try {
                                obj.put("resultCode", "SUCCESS");
                                data.put("signedData", "");
                                data.put("errorCode", fidoResult.getErrorCode());
                                obj.put("data", data);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    callbackFunction(getCallbackFunction(), obj.toString());
                }
                break;

                case FIDORequestCode.REQUEST_LOCAL_RESET:
                    GLog.d("fidoResetResult - errorCoode : " + fidoResult.getErrorCode());
                    if (fidoResult.getErrorCode() == 0) {
                        GLog.d("fidoResetResult - getUserKey : " + getUserKey());
                        reg = new FIDORegistration(mActivity, MAGIC_FIDO_TYPE.KFIDO, fidoAuthMgtResult, Constant.PATTERN);
                        fidoRegistration(getUserKey());
                    }
                    break;
            }
        }
    };


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
            if (viewMode.equals("FULL_TYPE")) {
                if (keypadType.equals("CHAR_TYPE")) { // 풀모드 - 문자키패드
                    //title 설정
//                magicVKeypad.setTitleText(title);
                    // subTitle 설정
//                magicVKeypad.setSubTitleText(subTitle);
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
                    //title 설정
//                magicVKeypad.setTitleText(title);
                    //subTitle 설정
//                magicVKeypad.setSubTitleText(subTitle);
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
//                if(screenshot.equals("TRUE")){
//                    magicVKeypad.setScreenshot(true);
//                }
                    // 키패드 실행
                    magicVKeypad.startNumKeypad(mOnClickInterface);
                }
            } else if (viewMode.equals("HALF_TYPE")) {
                if (keypadType.equals("CHAR_TYPE")) { // 하프모드 - 문자키패드
                    if (magicVKeypad.isKeyboardOpen()) {
                        GLog.d();
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
                    // 키패드 실행
                    magicVKeypad.startCharKeypad(mOnClickInterface);
                }
            } else { // 하프모드 - 숫자키패드
                if (magicVKeypad.isKeyboardOpen()) {
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
                /// 키패드 재배치 허용(DEFAULT : FALSE)
                if (options.getBoolean("UseReplace"))
                    magicVKeypad.setNumUseReplace(true);
                else
                    magicVKeypad.setNumUseReplace(false);
                //키패드 실행
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

        boolean successLicense = magicVKeypad.initializeMagicVKeypad(mActivity, strLicense); // true : 검증 성공, false : 검증 실패

        if (successLicense) {
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
            GLog.d("licenseResult === " + licenseResult);
//            if (licenseResult != 0) {
//                GLog.d("라이선스 검증 실패");
//            }

            byte[] decData = null;

            if (MagicVKeyPadSettings.bIsFullMode) { // 풀모드
                if (!MagicVKeyPadSettings.bUseE2E)
                    decData = magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData());
                if (magicVKeypadResult.getEncryptData() != null)
                    RSAEncryptData = new String(magicVKeypadResult.getEncryptData());

                if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_CANCEL_BUTTON) {
                    if (magicVKeypadResult.getFieldName().equals(MagicVKeyPadSettings.numFieldName)) {
//                        insertNum.setText("");
                    } else {
                        charResultData = "";
//                        insertChar.setText("");
                    }

                    JSONObject obj = new JSONObject();
                    JSONObject data = new JSONObject();
                    try {
                        obj.put("resultCode", "FAIL");
                        data.put("randomedPassword", "");
                        obj.put("data", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callbackFunction(getCallbackFunction(), obj.toString());

                } else if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_OK_BUTTON) {

                    if (magicVKeypadResult.getFieldName().equals(MagicVKeyPadSettings.numFieldName)) {
                        if (!MagicVKeyPadSettings.bUseE2E) {
                            if (decData != null) {
//                                insertNum.setText(new String(magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData())));
                            }

                        } else {
                            if (magicVKeypad.getEncryptData() != null) {
                                RSAEncryptData = new String(magicVKeypadResult.getEncryptData());
                            }
                        }
                    } else if (magicVKeypadResult.getFieldName().equals(MagicVKeyPadSettings.charFieldName)) {

                        if (decData != null) {
                            if (!MagicVKeyPadSettings.bUseE2E) {
//                                insertChar.setText(new String(magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData())));
//                                charResultData = new String(magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData()));
//                                insertChar.setText(charResultData);

                                String randomedPassword = null;
                                try {
                                    randomedPassword = new String(magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData()));
                                } catch (Exception e) {
                                    magicVKeypad.closeKeypad();
                                    return;
                                }
//                    String randomedPassword = byteArrayToHex(magicVKeypadResult.getEncryptData());
                                GLog.d("randomedPassword : " + randomedPassword);

                                magicVKeypad.closeKeypad();

                                JSONObject obj = new JSONObject();
                                JSONObject data = new JSONObject();
                                try {
                                    obj.put("resultCode", "SUCCESS");
                                    if (!"".equals(randomedPassword)) {
//                                        data.put("randomedPassword", randomedPassword);
                                        //취약점 수정으로 인해 패스워드 암호화 하여 전송
                                        data.put("randomedPassword", Base64.encodeToString(randomedPassword.getBytes(), Base64.NO_WRAP));
                                    } else {
                                        GLog.d("randomedPassword : " + randomedPassword);
                                    }
                                    obj.put("data", data);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                callbackFunction(getCallbackFunction(), obj.toString());
                            } else {
                                if (magicVKeypad.getEncryptData() != null)
                                    RSAEncryptData = new String(magicVKeypadResult.getEncryptData());
                            }
                        } else {
                            JSONObject obj = new JSONObject();
                            JSONObject data = new JSONObject();
                            try {
                                obj.put("resultCode", "SUCCESS");
                                data.put("randomedPassword", "");
                                obj.put("data", data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            callbackFunction(getCallbackFunction(), obj.toString());
                        }
                    }

                    if (magicVKeypadResult.getEncryptData() != null) {
                        if (!MagicVKeyPadSettings.bUseE2E) {
                            AESEncData = byteArrayToHex(magicVKeypadResult.getEncryptData());
                            AESDecData = new String(decData);
//                            tv_AESEncryptData.setText("AES256 암호화 데이터 : " + AESEncData);
//                            tv_AESDecryptData.setText("AES256 복호화 데이터 : " + AESDecData);
                        } else {
//                            tv_RSAEncryptData.setText("RSA 암호화 데이터 : " + RSAEncryptData);
                        }
                    }
                }
            } else { // 하프모드
                if (!MagicVKeyPadSettings.bUseE2E) {
                    GLog.d();
                    decData = magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData());
                    if (decData != null) {

                        GLog.d();
                        AESDecData = byteArrayToHex(magicVKeypadResult.getEncryptData());
                        AESEncData = new String(decData);
                    }
                }

                GLog.d();
                if (magicVKeypad.getEncryptData() != null)
                    RSAEncryptData = new String(magicVKeypad.getEncryptData());

                // 현재 focus가 cancelbutton일경우 어떤 행동 할 건지 설정
                if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_CANCEL_BUTTON) {

                    magicVKeypad.closeKeypad();

                    if (magicVKeypadResult.getFieldName().equals(MagicVKeyPadSettings.charFieldName)) {
//                        insertChar.setText("");
                    } else if (magicVKeypadResult.getFieldName().equals(MagicVKeyPadSettings.numFieldName)) {
//                        insertNum.setText("");
                    }

                    JSONObject obj = new JSONObject();
                    JSONObject data = new JSONObject();
                    try {
                        obj.put("resultCode", "FAIL");
                        data.put("randomedPassword", "");
                        obj.put("data", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callbackFunction(getCallbackFunction(), obj.toString());

                }
                // OK 버튼일 떄 설정
                else if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_OK_BUTTON) {

                    if (magicVKeypadResult.getEncryptData() != null) {
                        if (!MagicVKeyPadSettings.bUseE2E) {
//                            tv_AESEncryptData.setText("AES256 암호화 데이터 : " + byteArrayToHex(magicVKeypadResult.getEncryptData()));
//                            tv_AESDecryptData.setText("AES256 복호화 데이터 : " + new String(decData));
                        } else {
//                            tv_RSAEncryptData.setText("RSA 암호화 데이터 : " + RSAEncryptData);
                        }
                    }

                    String randomedPassword = null;
                    try {
                        randomedPassword = new String(decData);
                    } catch (Exception e) {
                        magicVKeypad.closeKeypad();
                        return;
                    }
//                    String randomedPassword = byteArrayToHex(magicVKeypadResult.getEncryptData());
                    GLog.d("randomedPassword : " + randomedPassword);

                    magicVKeypad.closeKeypad();

                    JSONObject obj = new JSONObject();
                    JSONObject data = new JSONObject();
                    try {
                        obj.put("resultCode", "SUCCESS");
                        if (!"".equals(randomedPassword)) {
                            data.put("randomedPassword", randomedPassword);
                        }
                        obj.put("data", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callbackFunction(getCallbackFunction(), obj.toString());

                } else if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_CHAR_NUM_BUTTON) {

                    if (magicVKeypadResult.getFieldName().equals(MagicVKeyPadSettings.numFieldName)) {
                        if (decData == null) {
//                            insertNum.setText("");
                        } else {
                            if (magicVKeypadResult.getEncryptData() != null) {
//                                insertNum.setText(new String(decData));
                            }
                        }
                    } else if (magicVKeypadResult.getFieldName().equals(MagicVKeyPadSettings.charFieldName)) {
                        if (decData == null) {
//                            insertChar.setText("");
                        } else {
                            if (!MagicVKeyPadSettings.bUseE2E) {
//                                insertChar.setText(new String(magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData())));
                            }
                        }
                    }
                }
            }
        }
    };

    public boolean isKeyboardOpen() {
        if (magicVKeypad == null) {
            return false;
        }
        return magicVKeypad.isKeyboardOpen();
    }

    public void closeKeyPad() {
        if (magicVKeypad != null) {
            magicVKeypad.closeKeypad();
        }
    }

    public String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for (final byte b : a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    //디바이스 OS에서 제공 가능한 생체인증 유형
    @JavascriptInterface
    public void getDeviceInfo(String strJsonObject) {
        GLog.d("getDeviceInfo - strJsonObject : " + strJsonObject);
        JSONObject jsonObject = null;
        String callback = "";
        try {
            if (!strJsonObject.equals("")) {
                jsonObject = new JSONObject(strJsonObject);
                if (!jsonObject.isNull("callback")) {
                    callback = jsonObject.getString("callback");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MagicFIDOUtil magicFIDOUtil = new MagicFIDOUtil(mActivity);
        boolean isAvailableFingerPrint = magicFIDOUtil.isAvailableFIDO(LOCAL_AUTH_TYPE.LOCAL_FINGERPRINT_TYPE);
        JSONObject obj = new JSONObject();
        JSONObject data = new JSONObject();

        SharedPreferences prefs = mActivity.getSharedPreferences(PreferenceManager.PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean isInvisiblePattern = prefs.getBoolean(Constant.PATTERN_INVISIBLE, false);

        try {
            data.put("faceid", "N");
            if (isAvailableFingerPrint) {
                data.put("fingerprint", "Y");
            } else {
                data.put("fingerprint", "N");
            }
            data.put("appVersion", BuildConfig.VERSION_NAME);
            data.put("isInvisiblePattern", isInvisiblePattern);
            obj.put("resultCode", "SUCCESS");
            obj.put("data", data);

        } catch (JSONException e) {
            e.printStackTrace();
            try {
                data.put("faceid", "N");
                if (isAvailableFingerPrint) {
                    data.put("fingerprint", "Y");
                } else {
                    data.put("fingerprint", "N");
                }
                data.put("appVersion", BuildConfig.VERSION_NAME);
                obj.put("resultCode", "FAIL");
                obj.put("data", data);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        callbackFunction(callback, obj.toString());
    }

    //push 사용자 등록
//    @JavascriptInterface
//    public void registryPush(String strJsonObject) {
//        GLog.d("registryPush - strJsonObject : " + strJsonObject);
//        JSONObject jsonObject = null;
//        String userId = "";
//        String callback = "";
//
//        JSONObject obj = new JSONObject();
//        try {
//            if (!strJsonObject.equals("")) {
//                jsonObject = new JSONObject(strJsonObject);
//
//                if (!jsonObject.isNull("userId")) {
//                    userId = jsonObject.getString("userId");
//                }
//
//                if (!jsonObject.isNull("callback")) {
//                    callback = jsonObject.getString("callback");
//                }
//
//                if (!userId.equals("")) {
//                    Tigen tigen = new Tigen(mActivity);
//                    tigen.init(userId, "", "");
//                }
//                obj.put("resultCode", "SUCCESS");
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            try {
//                obj.put("resultCode", "FAIL");
//            } catch (JSONException e1) {
//                e1.printStackTrace();
//            }
//        }
//
//        callbackFunction(callback, obj.toString());
//    }

    //앱종료
    @JavascriptInterface
    public void closeApp(String isHide) {
        if ("true".equals(isHide.toLowerCase())) {
            ActivityCompat.finishAffinity(mActivity);
        } else {
//            closePopup();
        }
    }

    //앱종료
//    @JavascriptInterface
//    public void closeApp() {
//        closePopup();
//    }
//
//    public void closePopup() {
//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//                // 작업 처리
//                CustomDialog customDialog = new CustomDialog(mActivity, new CustomDialogClickListener() {
//                    @Override
//                    public void onPositiveClick() {
//                        ActivityCompat.finishAffinity(mActivity);
//                    }
//
//                    @Override
//                    public void onNegativeClick() {
//
//                    }
//                }, "안내", mActivity.getResources().getString(R.string.close_app_message), CustomDialog.TWO_BUTTON, true);
//                customDialog.setCancelable(false);
//                customDialog.show();
//
//                Display display = mActivity.getWindowManager().getDefaultDisplay();
//                Point size = new Point();
//                display.getSize(size);
//
//                Window window = customDialog.getWindow();
//
//                int x = (int) (size.x * 0.9f);
//                int y = (int) (size.y * 0.7f);
//
//                window.setLayout(x, y);
//            }
//        });
//    }

    //디바이스에 등록된 생체 정보가 있는지 유무
    @JavascriptInterface
    public void isExsistEnrolledBiometric(String strJsonObject) {

        GLog.d("isExsistEnrolledBiometric - strJsonObject : " + strJsonObject);

        JSONObject jsonObject = null;
        String callback = "";
        try {
            if (!strJsonObject.equals("")) {
                jsonObject = new JSONObject(strJsonObject);

                if (!jsonObject.isNull("callback")) {
                    callback = jsonObject.getString("callback");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MagicFIDOUtil magicFIDOUtil = new MagicFIDOUtil(mActivity);
        FidoResult fidoResult = magicFIDOUtil.getLocalVerifyState(LOCAL_AUTH_TYPE.LOCAL_FINGERPRINT_TYPE);

        JSONObject obj = new JSONObject();
        int errorCode = fidoResult.getErrorCode();
        JSONObject data = new JSONObject();
        try {
            data.put("faceid", "N");
            if (errorCode == FidoResult.LOCAL_VERIFY_REGISTED) {
                data.put("fingerprint", "Y");
            } else {
                data.put("fingerprint", "N");
            }
            obj.put("resultCode", "SUCCESS");
            obj.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                data.put("faceid", "N");
                if (errorCode == FidoResult.LOCAL_VERIFY_REGISTED) {
                    data.put("fingerprint", "Y");
                } else {
                    data.put("fingerprint", "N");
                }
                obj.put("resultCode", "FAIL");
                obj.put("data", data);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        callbackFunction(callback, obj.toString());
    }

    //인증서 가져오기 모듈 호출 - 코드번호 입력
    @JavascriptInterface
    public void importCert(String strJsonObject) {

        initMagicMRS();

        JSONObject jsonObject = null;
        String authCode = "";
        String callback = "";
        try {
            if (!strJsonObject.equals("")) {
                jsonObject = new JSONObject(strJsonObject);
                if (!jsonObject.isNull("authCode")) {
                    authCode = jsonObject.getString("authCode");
                }

                if (!jsonObject.isNull("callback")) {
                    callback = jsonObject.getString("callback");
                }

                setCallbakckFuction(callback);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mMagicMRS.importCertificateWithAuthCodeWithoutUI(authCode);

    }


    //인증서 가져오기 모듈 호출 - QRCODE
    @JavascriptInterface
//    public void importCertByQRCode(String strJsonObject) {
//        ((MainActivity) mActivity).permissionCheck();
//
//        initMagicMRS();
//        JSONObject jsonObject = null;
//        String callback = "";
//        try {
//            if (!strJsonObject.equals("")) {
//                jsonObject = new JSONObject(strJsonObject);
//                if (!jsonObject.isNull("callback")) {
//                    callback = jsonObject.getString("callback");
//                }
//                setCallbakckFuction(callback);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public void qrcodeScan() {
        mMagicMRS.importCertificateWithQRCode(true);
    }

    //인증서 내보내기
    @JavascriptInterface
    public void exportCert(String strJsonObject) {
        initMagicMRS();
        MRSCertificate certificate = new MRSCertificate();

        JSONObject jsonObject = null;
        String dn = "";
        String randomedPassword = "";
        String callback = "";

        GLog.d("strJsonObject : " + strJsonObject);

        try {
            if (!strJsonObject.equals("")) {
                jsonObject = new JSONObject(strJsonObject);

                if (!jsonObject.isNull("dn")) {
                    dn = jsonObject.getString("dn");
                }

                if (!jsonObject.isNull("randomedPassword")) {
                    randomedPassword = jsonObject.getString("randomedPassword");
                }

                if (!jsonObject.isNull("callback")) {
                    callback = jsonObject.getString("callback");
                }

                setCallbakckFuction(callback);

                GLog.d("dn : " + dn);
                GLog.d("randomedPassword : " + randomedPassword);
                GLog.d("callback : " + callback);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            pki.Init(mActivity, MagicXSign_Type.XSIGN_DEBUG_LEVEL_1);

            int count = -1;
            pki.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER,
                    MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, "/sdcard");
            count = pki.MEDIA_GetCertCount();
            GLog.d("count : " + count);

            int nMediaType[] = new int[1];

            for (int i = 0; i < count; i++) {
                byte[] binCert = pki.MEDIA_ReadCert(i, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, nMediaType);
                String subjectDn = pki.CERT_GetAttribute(binCert, MagicXSign_Type.XSIGN_CERT_ATTR_SUBJECT_DN, true);
                GLog.d("subjectDn : " + subjectDn);
                //전달 받은 dn과 값은 값을갖고 있는 지 확인
                if (dn.equals(subjectDn)) {
                    byte[] signCert = pki.MEDIA_ReadCert(i, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, nMediaType);
                    byte[] signPri = pki.MEDIA_ReadPriKey(i, MagicXSign_Type.XSIGN_PKI_CERT_SIGN);
                    certificate.setSignCert(signCert);
                    certificate.setSignPri(signPri);
                }
            }

            pki.MEDIA_UnLoad();

        } catch (Exception e) {
            e.printStackTrace();
        }


        byte[] pw = randomedPassword.getBytes();    //테스트 용 만기된 인증서 (비밀번호 : 88888888)
//        byte[] pw = "88888888".getBytes();    //테스트 용 만기된 인증서 (비밀번호 : 88888888)
        MagicMRSExportCallback magicMRSExportCallback = new MagicMRSExportCallback() {
            @Override
            public void onMrsExCertViewInfo(final MRSDisplayInfo mrsDisplayInfo) {

                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        //인증번호 Set
                        String authCode = mrsDisplayInfo.getAuthCode();
                        GLog.d("mrsDisplayInfo.getAuthCode() : " + authCode);
                        setAuthCode(authCode);

                        boolean isSuccess = false;

                        //인증번호 생성 성공 여부
                        if ("".equals(authCode)) {
                            //실패
                            isSuccess = false;
                        } else {
                            //성공
                            isSuccess = true;
                        }

                        JSONObject obj = new JSONObject();
                        JSONObject data = new JSONObject();

                        try {
                            if (isSuccess) {
                                obj.put("resultCode", "SUCCESS");
                            } else {
                                obj.put("resultCode", "FAIL");
                            }

                            if (!"".equals(getAuthCode())) {
                                data.put("issuNum", getAuthCode());
                            }
                            obj.put("data", data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String result = obj.toString();
                        if (result != null) {
                            callbackFunction(getCallbackFunction(), result);
                        }
                    }
                });
            }
        };
        mMagicMRS.exportCertificateWithoutUI(certificate, pw, magicMRSExportCallback, 400, 400);
    }

    //인디케이터 색상변화 (변경완료, 테스트X)
    @JavascriptInterface
    public void setBackground(String strJsonObject) {
//        GLog.d("setBackground - strJsonObject : " + strJsonObject);
        JSONObject jsonObject = null;
        String callback = "";
        JSONObject obj = new JSONObject();
        try {
            if (!strJsonObject.equals("")) {
                jsonObject = new JSONObject(strJsonObject);

                if (!jsonObject.isNull("color")) {
                    color = jsonObject.getString("color");
                }

                if (!jsonObject.isNull("callback")) {
                    callback = jsonObject.getString("callback");
                }

                obj.put("resultCode", "SUCCESS");

                //인디케이터 색상 변경
                if (!color.equals("")) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.updateStatusBarColor(mActivity, color);
                        }
                    });
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                obj.put("resultCode", "FAIL");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        callbackFunction(callback, obj.toString());
    }

    //입력된 패턴 선 숨기기
    @JavascriptInterface
    public void setPatternInvisible(String strJsonObject) {
        GLog.d("setPatternInvisible - strJsonObject : " + strJsonObject);

        JSONObject jsonObject = null;
        boolean isInvisible;
        String callback = "";

        JSONObject obj = new JSONObject();
        try {
            if (!strJsonObject.equals("")) {
                jsonObject = new JSONObject(strJsonObject);
                isInvisible = jsonObject.optBoolean("isInvisible");

                if (!jsonObject.isNull("callback")) {
                    callback = jsonObject.getString("callback");
                }

                obj.put("resultCode", "SUCCESS");
                PreferenceManager.setBoolean(mActivity, Constant.PATTERN_INVISIBLE, isInvisible);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                obj.put("resultCode", "FAIL");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        callbackFunction(callback, obj.toString());
    }

    //PUSH BADGE 초기화
    @JavascriptInterface
    public void setPushBadgeCount(String strJsonObject) {
        GLog.d("setPushBadgeCount - strJsonObject : " + strJsonObject);
        JSONObject jsonObject = null;
        String badgeCount = "";
        String callback = "";

        JSONObject obj = new JSONObject();
        try {
            if (!strJsonObject.equals("")) {
                jsonObject = new JSONObject(strJsonObject);

                if (!jsonObject.isNull("badgeCount")) {
                    badgeCount = jsonObject.getString("badgeCount");
                }

                if (!jsonObject.isNull("callback")) {
                    callback = jsonObject.getString("callback");
                }

                if (!badgeCount.equals("")) {
                    Utils.updateIconBadge(mActivity, Integer.parseInt(badgeCount));
                    obj.put("resultCode", "SUCCESS");
                } else {
                    obj.put("resultCode", "FAIL");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                obj.put("resultCode", "FAIL");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        callbackFunction(callback, obj.toString());
    }

    //인증서 내보내기 취소
    @JavascriptInterface
    public void exportCertCancel(String strJsonObject) {
        GLog.d("exportCertCancel - strJsonObject : " + strJsonObject);
        mMagicMRS.cancelMagicMRS();
    }

    //캐쉬 삭제
    @JavascriptInterface
    public void clearCache(String strJsonObject) {
        GLog.d("clearCache - strJsonObject : " + strJsonObject);
        String callback = "";
        JSONObject jsonObject = null;
        JSONObject obj = new JSONObject();
        try {
            if (!strJsonObject.equals("")) {
                jsonObject = new JSONObject(strJsonObject);

                if (!jsonObject.isNull("callback")) {
                    callback = jsonObject.getString("callback");
                }

                //쿠키 삭제
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.clearCache(true);
                        mWebView.clearHistory();
                        Utils.clearCookies(mActivity);
                    }
                });

                obj.put("resultCode", "SUCCESS");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                obj.put("resultCode", "FAIL");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        callbackFunction(callback, obj.toString());
    }

    //UCPID 만들기
    @JavascriptInterface
    public void callSimpleUCPID(String strJsonObject) {
        GLog.d("strJsonObject : " + strJsonObject);
        JSONObject jsonObject = null;
        JSONObject obj = new JSONObject();
        JSONObject data = new JSONObject();

        String szAgreement = "";
        boolean bRealName = false;
        boolean bGender = false;
        boolean bNationalInfo = false;
        boolean bBirthDate = false;
        boolean bCi = false;
        String szUcpidNonce = "";
        String strUrl = "";
        String callback = "";
        byte[] ucpidRequestInfo;
        byte[] byteArrayNonce = null;

        try {
            if (!strJsonObject.isEmpty()) {
                jsonObject = new JSONObject(strJsonObject);

                if (!jsonObject.isNull("szAgreement")) {
                    szAgreement = jsonObject.getString("szAgreement");
                }

                if (!jsonObject.isNull("bRealName")) {
                    bRealName = jsonObject.getBoolean("bRealName");
                }

                if (!jsonObject.isNull("bGender")) {
                    bGender = jsonObject.getBoolean("bGender");
                }

                if (!jsonObject.isNull("bNationalInfo")) {
                    bNationalInfo = jsonObject.getBoolean("bNationalInfo");
                }

                if (!jsonObject.isNull("bBirthDate")) {
                    bBirthDate = jsonObject.getBoolean("bBirthDate");
                }

                if (!jsonObject.isNull("bCi")) {
                    bCi = jsonObject.getBoolean("bCi");
                }

                if (!jsonObject.isNull("szUcpidNonce")) {
                    szUcpidNonce = jsonObject.getString("szUcpidNonce");
                }

                if (!jsonObject.isNull("strUrl")) {
                    strUrl = jsonObject.getString("strUrl");
                }

                if (!jsonObject.isNull("callback")) {
                    callback = jsonObject.getString("callback");
                }

                if (!szUcpidNonce.isEmpty()) {
                    byteArrayNonce = szUcpidNonce.getBytes();
                }

                try {
                    ucpidRequestInfo = pki.CMS_MakeUCPIDRequestInfo(szAgreement, bRealName, bGender, bNationalInfo, bBirthDate, bCi, byteArrayNonce, strUrl);
                    data.put("ucpidRequestInfo", Base64.encodeToString(ucpidRequestInfo, Base64.DEFAULT));
                    data.put("errorCode", "");
                    data.put("errorMsg", "");
                    obj.put("resultCode", "SUCCESS");
                } catch (Exception e) {
                    data.put("ucpidRequestInfo", "");
                    data.put("errorCode", "400");
                    data.put("errorMsg", "UCPIDRequestInfo 동의에 실패하였습니다.");
                    obj.put("resultCode", "FAIL");
                }
                obj.put("data", data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        callbackFunction(callback, obj.toString());
    }

    //기본인증방식 App 데이터 갱신
    @JavascriptInterface
    public void updateCertType(String strJsonObject) {
        GLog.d("updateCertType - strJsonObject : " + strJsonObject);
        JSONObject jsonObject = null;
        JSONObject obj = new JSONObject();
        String certType = "";
        String callback = "";

        try {
            if (!TextUtils.isEmpty(strJsonObject)) {
                jsonObject = new JSONObject(strJsonObject);
                if (!jsonObject.isNull("callback")) {
                    callback = jsonObject.getString("callback");
                }
                if (!jsonObject.isNull("certType")) {
                    certType = jsonObject.getString("certType");
                }
                PreferenceManager.setString(mActivity, Constant.CERT_TYPE, certType);
                obj.put("resultCode", "SUCCESS");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                obj.put("resultCode", "FAIL");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        callbackFunction(callback, obj.toString());
    }

    //앱에서 보관중인 글자크기 코드 조회
    @JavascriptInterface
    public void setFontSizeLocalHTML(String strJsonObject) {
        GLog.d("setFontSizeLocalHTML - strJsonObject : " + strJsonObject);
        JSONObject jsonObject = null;
        JSONObject obj = new JSONObject();
        JSONObject data = new JSONObject();
        String callback = "";
        String font = "";
        font = PreferenceManager.getString(mActivity, Constant.FONT_SIZE);
        try {
            if (!TextUtils.isEmpty(strJsonObject)) {
                jsonObject = new JSONObject(strJsonObject);
                if (!jsonObject.isNull("callback")) {
                    callback = jsonObject.getString("callback");
                }
                obj.put("resultCode", "SUCCESS");
                data.put("font", font);
                obj.put("data", data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                obj.put("resultCode", "FAIL");
                data.put("font", font);
                obj.put("data", data);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        callbackFunction(callback, obj.toString());
    }

    // PDFViewer용 다운로드
    @JavascriptInterface
    public void testPDFViewer(String strJsonObject) {
        GLog.d("testPDFViewer2 - url : " + strJsonObject);
        try {
            JSONObject jsonObject = new JSONObject(strJsonObject);
            String key = jsonObject.getString("key");
            String url = jsonObject.getString("url");

            String callback = "";

            if (null != jsonObject.getString("callback")) {
                callback = jsonObject.getString("callback");
            }

            GLog.d("testPDFViewer2.1 - url : " + url);
            GLog.d("testPDFViewer2.2 - key : " + key);
            GLog.d("testPDFViewer2.3 - callback : " + callback);
//            new DownloadPDFAsynTask(mActivity).execute(url, key, callback);
        } catch (JSONException e) {
            GLog.d("testPDFViewer2.3 - error");
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void webviewReload() {
//        ((MainActivity) mActivity).webView.reload();
    }


    @JavascriptInterface
    public void returnAppSchemeUrl(String strJsonObject) {
        GLog.d("getAppSchemeUrl - url : " + strJsonObject);
        String privateCertification = "";
        try {
            JSONObject jsonObject = new JSONObject(strJsonObject);
            String appSchemeUrl = jsonObject.getString("sign_aos_app_scheme_url");
            privateCertification = jsonObject.getString("PRIVATE_CERTIFICATION");
            Intent appSchemeIntent = Intent.parseUri(appSchemeUrl, Intent.URI_INTENT_SCHEME);
            mActivity.startActivity(appSchemeIntent);

            if (!jsonObject.isNull("callback")) {
                String callback = jsonObject.getString("callback");
                setCallbakckFuction(callback);
            }

        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            if (privateCertification.equals("01")) {
                //네이버
                intent.setData(Uri.parse("market://details?id=com.nhn.android.search"));
            } else if (privateCertification.equals("02")) {
                //국민은행
                intent.setData(Uri.parse("market://details?id=com.kbstar.kbbank"));
            } else {
                //신한은행
                intent.setData(Uri.parse("market://details?id=com.shinhan.sbanking"));
            }
            mActivity.startActivity(intent);
            GLog.d("ActivityNotFoundException : " + e.getMessage());
        } catch (Exception e) {
            GLog.d("Exception : " + e.getMessage());
        }
    }

    //다중 전송요구서 등록
    @JavascriptInterface
    public void makeSignMulti(String strJsonObject) {
        GLog.d("makeSign : " + strJsonObject);

        int index = 0;
        String passwd = "";
        int nOption = 0;
        String signData = "";
        int decodingOption = 0;
        String callback = "";

        try {
            if (!strJsonObject.equals("")) {
                JSONObject jsonObject = new JSONObject(strJsonObject);

                if (!jsonObject.isNull("iCertIndex")) {
                    index = jsonObject.getInt("iCertIndex");
                }

                if (!jsonObject.isNull("sPassword")) {
                    passwd = jsonObject.getString("sPassword");
                }

                if (!jsonObject.isNull("nOption")) {
                    nOption = jsonObject.getInt("nOption");
                }

                if (!jsonObject.isNull("nsSrcString")) {
                    signData = jsonObject.getString("nsSrcString");
                }

                if (!jsonObject.isNull("nDecodingOption")) {
                    decodingOption = jsonObject.getInt("nDecodingOption");
                }

                if (!jsonObject.isNull("callback")) {
                    callback = jsonObject.getString("callback");
                }

                setCallbakckFuction(callback);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            GLog.d("signData : " + signData);

            JSONArray jsonArr = new JSONArray(signData);
            GLog.d("jsonArr.length : " + jsonArr.length());

            JSONArray jsonSignDatas = new JSONArray();
            String[] arrPasswd;

            for (int i = 0; i < jsonArr.length(); i++) {
                arrPasswd = new String[jsonArr.length()];
                arrPasswd[i] = passwd;
                byte[] bytePasswd = arrPasswd[i].getBytes();

                String strMakeSign = pkiMakeSign(index, bytePasswd, jsonArr.get(i).toString());

                GLog.d("strMakeSign : " + strMakeSign);
                if (strMakeSign == null) {
                    JSONObject obj = new JSONObject();
                    JSONObject data = new JSONObject();
                    try {
                        obj.put("resultCode", "FAIL");
                        obj.put("data", data);
                    } catch (JSONException jsonExceptione) {
                        jsonExceptione.printStackTrace();
                    }

                    String result = obj.toString();
                    GLog.d("result : " + result);
                    if (result != null) {
                        callbackFunction(getCallbackFunction(), result);
                    }
                    return;
                }

                jsonSignDatas.put(i, strMakeSign);
            }

            JSONObject obj = new JSONObject();
            JSONObject data = new JSONObject();

            for (int i = 0; i < jsonSignDatas.length(); i++) {
                GLog.d("jsonSignDatas : " + jsonSignDatas.get(i).toString());
            }
            data.put("signData", jsonSignDatas);
            try {
                obj.put("resultCode", "SUCCESS");
                obj.put("data", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String result = obj.toString();

            if (result != null) {
                callbackFunction(getCallbackFunction(), result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public String pkiMakeSign(int index, byte[] passwd, String signData) { //index, password, 서명원문
        String encodeSignData = null;

        try {
            byte[] signedData = makeSignData(index, signData, passwd, iSignType);
            if (signedData == null) {
                return null;
            }

            MagicXSign magicXSign = null;
            magicXSign = new MagicXSign();
            encodeSignData = magicXSign.BASE64_Encode(signedData);
        } catch (Exception var8) {
            var8.printStackTrace();
            iLastError = 300;
            sLastError = "[PKI:Sign] 오류\n" + var8.getMessage();
            return null;
        }

//        JSONObject jsonObject = new JSONObject();
//
//        try {
//            jsonObject.put("sign", encodeSignData);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            iLastError = 300;
//            sLastError = "[PKI:SignV] 오류2\n" + e.getMessage();
//            return null;
//        }
//        return jsonObject.toString();
        return encodeSignData;
    }

    public byte[] makeSignData(int index, String signData, byte[] passwd, int signType) {
        MagicXSign magicXSign = null;
        magicXSign = new MagicXSign();
        boolean var6 = false;
        Object var7 = null;

        Object var8;
        try {
            byte[] var25;
            try {
                magicXSign.Init(mActivity, MagicXSign_Type.XSIGN_DEBUG_LEVEL_0);
                String mediaRootPath = Environment.getExternalStorageDirectory().getPath();
                magicXSign.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI | MagicXSign_Type.XSIGN_PKI_TYPE_GPKI | MagicXSign_Type.XSIGN_PKI_TYPE_PPKI | MagicXSign_Type.XSIGN_PKI_TYPE_MPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, mediaRootPath);
                int var24 = magicXSign.MEDIA_GetCertCount();
                if (index < var24) {
                    var25 = magicXSign.CMS_SignData(signType, index, passwd, signData.getBytes());
                    return var25;
                }

                iLastError = 310;
                sLastError = "[PKI] 인증서 Index 오류";
                var8 = null;
            } catch (MagicXSign_Exception e) {
                e.printStackTrace();
                System.out.println("- Err Code[" + e.getErrorCode() + "]");
                System.out.println("- Err Msg [" + e.getErrorMessage() + "]");
                if (e.getErrorCode() == 2004) {
                    iLastError = 330;
                    sLastError = "[PKI] 인증서 비밀번호 오류";
                } else {
                    iLastError = 340;
                    sLastError = "[PKI:" + e.getErrorCode() + "] 오류\n" + e.getErrorMessage();
                }

                var25 = null;
                return var25;
            } catch (Exception e) {
                e.printStackTrace();
                iLastError = 340;
                sLastError = "[PKI] 오류\n" + e.getMessage();
                var25 = null;
                return var25;
            }
        } finally {
            try {
                magicXSign.MEDIA_UnLoad();
                magicXSign.Finish();
            } catch (MagicXSign_Exception var20) {
                var20.printStackTrace();
            }
        }
        return (byte[]) var8;
    }

    //콜백 함수 호출 공통으로 사용
    public void callbackFunction(String callback, String result) {

        GLog.d("callback : " + callback);
        GLog.d("result : " + result);

        if (callback == null || callback.equals("")) {
            return;
        }

        try {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:" + callback + "('" + result + "')");
                }
            });

        } catch (NullPointerException ne) {
            ne.printStackTrace();
        }
    }

    private void initMagicMRS() {
        MagicMRSCallback magicMRSCallback = new MagicMRSCallback() {
            @Override
            public void MRSCallbackResult(int i, MagicMRSResult magicMRSResult, MRSCertificate mrsCertificate) {
                GLog.d("MRSCallbackResult");
                GLog.d("getErrorCode [" + magicMRSResult.getErrorCode() + "]");
                GLog.d("getErrorDescription [" + magicMRSResult.getErrorDescription() + "]");

                String type = null;
                if (i == MagicMRSConfig.MAGICMRS_TYPE_IMPORT_AUTHCODE || i == MagicMRSConfig.MAGICMRS_TYPE_IMPORT_QRCODE) {
                    type = "인증서 가져오기";
                } else if (i == MagicMRSConfig.MAGICMRS_TYPE_EXPORT) {
                    type = "인증서 내보내기";
                }

                if (magicMRSResult.getErrorCode() == 0) {
                    showToastMessage(type + "에 성공하였습니다.");
                    // QRCode로 인증서 가져오기 , AuthCode로 가져오기
                    if (i == MagicMRSConfig.MAGICMRS_TYPE_IMPORT_QRCODE || i == MagicMRSConfig.MAGICMRS_TYPE_IMPORT_AUTHCODE) {
                        // 인증서 저장
                        processCertificate.writeCert(mrsCertificate);
                    }

                    //결과값을 json 스트릥으로 변경
                    String result = makeJsonResult(true, i, magicMRSResult.getErrorCode());
                    callbackFunction(getCallbackFunction(), result);

                } else {

                    String result = makeJsonResult(false, i, magicMRSResult.getErrorCode());
                    callbackFunction(getCallbackFunction(), result);
                    showToastMessage(type + "에 실패하였습니다.(" + magicMRSResult.getErrorCode() + ")");

                }

                try {
                    if (mMagicMRS != null) {
                        mMagicMRS.finalizeMagicMRS();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        };

        mMagicMRS = new MagicMRS(mActivity, magicMRSCallback);
        //MRS 초기화
        mMagicMRS.initializeMagicMRS();

        String serverIp = "";
        String serverPort = "";

        serverIp = Constant.PDS_MRS_SERVER_IP;
        serverPort = Constant.PDS_MRS_SERVER_PORT;

        mMagicMRS.setURL(serverIp, serverPort);
    }

    private String makeJsonResult(boolean isSuccess, int type, int errorCode) {
        JSONObject obj = new JSONObject();
        JSONObject data = new JSONObject();
        String result = "";
        try {
            if (isSuccess) {
                if (type == MagicMRSConfig.MAGICMRS_TYPE_EXPORT) {
                    //인증서 내보내기 성공
                    obj.put("resultCode", "EXPORT");
                    GLog.d("makeJsonResult - authCode : " + getAuthCode());
                    if (!"".equals(getAuthCode())) {
                        data.put("issuNum", getAuthCode());
                    }
                } else {
                    //인증서 가져오기 성공
                    obj.put("resultCode", "SUCCESS");
                }
            } else {
                if (errorCode == 3001) {
                    //내보내기 시간 초과시
                    data.put("errorCode", errorCode);
                    obj.put("resultCode", "TIMEOUT");
                } else {
                    //내보내기 실패
                    data.put("errorCode", errorCode);
                    obj.put("resultCode", "FAIL");
                }
            }
            obj.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = obj.toString();
        return result;
    }

    private void showToastMessage(final String msg) {
        if (Constant.IS_DEBUG) {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void finalizeLibrary() {
        if (magicVKeypad != null) {
            if (magicVKeypad.isKeyboardOpen())
                magicVKeypad.closeKeypad();
            magicVKeypad.finalizeMagicVKeypad();
        }
    }
}
