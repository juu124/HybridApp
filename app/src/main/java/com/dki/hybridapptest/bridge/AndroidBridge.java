package com.dki.hybridapptest.bridge;

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
import com.dki.hybridapptest.activities.HelloWorldActivity;
import com.dki.hybridapptest.activities.HybridModeActivity;
import com.dki.hybridapptest.activities.MoveWebViewActivity;
import com.dki.hybridapptest.activities.UserCertificationActivity;
import com.dki.hybridapptest.activities.UserListActivity;
import com.dki.hybridapptest.dialog.CustomYesNoDialog;
import com.dki.hybridapptest.dialog.InputDialog;
import com.dki.hybridapptest.encryption.EncryptionActivity;
import com.dki.hybridapptest.kfido.FIDORegistration;
import com.dki.hybridapptest.model.ContactInfo;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.DeviceInfo;
import com.dki.hybridapptest.utils.GLog;
import com.dki.hybridapptest.utils.HybridResult;
import com.dki.hybridapptest.utils.MagicVKeyPadSettings;
import com.dki.hybridapptest.utils.PreferenceManager;
import com.dki.hybridapptest.utils.ProcessCertificate;
import com.dki.hybridapptest.utils.SharedPreferencesAPI;
import com.dki.hybridapptest.utils.Utils;
import com.dki.hybridapptest.utils.WorkThread;
import com.dream.magic.fido.rpsdk.client.LOCAL_AUTH_TYPE;
import com.dream.magic.fido.rpsdk.client.MagicFIDOUtil;
import com.dream.magic.fido.rpsdk.util.KFidoUtil;
import com.dream.magic.fido.uaf.protocol.kfido.KCertificate;
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
//    private String color = "";
//    private FIDOAuthentication mAuth = null;
//    private FIDODeRegistration mDeregist = null;

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

    public AndroidBridge() {
    }

    // AndroidBride 기본 생성자
    public AndroidBridge(WebView webView, Activity activity) {
        if (webView != null) {
            this.mWebView = webView;
            if (activity != null) {
                this.mActivity = activity;
            } else {
                GLog.d("activity null");
            }
        } else {
            GLog.d("webView null");
        }

        //인증서 관련 처리
        processCertificate = new ProcessCertificate(mActivity);
        pki = new MagicXSign();

        kCertificate = new KCertificate();
        MagicFIDOUtil.setSSLEnable(false);

        magicFIDOUtil = new MagicFIDOUtil(mActivity);
        patternOption = new Hashtable<String, Object>();
    }

    // 생성자 (프로그래스 바 리스너)
    public AndroidBridge(WebView webView, Activity activity, ProgressBarListener progressBarListener) {
        if (webView != null) {
            this.mWebView = webView;
            if (activity != null) {
                this.mActivity = activity;
            } else {
                GLog.d("activity null");
            }
        } else {
            GLog.d("webView null");
        }
        GLog.d("progressBarListener ==== " + progressBarListener);
        if (progressBarListener != null) {
            this.progressBarListener = progressBarListener;
        } else {
            GLog.d("progressBarListener null");
        }

        //인증서 관련 처리
        processCertificate = new ProcessCertificate(mActivity);
        pki = new MagicXSign();

        kCertificate = new KCertificate();
        MagicFIDOUtil.setSSLEnable(false);

        magicFIDOUtil = new MagicFIDOUtil(mActivity);
        patternOption = new Hashtable<String, Object>();
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

    // 자동 로그인 해제
    @JavascriptInterface
    public void autoLoginOff() {
        Toast.makeText(mActivity, "자동 로그인 해제", Toast.LENGTH_SHORT).show();
        SharedPreferencesAPI.getInstance(mActivity).setAutoLogin(false);
    }

    // 로그인 페이지 접속
    @JavascriptInterface
    public void moveLoginPage() {
        GLog.d("SharedPreferencesAPI.getInstance(mActivity).getAutoLogin() == " + SharedPreferencesAPI.getInstance(mActivity).getAutoLogin());
        if (SharedPreferencesAPI.getInstance(mActivity).getAutoLogin()) {
            GLog.d("체크 : true");
            Toast.makeText(mActivity, "자동로그인", Toast.LENGTH_SHORT).show();
        } else {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl(Constant.WEB_VIEW_LOGIN_URL);
                }
            });
        }
    }

    // 로그인
    @JavascriptInterface
    public void login(String id, String pw, String check) {
        // 자동 로그인 체크 저장
        if (!TextUtils.isEmpty(check)) {
            SharedPreferencesAPI.getInstance(mActivity).setAutoLogin(Boolean.parseBoolean(check));
        }

        progressBarListener.showProgressBar();   // 프로그래스 바 노출

        GLog.d("name === " + id + "\n hash ==== " + pw + " \n check ====== " + check);

        if (TextUtils.equals(id, SharedPreferencesAPI.getInstance(mActivity).getLoginId()) &&
                TextUtils.equals(pw, SharedPreferencesAPI.getInstance(mActivity).getLoginPw())) {
            // id, pw 둘다 맞았을 때
            Toast.makeText(mActivity, "로그인 성공", Toast.LENGTH_SHORT).show();
            mWebView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBarListener.unShownProgressBar();  // 프로그래스 바 비노출
                    mWebView.loadUrl(Constant.WEB_VIEW_MAIN_URL);
                }
            }, 500);
        } else {
            // 로그인 실패
            progressBarListener.unShownProgressBar();
            Toast.makeText(mActivity, "없는 계정 입니다.", Toast.LENGTH_SHORT).show();
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
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // 작업 처리
                CustomYesNoDialog customDialog = new CustomYesNoDialog(mActivity, new CustomDialogClickListener() {
                    @Override
                    public void onPositiveClick(String text) {
                        ActivityCompat.finishAffinity(mActivity);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                }, "안내", mActivity.getResources().getString(R.string.close_app_message), true);
                customDialog.setCancelable(false);
                customDialog.show();

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

        SharedPreferences prefs = mActivity.getSharedPreferences(PreferenceManager.PREFERENCES_NAME, Context.MODE_PRIVATE);
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


    // 앱 위변조 인듯 (주석 잘 못 표기)
    @JavascriptInterface
    public void showEncryption() {
        mIntent = new Intent(mActivity, EncryptionActivity.class);
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
    public void showDialog() {
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
        mIntent = new Intent(mActivity, UserCertificationActivity.class);
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

    //설정 페이지 호출
    @JavascriptInterface
    public void callSettings(String strJsonObject) {
        GLog.d("callSettings - strJsonObject : " + strJsonObject);

        JSONObject jsonObject = null;
        String setType = "";
        String callback = "";
        String setValue = "";

        try {
            if (!TextUtils.isEmpty(strJsonObject)) {
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

                if (!TextUtils.isEmpty(setValue)) {
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
//    @JavascriptInterface
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

                // 전달 받은 dn과 값은 값을 가지고 있는 지 확인
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
        GLog.d("============" + successLicense);
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
                if (!MagicVKeyPadSettings.bUseE2E)
                    decData = magicVKeypad.getDecryptData(magicVKeypadResult.getEncryptData());
                if (magicVKeypad.getEncryptData() != null) {
                    if (magicVKeypadResult.getEncryptData() != null)
                        RSAEncryptData = new String(magicVKeypadResult.getEncryptData());
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