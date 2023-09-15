package com.dki.hybridapptest.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.PushApsAlertData;
import com.dki.hybridapptest.dto.PushApsData;
import com.dki.hybridapptest.dto.PushMpsData;
import com.dki.hybridapptest.dto.PushMpsImgUrl;
import com.dki.hybridapptest.ui.activity.MainActivity;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private NotificationChannel mChannel;
    private String msgData;
    private JSONObject jsonObject;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        GLog.d();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        GLog.d("message === " + message);
        if (message.getData().size() > 0) {
            Map<String, String> dataMap = message.getData();
            GLog.d("Message data payload: " + dataMap);

            msgData = dataMap.get("message");
            try {
                if (!TextUtils.isEmpty(msgData)) {
                    jsonObject = new JSONObject(msgData);
                    PushMpsData pushMpsData = (PushMpsData) jsonObject.get("mps");
                    PushMpsImgUrl pushMpsImgUrl = pushMpsData.getExt();
                    String imgUrl = pushMpsImgUrl.getImageUrl();
                    GLog.d("Message appid: " + pushMpsData.getAppId());
                    GLog.d("Message imgUrl: " + imgUrl);
                    GLog.d("Message seqno: " + pushMpsData.getSeqNo());
                    GLog.d("Message sender: " + pushMpsData.getSender());
                    GLog.d("Message senddate: " + pushMpsData.getSendDate());
                    GLog.d("Message db_in: " + pushMpsData.getDbIn());
                    GLog.d("Message pushkey: " + pushMpsData.getPushKey());
                    GLog.d("Message sms: " + pushMpsData.getSms());

                    PushApsData pushApsData = (PushApsData) jsonObject.get("aps");
                    PushApsAlertData pushApsAlertData = pushApsData.getAlert();
                    String title = pushApsAlertData.getTitle();
                    String body = pushApsAlertData.getBody();
                    GLog.d("Message Title: " + title);
                    GLog.d("Message Body: " + body);
                    showNotification(title, body);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            GLog.d("onReceived msg : " + msgData);
        }

        if (message.getNotification() != null) {
            GLog.d("메세지 세부 내용");
            GLog.d("Message Notification Title: " + message.getNotification().getTitle());
            GLog.d("Message Notification Body: " + message.getNotification().getBody());
            GLog.d("Message Notification getImageUrl: " + message.getNotification().getImageUrl());

        } else {
            GLog.d("message.getNotification() == null");
        }

    }

    private void showNotification(String messageTitle, String messageBody) {
        GLog.d("messageTitle === " + messageTitle);
        GLog.d("messageBody === " + messageBody);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        String mChannelName = getString(R.string.default_notification_channel_name);
        String channelId = getString(R.string.default_notification_channel_id);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mChannel == null) {
                mChannel = new NotificationChannel(channelId, mChannelName, NotificationManager.IMPORTANCE_HIGH);
            }
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(Constant.NOTIFICATION_ID_FOR_PEDOMETER, notificationBuilder.build());
        startForeground(Constant.NOTIFICATION_ID_FOR_PEDOMETER, notificationBuilder.build());
    }
}
