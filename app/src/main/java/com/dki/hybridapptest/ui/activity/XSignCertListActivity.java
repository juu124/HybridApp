package com.dki.hybridapptest.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.tester.XSignTester;
import com.dki.hybridapptest.utils.GLog;
import com.dki.hybridapptest.utils.XSignDialog;
import com.dreamsecurity.magicxsign.MagicXSign_Exception;
import com.dreamsecurity.magicxsign.MagicXSign_Type;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// USE_XSIGN_DREAM
public class XSignCertListActivity extends XSignBaseActivity {
    private XSignTester mXSignTester;
    private ListView mCertListView;
    private ArrayList<XSignTester.CertDetailInfo> mCertList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GLog.d();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cert_list);

        showFunctionListTitle(getFunctionMode());

        /*
         * 인증서 목록 구성 List
         */
        mCertListView = (ListView) findViewById(R.id.lst_cert_list);
        mCertListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {

                // 현재 선택된 인증서의 Index Value 설정
                setSelectedCertIndex(position);

                // 진입 메뉴에 따른 기능 분기
                switch (getFunctionMode()) {
                    case FUNC_MODE_CERT_SIGN:
                        processMakeSign();
                        break;

                    case FUNC_MODE_CERT_DELETE:
                        processDeleteCert(mCertList.get(getSelectedCertIndex()));
                        break;

                    case FUNC_MODE_CERT_CHECK_VID:
                        processCheckVID();
                        break;

                    case FUNC_MODE_CERT_XML_SIGN:
                        processXMLMakeSign();
                        break;

                    case FUNC_MODE_CERT_CRYPTO_ASYM_ENC_DEC:
                        processASymEncAndDec();
                        break;

                    case FUNC_MODE_CERT_CMS_CRYPTO_ENC_DEC:
                        processEnvelopedEndAndDec();
                        break;

                    case FUNC_MODE_UCPID_REQ_INFO:
                        gotoUcpidRequestInfo(mCertList.get(getSelectedCertIndex()));
                        break;

                    case FUNC_MODE_CERT_CHANGE_PASSWORD:
                    default:
                        gotoCertDetailInfo(mCertList.get(getSelectedCertIndex()));
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {

        if (mXSignTester == null)
            mXSignTester = new XSignTester(baseContext, getDebugSettingValue());

        makeCertListOnView();
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        if (mXSignTester != null)
            mXSignTester.finish();

        super.onDestroy();
    }

    /**
     * 인증서 정보 보기 화면으로 이동
     *
     * @param paramCert
     */
    private void gotoCertDetailInfo(XSignTester.CertDetailInfo paramCert) {
        Intent intent = new Intent(baseContext, XSignCertDetailActivity.class);

        intent.putExtra("DN", paramCert.getSubjectDN());
        intent.putExtra("Issur", paramCert.getIssur());
        intent.putExtra("OID", paramCert.getOID());
        intent.putExtra("Keyusage", paramCert.getKeyUsage());
        intent.putExtra("DateFrom", paramCert.getExpirationFrom());
        intent.putExtra("DateTo", paramCert.getExpirationTo());

        baseContext.startActivity(intent);
    }

    /**
     * 마이데이터 관련 UCPIDRequest Message 적용
     *
     * @param paramCert
     */
    private void gotoUcpidRequestInfo(XSignTester.CertDetailInfo paramCert) {
        Intent intent = new Intent(baseContext, XSignUcpidActivity.class);
        //intent.putExtra("CertInfo", paramCert);

        intent.putExtra("Index", paramCert.getIndex());
        intent.putExtra("DN", paramCert.getSubjectDN());
        intent.putExtra("Issur", paramCert.getIssur());
        intent.putExtra("OID", paramCert.getOID());
        intent.putExtra("Keyusage", paramCert.getKeyUsage());
        intent.putExtra("DateFrom", paramCert.getExpirationFrom());
        intent.putExtra("DateTo", paramCert.getExpirationTo());

        baseContext.startActivity(intent);
    }

    /**
     * 인증서 리스트를 가져와 화면에 출력한다.
     */
    private void makeCertListOnView() {
        try {
            /*
             * 인증서 목록을 구성한다.
             * 만약 인증서가 존재하지 않는다면 assets 폴더에 존재하는
             * Sample 용 인증서를 자동 저장 및 Load 하여 인증서 목록을 구성한다.
             */
            if (getFunctionMode() == FUNC_MODE_CERT_CMS_CRYPTO_ENC_DEC) {
                /*
                 * CMS 암/복호 테스트의 경우 암호용 인증서가 필요
                 * 만약 암호용 인증서가 존재하지 않는다면 assets 폴더에 존재하는
                 * Sample용 암호 인증서를 자동 저장 및 Load 하여 인증서 목록을 구성한다.
                 */
                mCertList = mXSignTester.makeCertList(MagicXSign_Type.XSIGN_PKI_CERT_KM);
            } else if (getFunctionMode() == FUNC_MODE_CERT_XML_SIGN) {
                mCertList = mXSignTester.makeXMLCertList();
            } else {
                mCertList = mXSignTester.makeCertList();
            }
        } catch (MagicXSign_Exception e) {
            Log.e(TAG, "getErrorCode: " + e.getErrorCode());
            Log.e(TAG, "getErrorMessage: " + e.getErrorMessage());
            e.printStackTrace();
            return;
        }

        mCertListView.setAdapter(new XSignCertList(baseContext, R.layout.layout_cert_list, mCertList));
    }

    /**
     * 인증서 서명 수행
     *
     * @param
     */
    private void processMakeSign() {
        XSignDialog.processSignData(baseContext, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                byte[] binSignData = null;
                Intent intent = new Intent(baseContext, XSignProcessResultActivity.class);

                EditText plainData = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_input1);
                EditText certPassword = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_input2);

                try {
                    binSignData = mXSignTester.certSignData(mCertList.get(getSelectedCertIndex()).getIndex(), plainData.getText().toString(), certPassword.getText().toString());

                    intent.putExtra("binSignData", binSignData);
                    intent.putExtra("Result", "서명 생성 성공");

                } catch (MagicXSign_Exception e) {
                    intent.putExtra("binSignData", binSignData);
                    intent.putExtra("Result", e.getErrorMessage());
                    e.printStackTrace();
                } finally {
                    baseContext.startActivity(intent);
                }
            }
        }).show();
    }

    /**
     * XML 서명을 위한 함수 추가.
     * 서명원문이 XML SignedInfo 값이 된다.
     */
    private void processXMLMakeSign() {
        Intent intent = getIntent();

        final boolean isSingle = intent.getExtras().getBoolean("isSingle");

        final String signedInfoVal = intent.getExtras().getString("signedInfoVal");

        XSignDialog.processXMLSignData(baseContext, signedInfoVal, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                byte[] bySignedData = null;
                byte[] byCurrentCert = null;

                // 다시 본래의 액티비티로 돌아감
                Intent resultIntent = new Intent(baseContext, XSignXMLActivity.class);

                EditText certPassword = (EditText) ((AlertDialog) dialog).findViewById(R.id.xml_password);

                try {
                    // CAUTION ==============================
                    // XML 인증서 리스트는 인덱스 값이 상이할 수 있다.
                    // mCertList 는 oid 필터에 의해 생성 되었기 때문.
                    // 즉, 화면에서 선택한 인증서의 인덱스는 getSelectedCertIndex() 로 처리하고,
                    // 서명함수를 호출할땐 실제의 인덱스를 설정해야하므로 인증서 리스트 객체에 있는 실제 인덱스를 사용한다.
                    // =======================================

                    // 해당 인증서의 실제 인덱스
                    int selectedCertIndex = mCertList.get(getSelectedCertIndex()).getIndex();

                    if (isSingle) {
                        // 20.05.28 : GPKI 는 signedInfo 변경
                        String plainText = signedInfoVal;
                        if (mCertList.get(getSelectedCertIndex()).getPubKeyAlg().indexOf("KCDSA") >= 0) {
                            plainText = mXSignTester.convertSignedInfoGPKI(signedInfoVal);
                        }

                        bySignedData = mXSignTester.xmlCertSignData(selectedCertIndex, plainText, certPassword.getText().toString());
                        byCurrentCert = mXSignTester.getCurrentCert(selectedCertIndex);

                        String sSignedDataB64 = mXSignTester.makeEncodeBase64(bySignedData);
                        String sCertB64 = mXSignTester.makeEncodeBase64(byCurrentCert);

                        resultIntent.putExtra("isSingle", isSingle);
                        resultIntent.putExtra("signedInfoVal", signedInfoVal);
                        resultIntent.putExtra("sSignedDataB64", sSignedDataB64);
                        resultIntent.putExtra("sCertB64", sCertB64);
                        resultIntent.putExtra("Result", "서명 생성 성공");
                    } else {
                        try {
                            // JSONObject <docID, signedInfo> 형태일 경우
                            JSONObject signedInfoObject = new JSONObject(signedInfoVal);
                            JSONObject signedResultObject = new JSONObject();

                            Iterator<String> keys = signedInfoObject.keys();
                            while (keys.hasNext()) {
                                String docID = keys.next();
                                String signedInfo = (String) signedInfoObject.get(docID);
                                if (mCertList.get(getSelectedCertIndex()).getPubKeyAlg().indexOf("KCDSA") >= 0) {
                                    signedInfo = mXSignTester.convertSignedInfoGPKI(signedInfo);
                                }

                                bySignedData = mXSignTester.xmlCertSignData(selectedCertIndex, signedInfo, certPassword.getText().toString());
                                String sSignDataB64 = mXSignTester.makeEncodeBase64(bySignedData);
                                signedResultObject.put(docID, sSignDataB64);
                            }

                            byCurrentCert = mXSignTester.getCurrentCert(selectedCertIndex);
                            String sCertB64 = mXSignTester.makeEncodeBase64(byCurrentCert);

                            resultIntent.putExtra("isSingle", isSingle);
                            resultIntent.putExtra("signObject", signedResultObject.toString());
                            resultIntent.putExtra("sCertB64", sCertB64);
                            resultIntent.putExtra("Result", "서명 생성 성공");
                        } catch (JSONException e) {
                            // JSONArray [singedInfo, ...] 형태일 경우

                            JSONArray signedInfoArr = new JSONArray(signedInfoVal);
                            JSONArray signedResultArr = new JSONArray();

                            for (int i = 0; i < signedInfoArr.length(); i++) {
                                String signedInfo = (String) signedInfoArr.get(i);
                                if (mCertList.get(getSelectedCertIndex()).getPubKeyAlg().indexOf("KCDSA") >= 0) {
                                    signedInfo = mXSignTester.convertSignedInfoGPKI(signedInfo);

                                }
                                bySignedData = mXSignTester.xmlCertSignData(selectedCertIndex, signedInfo, certPassword.getText().toString());
                                String sSignDataB64 = mXSignTester.makeEncodeBase64(bySignedData);
                                signedResultArr.put(sSignDataB64);
                            }

                            byCurrentCert = mXSignTester.getCurrentCert(selectedCertIndex);
                            String sCertB64 = mXSignTester.makeEncodeBase64(byCurrentCert);

                            resultIntent.putExtra("isSingle", isSingle);
                            resultIntent.putExtra("signObject", signedResultArr.toString());
                            resultIntent.putExtra("sCertB64", sCertB64);
                            resultIntent.putExtra("Result", "서명 생성 성공");

                        } catch (Exception e) {
                            // UnExpected value
                            e.printStackTrace();

                        }
                    }

                } catch (MagicXSign_Exception e) {
                    e.printStackTrace();
                    resultIntent.putExtra("binSignData", bySignedData);
                    resultIntent.putExtra("Result", e.getErrorMessage());

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    baseContext.startActivity(resultIntent);
                }

            }
        }).show();

    }

    /**
     * Enveloped Data 암/복호 수행
     *
     * @param
     */
    private void processEnvelopedEndAndDec() {
        XSignDialog.processInputTwoEdit(baseContext, R.string.function_list_cert_cms_enc_dec, R.string.dialog_input_plainText, R.string.dialog_input_cert_password,
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(baseContext, XSignProcessResultActivity.class);
                        EditText plainData = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_input1);
                        EditText certPassword = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_input2);
                        StringBuffer encBuffer = new StringBuffer();
                        StringBuffer decBuffer = new StringBuffer();

                        try {
                            if (mXSignTester.envelopedEncryptAndDecrypt(mCertList.get(getSelectedCertIndex()).getIndex(), plainData.getText().toString(), certPassword.getText().toString()
                                    , encBuffer, decBuffer) == true) {
                                intent.putExtra("EncData", encBuffer.toString());
                                intent.putExtra("DecData", decBuffer.toString());
                                intent.putExtra("Result", "Enveloped 암/복호 성공");
                            } else
                                intent.putExtra("Result", "Enveloped 암/복호 실패");

                        } catch (MagicXSign_Exception e) {
                            intent.putExtra("Result", e.getErrorMessage());
                            e.printStackTrace();
                        } finally {
                            baseContext.startActivity(intent);
                        }
                    }
                }).show();
    }

    /**
     * 인증서 삭제 수행
     *
     * @param paramCert
     */
    private void processDeleteCert(final XSignTester.CertDetailInfo paramCert) {
        XSignDialog.processShowCert(baseContext, paramCert, R.string.function_list_cert_delete, R.string.dialog_confirm_delete, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(baseContext, XSignProcessResultActivity.class);

                try {
                    //mXSignTester.deleteCert(getSelectedCertIndex());
                    mXSignTester.deleteCert(paramCert.getIndex());

                    intent.putExtra("Result", "인증서 삭제 성공");
                } catch (MagicXSign_Exception e) {
                    intent.putExtra("Result", e.getErrorMessage());
                    e.printStackTrace();
                } finally {
                    baseContext.startActivity(intent);
                    ((Activity) baseContext).finish();
                }
            }
        }).show();
    }

    /**
     * 인증서 본인확인 테스트
     */
    private void processCheckVID() {
        XSignDialog.processInputTwoEdit(baseContext, R.string.function_list_cert_check_vid, R.string.dialog_input_vid, R.string.dialog_input_cert_password,
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(baseContext, XSignProcessResultActivity.class);
                        EditText VID = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_input1);
                        EditText certPassword = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_input2);

                        try {

                            if (mXSignTester.checkVID(mCertList.get(getSelectedCertIndex()).getIndex(), VID.getText().toString(), certPassword.getText().toString()) == true)
                                intent.putExtra("Result", "VID 검증 성공");
                            else
                                intent.putExtra("Result", "VID 검증 실패");

                        } catch (MagicXSign_Exception e) {
                            intent.putExtra("Result", e.getErrorMessage());
                            e.printStackTrace();
                        } finally {
                            baseContext.startActivity(intent);
                        }
                    }
                }).show();
    }

    /**
     * 비대칭키 암/복호 테스트
     */
    private void processASymEncAndDec() {
        XSignDialog.processInputTwoEdit(baseContext, R.string.function_list_cert_asymmetric_enc_dec, R.string.dialog_input_plainText, R.string.dialog_input_cert_password,
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(baseContext, XSignProcessResultActivity.class);
                        EditText plainData = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_input1);
                        EditText certPassword = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_input2);
                        StringBuffer encBuffer = new StringBuffer();
                        StringBuffer decBuffer = new StringBuffer();

                        try {
                            if (mXSignTester.AsymcEncryptAndDecrypt(mCertList.get(getSelectedCertIndex()).getIndex(), plainData.getText().toString(), certPassword.getText().toString()
                                    , encBuffer, decBuffer) == true) {
                                intent.putExtra("EncData", encBuffer.toString());
                                intent.putExtra("DecData", decBuffer.toString());
                                intent.putExtra("Result", "비대칭 암/복호 성공");
                            } else
                                intent.putExtra("Result", "비대칭 암/복호 실패");

                        } catch (MagicXSign_Exception e) {
                            intent.putExtra("Result", e.getErrorMessage());
                            e.printStackTrace();
                        } finally {
                            baseContext.startActivity(intent);
                        }
                    }
                }).show();
    }

    /**
     * 인증서 List 구성을 위한 Adapter
     *
     * @author sonhyuntak
     */
    private class XSignCertList extends ArrayAdapter<XSignTester.CertDetailInfo> {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<XSignTester.CertDetailInfo> mDataList;
        private int mLayoutID;

        public XSignCertList(Context context, int resourceID, ArrayList<XSignTester.CertDetailInfo> arrayList) {
            super(context, resourceID, arrayList);

            mLayoutID = resourceID;
            mContext = context;
            mDataList = arrayList;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            XSignTester.CertDetailInfo certInfo = mDataList.get(position);

            if (convertView == null)
                convertView = mInflater.inflate(mLayoutID, null);

            ((TextView) convertView.findViewById(R.id.txt_cert_index)).setText("[Index " + position + "]");
            ((TextView) convertView.findViewById(R.id.txt_cert_dn)).setText(certInfo.getSubjectDN());

            return convertView;
        }
    }
}
