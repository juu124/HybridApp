package com.dki.hybridapptest.kfido;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;

import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.dream.magic.fido.rpsdk.callback.FIDOCallbackResult;
import com.dream.magic.fido.rpsdk.client.ContextKeys;
import com.dream.magic.fido.rpsdk.client.LOCAL_AUTH_TYPE;
import com.dream.magic.fido.rpsdk.client.MagicFIDOUtil;
import com.dream.magic.fido.rpsdk.client.Registration;
import com.dream.magic.fido.uaf.protocol.kfido.KCertificate;

import java.util.ArrayList;
import java.util.Hashtable;

public class FIDORegistration {

    public final int ConnectTimeOut = 1000 * 300;
    public final int ReadTimeOut = 1000 * 300;

    //RP SDK 를 사용하기 위한 객체
    private Registration mRegist = null;
    private FIDOCallbackResult mFidoCallback = null;
    private Context mContext = null;
    private String TAG = "FIDORegistraion";
    private String reqUrl = "";
    private String resUrl = "";

    public FIDORegistration(Context context, int FIDO_TYPE, FIDOCallbackResult fidoCallback, String certType) {
        mFidoCallback = fidoCallback;

        mContext = context;
        reqUrl = Constant.KPIDO_URL + Constant.REQ_URL + certType;
        resUrl = Constant.KPIDO_URL + Constant.RES_URL + certType;

        GLog.d("reqUrl : " + reqUrl + ", resUrl : " + resUrl);

        mRegist = new Registration(context, FIDO_TYPE, mFidoCallback, reqUrl, resUrl);
        mRegist.setNetworkTimeout(ConnectTimeOut, ReadTimeOut);

    }

    private void registerFIDO(Object obj, ArrayList<byte[]> tobeSignDatas) {

//		setAuthOption(mContext);
        // RPContext를 위한 세팅
        Hashtable<Object, Object> settingValue = new Hashtable<Object, Object>();
        settingValue.put(ContextKeys.KEY_DEVICEMODEL, Build.MODEL);
        settingValue.put(ContextKeys.KEY_DEVICEOS, "A|" + VERSION.RELEASE);
        mRegist.startRegistration((KCertificate) obj, settingValue, tobeSignDatas);

    }

    public void registFIDO(KCertificate kCert, ArrayList<byte[]> tobeSignDatas) {
        registerFIDO(kCert, tobeSignDatas);
    }


    public ArrayList<byte[]> getSignedData() {
        return mRegist.getSignedData();
    }

    //인증장치 옵션 설정
    private void setAuthOption(Context mContext) {
        MagicFIDOUtil magicFIDOUtil = new MagicFIDOUtil(mContext);

        Hashtable<String, Object> authOption = new Hashtable<String, Object>();
//		authOption.put(MagicFIDOUtil.KEY_RETRY_COUNT_TO_LOCK , 0);
        authOption.put(MagicFIDOUtil.KEY_RETRY_COUNT_TO_LOCK, 3);
        authOption.put(MagicFIDOUtil.KEY_MIN_LENGTH, 3);
        authOption.put(MagicFIDOUtil.KEY_MAX_LENGTH, 5);
//		authOption.put(MagicFIDOUtil.KEY_LOCK_TIME,  0 );
        authOption.put(MagicFIDOUtil.KEY_LOCK_TIME, 10);
        authOption.put(MagicFIDOUtil.KEY_USE_NUMBER_KEYPAD, true);
        //패스코드 숫자키패드 셔플 비활성화 여부 설정 (true : 셔플 비활성화)
        authOption.put(MagicFIDOUtil.KEY_SHUFFLE_DISABLE, true);
        magicFIDOUtil.setAuthenticatorOptions(LOCAL_AUTH_TYPE.LOCAL_PACODE_TYPE, authOption);
    }


}