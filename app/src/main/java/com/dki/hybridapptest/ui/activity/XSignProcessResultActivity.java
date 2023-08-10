package com.dki.hybridapptest.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.tester.XSignCertPolicy;

public class XSignProcessResultActivity extends XSignBaseActivity {

    private Intent mParamIntent;
    private TextView mResultView;
    private final String CRLF = "\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_result);

        mResultView = (TextView) findViewById(R.id.txt_process_result);
        mParamIntent = getIntent();

        showFunctionListTitle(getFunctionMode());
        displayResult();
    }

    /**
     * 기능 처리 결과에 대한 Result를 화면에 출력
     */
    private void displayResult() {
        switch (getFunctionMode()) {
            case FUNC_MODE_CERT_SIGN:
                displaySignDataResult();
                break;
            case FUNC_MODE_CERT_XML_SIGN:
                disPlayXMLSignDataResult();
                break;

            case FUNC_MODE_CERT_DELETE:
                displayCertDeleteResult();
                break;

            case FUNC_MODE_CERT_CHANGE_PASSWORD:
                displayCertChangePassword();
                break;

            case FUNC_MODE_CRYPTO_SEED_ENC_DEC:
            case FUNC_MODE_CERT_CRYPTO_ASYM_ENC_DEC:
            case FUNC_MODE_CERT_CMS_CRYPTO_ENC_DEC:
                displayCryptoEncDecResult();
                break;

            case FUNC_MODE_CRYPTO_HASH:
                displayHashTestResult();
                break;

            case FUNC_MODE_CRYPTO_BASE64:
                displayBase64TestResult();
                break;

            case FUNC_MODE_CERT_CHECK_VID:
                displayCheckVIDResult();
                break;

            // 20210326 wj : pfxExport
            case FUNC_MODE_CERT_PFX_EXPORT:
                displayPfxExport();
                break;

            // 20210326 wj : pfxImport
            case FUNC_MODE_CERT_PFX_IMPORT:
                displayPfxImport();
                break;

            // 20210426 wj : 개인키 암호화
            case FUNC_MODE_KEY_ENCPRIVATEKEY:
                displayEncPriKey();
                break;

            // 마이데이터 관련 UCPIDRequest Message 적용
            case FUNC_MODE_UCPID_REQ_INFO:
                displayUcpidRequest();
                break;
        }
    }

    /**
     * 서명 기능 결과에 대한 화면 출력
     */
    private void displaySignDataResult() {
        byte[] binSignData = mParamIntent.getByteArrayExtra("binSignData");
        String Result = mParamIntent.getStringExtra("Result");

        mResultView.setText("[인증서 서명결과] " + Result);
        mResultView.append(CRLF + CRLF);
        mResultView.append("[인증서 서명데이터]" + CRLF + XSignCertPolicy.ByteToHex(binSignData));
    }

    /**
     * XML 서명 기능 결과에 대한 화면 출력
     */
    private void disPlayXMLSignDataResult() {
        byte[] binSignData = mParamIntent.getByteArrayExtra("binSignData");
        String Result = mParamIntent.getStringExtra("Result");

        mResultView.setText("[인증서 서명결과] " + Result);
        mResultView.append(CRLF + CRLF);
        mResultView.append("[인증서 서명데이터]" + CRLF + XSignCertPolicy.ByteToHex(binSignData));

        // TODO : 결과 서버 전송
    }

    /**
     * 인증서 비밀번호 변경 결과에 대한 화면 출력
     */
    private void displayCertChangePassword() {
        String Result = mParamIntent.getStringExtra("Result");

        mResultView.setText("[인증서 비밀번호 변경결과] " + Result);
    }

    /**
     * 인증서 삭제 결과에 대한 화면 출력
     */
    private void displayCertDeleteResult() {
        String Result = mParamIntent.getStringExtra("Result");

        mResultView.setText("[인증서 삭제결과] " + Result);
    }

    /**
     * 암/복호 테스트 결과
     */
    private void displayCryptoEncDecResult() {
        String szDecData = mParamIntent.getStringExtra("DecData");
        String szEncData = mParamIntent.getStringExtra("EncData");
        String Result = mParamIntent.getStringExtra("Result");

        mResultView.setText("[암/복호 테스트 결과] " + Result);
        mResultView.append(CRLF + CRLF);

        mResultView.append("[암호화 데이터]" + CRLF + szEncData);
        mResultView.append(CRLF + CRLF);
        mResultView.append("[복호화 데이터]" + CRLF + szDecData);
    }

    /**
     * Hash 테스트 결과
     */
    private void displayHashTestResult() {
        String szHashData = mParamIntent.getStringExtra("binHash");
        String Result = mParamIntent.getStringExtra("Result");

        mResultView.setText("[Hash 생셩 결과] " + Result);
        mResultView.append(CRLF + CRLF);
        mResultView.append("[Hash 데이터]" + CRLF + szHashData);
    }

    /**
     * Base64 테스트 결과
     */
    private void displayBase64TestResult() {
        String szBase64Enc = mParamIntent.getStringExtra("base64Enc");
        String szBase64Dec = mParamIntent.getStringExtra("base64Dec");
        String Result = mParamIntent.getStringExtra("Result");

        mResultView.setText("[Hash 생셩 결과] " + Result);
        mResultView.append(CRLF + CRLF);
        mResultView.append("[Base64 Encode]" + CRLF + szBase64Enc);
        mResultView.append(CRLF + CRLF);
        mResultView.append("[Base64 Decode]" + CRLF + szBase64Dec);
    }

    /**
     * 인증서 VID 검증 결과에 대한 화면 출력
     */
    private void displayCheckVIDResult() {
        String Result = mParamIntent.getStringExtra("Result");

        mResultView.setText("[VID 검증 결과]" + CRLF + Result);
    }

    /**
     * 20210326 wj : 인증서 PfxExport 결과에 대한 화면 출력
     */
    private void displayPfxExport() {
        String szCert = mParamIntent.getStringExtra("Cert");
        String Result = mParamIntent.getStringExtra("Result");

        mResultView.setText("[PfxExport 결과] " + Result);
        mResultView.append(CRLF + CRLF);

        mResultView.append("[Pfx인증서]" + CRLF + szCert);
        mResultView.append(CRLF + CRLF);
    }

    /**
     * 20210326 wj : 인증서 PfxImport 결과에 대한 화면 출력
     */
    private void displayPfxImport() {
        String szCert = mParamIntent.getStringExtra("Cert");
        String szPriKey = mParamIntent.getStringExtra("PriKey");
        int szFlag = mParamIntent.getIntExtra("Flag", 0);
        String Result = mParamIntent.getStringExtra("Result");

        mResultView.setText("[PfxImport 결과] " + Result);
        mResultView.append(CRLF + CRLF);

        mResultView.append("[인증서]" + CRLF + szCert);
        mResultView.append(CRLF + CRLF);
        mResultView.append("[개인키]" + CRLF + szPriKey);
        mResultView.append(CRLF + CRLF);
        mResultView.append("[암호화여부]" + CRLF + szFlag);
    }

    /**
     * 20210426 wj : 개인키 암호화 결과에 대한 화면 출력
     */
    private void displayEncPriKey() {
        String szEndKey = mParamIntent.getStringExtra("EncPriKey");
        String Result = mParamIntent.getStringExtra("Result");

        mResultView.setText("[EncPriKey 결과] " + Result);
        mResultView.append(CRLF + CRLF);

        mResultView.append("[암호화된 개인키]" + CRLF + szEndKey);
        mResultView.append(CRLF + CRLF);
    }

    /**
     * 마이데이터 관련 UCPIDRequest Message 적용
     */
    private void displayUcpidRequest() {
        String szResult = mParamIntent.getStringExtra("Result");
        String szSignedUcpid = mParamIntent.getStringExtra("UcpidSigned");

        mResultView.setText("[UCPIDRequestInfo 생성 결과]" + CRLF);
        mResultView.append(szResult + CRLF + CRLF);
        mResultView.append(szSignedUcpid + CRLF);
    }
}
