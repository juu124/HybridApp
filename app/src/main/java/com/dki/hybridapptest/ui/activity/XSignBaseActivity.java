package com.dki.hybridapptest.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.utils.Constant;
import com.dreamsecurity.magicxsign.MagicXSign_Type;

// USE_XSIGN_DREAM
public class XSignBaseActivity extends Activity {

    protected Context baseContext;
    protected String TAG;

    protected final static int FUNC_MODE_CERT_SIGN = 0;
    protected final static int FUNC_MODE_CERT_DELETE = 1;
    protected final static int FUNC_MODE_CERT_CHANGE_PASSWORD = 2;
    protected final static int FUNC_MODE_CERT_CHECK_VID = 3;
    // 2020.02.05 : XML 추가
    protected final static int FUNC_MODE_CERT_XML_SIGN = 4;

    protected final static int FUNC_MODE_CERT_CRYPTO_ASYM_ENC_DEC = 5;
    protected final static int FUNC_MODE_CERT_CMS_CRYPTO_ENC_DEC = 6;

    protected final static int FUNC_MODE_CRYPTO_SEED_ENC_DEC = 7;
    protected final static int FUNC_MODE_CRYPTO_HASH = 8;
    protected final static int FUNC_MODE_CRYPTO_BASE64 = 9;
    protected final static int FUNC_MODE_CERT_SHOW_DETAIL = 10;

    // 20210326 wj : PFX 추가
    protected final static int FUNC_MODE_CERT_PFX_EXPORT = 11;
    protected final static int FUNC_MODE_CERT_PFX_IMPORT = 12;
    // 20210426 wj : 개인키 암호화
    protected final static int FUNC_MODE_KEY_ENCPRIVATEKEY = 13;

    // 마이데이터 관련 UCPIDRequest Message 적용
    protected final static int FUNC_MODE_UCPID_REQ_INFO = 14;

    protected final static int FUNC_MODE_CERT_LIST = 100;
    protected final static String SET_KEY_DEBUG_LEVEL = "debug_level";

    private static int mFunctionMode;      // 현재 수행중인 Main Menu Index 저장 변수
    private static int mSelectedCertIndex; // 현재 선택된 인증서 Index 저장 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // 화면 캡쳐 방지
        if (Constant.USE_SCREEN_SHOT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        baseContext = this;
        TAG = baseContext.getClass().getSimpleName();
    }

    /**
     * MagicXSign Debug Level 설정 가져오기
     */
    protected int getDebugSettingValue() {
        SharedPreferences pref = getSharedPreferences("XSignSetting", MODE_PRIVATE);
        int nValue = pref.getInt(SET_KEY_DEBUG_LEVEL, -1);

        if (nValue == -1) {
            nValue = MagicXSign_Type.XSIGN_DEBUG_LEVEL_0;
            saveSettingValue(SET_KEY_DEBUG_LEVEL, nValue);
        }
        return nValue;
    }

    /**
     * MagicXSign Debug Level 설정
     *
     * @param szKey
     * @param nValue
     */
    protected void saveSettingValue(String szKey, int nValue) {
        SharedPreferences pref = getSharedPreferences("XSignSetting", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(szKey, nValue);
        editor.commit();
    }

    /**
     * 현재 선택된 테스트 메뉴 Index Geter/Seter
     */
    protected void setFunctionMode(int nFuncMode) {
        mFunctionMode = nFuncMode;
    }

    protected int getFunctionMode() {
        return mFunctionMode;
    }

    /**
     * 현재 선택된 인증서 Index Geter/Seter
     */
    protected void setSelectedCertIndex(int nSelectedIndex) {
        Log.d(TAG, "setSelectedCertIndex ===");
        mSelectedCertIndex = nSelectedIndex;
    }

    protected int getSelectedCertIndex() {
        Log.d(TAG, "getSelectedCertIndex ===");
        return mSelectedCertIndex;
    }

    /**
     * 화면 Title 정보 출력
     *
     * @param title
     */
    protected void setActivityTitle(String title) {
        TextView titleView = (TextView) findViewById(R.id.commonTitle);

        if (titleView != null)
            titleView.setText(title);
    }

    /**
     * Test 하는 기능에 따라 Title 정보를 출력한다
     *
     * @param ListMode
     */
    protected void showFunctionListTitle(int ListMode) {
        switch (ListMode) {
            case FUNC_MODE_CERT_SHOW_DETAIL:
                setActivityTitle(getResources().getString(R.string.function_cert_detail));
                break;

            case FUNC_MODE_CERT_SIGN:
                setActivityTitle(getResources().getString(R.string.function_list_cert_sign));
                break;

            case FUNC_MODE_CERT_DELETE:
                setActivityTitle(getResources().getString(R.string.function_list_cert_delete));
                break;

            case FUNC_MODE_CERT_CHANGE_PASSWORD:
                setActivityTitle(getResources().getString(R.string.function_list_cert_change_passwd));
                break;

            case FUNC_MODE_CERT_CHECK_VID:
                setActivityTitle(getResources().getString(R.string.function_list_cert_check_vid));
                break;

            case FUNC_MODE_CERT_XML_SIGN:
                setActivityTitle(getResources().getString(R.string.function_list_cert_xml_sign));
                break;

            case FUNC_MODE_CERT_CRYPTO_ASYM_ENC_DEC:
                setActivityTitle(getResources().getString(R.string.function_list_cert_asymmetric_enc_dec));
                break;

            case FUNC_MODE_CERT_CMS_CRYPTO_ENC_DEC:
                setActivityTitle(getResources().getString(R.string.function_list_cert_cms_enc_dec));
                break;

            case FUNC_MODE_CRYPTO_BASE64:
                setActivityTitle(getResources().getString(R.string.function_list_crypto_base64));
                break;

            case FUNC_MODE_CRYPTO_HASH:
                setActivityTitle(getResources().getString(R.string.function_list_crypto_hash));
                break;

            case FUNC_MODE_CRYPTO_SEED_ENC_DEC:
                setActivityTitle(getResources().getString(R.string.function_list_crypto_symmetric_enc_dec));
                break;

            case FUNC_MODE_CERT_LIST:
                setActivityTitle(getResources().getString(R.string.function_cert_list_info));
                break;

            case FUNC_MODE_CERT_PFX_EXPORT:
                setActivityTitle(getResources().getString(R.string.function_cert_pfx_export));
                break;

            case FUNC_MODE_CERT_PFX_IMPORT:
                setActivityTitle(getResources().getString(R.string.function_cert_pfx_import));
                break;

            case FUNC_MODE_KEY_ENCPRIVATEKEY:
                setActivityTitle(getResources().getString(R.string.function_cert_encPriKey));
                break;

            case FUNC_MODE_UCPID_REQ_INFO:
                setActivityTitle(getResources().getString(R.string.function_ucpid_request_info));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
