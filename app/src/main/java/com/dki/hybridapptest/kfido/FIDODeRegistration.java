package com.dki.hybridapptest.kfido;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;

import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.dream.magic.fido.rpsdk.callback.FIDOCallbackResult;
import com.dream.magic.fido.rpsdk.client.ContextKeys;
import com.dream.magic.fido.rpsdk.client.Deregistration;

import java.util.Hashtable;

public class FIDODeRegistration {

    public final int ConnectTimeOut = 1000 * 300;
    public final int ReadTimeOut = 1000 * 300;

    //RP SDK 를 사용하기 위한 객체
    private Deregistration mDeregist = null;
    private FIDOCallbackResult mFidoCallback = null;
    private String mUserKey;
    private String reqUrl = "";

    private String TAG = "FIDODeRegistration";

    public FIDODeRegistration(Context context, int FIDO_TYPE, FIDOCallbackResult fidoCallback, String certType, String userKey) {
        mFidoCallback = fidoCallback;

        reqUrl = Constant.KPIDO_URL + Constant.REQ_URL + certType;
        GLog.d("reqUrl : " + reqUrl);

        mDeregist = new Deregistration(context, FIDO_TYPE, mFidoCallback, reqUrl);
        mDeregist.setNetworkTimeout(ConnectTimeOut, ReadTimeOut);
        mUserKey = userKey;
    }

    public void deRegistFIDO() {
        // RPContext를 위한 세팅
        Hashtable<Object, Object> settingValue = new Hashtable<Object, Object>();
        settingValue.put(ContextKeys.KEY_DEVICEMODEL, Build.MODEL);
        settingValue.put(ContextKeys.KEY_DEVICEOS, "A|" + VERSION.RELEASE);
        if (!"".equals(mUserKey)) {
            settingValue.put(ContextKeys.KEY_USERID, mUserKey);
        }
        mDeregist.startDeregistration(settingValue);
    }

}