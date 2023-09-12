package com.dki.hybridapptest.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dki.hybridapptest.Interface.CustomDialogClickListener;
import com.dki.hybridapptest.Interface.IsHeaderVisibleListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dialog.CustomDialog;
import com.dki.hybridapptest.dialog.InputDialog;
import com.dki.hybridapptest.dto.LoginResponse;
import com.dki.hybridapptest.retrofit.RetrofitApiManager;
import com.dki.hybridapptest.retrofit.RetrofitInterface;
import com.dki.hybridapptest.ui.activity.bridge.AndroidBridge;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.dki.hybridapptest.utils.SharedPreferencesAPI;
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
import com.google.android.material.navigation.NavigationView;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private WebView mWebView;
    private WebSettings mWebSettings;
    private Intent mIntent;
    private AndroidBridge androidBridge = null;
    private Handler handler = new Handler(Looper.getMainLooper());

    // 파일 업로드
    private ValueCallback mFilePathCallback;
    private String currentPhotoPath;
    private File photoFile = null;
    private String photoPath = null;

    // 인증서
    private MagicMRS mMagicMRS = null;
    private Uri mCameraOutputFileUri = null;

    // 로그인
    private String id;
    private String pwd;
    private boolean isLoginCheck;

    // 타이틀 UI
    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView mTitle;

    // 햄버거 메뉴
    private NavigationView navigationView;
    private InputDialog inputDialog;
    private String url;

    // 화면 크기
    private DisplayMetrics displayMetrics;
    private boolean isDisplaySizeMode = true;

    // push
    private BroadcastReceiver mMainBroadcastReceiver;
    String token = null;

    // 프로그래스 바
    private RelativeLayout mProgressBar;

    // 업로드 카메라 이미지 파일
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
                } else {
                    GLog.d("intent not null");
                    Uri photoValue = intent.getData();
                    if (photoValue != null) {
                        Uri[] uris = {photoValue};
                        if (mFilePathCallback != null) {
                            mFilePathCallback.onReceiveValue(uris);
                        }
                    }
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu:
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
                break;
            case android.R.id.home: // 타이틀 back 버튼
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (mWebView.canGoBack()) {
                    mWebView.goBack();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
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
        toolbar = findViewById(R.id.toolbar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        drawerLayout = findViewById(R.id.drawer);

        id = SharedPreferencesAPI.getInstance(MainActivity.this).getLoginId();
        pwd = SharedPreferencesAPI.getInstance(MainActivity.this).getLoginPw();
        isLoginCheck = SharedPreferencesAPI.getInstance(this).getAutoLogin();
//        url = SharedPreferencesAPI.getInstance(MainActivity.this).getUrl();

        GLog.d("isDisplaySizeMode === " + isDisplaySizeMode);
//        changeDisplaySize(isDisplaySizeMode);

        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);

        // 타이틀 UI displayHeader값 들어오기 전 초기화
        titleBarInit();

        // 네비게이션 뷰 이벤트 받기 위한 리스너
        navigationView = (NavigationView) findViewById(R.id.nv_main_navigation_root);
        navigationView.setNavigationItemSelectedListener(this);

        androidBridge = new AndroidBridge(mWebView, MainActivity.this, new IsHeaderVisibleListener() {
            @Override
            public void isVisible(boolean isHeaderVisible, String title) {
                if (isHeaderVisible) {
                    toolbar.setVisibility(View.VISIBLE);
                    setSupportActionBar(toolbar);
                    actionBar = getSupportActionBar();
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setDisplayShowTitleEnabled(false); // 커스텀 타이틀을 사용하면 기본 타이틀은 사용하지 말아야한다.

                    // 뒤로가기 버튼 이미지 불러오기
                    actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_aos_new_24);

                    // 타이틀 UI 제목
                    String toolBarTitle;
                    if (title != null) {
                        toolBarTitle = title;
                    } else {
                        toolBarTitle = "제목";
                    }

                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mTitle.setText(toolBarTitle);
                    GLog.d("toolbar VISIBLE =====");
                } else {
                    toolbar.setVisibility(View.GONE);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    GLog.d("toolbar GONE =====");
                }
            }
        });

        mWebView.clearCache(true);
        mWebView.clearHistory();

        // push 토큰 확인 (push 사용할 때 주석 풀기 (토큰 확인용))
        if (Constant.USE_PUSH_FIREBASE) {
            getFCMToken();
        }

        // 로그인 화면 프로그래스 바 (로딩)
//        androidBridge = new AndroidBridge(mWebView, MainActivity.this, new ProgressBarListener() {
//            @Override
//            public void showProgressBar() {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mProgressBar.setVisibility(View.VISIBLE);
//                    }
//                });
//            }
//
//            @Override
//            public void unShownProgressBar() {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mProgressBar.setVisibility(View.GONE);
//                    }
//                });
//            }
//        });

        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebView.addJavascriptInterface(androidBridge, "DKITec");
        loginCheck(""); // 자동 로그인 체크 여부 화면 이동
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

        // webView 화면 (전화, e-mail, 외부 url 링크)
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            // webView 에러
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                GLog.d();
                super.onReceivedError(view, request, error);
                GLog.d("error====" + error.getErrorCode());
                switch (error.getErrorCode()) {
                    case ERROR_AUTHENTICATION: // 서버에서 사용자 인증 실패
                    case ERROR_BAD_URL: // 잘못된 URL
                    case ERROR_CONNECT: // 서버로 연결 실패
                    case ERROR_FAILED_SSL_HANDSHAKE: // SSL handshake 수행 실패
                    case ERROR_FILE: // 일반 파일 오류
                    case ERROR_FILE_NOT_FOUND: // 파일을 찾을 수 없습니다
                    case ERROR_HOST_LOOKUP: // 서버 또는 프록시 호스트 이름 조회 실패
                    case ERROR_IO: // 서버에서 읽거나 서버로 쓰기 실패
                    case ERROR_PROXY_AUTHENTICATION: // 프록시에서 사용자 인증 실패
                    case ERROR_REDIRECT_LOOP: // 너무 많은 리디렉션
                    case ERROR_TIMEOUT: // 연결 시간 초과
                    case ERROR_TOO_MANY_REQUESTS: // 페이지 로드중 너무 많은 요청 발생
                    case ERROR_UNKNOWN: // 일반 오류
                    case ERROR_UNSUPPORTED_AUTH_SCHEME: // 지원되지 않는 인증 체계
                    case ERROR_UNSUPPORTED_SCHEME:
                        view.loadUrl("about:blank"); // 빈페이지 출력
                        CustomDialog customDialog = new CustomDialog(MainActivity.this, new CustomDialogClickListener() {
                            @Override
                            public void onPositiveClick(String text, boolean value) {
                                // 확인시 클릭 이벤트
                                finish();
                            }

                            @Override
                            public void onNegativeClick() {

                            }
                        }, "에러", "네트워크 상태가 원활하지 않습니다. 어플을 종료합니다.", Constant.ONE_BUTTON, true);
                        customDialog.setCancelable(false);
                        customDialog.show();
                        customDialog.setOneButtonText("확인");
                }
            }

            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (TextUtils.isEmpty(url)) {
                    GLog.d("url is null");
                } else {
                    // 전화
                    if (url.startsWith("tel:")) {
                        mIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                        startActivity(mIntent);
                    }
                    // 이메일
                    else if (url.startsWith("mailto:")) {
                        mIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                        startActivity(mIntent);
                    }
                    // 외부 url 링크
                    else if (url.startsWith("https:")) {
                        mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(mIntent);
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

                            mIntent = new Intent(Intent.ACTION_SENDTO, phone);
                            mIntent.putExtra("sms_body", msg1);
                            startActivity(mIntent);
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
    }

    // url 검색 화면 크기 조절
    public void changeDisplaySize(boolean isfullMode) {
        GLog.d();
        displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
//        String url = SharedPreferencesAPI.getInstance(MainActivity.this).getUrl();
//        boolean isfullMdoe = SharedPreferencesAPI.getInstance(MainActivity.this).getDisplayFullMode();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        GLog.d("width === " + displayMetrics.widthPixels);
        GLog.d("height === " + displayMetrics.heightPixels);
        int width;
        int height;

        if (!isfullMode) {
            GLog.d("isfullMode false");
            width = (int) (displayMetrics.widthPixels * 0.9);
            height = (int) (displayMetrics.heightPixels * 0.9);
//            width = (int) (displayMetrics.widthPixels * 0.9);
//            height = (int) (displayMetrics.heightPixels * 0.9);
            getWindow().getAttributes().height = height;
            getWindow().getAttributes().width = width;
            GLog.d("width === " + width);
            GLog.d("height === " + height);
            getWindow().setAttributes(getWindow().getAttributes());
        } else {
            GLog.d("isfullMode true");
            width = (int) displayMetrics.widthPixels;
            height = (int) displayMetrics.heightPixels;
//            width = (int) (displayMetrics.widthPixels * 1.0);
//            height = (int) (displayMetrics.heightPixels * 1.1);
            getWindow().getAttributes().height = height;
            getWindow().getAttributes().width = width;
            GLog.d("width === " + width);
            GLog.d("height === " + height);
            getWindow().setAttributes(getWindow().getAttributes());
        }
    }

    // 자동 로그인 체크 여부 화면 이동
    public void loginCheck(String url) {
//        changeDisplaySize(isDisplaySizeMode);
        if (isLoginCheck) { // 자동 로그인 true - 메인 화면으로 이동
            RetrofitApiManager.getInstance().requestPostLogin(id, pwd, new RetrofitInterface() {
                @Override
                public void onResponse(Response response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse loginResponse = (LoginResponse) response.body();
//                            GLog.d("data == " + loginResponse.getData());
//                            GLog.d("resultCode == " + loginResponse.getResultCode());
//                            GLog.d("isLoginCheck == " + loginResponse.getData().getIsLoginCheck());

                            // 자동 로그인 여부에 따른 웹뷰 화면
                            if (loginResponse.getData().getIsLoginCheck()) {
                                if (!TextUtils.isEmpty(url)) {
                                    mWebView.loadUrl(url);
                                    GLog.d("url 화면");
                                } else {
                                    mWebView.loadUrl(Constant.WEB_VIEW_MAIN_URL);
                                    GLog.d("메인 화면");
                                }
                            } else {
                                if (!TextUtils.isEmpty(url)) {
                                    mWebView.loadUrl(url);
                                    GLog.d("url 화면");
                                } else {
                                    GLog.d("로그인 화면");
                                    mWebView.loadUrl(Constant.WEB_VIEW_LOGIN_URL);
                                }
                            }

                        } else {
                            GLog.d("오류 메세지 == " + response.errorBody().toString());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    GLog.d("오류 메세지 == " + t.toString());
                }
            });
        } else { // 자동 로그인 false - 로그인 화면으로 이동
            if (!TextUtils.isEmpty(url)) {
                mWebView.loadUrl(url);
                GLog.d("url 화면");
            } else {
                mWebView.loadUrl(Constant.WEB_VIEW_LOGIN_URL);
                GLog.d("메인 화면");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GLog.d();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GLog.d();
        unregisterReceiver();
    }

    // 웹 뷰에서 뒤로 가기
    @Override
    public void onBackPressed() {
        // 햄버거 메뉴 뒤로 가기
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
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

    // 타이틀 UI 초기화
    public void titleBarInit() {
        GLog.d();
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false); // 커스텀 타이틀을 사용하면 기본 타이틀은 사용하지 말아야한다.

        // 뒤로가기 버튼 이미지 불러오기
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_aos_new_24);
        String toolBarTitle = "HybridApp";
        mTitle.setText(toolBarTitle);
    }

    // web 플러그 인 초기화
    public void webPlugin_Init(Context c) {
        GLog.d();
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

    // 햄버거 메뉴 목록
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        // 홈 버튼
        if (id == R.id.go_to_home) {
            // 홈 버튼 누를 시 url, isfullmode 초기화
            changeDisplaySize(true);
            mWebView.loadUrl(Constant.WEB_VIEW_MAIN_URL);
            Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.END);
        }
        // url 입력
        else if (id == R.id.insert_url) {
            inputDialog = new InputDialog(this, new CustomDialogClickListener() {
                @Override
                public void onPositiveClick(String text, boolean isFullMode) {
                    if (TextUtils.isEmpty(text)) {
                        Toast.makeText(MainActivity.this, "url을 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                GLog.d("isfullmode ==== " + isFullMode);
                                isDisplaySizeMode = isFullMode;
                                changeDisplaySize(isDisplaySizeMode);
                                SharedPreferencesAPI.getInstance(MainActivity.this).setUrl(text);
                                mWebView.loadUrl(text);
                                drawerLayout.closeDrawer(GravityCompat.END);
                            }
                        });
                    }
                }

                @Override
                public void onNegativeClick() {
                }
            });
            inputDialog.show();
        }
        // 종료
        else if (id == R.id.quit) {
            CustomDialog customDialog = new CustomDialog(MainActivity.this, new CustomDialogClickListener() {
                @Override
                public void onPositiveClick(String text, boolean value) {
                    ActivityCompat.finishAffinity(MainActivity.this);
                }

                @Override
                public void onNegativeClick() {
                    Toast.makeText(MainActivity.this, "취소", Toast.LENGTH_SHORT).show();
                }
            }, "안내", getResources().getString(R.string.close_app_message), Constant.TWO_BUTTON, true);
            customDialog.setCancelable(false);
            customDialog.show();
            customDialog.setTwoButtonText("취소", "종료");
        }
        return true;
    }
}