package com.dki.hybridapptest.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dki.hybridapptest.Interface.ProgressBarListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.retrofit.RetrofitApiManager;
import com.dki.hybridapptest.retrofit.RetrofitInterface;
import com.dki.hybridapptest.ui.activity.bridge.AndroidBridge;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.dki.hybridapptest.utils.Utils;
import com.dreamsecurity.magicmrs.MRSCertificate;
import com.dreamsecurity.magicmrs.MagicMRS;
import com.dreamsecurity.magicmrs.MagicMRSCallback;
import com.dreamsecurity.magicmrs.MagicMRSResult;
import com.dreamsecurity.magicxsign.MagicXSign;
import com.dreamsecurity.magicxsign.MagicXSign_Type;
import com.dreamsecurity.xsignweb.XSignWebPlugin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import m.client.push.library.PushHandler;
import m.client.push.library.common.Logger;
import m.client.push.library.common.PushConstants;
import m.client.push.library.common.PushConstantsEx;
import m.client.push.library.utils.PushUtils;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    private WebSettings mWebSettings;
    private Intent mAction;
    private AndroidBridge androidBridge = null;
    private Handler handler = new Handler(Looper.getMainLooper());

    // 파일 업로드
    private ValueCallback mFilePathCallback;
    private String currentPhotoPath;
    private File photoFile = null;
    private String photoPath = null;
    private Uri photoURI;

    // 인증서
    private MagicMRS mMagicMRS = null;
    private Uri mCameraOutputFileUri = null;

    private void uploadImgFileFromCamera(Uri uri) {
        GLog.d("uploadImgFileFromCamera uri == " + uri);

        if (uri != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (mFilePathCallback != null) {
                    Uri[] uris = {Uri.parse(uri.toString())};
                    mFilePathCallback.onReceiveValue(uris);
                }
            } else {
                Intent data = new Intent();
                data.setData(mCameraOutputFileUri);
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(Activity.RESULT_OK, data));
                }
            }
        }
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                GLog.d("RESULT_OK");
                Intent intent = result.getData();

                if (intent == null) {
                    GLog.d("intent null");
                    uploadImgFileFromCamera(mCameraOutputFileUri);
//                    Uri[] results = {photoURI};
//                    GLog.d("results ==== " + results);
//                    if (mFilePathCallback != null) {
//                        GLog.d("mFilePathCallback not null ==== ");
//                        mFilePathCallback.onReceiveValue(results);
//                    }
                } else {
                    GLog.d("intent not null");
                    Uri photoValue = intent.getData();
                    if (photoValue != null) {
                        Uri[] uris = {photoValue};
                        if (mFilePathCallback != null) {
                            mFilePathCallback.onReceiveValue(uris);
                        }
                    }
//                    mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(result.getResultCode(), result.getData()));
                }
                mFilePathCallback = null;
            } else {
                //cancel 했을 경우
                GLog.d("RESULT_CANCELED");
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                    mFilePathCallback = null;
                }
            }
        }
    });

    // push
    private BroadcastReceiver mMainBroadcastReceiver;
    String token = null;

    // 프로그래스 바
    private RelativeLayout mProgressBar;

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        switch (requestCode) {
//            case FILECHOOSER_NORMAL_REQ_CODE:
//                if (resultCode == RESULT_OK) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
//                    } else {
//                        mFilePathCallback.onReceiveValue(new Uri[]{data.getData()});
//                    }
//                    mFilePathCallback = null;
//                } else {
//                    //cancel 했을 경우
//                    if (mFilePathCallback != null) {
//                        mFilePathCallback.onReceiveValue(null);
//                        mFilePathCallback = null;
//                    }
//                }
//                break;
//            default:
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    // 사진이 저장될 폴더
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Utils.makeDirectory(getApplicationContext());
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면 캡쳐 방지
        if (Constant.USE_SCREEN_SHOT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.webview);
        mProgressBar = findViewById(R.id.dialog_user_info_progressbar);
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);

        mWebView.clearCache(true);
        mWebView.clearHistory();

        // push 토큰 확인 (push 사용할 때 주석 풀기 (토큰 확인용))
        if (Constant.USE_PUSH_FIREBASE) {
            getFCMToken();
        }

        // 로그인 화면 프로그래스 바
        androidBridge = new AndroidBridge(mWebView, MainActivity.this, new ProgressBarListener() {
            @Override
            public void showProgressBar() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void unShownProgressBar() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
        mWebView.addJavascriptInterface(androidBridge, "DKITec");
        mWebView.loadUrl(Constant.WEB_VIEW_MAIN_URL);
        webPlugin_Init(MainActivity.this);

        // 파일 업로드 (카메라, 내 파일)
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                GLog.d();
                // 파일 선택시 초기화
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                // Check if the device has a camera feature
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    // 카메라 인텐트
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent != null) {
                        // 카메라 애 설치 여부 확인
                        takePictureIntent.resolveActivity(getApplicationContext().getPackageManager());
                        try {
                            photoFile = createImageFile();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            GLog.d("Build.VERSION.SDK_INT >= N ");
                            mCameraOutputFileUri = FileProvider.getUriForFile(MainActivity.this, MainActivity.this.getPackageName(), photoFile);
                        } else {
                            mCameraOutputFileUri = Uri.fromFile(photoFile);
                        }
//                        photoURI = FileProvider.getUriForFile(MainActivity.this,
//                                "com.dki.hybridapptest.provider",
//                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraOutputFileUri);
                        GLog.d("photoFile  === " + photoFile);
                    }

                    Intent chooserIntent;
                    // Check if there's a camera app available to handle this intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");  //모든 contentType 파일 표시
                        Intent[] initialIntents = new Intent[]{takePictureIntent};
//                        ArrayList<Intent> initialIntents = new ArrayList<>();
//                        intentArrayList.add(takePictureIntent);
                        chooserIntent = Intent.createChooser(intent, "골라주세요 제바알~~");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntents);

//                        startActivityForResult(chooserIntent, 100);
                        startActivityResult.launch(chooserIntent);
                    }
                }
                return true;
            }
        });

        RetrofitApiManager.getInstance().requestPostUser(new RetrofitInterface() {
            @Override
            public void onResponse(Response response) {
            }

            @Override
            public void onFailure(Throwable t) {
                GLog.d("오류 메세지 == " + t.toString());
            }
        });

        // webView 화면 (전화, e-mail, 외부 url 링크)
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (TextUtils.isEmpty(url)) {
                    GLog.d("url is null");
                } else {
                    // 전화
                    if (url.startsWith("tel:")) {
                        mAction = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                        startActivity(mAction);
                    }
                    // 이메일
                    else if (url.startsWith("mailto:")) {
                        mAction = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                        startActivity(mAction);
                    }
                    // 외부 url 링크
                    else if (url.startsWith("https:")) {
                        mAction = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(mAction);
                    }
                    // SMS 보내기
                    else if (url.startsWith("smsto:")) {
                        String phoneStr, msgStr;
                        Uri phone;
                        if (url.contains("?")) {
                            phoneStr = url.substring(0, url.indexOf("?"));
                            msgStr = url.substring(url.indexOf("?") + 1);

                            phone = Uri.parse(phoneStr);
                            String msg1;
                            try {
                                msg1 = URLDecoder.decode(msgStr, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }

                            GLog.d("phone == " + phone);
                            GLog.d("msg1 == " + msg1);

                            mAction = new Intent(Intent.ACTION_SENDTO, phone);
                            mAction.putExtra("sms_body", msg1);
                            startActivity(mAction);
                        } else {
                            Toast.makeText(MainActivity.this, "SMS 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                            GLog.d("SMS 형식이 아닙니다.");
                        }
                    }
                }
                return true;
            }
        });

        MagicMRSCallback callback = new MagicMRSCallback() {
            @Override
            public void MRSCallbackResult(int i, MagicMRSResult magicMRSResult, MRSCertificate mrsCertificate) {
                GLog.d("getErrorCode [" + magicMRSResult.getErrorCode() + "]");
                GLog.d("getErrorDescription [" + magicMRSResult.getErrorDescription() + "]");
                GLog.d();
            }
        };

        mMagicMRS = new MagicMRS(this, callback);
//        mMagicMRS.initializeMagicMRS();
//        mMagicMRS.setURL(/*MagicMRS Server IP*/, /*MagicMRS Server Port*/);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    // 웹 뷰에서 뒤로 가기
    @Override
    public void onBackPressed() {
        if (TextUtils.equals(mWebView.getUrl(), Constant.WEB_VIEW_MAIN_URL)) {
            super.onBackPressed();
        } else if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // push 토큰 확인
    private String getFCMToken() {
        GLog.d();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    GLog.w("Fetching FCM registration token failed ==== " + task.getException());
                    return;
                }
                token = task.getResult();
                GLog.d("FCM token is ==== " + token);
            }
        });
        return token;
    }

    // 리시버 등록
    public void registerReceiver() {
        if (mMainBroadcastReceiver != null) {
            return;
        }

        GLog.d("registerReceiver ===== ");

        // 화면에서 서비스 등록 결과를 받기 위한 리시버 등록 - 패키지명.ACTION_COMPLETED
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.this.getPackageName() + PushConstantsEx.ACTION_COMPLETED);

        mMainBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // 수신된 인텐트(결과값 응답) 의 정상 데이터 여부 체크 (푸시 타입/액션 타입) - 반드시 구현
                if (!PushUtils.checkValidationOfCompleted(intent, context)) {
                    return;
                }

                //push 타입 판단
                JSONObject result_obj = null; // 수신된 오브젝트
                String result_code = ""; // 결과 코드
                String result_msg = ""; // 결과 메세지
                try {
                    result_obj = new JSONObject(intent.getExtras().getString(PushConstants.KEY_RESULT));
                    result_code = result_obj.getString(PushConstants.KEY_RESULT_CODE);
                    result_msg = result_obj.getString(PushConstants.KEY_RESULT_MSG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 액션 타입
                String bundle = intent.getStringExtra(PushConstantsEx.KEY_BUNDLE);

                //액션에 따라 분기 (이미 서비스 등록이 완료된 상태인 경우 다음 process 이동)
                if (bundle.equals(PushConstantsEx.COMPLETE_BUNDLE.IS_REGISTERED_SERVICE)) {
                    //mProgressDialog.dismiss();
                    Logger.e(result_code);
                    String resultPushType = intent.getExtras().getString(PushConstants.KEY_PUSH_TYPE);
                    if (resultPushType.equals(PushHandler.getInstance().getPushConfigInfo(getApplicationContext()).getPushType())) {
                        if (result_code.equals(PushConstants.RESULTCODE_OK)) {
                            mWebView.loadUrl(Constant.WEB_VIEW_LOGIN_URL);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, result_msg, Toast.LENGTH_LONG).show();
                    }

                    // 최초 서비스 등록이 완료된 경우 다음 process 이동
                } else if (bundle.equals(PushConstantsEx.COMPLETE_BUNDLE.REG_PUSHSERVICE)) {

                    // 등록 성공
                    if (result_code.equals(PushConstants.RESULTCODE_OK)) {
                        Toast.makeText(context, "등록 성공!", Toast.LENGTH_SHORT).show();
                        mWebView.loadUrl(Constant.WEB_VIEW_LOGIN_URL);
                    }
                    // 통신 에러
                    else if (result_code.equals(PushConstants.RESULTCODE_HTTP_ERR)) {
                        Toast.makeText(context, "[MainActivity] error code: " + result_code + " msg: " + result_msg, Toast.LENGTH_SHORT).show();
                    }
                    // 인증키 오류
                    else if (result_code.equals(PushConstants.RESULTCODE_AUTHKEY_ERR)) {
                        Toast.makeText(context, "[MainActivity] error code: " + result_code + " msg: " + result_msg, Toast.LENGTH_SHORT).show();
                    }
                    // 서버 응답 에러
                    else if (result_code.equals(PushConstants.RESULTCODE_RESPONSE_ERR)) {
                        Toast.makeText(context, "[MainActivity] error code: " + result_code + " msg: " + result_msg, Toast.LENGTH_SHORT).show();
                    }
                    // 라이브러리 에러 - 파싱 에러
                    else if (result_code.equals(PushConstants.RESULTCODE_INTERNAL_ERR)) {
                        Toast.makeText(context, "[MainActivity] error code: " + result_code + " msg: " + result_msg, Toast.LENGTH_SHORT).show();
                    }
                    // 기타
                    else {
                        Toast.makeText(context, "[MainActivity] error code: " + result_code + " msg: " + result_msg, Toast.LENGTH_SHORT).show();
                        //System.exit(0);
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMainBroadcastReceiver, intentFilter);
    }

    // 리시버 해제
    public void unregisterReceiver() {
        if (mMainBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMainBroadcastReceiver);
            mMainBroadcastReceiver = null;
        }
    }

    // web 플러그 인 초기화
    public void webPlugin_Init(Context c) {
        GLog.d("잘 들어왔습니다. =========" + c);
        if (Constant.USE_XSIGN_DREAM || Constant.USE_XSIGN_PLUGIN_DREAM) {
            int SDK_INT = Build.VERSION.SDK_INT;
            try {
                MagicXSign xSign = null;
                XSignWebPlugin xWeb = null;

                xWeb = new XSignWebPlugin(this, this, mWebView);
                xSign = new MagicXSign();

                xSign.Init(this, MagicXSign_Type.XSIGN_DEBUG_LEVEL_1);
                String mediaRootPath = Environment.getExternalStorageDirectory().getPath();
                xSign.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI | MagicXSign_Type.XSIGN_PKI_TYPE_GPKI | MagicXSign_Type.XSIGN_PKI_TYPE_PPKI | MagicXSign_Type.XSIGN_PKI_TYPE_MPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, mediaRootPath);

                MagicXSign pki = new MagicXSign();

                if (xWeb.setStorage(MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_DISK) == MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_DISK)
                    System.out.println("----- DB 사용 -----");

                try {
                    pki.Init(MainActivity.this, MagicXSign_Type.XSIGN_DEBUG_LEVEL_0);
                    int count = -1;
                    pki.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER,
                            MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_TYPE_ALL, "/sdcard");
                    count = pki.MEDIA_GetCertCount();
                    GLog.d("count : " + count);

                    int nMediaType[] = new int[1];
                    for (int i = 0; i < count; i++) {
                        byte[] signCert = pki.MEDIA_ReadCert(i, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, nMediaType);
                        byte[] signPri = pki.MEDIA_ReadPriKey(i, MagicXSign_Type.XSIGN_PKI_CERT_SIGN);
                        pki.MEDIA_WriteCertAndPriKey(Base64.decode(signCert, Base64.DEFAULT), Base64.decode(signPri, Base64.DEFAULT), MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_DISK);
                    }
                    pki.MEDIA_UnLoad();
                } catch (Exception e) {
                    GLog.d("webPlugin_Init ====== " + e);
                }
            } catch (Exception e) {
                GLog.d("webPlugin_Init ====== " + e);
            }
        }
    }
}