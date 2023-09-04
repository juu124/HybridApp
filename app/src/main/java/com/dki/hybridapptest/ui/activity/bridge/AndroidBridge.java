package com.dki.hybridapptest.ui.activity.bridge;

import static com.dream.magic.fido.authenticator.common.asm.authinfo.ASMInstallAuth.byteArrayToHex;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.dki.hybridapptest.BuildConfig;
import com.dki.hybridapptest.Interface.CustomDialogClickListener;
import com.dki.hybridapptest.Interface.ProgressBarListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dialog.CustomDialog;
import com.dki.hybridapptest.dialog.InputDialog;
import com.dki.hybridapptest.dto.SMSInfo;
import com.dki.hybridapptest.kfido.FIDORegistration;
import com.dki.hybridapptest.model.ContactInfo;
import com.dki.hybridapptest.ui.activity.EncryptionActivity;
import com.dki.hybridapptest.ui.activity.HelloWorldActivity;
import com.dki.hybridapptest.ui.activity.HybridModeActivity;
import com.dki.hybridapptest.ui.activity.MoveWebViewActivity;
import com.dki.hybridapptest.ui.activity.UserListActivity;
import com.dki.hybridapptest.ui.activity.XSignMainActivity;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.DeviceInfo;
import com.dki.hybridapptest.utils.GLog;
import com.dki.hybridapptest.utils.HybridResult;
import com.dki.hybridapptest.utils.MagicVKeyPadSettings;
import com.dki.hybridapptest.utils.ProcessCertificate;
import com.dki.hybridapptest.utils.SharedPreferencesAPI;
import com.dki.hybridapptest.utils.Utils;
import com.dki.hybridapptest.utils.WorkThread;
import com.dream.magic.fido.rpsdk.client.LOCAL_AUTH_TYPE;
import com.dream.magic.fido.rpsdk.client.MagicFIDOUtil;
import com.dream.magic.fido.uaf.protocol.kfido.KCertificate;
import com.dreamsecurity.magicmrs.MRSCertificate;
import com.dreamsecurity.magicmrs.MagicMRS;
import com.dreamsecurity.magicmrs.MagicMRSCallback;
import com.dreamsecurity.magicmrs.MagicMRSResult;
import com.dreamsecurity.magicmrs.etc.MagicMRSConfig;
import com.dreamsecurity.magicvkeypad.MagicVKeypad;
import com.dreamsecurity.magicvkeypad.MagicVKeypadOnClickInterface;
import com.dreamsecurity.magicvkeypad.MagicVKeypadResult;
import com.dreamsecurity.magicvkeypad.MagicVKeypadType;
import com.dreamsecurity.magicxsign.MagicXSign;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

import m.client.push.library.PushManager;
import m.client.push.library.common.PushConstants;
import m.client.push.library.common.PushLog;
import m.client.push.library.utils.PushUtils;

public class AndroidBridge {
    private WebView mWebView;
    private Activity mActivity;
    private Intent mIntent;
    private Handler handler = new Handler(Looper.getMainLooper());

    private String mCallbackFuntion = "";
    private String mAuthCode = "";
    private String mUserKey = "";
    private String mIssuedn = ""; //발급자
    private String mSubjdn = "";  //주체자
    private String mSn = "";      //sn
    private String mUcpidData = ""; //UCPID 원문

    // FIDO 인증
    private FIDORegistration reg = null;

    private final byte[] tobeData1 = "Hello".getBytes();

    // 패턴 선 숨기기
    private boolean mIsInvisible;
    private Hashtable<String, Object> patternOption = null;

    // 인증서
    private KCertificate kCertificate = null;
    private MagicFIDOUtil magicFIDOUtil = null;
    private MagicXSign pki;
    private ProcessCertificate processCertificate = null;
    //    private JSONArray arrSignText;
    private MagicMRS mMagicMRS = null;

    // show Dialog 버튼 이벤트
    private InputDialog inputDialog;

    // 보안 키패드
    private String RSAEncryptData = "";
    private String charResultData = "";
    private String AESEncData = "";
    private String AESDecData = "";
    private MagicVKeypad magicVKeypad;
    private String strViewMode;
    private String strfieldID;

    // 웹뷰 move
    private String url;
    private boolean fullMode = true;

    // 자동 로그인
    private ProgressBarListener progressBarListener;

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

    public AndroidBridge() {
    }

    // AndroidBride 기본 생성자
    public AndroidBridge(WebView webView, Activity activity) {
        this.mWebView = webView;
        this.mActivity = activity;

        if (Constant.USE_XSIGN_DREAM) {
            init();  // 인증서 관련 처리
        }
    }

    // 생성자 (프로그래스 바 리스너)
    public AndroidBridge(WebView webView, Activity activity, ProgressBarListener progressBarListener) {
        this.mWebView = webView;
        this.mActivity = activity;
        this.progressBarListener = progressBarListener;

        if (Constant.USE_XSIGN_DREAM) {
            init();  // 인증서 관련 처리
        }
    }

    // 인증서 관련 처리
    private void init() {
        processCertificate = new ProcessCertificate(mActivity);
        pki = new MagicXSign();

        kCertificate = new KCertificate();
        MagicFIDOUtil.setSSLEnable(false);

        magicFIDOUtil = new MagicFIDOUtil(mActivity);
        patternOption = new Hashtable<String, Object>();
    }

    public String getUcpidData() {
        return mUcpidData;
    }

    // 콜백 함수 호출 공통으로 사용
    public void callbackFunction(String callback, String result) {
        GLog.d("callback : " + callback);
        GLog.d("result : " + result);

        // 콜백 값 null 체크
        if (TextUtils.isEmpty(callback)) {
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

    // 인증서 전자서명
    @JavascriptInterface
    public void importCert(String strJsonObject) {
        GLog.d();

        initMagicMRS();

        JSONObject jsonObject = null;
        String authCode = "";
        String callback = "";
        try {
            if (!TextUtils.isEmpty(strJsonObject)) {
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

    private void showToast(final String msg) {
        if (Constant.IS_DEBUG) {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
                }
            });
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
//                    showToast(type + "에 성공하였습니다.");
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
                    showToast(type + "에 실패하였습니다.(" + magicMRSResult.getErrorCode() + ")");
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

    // 구간 암호화
    @JavascriptInterface
    public void useEncryption() {
        GLog.d();
        mIntent = new Intent(mActivity, EncryptionActivity.class);
        mActivity.startActivity(mIntent);
    }

    // 파일 업로드
    @JavascriptInterface
    public void fileUpload() {
        GLog.d();
        handler.post(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(Constant.WEB_FILE_UPLOAD_URL);
            }
        });
    }

//    // SMS 보내기
//    @JavascriptInterface
//    public void sendSMS(String phoneNumbers, String shareMsg) {
//        GLog.d("브릿지::: jsSendSMS(" + phoneNumbers + "," + shareMsg + ")");
//        if (!(TextUtils.isEmpty(phoneNumbers) && TextUtils.isEmpty(shareMsg))) {
//            Uri phone = Uri.parse("sms:" + phoneNumbers);
//            Intent sendToSMS = new Intent(Intent.ACTION_SENDTO, phone);
//            sendToSMS.putExtra("sms_body", shareMsg);
//            mActivity.startActivity(sendToSMS);
//        }
//    }

//    // SMS 보내기
//    @JavascriptInterface
//    public void sendSMS(String callback, ArrayList<String> phoneNumbers, String message) {
//        GLog.d("브릿지::: jsSendSMS(" + callback + "," + phoneNumbers + ", " + message + ")");
////        if (!(TextUtils.isEmpty(phoneNumbers) && TextUtils.isEmpty(shareMsg))) {
////            Uri phone = Uri.parse("sms:" + phoneNumbers);
////            Intent sendToSMS = new Intent(Intent.ACTION_SENDTO, phone);
////            sendToSMS.putExtra("sms_body", shareMsg);
////            mActivity.startActivity(sendToSMS);
////        }
//    }

    // SMS 보내기
    @JavascriptInterface
    public void sendToSMS(String strJsonObject) {
        JSONObject callBackJsonObj = new JSONObject();
        JSONObject smsJsonObj = null;
        SMSInfo smsInfo = null;
        try {
            if (!TextUtils.isEmpty(strJsonObject)) {
                smsJsonObj = new JSONObject(strJsonObject);
                if (!smsJsonObj.isNull("callback")) {
                    smsInfo = new SMSInfo();
                    smsInfo.setCallback(smsJsonObj.getString("callback"));
                    smsInfo.setMessage(smsJsonObj.getString("message"));

                    JSONArray jsonArray = new JSONArray(smsJsonObj.getString("phoneNumbers"));
                    ArrayList<String> arrayList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        GLog.d("phoneNumbers [" + i + "] == " + jsonArray.get(i).toString());
                        arrayList.add(jsonArray.get(i).toString());
                    }
                    smsInfo.setPhoneNumbers(arrayList.toString());
                    // 번호가 여러개 일때
                    if (smsInfo.getPhoneNumbers().startsWith("[") && smsInfo.getPhoneNumbers().endsWith("]")) {
                        String phoneNumbers = smsInfo.getPhoneNumbers().substring(1, smsInfo.getPhoneNumbers().length() - 1);
                        smsInfo.setPhoneNumbers(phoneNumbers);
                        GLog.d("phoneNumbers = " + phoneNumbers);
                    }
                    GLog.d("phoneNumber == " + smsInfo.getPhoneNumbers());

                    Uri phone = Uri.parse("sms:" + smsInfo.getPhoneNumbers());
                    Intent sendToSMS = new Intent(Intent.ACTION_SENDTO, phone);
                    sendToSMS.putExtra("sms_body", smsInfo.getMessage());
                    mActivity.startActivity(sendToSMS);

                    // todo :: 콜백 선언하기
//                    if () {
//                        callBackjsonObj.put("resultCode", "SUCCESS");
//                    } else {
//                        callBackjsonObj.put("resultCode", "FAIL");
//                    }
                    callBackJsonObj.put("resultCode", "SUCCESS");
                }
//                else {
//                    callBackjsonObj.put("resultCode", "FAIL");
//                }
                callbackFunction(smsJsonObj.toString(), callBackJsonObj.toString());
            }
        } catch (Exception e) {

        }
    }

    // 화면 캡처
    @JavascriptInterface
    public void captureScreen() {
        GLog.d();
        boolean result = Utils.screenCapture(mWebView.getRootView());

        if (result) {
            Toast.makeText(mActivity, "캡처완료", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mActivity, "캡처실패", Toast.LENGTH_SHORT).show();
        }
    }

    // 파일 다운로드
    @JavascriptInterface
    public void fileDownload(String strJsonObject) {
        GLog.d("fileDownload====== " + strJsonObject);
        handler.post(new Runnable() {
            @Override
            public void run() {
                String url = "";
                String fileName = "";
                JSONObject jsonObj = null;
                try {
                    if (!TextUtils.isEmpty(strJsonObject)) {
                        jsonObj = new JSONObject(strJsonObject);
                        if (!jsonObj.isNull("url")) {
                            url = jsonObj.getString("url");
                        }
                        if (!jsonObj.isNull("fileName")) {
                            fileName = jsonObj.getString("fileName");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                GLog.d("fileDownload====== url : " + url);
                Utils.fileDownload(mActivity, url, fileName);
            }
        });
    }

    // 자동 로그인 해제
    @JavascriptInterface
    public void autoLoginOff() {
        Toast.makeText(mActivity, "자동 로그인 해제", Toast.LENGTH_SHORT).show();
        SharedPreferencesAPI.getInstance(mActivity).setAutoLogin(false);
        SharedPreferencesAPI.getInstance(mActivity).setLoginId("");
        SharedPreferencesAPI.getInstance(mActivity).setLoginPw("");
    }

    // 로그인 저장
    @JavascriptInterface
    public void setLoginInfo(String strJsonObject) {
        GLog.d();
        String callback = "";
        JSONObject jsonObj = null;
        try {
            if (!TextUtils.isEmpty(strJsonObject)) {
                GLog.d("로그인 정보 ===== " + strJsonObject);
                jsonObj = new JSONObject(strJsonObject);
                SharedPreferencesAPI.getInstance(mActivity).setLoginId(jsonObj.getString("id"));
                SharedPreferencesAPI.getInstance(mActivity).setLoginPw(jsonObj.getString("pwd"));
                SharedPreferencesAPI.getInstance(mActivity).setAutoLogin(jsonObj.getBoolean("isAutoLogin"));
                Toast.makeText(mActivity, "로그인 성공", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 프로그래스바 리스너 show/unshown
    private void showProgressBarListener(boolean isShown) {
        if (progressBarListener == null) {
            return;
        }
        if (isShown) {
            progressBarListener.showProgressBar();
        } else {
            progressBarListener.unShownProgressBar();
        }
    }

    // 로그인 페이지 접속 (서버 생성 전에 사용)
//    @JavascriptInterface
//    public void moveLoginPage() {
//        GLog.d("SharedPreferencesAPI.getInstance(mActivity).getAutoLogin() == " + SharedPreferencesAPI.getInstance(mActivity).getAutoLogin());
//
//        // 로그인 화면으로 이동
//        loadWebView(true, Constant.WEB_VIEW_LOGIN_URL, 0);
//
//        if (SharedPreferencesAPI.getInstance(mActivity).getAutoLogin() &&
//                TextUtils.equals(Constant.LOGIN_ID, SharedPreferencesAPI.getInstance(mActivity).getLoginId()) &&
//                TextUtils.equals(Constant.LOGIN_PW, SharedPreferencesAPI.getInstance(mActivity).getLoginPw())) {
//            loadWebView(false, Constant.WEB_VIEW_MAIN_URL, 500); // 메인 화면 이동 및 프로그래스 바 노출
//            Toast.makeText(mActivity, "자동로그인", Toast.LENGTH_SHORT).show();
//        } else {
//            loadWebView(false, "", 500); // 프로그래스 바 비노출
//        }
//    }

    // 로그인 (서버 생생 전에 사용)
//    @JavascriptInterface
//    public void login(String id, String pwd, boolean isAutoLogin) {
//        loadWebView(true, "", 0); // 프로그래스 바 노출
//        GLog.d("name === " + id + "\n hash ==== " + pwd + " \n check ====== " + isAutoLogin);
//
//        // id가 null 일 때
//        if (TextUtils.isEmpty(id)) {
//            Toast.makeText(mActivity, "Username을 입력하세요.", Toast.LENGTH_SHORT).show();
//        }
//        // pw가 null 일 때
//        else if (TextUtils.isEmpty(pwd)) {
//            Toast.makeText(mActivity, "Password를 입력하세요.", Toast.LENGTH_SHORT).show();
//        }
//        // id가 똑같지 않을 때
//        else if (!TextUtils.equals(Constant.LOGIN_ID, id)) {
//            Toast.makeText(mActivity, "Username을 다시 입력하세요.", Toast.LENGTH_SHORT).show();
//        }
//        // PW가 똑같지 않을 때 때
//        else if (!TextUtils.equals(Constant.LOGIN_PW, pwd)) {
//            Toast.makeText(mActivity, "Password를 다시 입력하세요.", Toast.LENGTH_SHORT).show();
//        } else {
////            JSONObject jsonObject = new JSONObject();
////
////            JSONObject data = new JSONObject();
////            data.put("id", id);
////            data.put("pwd", pwd);
////            data.put("isAutoLogin", isAutoLogin);
////
////            JSONArray req_array = new JSONArray();
////            req_array.put(data);
////
////            jsonObject.put("REQ_DATA", req_array);
////            ArrayList<UserAccount> userAccounts = new ArrayList<>();
////            for (:
////                 ) {
////
////            }
////            userAccounts.get(0).setId(id);
//
//            // 입력한 id, pw 일치 했을 시 SharedPreferences에 저장
//            SharedPreferencesAPI.getInstance(mActivity).setLoginId(id);
//            SharedPreferencesAPI.getInstance(mActivity).setLoginPw(pwd);
//            SharedPreferencesAPI.getInstance(mActivity).setAutoLogin(isAutoLogin);
//            Toast.makeText(mActivity, "로그인 성공", Toast.LENGTH_SHORT).show();
//
////
////
////            // 자동 로그인 체크 저장
////            if (isAutoLogin) {
////                SharedPreferencesAPI.getInstance(mActivity).setAutoLogin(isAutoLogin);
////            }
//
//            // push 사용자 등록
//            initPush(id, pwd);
//
//            // 메인 화면 이동 및 프로그래스 바 노출
//            loadWebView(false, Constant.WEB_VIEW_MAIN_URL, 500);
//        }
//        loadWebView(false, "", 500); // 프로그래스 바 비노출
//    }

    // webView 이동 및 프로그래스 바 노출여부
    private void loadWebView(boolean showProgressBarListener, String url, int delayTime) {
        mWebView.postDelayed(new Runnable() {
            @Override
            public void run() {
                showProgressBarListener(showProgressBarListener);  // 프로그래스 바 비노출
                if (!TextUtils.isEmpty(url)) {
                    mWebView.loadUrl(url);
                }
            }
        }, delayTime);
    }

    // Push - id와 pw로 User 등록
    private void initPush(String id, String pw) {
        GLog.d();
        final JSONObject params = new JSONObject();
        try {
            params.put(PushConstants.KEY_CUID, id);
            params.put(PushConstants.KEY_CNAME, pw);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (PushUtils.checkNetwork(mActivity)) {
            GLog.d();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    GLog.d("push checkNetWork true");
                    PushManager.getInstance().registerServiceAndUser(mActivity, params);
//                    GLog.d("push checkNetWork true ==== " + PushManager.getInstance().getPushJsonData());
                }
            });
        } else {
            PushLog.e("MainActivity", "network is not connected.");
            Toast.makeText(mActivity, "[MainActivity] network is not connected.", Toast.LENGTH_SHORT).show();
        }
    }

    // Push - 서비스 등록 해제
    @JavascriptInterface
    public void loginPushOff() {
        if (PushUtils.checkNetwork(mActivity)) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    GLog.d("푸시 서비스 등록 해제 메서드 들어옴");
                    // 푸시 서비스 등록 해제 - 서비스 및 사용자 해제
                    PushManager.getInstance().unregisterPushService(mActivity);
                }
            });
        } else {
            PushLog.e("MainActivity", "network is not connected.");
            Toast.makeText(mActivity, "[MainActivity] network is not connected.", Toast.LENGTH_SHORT).show();
        }
    }

    // 연락처 조회
    @JavascriptInterface
    public void searchPhoneAddress(String strJsonObject) {
        String callback = "";
        JSONObject jsonObj = null;
        try {
            if (!TextUtils.isEmpty(strJsonObject)) {
                jsonObj = new JSONObject(strJsonObject);
                if (!jsonObj.isNull("callback")) {
                    callback = jsonObj.getString("callback");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String finalCallback = callback;
        WorkThread.execute(() -> {
            try {
                final String[] INITIAL_SOUND = {"ㄱ", "ㄴ", "ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅅ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ", "#",
                        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};//,
//                        "ក" ,"ខ" ,"គ" ,"ឃ" ,"ង" ,"ច" ,"ឆ" ,"ជ" ,"ឈ" ,"ញ" ,"ដ" ,"ឋ" ,"ឌ" ,"ឍ" ,"ណ" ,"ត" ,"ថ" ,"ទ" ,"ធ" ,"ន" ,"ប" ,"ផ" ,"ព" ,"ភ" ,"ម" ,"យ" ,"រ" ,"ល" ,"វ" ,"ស" ,"ហ" ,"ឡ" ,"អ"};

                JSONObject jsonObject = new JSONObject();
                ArrayList<ContactInfo> contactInfos = getContactList();
                GLog.d("contactInfos === " + contactInfos.get(0));
                JSONObject initialJson = new JSONObject();
                int idx = 1;
                for (int i = 0; i < INITIAL_SOUND.length; i++) {
                    JSONArray jsonArray = new JSONArray();
                    for (ContactInfo contactInfo : contactInfos) {
                        if (INITIAL_SOUND[i].equals(contactInfo.initial)) {
                            JSONObject contact = new JSONObject();
                            contact.put("contactSn", idx++);
                            contact.put("name", Utils.encodeBase64String(contactInfo.displayName));
                            String phoneNumber = contactInfo.phoneNumber;
                            phoneNumber = phoneNumber.replaceAll("-", "");
                            contact.put("phone", Utils.encodeBase64String(phoneNumber));
                            jsonArray.put(contact);
                        }
                    }
                    if (jsonArray.length() > 0) {
                        if ("#".equals(INITIAL_SOUND[i])) {
                            initialJson.put("others", jsonArray);
                        } else {
                            initialJson.put(INITIAL_SOUND[i], jsonArray);
                        }
                    }
                }

                jsonObject.put("contact", initialJson);

                String resultValue = jsonObject.toString();
                GLog.d("resultValue : " + resultValue);
                callbackFunction(finalCallback, resultValue);
                evaluate("callbackSearchContact('" + resultValue + "')", null);
            } catch (Exception ex) {
                GLog.e("에러 === " + ex);
            }
        });
    }

    // 연락처 얻기
    ArrayList<ContactInfo> getContactList() {
        ArrayList<ContactInfo> dataList = new ArrayList<>();
        ContentResolver contentResolver = mActivity.getContentResolver();

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
        };

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        GLog.d(" sortOrder ==== " + sortOrder);

        Cursor cursor = contentResolver.query(uri, projection, null, null, sortOrder);

        if (cursor.moveToFirst()) {
            GLog.d();
            do {
                String displayName = cursor.getString(2);
                String phoneNumber = cursor.getString(3);
                ContactInfo info = new ContactInfo(
                        cursor.getLong(0),
                        cursor.getLong(1),
                        displayName,
                        phoneNumber
                );
                dataList.add(info);

            } while (cursor.moveToNext());
        }
        GLog.d();
        return Utils.distinct(dataList);
    }

    private void evaluate(String javascript, @Nullable ValueCallback<String> resultCallback) {
        mWebView.post(() -> {
                    mWebView.evaluateJavascript("javascript:" + javascript, s -> {
                        if (resultCallback != null) {
                            resultCallback.onReceiveValue(s);
                        }
                    });
                    GLog.d("브릿지::: " + javascript);
                }
        );
    }

    // 앱 스토어 이동하기
    @JavascriptInterface
    public void moveAppStore() {
        mIntent = new Intent(Intent.ACTION_VIEW);
        String packageName = "kr.or.mydatacenter.pds";
        PackageManager packageManager = mActivity.getPackageManager();

        try {
            // 앱이 설치되어 있는 경우
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            mIntent = packageManager.getLaunchIntentForPackage(packageName);
            GLog.d("설치 된 상태 ");

        } catch (Exception e) {
            // 설치가 안되어 있는 경우
            GLog.d("설치 안된 상태");
            mIntent.setData(Uri.parse("market://details?id=" + "kr.or.mydatacenter.pds")); // 예시로 신정원 앱 'MyPDS(나의금융생활)' 패키지 명으로 지정했음.
//            mIntent.setData(Uri.parse("market://details?id=" + getPackageName()));   // 앱이 등록이 되어있는 상태라면 정상적인 화면이 나올것.
        }
        mActivity.startActivity(mIntent);
    }

    @JavascriptInterface
    public void closeApp() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // 작업 처리
                CustomDialog customDialog = new CustomDialog(mActivity, new CustomDialogClickListener() {
                    @Override
                    public void onPositiveClick(String text) {
                        ActivityCompat.finishAffinity(mActivity);
                    }

                    @Override
                    public void onNegativeClick() {
                        Toast.makeText(mActivity, "취소", Toast.LENGTH_SHORT).show();
                    }
                }, "안내", mActivity.getResources().getString(R.string.close_app_message), Constant.TWO_BUTTON, true);
                customDialog.setCancelable(false);
                customDialog.show();
                customDialog.setTwoButtonText("취소", "종료");

//                Display display = mActivity.getWindowManager().getDefaultDisplay();
//                Point size = new Point();
//                display.getSize(size);
//
//                Window window = customDialog.getWindow();
//
//                int x = (int) (size.x * 0.9f);
//                int y = (int) (size.y * 0.2f);
//
//                window.setLayout(x, y);
            }
        });
    }

    // SW 정보 표시
    @JavascriptInterface
    public void showSWInfo(String strJsonObject) {
        GLog.d("getDeviceInfo - strJsonObject : " + strJsonObject);

        String deviceModel = DeviceInfo.getDeviceModel();  // 디바이스 모델명
        String deviceOs = DeviceInfo.getDeviceOs();  // 디바이스 OS명
        JSONObject jsonObject = null;
        String callback = "";
        try {
            if (!TextUtils.isEmpty(strJsonObject)) {
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

        SharedPreferences prefs = mActivity.getSharedPreferences(SharedPreferencesAPI.SP_NAME, Context.MODE_PRIVATE);
        boolean isInvisiblePattern = prefs.getBoolean(Constant.PATTERN_INVISIBLE, false);  // 패턴
        boolean isFirstLaunch = prefs.getBoolean(Constant.FIRST_LAUNCH, true);  // 최초 실행 여부

        try {
            // 어플 최초 실행함
            if (isFirstLaunch) {
                data.put("faceid", "N");
                if (isAvailableFingerPrint) {
                    // 지문 인식 가능
                    data.put("fingerprint", "Y");
                } else {
                    // 지문 인식 가능하지 않음
                    data.put("fingerprint", "N");
                }

                data.put("appVersion", BuildConfig.VERSION_NAME);
                data.put("isInvisiblePattern", isInvisiblePattern);
                data.put("firstLaunch", isFirstLaunch);
                data.put("deviceModel", deviceModel);
                data.put("deviceOs", deviceOs);
                obj.put("resultCode", "SUCCESS");
                obj.put("data", data);
                // 앱 최초 실행하지 않음
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(Constant.FIRST_LAUNCH, false);
                editor.apply();
            } else {
                // 어플 최초 실행하지 않음
                data.put("faceid", "N");
                if (isAvailableFingerPrint) {
                    // 지문 인식 가능
                    data.put("fingerprint", "Y");
                } else {
                    // 지문 인식 가능하지 않음
                    data.put("fingerprint", "N");
                }

                data.put("appVersion", BuildConfig.VERSION_NAME);
                data.put("isInvisiblePattern", isInvisiblePattern);
                data.put("firstLaunch", isFirstLaunch);
                data.put("deviceModel", deviceModel);
                data.put("deviceOs", deviceOs);
                obj.put("resultCode", "SUCCESS");
                obj.put("data", data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                // 어플 최초 실행함
                if (isFirstLaunch) {
                    data.put("faceid", "N");
                    if (isAvailableFingerPrint) {
                        // 지문 인식 가능
                        data.put("fingerprint", "Y");
                    } else {
                        // 지문 인식 가능하지 않음
                        data.put("fingerprint", "N");
                    }

                    data.put("appVersion", BuildConfig.VERSION_NAME);
                    data.put("isInvisiblePattern", isInvisiblePattern);
                    data.put("firstLaunch", isFirstLaunch);
                    data.put("deviceModel", deviceModel);
                    data.put("deviceOs", deviceOs);
                    obj.put("resultCode", "SUCCESS");
                    obj.put("data", data);

                    // 앱 최초 실행하지 않음
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(Constant.FIRST_LAUNCH, false);
                    editor.apply();
                } else {
                    data.put("faceid", "N");
                    if (isAvailableFingerPrint) {
                        // 지문 인식 가능
                        data.put("fingerprint", "Y");
                    } else {
                        // 지문 인식 가능하지 않음
                        data.put("fingerprint", "N");
                    }

                    data.put("appVersion", BuildConfig.VERSION_NAME);
                    data.put("isInvisiblePattern", isInvisiblePattern);
                    data.put("firstLaunch", isFirstLaunch);
                    data.put("deviceModel", deviceModel);
                    data.put("deviceOs", deviceOs);
                    obj.put("resultCode", "SUCCESS");
                    obj.put("data", data);
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        // 콜백 함수
        callbackFunction(callback, obj.toString());
        Toast.makeText(mActivity, "sw 정보 표시됨", Toast.LENGTH_SHORT).show();
    }

    // 웹뷰 페이지 이동
    @JavascriptInterface
    public void moveWebView() {
        mIntent = new Intent(mActivity, MoveWebViewActivity.class);
        mActivity.startActivity(mIntent);
    }

    // HybridModeActivity 보안 키보드 버튼
    @JavascriptInterface
    public void showKeyboard() {
        mIntent = new Intent(mActivity, HybridModeActivity.class);
        mActivity.startActivity(mIntent);
    }

    // Say hello, hello, world 버튼 이벤트
    @JavascriptInterface
    public void onClickButton(String word) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl("javascript:nativeToWeb()");
            }
        });
        // hello 버튼일 때
        if (TextUtils.equals(word, mActivity.getString(R.string.hello))) {
            mIntent = new Intent(mActivity, HelloWorldActivity.class);
            mActivity.startActivity(mIntent);
        }
        // world 버튼일 떄
        else if (TextUtils.equals(word, mActivity.getString(R.string.world))) {
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
    public void showInputDialog() {
        inputDialog = new InputDialog(mActivity, new CustomDialogClickListener() {
            @Override
            public void onPositiveClick(String text) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:nativeToWebWithMsg('" + text + "')");
                    }
                });
            }

            @Override
            public void onNegativeClick() {
            }
        });
        inputDialog.show();
    }

    // show User Information 선택
    @JavascriptInterface
    public void showUserList() {
        mIntent = new Intent(mActivity, UserListActivity.class);
        mActivity.startActivity(mIntent);
    }

    // show User Information Certification 선택
    @JavascriptInterface
    public void showCertificationList() {
        mIntent = new Intent(mActivity, XSignMainActivity.class);
        mActivity.startActivity(mIntent);
    }

    // 외부 브라우저 호출
    @JavascriptInterface
    public void openBrowser(String strJsonObject) {
        JSONObject jsonObject = null;
        String mUrl;
        try {
            if (!TextUtils.isEmpty(strJsonObject)) {
                jsonObject = new JSONObject(strJsonObject);
                mUrl = jsonObject.getString("url");

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
                mActivity.startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    boolean bUseDummyData = false;

    // 보안 키패드 설정
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

            // 풀모드(CHAR, NUM) - 현재 라이브러리 문제로 작동 되지 않음
            if (TextUtils.equals(viewMode, "FULL_TYPE")) {
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
            } else if (TextUtils.equals(viewMode, "HALF_TYPE")) {
                if (TextUtils.equals(keypadType, "CHAR_TYPE")) {  // 하프모드 - 문자키패드
                    if (magicVKeypad.isKeyboardOpen()) {
                        magicVKeypad.closeKeypad();
                    }
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

                    magicVKeypad.startNumKeypad(mOnClickInterface);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void licenseAuth(MagicVKeypad magicVKeypad) {
        this.magicVKeypad = magicVKeypad;
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
                if (!MagicVKeyPadSettings.bUseE2E) {
                    decData = magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData());
                }

                if (magicVKeypad.getEncryptData() != null) {
                    if (magicVKeypadResult.getEncryptData() != null) {
                        RSAEncryptData = new String(magicVKeypadResult.getEncryptData());
                    }
                }

                if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_OK_BUTTON) { // 확인
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
                    } else {
                        strScript += strfieldID + "','" + "" + "')";
                        mWebView.loadUrl(strScript);
                    }
                }
            }
            // 하프모드
            else {
                GLog.d("하프모드 ");
                if (!MagicVKeyPadSettings.bUseE2E) {
                    decData = magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData());
                    if (decData != null) {
                        AESDecData = byteArrayToHex(magicVKeypadResult.getEncryptData());
                        AESEncData = new String(decData);
                    }
                }

                if (magicVKeypad.getEncryptData() != null) {
                    RSAEncryptData = new String(magicVKeypad.getEncryptData());
                }

                if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_CANCEL_BUTTON) { // 취소
                    magicVKeypad.closeKeypad();
                    strScript += strViewMode + "','" + strfieldID + "','" + "" + "')";
                    HybridResult.strScript = strScript;
                    mWebView.loadUrl(strScript);
                } else if (magicVKeypadResult.getButtonType() == MagicVKeypadType.MAGICVKEYPAD_TYPE_OK_BUTTON) { // 확인
                    if (magicVKeypadResult.getEncryptData() != null) {
                        if (MagicVKeyPadSettings.bUseE2E == false) {
                            GLog.d("확인 AESEncData ==== " + AESEncData);
                        } else {
                            RSAEncryptData = new String(magicVKeypad.getEncryptData());
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