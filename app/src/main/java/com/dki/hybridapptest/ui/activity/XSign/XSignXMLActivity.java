package com.dki.hybridapptest.ui.activity.XSign;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dki.hybridapptest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;


/**
 * 20.04.13 : XML 서명을 위한 클래스
 */
public class XSignXMLActivity extends XSignBaseActivity {

    //private static final String XML_URL_ROOT = "http://10.10.30.151:8080/XSIGN_AJAX/";
    private static final String XML_URL_ROOT = "http://10.10.28.97:8880/XSignWebPlugin_v1.11/";
    private static final String XML_SINGLE_URL = XML_URL_ROOT + "xml.jsp";
    private static final String XML_MULTI_URL = XML_URL_ROOT + "xmlMulti.jsp";
    private static final String XML_SUBMIT_URL = XML_URL_ROOT + "xmlR.jsp";

    private boolean isSingle = true;

    // for single sign
    private String sSignedDataB64 = null;
    private String sCertB64 = null;
    private String signedInfoVal = null;

    // for multi sign
    private JSONObject xmlMultiSignObject;
    private JSONArray xmlMultiSignArr;

    TextView signedInfoLabel = null;
    RadioGroup rg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityTitle("MagicXSign XML");

        setContentView(R.layout.activity_user_certification);

        signedInfoLabel = (TextView) findViewById(R.id.signedInfoVal);
        rg = (RadioGroup) findViewById(R.id.radioGroup);
        rg.setOnCheckedChangeListener(radioGroupButtonChangeListner);

        Intent intent = getIntent();

        String result = intent.getStringExtra("Result");

        // 서명 했는지 판별
        if (result != null) {

            this.isSingle = intent.getBooleanExtra("isSingle", true);

            if (this.isSingle) {

                this.sSignedDataB64 = intent.getStringExtra("sSignedDataB64");
                this.sCertB64 = intent.getStringExtra("sCertB64");
                this.signedInfoVal = intent.getStringExtra("signedInfoVal");
                showSignResult(this.sSignedDataB64, this.signedInfoVal, result);

            } else {

                try {
                    this.xmlMultiSignObject = new JSONObject((String) intent.getExtras().get("signObject"));
                    this.sCertB64 = intent.getStringExtra("sCertB64");

                    String signedInfoStream = "";

                    Iterator<String> keys = this.xmlMultiSignObject.keys();

                    while (keys.hasNext()) {
                        signedInfoStream += keys.next() + ",";
                    }
                    showSignResult(this.xmlMultiSignObject.toString(), signedInfoStream, result);

                } catch (JSONException e) {

                    try {
                        this.xmlMultiSignArr = new JSONArray((String) intent.getExtras().get("signObject"));
                        this.sCertB64 = intent.getStringExtra("sCertB64");

                        showSignResult(this.xmlMultiSignArr.toString(), null, result);

                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }

                } catch (Exception e) {

                    // unexpected Data
                    e.printStackTrace();

                }


            }


        }


    }

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListner = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.radioButton) {
                isSingle = true;
            } else {
                isSingle = false;
            }
        }
    };


    public void onClickRequestSignedInfo(View vw) {

        if (isSingle) {
            XMLSignedInfoRequest req = new XMLSignedInfoRequest();
            req.execute();
        } else {
            XMLSignedInfoMultiRequest req = new XMLSignedInfoMultiRequest();
            req.execute();
        }
    }

    public void onClickCertList(View vw) {

        Intent intent = new Intent(baseContext, XSignCertListActivity.class);
        intent.putExtra("isSingle", isSingle);

        final String signedInfoVal = (String) signedInfoLabel.getText();

        if (signedInfoVal != null && signedInfoVal.length() > 0) {

            if (isSingle) {

                intent.putExtra("signedInfoVal", signedInfoVal);

            } else {

                intent.putExtra("signedInfoVal", signedInfoVal);

            }

            baseContext.startActivity(intent);

        }

    }

    public void showSignResult(String sSignedData, String signedInfoVal, String result) {

        if (signedInfoVal != null) {
            TextView signedInfoArea = (TextView) findViewById(R.id.signedInfoVal);
            signedInfoArea.setText(signedInfoVal);
        }

        TextView signedArea = (TextView) findViewById(R.id.signedDataVal);
        signedArea.setText(result + "\n" + sSignedData);
    }

    public void onClickSubmit(View vw) {

        XMLSignDataSubmit submit = new XMLSignDataSubmit();
        if (this.isSingle) {

            submit.execute(this.sSignedDataB64, this.signedInfoVal, this.sCertB64);

        } else {
            submit.execute("");
        }


    }

    private String getCookieSession() {
        String sessionId = null;
        SharedPreferences pref = baseContext.getSharedPreferences("sessionCookie", Context.MODE_PRIVATE);
        sessionId = pref.getString("sessionid", null);
        return sessionId;
    }

    private void setCookieSession(HttpURLConnection conn) {

        List<String> cookies = conn.getHeaderFields().get("Set-Cookie");

        String sessionId = null;
        if (cookies != null) {
            for (String cookie : cookies) {
                sessionId = cookie.split(";\\s*")[0];
            }

            // store to pref
            if (sessionId != null) {
                SharedPreferences pref = baseContext.getSharedPreferences("sessionCookie", baseContext.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("sessionid", sessionId);
                edit.apply();
            }
        }
    }


    private class XMLSignedInfoRequest extends AsyncTask<String, Void, String> {

        String xmlDoc = "<TaxInvoice xsi:schemaLocation=\"urn:kr:or:kec:standard:Tax:ReusableAggregateBusinessInformationEntitySchemaModule:1:0 http://www.kec.or.kr/standard/Tax/TaxInvoiceSchemaModule_1.0.xsd\" xmlns=\"urn:kr:or:kec:standard:Tax:ReusableAggregateBusinessInformationEntitySchemaModule:1:0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "<ExchangedDocument>" +
                "<Test>1</Test>" +
                "</ExchangedDocument>" +
                "<TestDocument>1</TestDocument>" +
                "</TaxInvoice>";

        protected void onPreExecute() {

        }

        protected String doInBackground(String... params) {
            try {
                URL url = new URL(XML_SINGLE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // session set
                String sessionId = getCookieSession();
                conn.setRequestProperty("Cookie", sessionId);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Cache-Control", "no-cache");

                OutputStream os = null;
                InputStream is = null;
                ByteArrayOutputStream baos = null;

                String responseData = null;

                // param setting
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("xmlDoc", xmlDoc);

                os = conn.getOutputStream();
                os.write(jsonObject.toString().getBytes());
                os.flush();

                int resCode = conn.getResponseCode();

                // response ok
                if (resCode == HttpURLConnection.HTTP_OK) {
                    is = conn.getInputStream();
                    baos = new ByteArrayOutputStream();
                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData = null;
                    int nLen = 0;
                    while ((nLen = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                        baos.write(byteBuffer, 0, nLen);
                    }
                    byteData = baos.toByteArray();

                    responseData = new String(byteData);
                    setCookieSession(conn); // session set

                    JSONObject resJSON = new JSONObject(responseData);
                    if (resJSON.get("nRv").equals("0")) {

                        String signedInfo = (String) resJSON.get("signedInfo");
                        String documentID = (String) resJSON.get("documentID");

                        return signedInfo;

                    } else {
                        System.out.println("[ERROR] Fail to retrieve SignedInfo");
                    }

                } else {

                    System.out.println("[ERROR] Fail to response from the server code :: " + resCode);
                    os.close();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);

            TextView signedInfoVal = (TextView) findViewById(R.id.signedInfoVal);
            signedInfoVal.setText(s);
        }

        @Override
        protected void onCancelled() {
            System.out.println("cancelled");
        }

    }

    private class XMLSignedInfoMultiRequest extends AsyncTask<String, Void, String> {

        String xmlDoc1 = "<TaxInvoice xsi:schemaLocation=\"urn:kr:or:kec:standard:Tax:ReusableAggregateBusinessInformationEntitySchemaModule:1:0 http://www.kec.or.kr/standard/Tax/TaxInvoiceSchemaModule_1.0.xsd\" xmlns=\"urn:kr:or:kec:standard:Tax:ReusableAggregateBusinessInformationEntitySchemaModule:1:0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "<ExchangedDocument>" +
                "<Test>1</Test>" +
                "</ExchangedDocument>" +
                "<TestDocument>1</TestDocument>" +
                "</TaxInvoice>";
        String xmlDoc2 = "<TaxInvoice xsi:schemaLocation=\"urn:kr:or:kec:standard:Tax:ReusableAggregateBusinessInformationEntitySchemaModule:1:0 http://www.kec.or.kr/standard/Tax/TaxInvoiceSchemaModule_1.0.xsd\" xmlns=\"urn:kr:or:kec:standard:Tax:ReusableAggregateBusinessInformationEntitySchemaModule:1:0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "<ExchangedDocument>" +
                "<Test>2</Test>" +
                "</ExchangedDocument>" +
                "<TestDocument>2</TestDocument>" +
                "</TaxInvoice>";
        String xmlDoc3 = "<TaxInvoice xsi:schemaLocation=\"urn:kr:or:kec:standard:Tax:ReusableAggregateBusinessInformationEntitySchemaModule:1:0 http://www.kec.or.kr/standard/Tax/TaxInvoiceSchemaModule_1.0.xsd\" xmlns=\"urn:kr:or:kec:standard:Tax:ReusableAggregateBusinessInformationEntitySchemaModule:1:0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "<ExchangedDocument>" +
                "<Test>3</Test>" +
                "</ExchangedDocument>" +
                "<TestDocument>3</TestDocument>" +
                "</TaxInvoice>";

        protected void onPreExecute() {
        }

        protected String doInBackground(String... params) {
            try {
                URL url = new URL(XML_MULTI_URL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // session setting
                String sessionId = getCookieSession();
                conn.setRequestProperty("Cookie", sessionId);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Cache-Control", "no-cache");

                OutputStream os = null;
                InputStream is = null;
                ByteArrayOutputStream baos = null;

                // param setting
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(xmlDoc1);
                jsonArray.put(xmlDoc2);
                jsonArray.put(xmlDoc3);

                os = conn.getOutputStream();
                os.write(jsonArray.toString().getBytes());
                os.flush();

                String responseData = null;
                int resCode = conn.getResponseCode();

                setCookieSession(conn); // session set

                if (resCode == HttpURLConnection.HTTP_OK) {

                    is = conn.getInputStream();
                    baos = new ByteArrayOutputStream();
                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData = null;
                    int nLen = 0;
                    while ((nLen = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                        baos.write(byteBuffer, 0, nLen);
                    }
                    byteData = baos.toByteArray();

                    responseData = new String(byteData);

                    JSONObject resJSON = new JSONObject(responseData);
                    if (resJSON.get("nRv").equals("0")) {

                        JSONObject signedInfoObj = (JSONObject) resJSON.get("signedInfoObj");
                        JSONArray signedInfoArr = (JSONArray) resJSON.get("signedInfoArray");


                        // JSONObject or JSONArray
                        // return signedInfoObj.toString();
                        return signedInfoArr.toString();


                    } else {
                        System.out.println("[ERROR] Fail to retrieve SignedInfo");
                    }


                } else {
                    System.out.println("[ERROR] Fail to Request to server");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);

            TextView signedInfoVal = (TextView) findViewById(R.id.signedInfoVal);
            signedInfoVal.setText(s);
        }

        @Override
        protected void onCancelled() {
            System.out.println("cancelled");
        }
    }

    private class XMLSignDataSubmit extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {

        }

        protected String doInBackground(String... params) {
            try {
                URL url = new URL(XML_SUBMIT_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Cache-Control", "no-cache");

                // session set
                String sessionId = getCookieSession();
                conn.setRequestProperty("Cookie", sessionId);

                OutputStream os = null;
                InputStream is = null;
                ByteArrayOutputStream baos = null;

                // param setting
                JSONObject jsonObject = new JSONObject();

                if (isSingle) {
                    jsonObject.put("signedData", params[0]); // 서명결과 값
                    jsonObject.put("signOrigin", params[1]); // 서명원문 : signedInfo
                    jsonObject.put("cert", params[2]);
                } else {

                    if (xmlMultiSignObject != null) {
                        // jsonObject
                        jsonObject.put("signedData", xmlMultiSignObject);
                    } else if (xmlMultiSignArr != null && xmlMultiSignArr.length() > 0) {
                        // jsonArray
                        jsonObject.put("signedData", xmlMultiSignArr);
                    }

                    jsonObject.put("cert", sCertB64);
                }

                os = conn.getOutputStream();
                os.write(jsonObject.toString().getBytes());
                os.flush();

                String responseData = null;
                int resCode = conn.getResponseCode();

                // response ok
                if (resCode == HttpURLConnection.HTTP_OK) {
                    is = conn.getInputStream();
                    baos = new ByteArrayOutputStream();
                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData = null;
                    int nLen = 0;
                    while ((nLen = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                        baos.write(byteBuffer, 0, nLen);
                    }
                    byteData = baos.toByteArray();

                    responseData = new String(byteData);

                    JSONObject resJSON = new JSONObject(responseData);
                    if (resJSON.get("nRv").equals("0")) {

                        boolean isVerifyOK = (Boolean) resJSON.get("verify");
                        String sResult = (String) resJSON.get("sResult");
                        return "Success";

                    } else {
                        System.out.println("[ERROR] Fail to Verify XML...");
                    }

                } else {

                    System.out.println("[ERROR] Fail to response from the server code :: " + resCode);
                    os.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String s) {

            if (s != null && s.length() > 0) {
                Toast.makeText(baseContext, "요청 성공", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(baseContext, "요청 실패 [" + s + "]", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            System.out.println("Submit cancelled...");
        }

    }


}
