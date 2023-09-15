package com.dki.hybridapptest.utils;

public class Constant {

    /********************* 라이브러리  ************************/
    //true: 디버그 false: 배포버전
    public static boolean IS_DEBUG = true;

    //true: 앱 위변조 false: 위변조 사용 불가  (배포 버전(디버그모드가 아닐 떄)일 때 위변조 사용, 디버그 모드일 때 사용불가능)
    public static boolean USE_TRUST_APP_DREAM = !IS_DEBUG;

    //true: 인증서  false: 인증서 관련 기능 사용 불가
    public static boolean USE_XSIGN_DREAM = true;

    //true: 웹 플러그인  false: 웹 플러그인 기능 사용 불가
    public static boolean USE_XSIGN_PLUGIN_DREAM = true;

    //true: 보안 키보드 false: 보안 키보드 사용 불가
    public static boolean USE_MAGIC_KEYPAD_DREAM = true;

    //true: 백신 false: 백신 기능 사용 불가
    public static boolean USE_VACCINE_DREAM = false;

    //true: push fasle: push 기능 사용 불가
    public static boolean USE_PUSH_FIREBASE = true;

    //true: 화면 캡쳐 fasle: 화면 캡쳐 기능 사용 불가
    public static boolean USE_SCREEN_SHOT = false;

    //true: 프로그래스 바 fasle: 프로그래스 바 사용 불가
    public static boolean USE_PROGRESS_BAR = false;

    /********************* 웹 URL ************************/
    // 메인 서버 주소
    public static final String WEB_VIEW_MAIN_URL = "http://10.112.58.208/";

    // sample.html 주소
//    public static final String WEB_VIEW_MAIN_URL = "file:///android_asset/sample.html";

    // MagicVKeypad_Sample.html 주소
    public static final String WEB_MAGIC_KEYPAD_URL = "file:///android_asset/MagicVKeypad_Sample.html?option=";

    // 파일 업로드 주소
    public static final String WEB_FILE_UPLOAD_URL = "http://10.112.58.208/upload";

    // 인증서 주소
    public static final String WEB_SIGN_DISITAL_URL = "file:///android_asset/index.html";

    // 로그인 주소
//    public static final String WEB_VIEW_MAIN_URL = "http://10.112.58.208/login";
    public static final String WEB_VIEW_LOGIN_URL = "http://10.112.58.208/login";

    //    public static String BASE_URL = "https://devpds.mydatacenter.or.kr:9443";
    public static String BASE_URL = "https://pds.mydatacenter.or.kr:9443";
    public static String TUTORIAL_URL = "/membership/identification.do";
    public static String HOST_URL = "/auth/index.do";

    //kfido 주소
//    public static String KPIDO_URL = "https://devfidosvc.mydatacenter.or.kr:9443";
    public static String KPIDO_URL = "https://fidosvc.mydatacenter.or.kr:9443";
    public static String REQ_URL = "/processUafRequest.jspx?site=";
    public static String RES_URL = "/processUafResponse.jspx?site=";

    // 인증서 가져오기 내보내기 IP, PORT 설정
//    public static String PDS_MRS_SERVER_IP = "https://devmrs.mydatacenter.or.kr";
    public static String PDS_MRS_SERVER_IP = "https://mrs.mydatacenter.or.kr";
    public static String PDS_MRS_SERVER_PORT = "25050";

    //Trust 앱 주소
//    public static String SERVER_URL_VERITY = "https://devpds.mydatacenter.or.kr:9443/trustapp_svr/trustapp_server_demo.jsp";
    public static String SERVER_URL_VERITY = "https://pds.mydatacenter.or.kr:9443/trustapp_svr/trustapp_server_demo.jsp";

    // 테스트 주소
    public static String TEST_URL_NAVER = "https://m.naver.com/";
    public static String TEST_URL_GOOGLE = "https://www.google.com/";

    /*********************  API URL ************************/
    // user List 주소
    public static final String USERS_INFO_URL = "https://reqres.in/api/";

    // user login 주소
    public static final String USERS_LOGIN_CHECK_URL = "http://10.112.58.208/pilot/api/";

    //푸쉬 서버 주소
    public static String PUSH_SERVER_URL = "https://pushapi.mydatacenter.or.kr:9443";


    /********************* 프리퍼런스 키값 ************************/
    //앱 처음 실행시 튜토리얼 실행
    public static String TUTORIAL_KEY = "TUTORIAL";

    //앱 최초 실행
    public static String FIRST_LAUNCH = "FIRST_LAUNCH";

    //패턴 선보이기
    public static String PATTERN_INVISIBLE = "PATTERN_INVISIBLE";

    //인증방식
    public static String CERT_TYPE = "CERT_TYPE";

    //폰트사이즈
    public static String FONT_SIZE = "FONT_SIZE";

    //URL 주소
    public static String SELECT_URL = "SELECT_URL";


    /********************* 여러 키 값 ************************/
    // Push 키 값
    public static final String KEY_CUID = "CUID";
    public static final String KEY_CNAME = "CNAME";
    public static final int NOTIFICATION_ID_FOR_PEDOMETER = 1000;

    // 다이얼로그 키 값
    public static String TWO_BUTTON = "TWO_BUTTON";
    public static String ONE_BUTTON = "ONE_BUTTON";

    //Kfido 인증방식
    public static String PATTERN = "PATTERN";
    public static String FINGER = "FINGER";


    /********************* 파일 이름 ************************/
    public static String FILE_CACHE_DIRECTORY_NAME = "pickImg";


    /********************* 코드 번호 ************************/
    public static int PDF_ENC_VIEWER_REQUEST_CODE = 10001;
}
