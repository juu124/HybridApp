package com.dki.hybridapptest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.tester.XSignCertPolicy;
import com.dki.hybridapptest.tester.XSignTester;
import com.dreamsecurity.dstoolkit.exception.DSToolkitException;
import com.dreamsecurity.dstoolkit.util.Base64;
import com.dreamsecurity.magicxsign.MagicXSign_Exception;

public class XSignUcpidActivity extends XSignBaseActivity implements View.OnClickListener {

    private XSignTester mXSignTester;
    private XSignTester.CertDetailInfo mCertDetailInfo;
    private Intent mParamIntent;
    private int mCertIdx = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParamIntent = getIntent();

        setContentView(R.layout.activity_ucpid);
        showFunctionListTitle(FUNC_MODE_UCPID_REQ_INFO);

        //mCertDetailInfo = (XSignTester.CertDetailInfo) mParamIntent.getSerializableExtra("CertInfo");

        setCertDetailInfo();

        Button btn = findViewById(R.id.btn_req_ucpid);
        btn.setOnClickListener(this);
    }

    /**
     * 선택된 인증서에 대한 상세 정보를 화면에 출력한다.
     */
    private void setCertDetailInfo() {
        Intent paramIntent = getIntent();

        mCertIdx = paramIntent.getIntExtra("Index", -1);
        ((TextView) findViewById(R.id.txt_cert_info_name)).setText(XSignCertPolicy.parserUserName(paramIntent.getStringExtra("DN")));
        ((TextView) findViewById(R.id.txt_cert_info_issur)).setText(XSignCertPolicy.parseISSUER(paramIntent.getStringExtra("Issur")));
        ((TextView) findViewById(R.id.txt_cert_info_objective)).setText(XSignCertPolicy.parseOID(paramIntent.getStringExtra("OID")));
        ((TextView) findViewById(R.id.txt_cert_info_purpose)).setText(XSignCertPolicy.getKeyPurposeForString(paramIntent.getStringExtra("Keyusage")));
        ((TextView) findViewById(R.id.txt_cert_info_dn)).setText(paramIntent.getStringExtra("DN"));
        ((TextView) findViewById(R.id.txt_cert_info_from)).setText(paramIntent.getStringExtra("DateFrom"));
        ((TextView) findViewById(R.id.txt_cert_info_to)).setText(paramIntent.getStringExtra("DateTo"));

        String oValue = ((TextView) findViewById(R.id.ovalue)).getText().toString();
        oValue += XSignCertPolicy.GetDataFormString("o=", paramIntent.getStringExtra("Issur"));
        ((TextView) findViewById(R.id.ovalue)).setText(oValue);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_req_ucpid:
                Intent intent = new Intent(baseContext, XSignProcessResultActivity.class);
                intent.putExtra("Mode", getFunctionMode());
                byte[] ucpidSigned = null;

                try {
                    ucpidSigned = mXSignTester.makeUcpidReqInfo(mCertIdx, ((EditText) findViewById(R.id.cert_pwd)).getText().toString(),
                            ((TextView) findViewById(R.id.useragreement)).getText().toString(),
                            ((CheckBox) findViewById(R.id.real_name)).isChecked(),
                            ((CheckBox) findViewById(R.id.gender)).isChecked(),
                            ((CheckBox) findViewById(R.id.national)).isChecked(),
                            ((CheckBox) findViewById(R.id.birth)).isChecked(),
                            ((CheckBox) findViewById(R.id.ci)).isChecked());

                    Base64 base64 = new Base64();

                    intent.putExtra("Result", "UCPIDRequestInfo 생성 성공");
                    intent.putExtra("UcpidSigned", base64.encode(ucpidSigned));
                } catch (MagicXSign_Exception e) {
                    intent.putExtra("Result", e.getErrorMessage());
                    intent.putExtra("UcpidSigned", "UcpidRequestInfo 생성 실패");
                } catch (DSToolkitException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {

        if (mXSignTester == null)
            mXSignTester = new XSignTester(baseContext, getDebugSettingValue());
        super.onResume();
    }
}
