package com.dki.hybridapptest.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.tester.XSignCertPolicy;
import com.dki.hybridapptest.tester.XSignTester;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.dki.hybridapptest.utils.XSignDialog;
import com.dreamsecurity.dstoolkit.util.Base64;
import com.dreamsecurity.magicxsign.MagicXSign;
import com.dreamsecurity.magicxsign.MagicXSign_Exception;
import com.dreamsecurity.magicxsign.MagicXSign_Type;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

// USE_XSIGN_DREAM
public class XSignMainActivity extends XSignBaseActivity {

    private XSignTester mXSignTester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xsign_main);

        // Main Menu 구성
        ArrayList<String> funcArray = new ArrayList<String>();
        funcArray.add(getResources().getText(R.string.function_list_cert_sign).toString());
        funcArray.add(getResources().getText(R.string.function_list_cert_delete).toString());
        funcArray.add(getResources().getText(R.string.function_list_cert_change_passwd).toString());
        funcArray.add(getResources().getText(R.string.function_list_cert_check_vid).toString());

        // 2020.02.05 추가
        funcArray.add(getResources().getText(R.string.function_list_cert_xml_sign).toString());

        funcArray.add(getResources().getText(R.string.function_list_cert_asymmetric_enc_dec).toString());
        funcArray.add(getResources().getText(R.string.function_list_cert_cms_enc_dec).toString());
        funcArray.add(getResources().getText(R.string.function_list_crypto_symmetric_enc_dec).toString());
        funcArray.add(getResources().getText(R.string.function_list_crypto_hash).toString());
        funcArray.add(getResources().getText(R.string.function_list_crypto_base64).toString());
        funcArray.add(getResources().getText(R.string.function_cert_detail).toString());
        // 20210326 wj : pfx 추가
        funcArray.add(getResources().getText(R.string.function_cert_pfx_export).toString());
        funcArray.add(getResources().getText(R.string.function_cert_pfx_import).toString());

        // 20210426 wj : 개인키 암호화
        funcArray.add(getResources().getText(R.string.function_cert_encPriKey).toString());

        // 마이데이터 관련 UCPIDRequest Message 적용
        funcArray.add(getResources().getText(R.string.function_ucpid_request_info).toString());

        GLog.d("첫 번째 값은? === " + funcArray.get(0));

        /*
         * Main Menu 목록 List
         */
        ListView funcList = (ListView) findViewById(R.id.lst_function_list);
        funcList.setAdapter(new XSignFunctionList(this, R.layout.layout_function_list, funcArray));
        funcList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                setFunctionMode(position); // 현재 선택된 Menu Index 를 저장

                switch (getFunctionMode()) {
                    case FUNC_MODE_CRYPTO_SEED_ENC_DEC:
                        Log.d(TAG, "FUNC_MODE_CRYPTO_SEED_ENC_DEC ===");
                        processSeedEncAndDec();
                        break;

                    case FUNC_MODE_CRYPTO_HASH:
                        Log.d(TAG, "FUNC_MODE_CRYPTO_SEED_ENC_DEC ===");
                        processMakeHash();
                        break;

                    case FUNC_MODE_CRYPTO_BASE64:
                        Log.d(TAG, "FUNC_MODE_CRYPTO_BASE64 ===");
                        processMakeBase64();
                        break;

                    // 20.04.13 : XML 서명 추가
                    case FUNC_MODE_CERT_XML_SIGN:
                        Log.d(TAG, "FUNC_MODE_CERT_XML_SIGN ===");
                        gotoXMLActivity();
                        break;

                    // 20210326 wj : Pfx 추가
                    case FUNC_MODE_CERT_PFX_EXPORT:
                        Log.d(TAG, "FUNC_MODE_CERT_PFX_EXPORT ===");
                        processPfxExport();
                        break;

                    case FUNC_MODE_CERT_PFX_IMPORT:
                        Log.d(TAG, "FUNC_MODE_CERT_PFX_IMPORT ===");
                        processPfxImport();
                        break;

                    // 20210426 wj : 개인키 암호화
                    case FUNC_MODE_KEY_ENCPRIVATEKEY:
                        Log.d(TAG, "FUNC_MODE_KEY_ENCPRIVATEKEY ===");
                        processENCPriKey();
                        break;

                    default:
                        gotoCertListActivity();
                        Log.d(TAG, "gotoCertListActivity ===");
                        break;
                }

            }
        });
    }

    @Override
    protected void onResume() {
        if (mXSignTester == null) {
            mXSignTester = new XSignTester(this, getDebugSettingValue());
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mXSignTester != null) {
            mXSignTester.finish();
        }
        super.onDestroy();
    }

    /**
     * 화면 하단 버튼 선택 Event
     *
     * @param btn
     */
    public void selectBottomBtn(View btn) {
        Intent intent = null;

        if (Constant.USE_XSIGN_DREAM) {
            switch (btn.getId()) {
                case R.id.btn_cert_list:
                    GLog.d("인증서 목록 버튼 누름");
                    intent = new Intent(baseContext, XSignCertListActivity.class);
                    setFunctionMode(FUNC_MODE_CERT_LIST);
                    baseContext.startActivity(intent);
                    break;

                case R.id.btn_xsign_setting:
                    GLog.d("디버그 설정 버튼 누름");
                    intent = new Intent(baseContext, XSignSettingActivity.class);
                    baseContext.startActivity(intent);
                    break;

                case R.id.btn_exit:
                    GLog.d("종료 버튼 누름");
                    finish();
                    break;
            }
        }
    }

    /**
     * 인증서 목록 화면으로 이동
     */
    private void gotoCertListActivity() {
        if (Constant.USE_XSIGN_DREAM) {
            Intent intent = new Intent(baseContext, XSignCertListActivity.class);
            baseContext.startActivity(intent);
        }
    }

    /**
     * 20.04.13 : XML 서명화면으로 이동
     */
    private void gotoXMLActivity() {
        if (Constant.USE_XSIGN_DREAM) {
            Intent intent = new Intent(baseContext, XSignXMLActivity.class);
            baseContext.startActivity(intent);
        }
    }

    /**
     * assets 폴더에 있는 Sample 인증서를 byte Array 형태로 Load
     *
     * @param Certname
     * @return
     * @throws Exception
     */
    private byte[] getCertByteArray(String Certname) throws Exception {
        InputStream in;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        // ASSETS에 존재하는 인증서 파일을 읽어온다.
        in = getAssets().open(Certname);

        int nRead;
        byte[] data = new byte[1024];
        // ASSETS에 존재하는 DSTK 환경설정 파일을 byte[] 로 읽어온다.
        while ((nRead = in.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();

        return buffer.toByteArray();
    }

    /**
     * 20210326 wj : pfx export
     */
    private void processPfxExport() {
        if (Constant.USE_XSIGN_DREAM) {
            XSignDialog.processInputPassword(baseContext, R.string.function_cert_pfx_export, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    byte[] bRetPfx = null;
                    EditText password = (EditText) ((android.app.AlertDialog) dialog).findViewById(R.id.edit_input1);

                    Intent intent = new Intent(baseContext, XSignProcessResultActivity.class);
                    try {
                        MagicXSign Xsign = new MagicXSign();

                        // 인증서 및 개인키
                        // 비밀번호 : qwer1234
                        byte[] binCert = getCertByteArray("signCert.der");
                        byte[] binPri = getCertByteArray("signPri.key");


                        // 암호화 여부.
                        // MagicXSign_Type.XSIGN_PFXEXPORT_NOENC_OPT : 개인키 암호화를 하지 않는 경우. 결과는 복호화된 개인키
                        // 또는 MagicXSign_Type.XSIGN_PFXEXPORT_ENC_OPT : 개인키 암호화를 한 경우. 결과는 암호화된 개인키
                        int nFlag = MagicXSign_Type.XSIGN_PFXEXPORT_NOENC_OPT;

                        try {
                            bRetPfx = Xsign.PFX_Export(binCert, binPri, password.getText().toString().getBytes(), nFlag);
                        } catch (Exception e) {
                        }

                        if (bRetPfx != null) {
                            Base64 base64 = new Base64();
                            intent.putExtra("Cert", base64.encode(bRetPfx));
                            intent.putExtra("Result", "PfxExport 성공");
                        } else {
                            intent.putExtra("Result", "PfxExport 실패");
                        }
                    } catch (MagicXSign_Exception e) {

                        intent.putExtra("Result", e.getErrorMessage());

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        baseContext.startActivity(intent);
                    }
                }
            }).show();
        }
    }


    /**
     * 20210326 wj : pfx import
     */
    private void processPfxImport() {
        Log.d(TAG, "processPfxImport");
        if (Constant.USE_XSIGN_DREAM) {
            XSignDialog.processInputPassword(baseContext, R.string.function_cert_pfx_import, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText password = (EditText) ((android.app.AlertDialog) dialog).findViewById(R.id.edit_input1);
                    Intent intent = new Intent(baseContext, XSignProcessResultActivity.class);

                    try {
                        MagicXSign Xsign = new MagicXSign();

                        // 아래 두 개의 pfx 인증서 모두 비밀번호 : qwer1234
                        byte[] signCert = getCertByteArray("암호화된.pfx");
//					byte[] signCert = getCertByteArray("암호화되지않은.pfx");

                        String sRetCert = null;
                        String sRetKey = null;
                        int nFlag = 0;
                        ArrayList<com.dreamsecurity.magicxsign.MagicXSign.CertPFX> certPFX = new ArrayList<>();

                        try {
                            Base64 base64 = new Base64();

                            certPFX = Xsign.PFX_Import(signCert, password.getText().toString().getBytes());
                            for (int i = 0; i < certPFX.size(); i++) {
                                sRetCert = base64.encode(certPFX.get(i).getCert());
                                sRetKey = base64.encode(certPFX.get(i).getKey());
                                nFlag = certPFX.get(i).getFlag();
                            }
                        } catch (Exception e) {
                        }

                        if (sRetCert != null) {
                            intent.putExtra("Cert", sRetCert);
                            intent.putExtra("PriKey", sRetKey);
                            intent.putExtra("Flag", nFlag);
                            intent.putExtra("Result", "PfxImport 성공");
                        } else {
                            intent.putExtra("Result", "PfxImport 실패");
                        }
                    } catch (MagicXSign_Exception e) {

                        intent.putExtra("Result", e.getErrorMessage());

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        baseContext.startActivity(intent);
                    }
                }
            }).show();
        }
    }

    /**
     * 20210426 wj : 개인키 암호화 (pfxImport 결과가 복호화된 개인키인 경우 사용)
     */
    private void processENCPriKey() {
        Log.d(TAG, "processENCPriKey");
        if (Constant.USE_XSIGN_DREAM) {
            XSignDialog.processInputPassword(baseContext, R.string.function_cert_encPriKey, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    byte[] bEncKey = null;
                    EditText password = (EditText) ((android.app.AlertDialog) dialog).findViewById(R.id.edit_input1);
                    Intent intent = new Intent(baseContext, XSignProcessResultActivity.class);

                    try {
                        MagicXSign Xsign = new MagicXSign();
                        Base64 base64 = new Base64();

                        // pfxImport 복호화된 개인키
                        // 비밀번호 : qwer1234
                        byte[] binPri = base64.decode("MIIE5QIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDQG5fBwbtEMxHmG2CkuxDfbTpW/Crn5Mv9dpnr0l0vTTk/ihqGNJleRJjEKggygmB+86iYhMzw7Ohl0atkmkW5xlgZqr7OKNG6sjkC6V9z7PRR5LcZkoX7f6/EXIdaQvzVZ8OZb79ioI0cNvDBCkfc4vkbUEmCCLwCxShFezxSFUb5I/Q4ZBxlMgZpWsCp+sIcmhqzDUcLCOstJtN8UbAqRIma1w8ZobP1owv2OEMGn17EGLaYPK3Tao5+BqbW9DC6F+l/cejOllpR49LmrjPMQp0YqlIybc6YDMvFAyQNALQHiYmPs4amRMbqVGQDfM0I5QoQpGlxcT9lxXLqiPZXAgMBAAECggEAaFTtCB5wHAjeeFZRZUTDeL/x95oEiK2T90Z2dFvKi+RTRx+dnJrSKQiK7g2Efo4Ogpb1d0Fc3YxoFmO/YXhWbISbtoJ7li+wtcCUBHmreRraghQAF7n1odip7e/Vi9L5nqOe1FXJxVBobjS9Dopw7LR93supjp+CYoElZ3AZFazdrAmc0vikkfe4aMHo9wkUHrkLoIb3JEO2NoTwxaxN0Zqf1FgKpH+oNoEbk0Ioik/NkNic8lKgQ4S5mzWInibapbgGPinbaQtzwOPww5B0TVIDTECDVQJcCCqeRPrXn1yc47XAusVbd4Z1Yi08CgIylNGOJ/UJ8gH2QWrxFzuSgQKBgQDj60m5zTx66+h5EAXFVXFD/YsuNjNSfPMYrf7OV4GXVoo8HWb2zGKdYpQiV+1nxxsrFYQ6sDNveJ2zaEVoGq3n/lzotXfe1dmiHBFmfjLMJf5/WNsEk/u3QfZjyXg+kINg2HFFkAI1xXZvGcr+984++ecF9i4N99F0ql4EvAcFCQKBgQDpv3GQprPCDar1oK9JzLLLalDadhlplynfwZQMSqvqt5y+8YCPAo3XAm+y6yqJ3oYyjg58cctjtNT7P8YTwnMYU90RciY4cvrz1CYvg1aBSReLKBAB92mLIW1PXUV8AJoG8txkSHMfKTi030oEL8lChOSvkY4XWl13zah8cFtYXwKBgFUaawoEt3uSkNh7ghwf/k4L4ydbN6iqXT8u4QD9Lbdrqewucl7fDEeGIpf8SvpAH0XkH96mIl6SJBh0a84mgB8rHFgMQnkjUsM0Rc8GekM+QJweepFoDEpuR+kUtmBuJ5BG4Wy/DAQ1+jYb5G916j4bpAbW2HWAvmYYo0iTSO+5AoGAHw71BAdiczJluOV05RVx3F1wCNcQYVtYkQajqU5ysWlcRnLIZjgsqJkGRnvA1zjeE/GUMyzbnY/1jLzYkN+Rc3YRNbQ5J97/QU67FC0bXWpc2nykQ96gA4CZiaYXCXb7AFlct2Z5BXbwtffFWfEPiOsnh7yLaMb1DGojLQWw3XkCgYBEor5WOiDxkwdPtxYas2co8rxiP+kcXA++gEuEoXXPPTNwp/7nxYXhlAcDaWJPyJdO4e+nNwqun5ggBxXtbrq8OzPqGYdOHD0aZXtSW+9XvKSgIZxhJ0nhEA5U1LXk+pvJKzhGvSVyermFA5dNc/AB4DXghBTD8HoZQ4vdQmab+6AnMCUGCiqDGoyaRAoBAQMxFwMVAAAAAAAAAAAAAAE8AJABPABIEMUA");

                        bEncKey = Xsign.EncPrivateKey(binPri, password.getText().toString().getBytes());
                        if (bEncKey != null) {
                            intent.putExtra("EncPriKey", base64.encode(bEncKey));
                            intent.putExtra("Result", "성공");
                        } else {
                            intent.putExtra("Result", "실패");
                        }
                    } catch (MagicXSign_Exception e) {
                        intent.putExtra("Result", e.getErrorMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        baseContext.startActivity(intent);
                    }
                }
            }).show();
        }
    }

    /**
     * 대칭키 암/복호 테스트
     */
    private void processSeedEncAndDec() {
        if (Constant.USE_XSIGN_DREAM) {
            XSignDialog.processInputPlainText(baseContext, R.string.function_list_crypto_symmetric_enc_dec, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText edit1 = (EditText) ((android.app.AlertDialog) dialog).findViewById(R.id.edit_input1);
                    StringBuffer encBuffer = new StringBuffer();
                    StringBuffer decBuffer = new StringBuffer();

                    Intent intent = new Intent(baseContext, XSignProcessResultActivity.class);
                    try {
                        // 대칭키 암/복호 생성, SEED 알고리즘 이용
                        if (mXSignTester.seedEncryptAndDecrypt(edit1.getText().toString(), encBuffer, decBuffer)) {
                            intent.putExtra("EncData", encBuffer.toString());
                            intent.putExtra("DecData", decBuffer.toString());
                            intent.putExtra("Result", "암/복호 테스트 성공");
                        } else {
                            intent.putExtra("Result", "암/복호 실패");
                        }
                    } catch (MagicXSign_Exception e) {
                        intent.putExtra("Result", e.getErrorMessage());
                    } finally {
                        baseContext.startActivity(intent);
                    }

                }
            }).show();
        }
    }

    /**
     * Hash 생성 테스트
     */
    private void processMakeHash() {
        if (Constant.USE_XSIGN_DREAM) {
            XSignDialog.processInputPlainText(baseContext, R.string.function_list_crypto_hash, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText edit1 = (EditText) ((android.app.AlertDialog) dialog).findViewById(R.id.edit_input1);
                    byte[] binHash = null;
                    Intent intent = new Intent(baseContext, XSignProcessResultActivity.class);

                    try {
                        // Hash 생성
                        binHash = mXSignTester.makeHash(edit1.getText().toString());
                        intent.putExtra("binHash", XSignCertPolicy.ByteToHex(binHash));
                        intent.putExtra("Result", "Hash 생성 테스트 성공");
                    } catch (MagicXSign_Exception e) {
                        intent.putExtra("Result", e.getErrorMessage());
                    } finally {
                        baseContext.startActivity(intent);
                    }
                }
            }).show();
        }
    }

    /**
     * Base64 생성 테스트
     */
    private void processMakeBase64() {
        if (Constant.USE_XSIGN_DREAM) {
            XSignDialog.processInputPlainText(baseContext, R.string.function_list_crypto_base64, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText edit1 = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_input1);
                    String szEncBase64;
                    byte[] binDecBase64;

                    Intent intent = new Intent(baseContext, XSignProcessResultActivity.class);


                    try {
                        // Base64 Encode and Decode 값
                        szEncBase64 = mXSignTester.makeEncodeBase64(edit1.getText().toString().getBytes());
                        binDecBase64 = mXSignTester.makeDecodeBase64(szEncBase64);

                        intent.putExtra("base64Enc", szEncBase64);
                        intent.putExtra("base64Dec", new String(binDecBase64));
                        intent.putExtra("Result", "Base64 테스트 성공");

                    } catch (MagicXSign_Exception e) {

                        intent.putExtra("Result", e.getErrorMessage());

                    } finally {
                        baseContext.startActivity(intent);
                    }
                }
            }).show();
        }
    }

    /**
     * Main Menu 출력을 위한 List Adapter
     *
     * @author sonhyuntak
     */
    private class XSignFunctionList extends ArrayAdapter<String> {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<String> mDataList;
        private int mLayoutID;

        public XSignFunctionList(Context context, int resourceID, List<String> arrayList) {
            super(context, resourceID, arrayList);

            mLayoutID = resourceID;
            mContext = context;
            mDataList = arrayList;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)
                convertView = mInflater.inflate(mLayoutID, null);

            ((TextView) convertView.findViewById(R.id.txt_function_case)).setText("[Case " + (position + 1) + "]");
            ((TextView) convertView.findViewById(R.id.txt_function_name)).setText(mDataList.get(position));
            return convertView;
        }
    }
}
