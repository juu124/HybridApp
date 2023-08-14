package com.dki.hybridapptest.utils;

public class Constant {

    //true: 디버그 false : 배포버전
    public static boolean IS_DEBUG = true;

    //    public static String BASE_URL ="https://devpds.mydatacenter.or.kr:9443";
    public static String BASE_URL = "https://pds.mydatacenter.or.kr:9443";
    public static String TUTORIAL_URL = "/membership/identification.do";
    public static String HOST_URL = "/auth/index.do";

    //kfido url
//    public static String KPIDO_URL = "https://devfidosvc.mydatacenter.or.kr:9443";
    public static String KPIDO_URL = "https://fidosvc.mydatacenter.or.kr:9443";
    public static String REQ_URL = "/processUafRequest.jspx?site=";
    public static String RES_URL = "/processUafResponse.jspx?site=";
    //Kfido 인증방식
    public static String PATTERN = "PATTERN";
    public static String FINGER = "FINGER";

    // 인증서 가져오기 내보내기 IP, PORT 설정
//    public static String PDS_MRS_SERVER_IP = "https://devmrs.mydatacenter.or.kr";
    public static String PDS_MRS_SERVER_IP = "https://mrs.mydatacenter.or.kr";
    public static String PDS_MRS_SERVER_PORT = "25050";

    //푸쉬 서버 주소
    public static String PUSH_SERVER_URL = "https://pushapi.mydatacenter.or.kr:9443";

    //Trust 앱 url
//    public static String SERVER_URL_VERITY = "https://devpds.mydatacenter.or.kr:9443/trustapp_svr/trustapp_server_demo.jsp";
    public static String SERVER_URL_VERITY = "https://pds.mydatacenter.or.kr:9443/trustapp_svr/trustapp_server_demo.jsp";

    /********************* 프리퍼런스 키값 시작 ************************/
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
    /********************* 프리퍼런스 키값 끝 ************************/

    public static int PDF_ENC_VIEWER_REQUEST_CODE = 10001;

    // 로그인
    public static final String WEB_VIEW_LOGIN_URL = "file:///android_asset/autologin.html";
    public static final String LOGIN_ID = "1234";
    public static final String LOGIN_PW = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";

    // sample.html 주소
    public static final String WEB_VIEW_MAIN_URL = "file:///android_asset/sample.html";

    // web server 주소
//    public static final String WEB_VIEW_MAIN_URL = "http://10.112.58.208";

    // user List 주소
    public static final String USERS_INFO_URL = "https://reqres.in/api/";

    // Push Key 값
    public static final String KEY_CUID = "CUID";
    public static final String KEY_CNAME = "CNAME";

    // 파일 다운로드 url
    public static final String FILE_DOWNLOAD_URL = "https://filesamples.com/samples/document/ppt/sample2.ppt";

    // 다이얼로그
    public static String TWO_BUTTON = "TWO_BUTTON";
    public static String ONE_BUTTON = "ONE_BUTTON";

}
