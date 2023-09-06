package com.dki.hybridapptest.ui.activity.XSign;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.tester.XSignCertPolicy;
import com.dki.hybridapptest.tester.XSignTester;
import com.dreamsecurity.magicxsign.MagicXSign_Exception;

// USE_XSIGN_DREAM
public class XSignCertDetailActivity extends XSignBaseActivity {

    private EditText mOldPassword;
    private EditText mNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cert_detail);

        if (getFunctionMode() == FUNC_MODE_CERT_LIST)
            showFunctionListTitle(FUNC_MODE_CERT_SHOW_DETAIL);
        else
            showFunctionListTitle(getFunctionMode());

        setCertDetailInfo();

        mOldPassword = (EditText) findViewById(R.id.edit_input_curPassword);
        mNewPassword = (EditText) findViewById(R.id.edit_input_changePassword);
    }

    /**
     * 선택된 인증서에 대한 상세 정보를 화면에 출력한다.
     */
    private void setCertDetailInfo() {

        Intent paramIntent = getIntent();

        ((TextView) findViewById(R.id.txt_cert_info_name)).setText(XSignCertPolicy.parserUserName(paramIntent.getStringExtra("DN")));
        ((TextView) findViewById(R.id.txt_cert_info_issur)).setText(XSignCertPolicy.parseISSUER(paramIntent.getStringExtra("Issur")));
        ((TextView) findViewById(R.id.txt_cert_info_objective)).setText(XSignCertPolicy.parseOID(paramIntent.getStringExtra("OID")));
        ((TextView) findViewById(R.id.txt_cert_info_purpose)).setText(XSignCertPolicy.getKeyPurposeForString(paramIntent.getStringExtra("Keyusage")));
        ((TextView) findViewById(R.id.txt_cert_info_dn)).setText(paramIntent.getStringExtra("DN"));
        ((TextView) findViewById(R.id.txt_cert_info_from)).setText(paramIntent.getStringExtra("DateFrom"));
        ((TextView) findViewById(R.id.txt_cert_info_to)).setText(paramIntent.getStringExtra("DateTo"));

        if (getFunctionMode() == FUNC_MODE_CERT_CHANGE_PASSWORD)
            ((LinearLayout) findViewById(R.id.layout_change_password)).setVisibility(View.VISIBLE);
        else
            ((LinearLayout) findViewById(R.id.layout_change_password)).setVisibility(View.GONE);
    }

    /**
     * 인증서 삭제 버튼 함수
     *
     * @param v
     */
    public void selectBtnPasswordChange(View v) {
        switch (v.getId()) {
            case R.id.btn_password_change:
                processChangePassword();
                break;
        }
        finish();
    }

    /**
     * Main menu 에서 인증서 삭제 기능 선택 시 인증서 상세보기에서 인증서 삭제 수행
     */
    private void processChangePassword() {
        XSignTester xSignTester = new XSignTester(baseContext, getDebugSettingValue());
        Intent intent = new Intent(baseContext, XSignProcessResultActivity.class);
        intent.putExtra("Mode", getFunctionMode());

        try {
            xSignTester.certChangePassword(getSelectedCertIndex(), mOldPassword.getText().toString(), mNewPassword.getText().toString());
            intent.putExtra("Result", "비밀번호 변경 성공");
        } catch (MagicXSign_Exception e) {
            intent.putExtra("Result", e.getErrorMessage());
            e.printStackTrace();
        } finally {
            xSignTester.finish();
            baseContext.startActivity(intent);
        }
    }
}
