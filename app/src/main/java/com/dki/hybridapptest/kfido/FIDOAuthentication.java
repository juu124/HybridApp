package com.dki.hybridapptest.kfido;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;

import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.dream.magic.fido.authenticator.local.kfido.KFIDOCertInfo;
import com.dream.magic.fido.rpsdk.callback.FIDOCallbackResult;
import com.dream.magic.fido.rpsdk.client.Authentication;
import com.dream.magic.fido.rpsdk.client.ContextKeys;
import com.dream.magic.fido.rpsdk.util.KFidoUtil;

import java.util.ArrayList;
import java.util.Hashtable;

public class FIDOAuthentication {

    public final int ConnectTimeOut = 1000 * 300;
    public final int ReadTimeOut = 1000 * 300;

    //RP SDK 를 사용하기 위한 객체
    private Authentication mAuth = null;
    private FIDOCallbackResult mFidoCallback = null;

    private String mSubjdn = null;
    private String reqUrl = "";
    private String resUrl = "";

    private String TAG = "FIDOAuthentication";

    public FIDOAuthentication(Context context, int FIDO_TYPE, FIDOCallbackResult fidoCallback, String certType, String subjdn) {
        mFidoCallback = fidoCallback;

        reqUrl = Constant.KPIDO_URL + Constant.REQ_URL + certType;
        resUrl = Constant.KPIDO_URL + Constant.RES_URL + certType;

        GLog.d("reqUrl : " + reqUrl + ", resUrl : " + resUrl);

        mAuth = new Authentication(context, FIDO_TYPE, mFidoCallback, reqUrl, resUrl);
        mAuth.setNetworkTimeout(ConnectTimeOut, ReadTimeOut);
        this.mSubjdn = subjdn;

    }

    //========= FIDO 인증 =======
    private void startAuthentication(String transferMessage, ArrayList<byte[]> tobedatas) {

        // RPContext를 위한 세팅
        Hashtable<Object, Object> settingValue = new Hashtable<Object, Object>();
        if (!TextUtils.isEmpty(mSubjdn)) {
            //subjDn 값이 있을경우 getRegistedCertListWithUserID로 인증서 추출
            try {
                KFIDOCertInfo kCertificateInfo[] = mAuth.getRegistedCertListWithUserID(mSubjdn);
//				GLog.d("FIDOAuthentication", "kCertificateInfo : " + kCertificateInfo);
//				GLog.d("FIDOAuthentication", "kCertificateInfo.length : " + kCertificateInfo.length);
                if (kCertificateInfo != null && kCertificateInfo.length == 1) {
                    settingValue.put(ContextKeys.KEY_CERTIFICATE, kCertificateInfo[0]); // value Type : KFIDOCertInfo
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {

            try {
                //subjDn 값이 없지만 인증서 목록이 하나일경우 첫번째 인증서 추출
                KFIDOCertInfo kCertificateInfo[] = mAuth.getRegistedCertList();
                if (kCertificateInfo != null && kCertificateInfo.length == 1) {
                    settingValue.put(ContextKeys.KEY_CERTIFICATE, kCertificateInfo[0]); // value Type : KFIDOCertInfo
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        // ======== KEY_CERTIFICATE 를 제외한 다른 Key의 value 는 String 으로 설정 ==========
        settingValue.put(ContextKeys.KEY_DEVICEMODEL, Build.MODEL);
        settingValue.put(ContextKeys.KEY_DEVICEOS, "A|" + VERSION.RELEASE);
        if (transferMessage != null) {
            transferMessage = KFidoUtil.getBase64URLToString(transferMessage.getBytes());
            settingValue.put(ContextKeys.KEY_TRANSACTIONTEXT, transferMessage);
        }
        // ============================================================================

        mAuth.setBioVerifyStateCheckEnable(true);
        mAuth.startAuthentication(settingValue, tobedatas);

    }

    public void transgerAuthFIDO(String transferMessage) {
        startAuthentication(transferMessage, null);
    }

    public void loginAuthFIDO(ArrayList<byte[]> tobedatas) {
        startAuthentication(null, tobedatas);
    }

    public ArrayList<byte[]> getSignedData() {
        return mAuth.getSignedData();
    }

    public byte[] getDn() {
        return mAuth.getLastUsedCert();
    }

}