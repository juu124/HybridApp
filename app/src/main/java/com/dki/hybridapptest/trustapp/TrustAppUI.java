package com.dki.hybridapptest.trustapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dreamsecurity.trustapp.TrustAppToolkit;

import java.util.Timer;
import java.util.TimerTask;

public class TrustAppUI {
    public static String PACKAGE_LIC = "";
    // /////////////////////////////////////////////
    // TrustAppToolkit Assign START
    // /////////////////////////////////////////////
    static TrustAppToolkit aap = new TrustAppToolkit();
    // /////////////////////////////////////////////
    // Assign END
    // /////////////////////////////////////////////
    static Context _context = null;
    static Activity activity = null;
    //    static com.example.trustappandroid.TrustAppUI popup;
    static String strError = "";
    static String strErrorMessage = "";
    protected static String strTokenB64;
    protected static String strClientValue;

    public static ProgressDialog mPgDialog;

    public interface OnAAPlusFinish {
        void onAAPlusFinish(boolean result, String strToken, String strClientValue, String strError);
    }

    private static OnAAPlusFinish myCallback = null;

    public static void setFinishCallback(OnAAPlusFinish cb) {
        myCallback = cb;
    }

    public static String getToken() {
        return aap.getToken();
    }

    public static String getClientValue() {
        return aap.getClientValue();
    }

    public TrustAppUI(Activity context) {
//		super(context);
        activity = context;
    }

    public static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            mPgDialog.dismiss();
            if (msg.what == 1) {
                myCallback.onAAPlusFinish(true, strTokenB64, strClientValue, null);
            }
            if (msg.what == 0) {
                myCallback.onAAPlusFinish(false, null, null, strErrorMessage);
            }

        }
    };


    @SuppressWarnings("unchecked")
    /*
     * 앱 위변조 검증을 위해 토큰 발행 및 클라이언트 검증값 생성을 한다.
     *
     * 로그인에 넣을 경우의 예시
     * 1. 서명 완료 후 로그인 수행 전에 이 함수를 호출한다.
     * 2. 로그인에 사용할 값들(업무와 연동)은 HashMap에 넣어서 전달한다.
     * 3. HashMap에 있는 값들은 실제 로그인을 수행하는 StartAAPlus_Login 에서 전달하도록 한다.
     * 4. StartAAPlus_Login 은 고객업무에 따라 바뀔 수 있다.
     *
     * context : 화면을 띄우기 위해 현재 화면의 context를 준다.
     * strAppName : 어플리케이션 이름, DeviceID 정보를 만들때 이용하므로 구분할 수 있는 프로그램명을 준다.
     * strDisplayString : 위변조 검증 중 보여줄 STRING (null 이면 "어플리케이션 위변조 검증중입니다.")
     * tran_req_data : 로그인 시 전달되는 데이터들
     */
    public static void StartTrustApp_Start(
            final Context context,
            final String strAppName,
            final String strDisplayString) {

        _context = context;

        // debug 출력 : 미출력은 주석처리
        aap.setDebugMode(true);

        // ///////////////////////////
        // View START
        // ///////////////////////////
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                mPgDialog = new ProgressDialog(((Activity) _context), android.R.style.Theme_Panel);
                mPgDialog.setCanceledOnTouchOutside(false); // 허니콤 추가
                mPgDialog.setCancelable(false);

                if (strDisplayString != null) {
                    mPgDialog.setMessage(strDisplayString);
                } else {
                    mPgDialog.setMessage("어플리케이션 위변조 검증중입니다.");
                }
                mPgDialog.show();
            }
        });
        // ///////////////////////////
        // View END
        // ///////////////////////////

        // ///////////////////////////
        // TOKEN START
        // ///////////////////////////
        TimerTask task = new TimerTask() {
            public void run() {
                try {
                    // CONTEXT, APP_NAME, PACKAGE_LIC, VERIFY_SVR
                    aap.process(_context, strAppName, PACKAGE_LIC, false);
                    strTokenB64 = aap.getToken();
                    strClientValue = aap.getClientValue();
                    handler.sendEmptyMessage(1);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Log.e("err", "NullPointerException [" + e.toString() + "]");
                } catch (Exception e) {
                    e.printStackTrace();

                    strError = e.toString();
                    Log.e("err", "Exception [" + e.toString() + "]");
                    strErrorMessage = "";
                    int idx = strError.indexOf("ERROR:");

                    if (idx > 0) {
                        strErrorMessage = strError.substring(idx, strError.length());
                        Log.e("err", "strErrorMessage [" + strErrorMessage + "]");
                    } else {
                        strErrorMessage = strError;
                        Log.e("err", "strErrorMessage [" + strErrorMessage + "]");
                    }
                    handler.sendEmptyMessage(0);
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 30);
        // ///////////////////////////
        // TOKEN END
        // ///////////////////////////
    }

    private static void alert(String title, String value, Context context) {
        AlertDialog successAlt = new AlertDialog.Builder(context).setTitle(title).setMessage(value)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        requestKill();
                    }
                }).create();
        successAlt.show();

        return;
    }

    /*
     * 실제로 Kill하지는 않고 메인 화면으로 되돌려 준다.
     * - 해당 클래스는 고객 업무의 메인 클래스로 선언해 준다.
     */
    public static void requestKill() {

        alert("requestKill", "Kill or back to main page", _context);
    }

}