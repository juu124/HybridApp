package com.dki.hybridapptest.fcm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.dki.hybridapptest.utils.GLog;

import java.nio.charset.StandardCharsets;

import m.client.push.library.common.PushConstants;

public class MessageArrivedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        GLog.i("onReceived action : " + action);

        if (!TextUtils.isEmpty(action) && action.equals(context.getPackageName() + PushConstants.ACTION_GCM_MESSAGE_ARRIVED)) {
            try {
                // Retrieve payload data in three ways
                String data = intent.getStringExtra(PushConstants.KEY_JSON);
                String rawData = intent.getStringExtra(PushConstants.KEY_ORIGINAL_PAYLOAD_STRING);
                byte[] rawDataBytes = intent.getByteArrayExtra(PushConstants.KEY_ORIGINAL_PAYLOAD_BYTES);

                GLog.d(GLog.getPretty(data));
                GLog.i("received raw data : " + rawData);

                if (rawDataBytes != null) {
                    GLog.i("received bytes data : " + new String(rawDataBytes, StandardCharsets.UTF_8));
                }

                // Create a notification (You may uncomment this line if you want to create a notification)
                // PushNotifyHelper.showNotification(context, new JSONObject(data));
            } catch (Exception e) {
                GLog.e("onReceived exception : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
