package com.dki.hybridapptest.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.PushApsData;
import com.dki.hybridapptest.ui.activity.MainActivity;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private NotificationChannel mChannel;
    private String msgData;
    private PushApsData pushApsData;
    private String imgUrl = null;
    private String title = null;
    private String body = null;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        GLog.d("message === " + message);
        if (message.getData().size() > 0) {
            Map<String, String> dataMap = message.getData();
            GLog.d("Message data payload: " + dataMap);
            JSONObject jsonObject = null;
            msgData = dataMap.get("message");
            try {
                if (!TextUtils.isEmpty(msgData)) {
                    GLog.d("Message data msgData: " + msgData);
                    jsonObject = new JSONObject(msgData);
                    String imgUrl = null;
                    String title = null;
                    String body = null;

//                    String mps = jsonObject.getString("mps");
//                    String aps = jsonObject.getString("aps");
//                    JSONObject jsonAps = new JSONObject(aps);
//                    String alert = jsonAps.getString("alert");
//                    JSONObject jsonAlert = new JSONObject(alert);
//                    PushApsData pushApsData = new PushApsData();
//                    pushApsData.setAlert((PushApsAlertData) jsonAps.get("alert"));
//                    PushApsAlertData pushApsAlertData = new PushApsAlertData(jsonAlert.getString("title"), jsonAlert.getString("body"));
//
//                    title = pushApsAlertData.getTitle();
//                    body = pushApsAlertData.getBody();

                    // dto 사용 안한 경우
                    String mps = jsonObject.getString("mps");
                    JSONObject jsonMps = new JSONObject(mps);
                    String aps = jsonObject.getString("aps");
                    JSONObject jsonAps = new JSONObject(aps);

                    GLog.d("Message jsonMps: " + jsonMps);
                    GLog.d("Message jsonAps: " + jsonAps);

                    if (!TextUtils.isEmpty(jsonMps.getString("ext"))) {
                        String ext = jsonMps.getString("ext");
                        JSONObject jsonExt = new JSONObject(ext);
                        imgUrl = jsonExt.getString("imageUrl");
                        GLog.d("Message imgUrl: " + imgUrl);
                    }

                    String alert = jsonAps.getString("alert");
                    JSONObject jsonAlert = new JSONObject(alert);
                    title = jsonAlert.getString("title");
                    body = jsonAlert.getString("body");
                    GLog.d("Message title: " + title);
                    GLog.d("Message body: " + body);

                    showNotification(title, body, imgUrl);
                }
            } catch (JSONException e) {
                try {
                    GLog.d("사진 없어서 여기로 옴");

//                    String aps = jsonObject.getString("aps");
//                    JSONObject jsonAps = new JSONObject(aps);
//                    String alert = jsonAps.getString("alert");
//                    JSONObject jsonAlert = new JSONObject(alert);
//                    PushApsData pushApsData = new PushApsData();
//                    pushApsData.setAlert((PushApsAlertData) jsonAps.get("alert"));
//                    PushApsAlertData pushApsAlertData = new PushApsAlertData(jsonAlert.getString("title"), jsonAlert.getString("body"));
//
//                    title = pushApsAlertData.getTitle();
//                    body = pushApsAlertData.getBody();

                    // dto 사용 안한 경우 (사진 없을 때)
                    String mps = jsonObject.getString("mps");
                    JSONObject jsonMps = new JSONObject(mps);
                    String aps = jsonObject.getString("aps");
                    JSONObject jsonAps = new JSONObject(aps);

                    GLog.d("Message jsonMps: " + jsonMps);
                    GLog.d("Message jsonAps: " + jsonAps);

                    String alert = jsonAps.getString("alert");
                    JSONObject jsonAlert = new JSONObject(alert);
                    String title = jsonAlert.getString("title");
                    String body = jsonAlert.getString("body");
                    GLog.d("Message title: " + title);
                    GLog.d("Message body: " + body);

                    showNotification(title, body, imgUrl);
                } catch (JSONException ex) {
                    GLog.d("title, text exception :: " + ex.getMessage());
//                    throw new RuntimeException(ex);
                }
                GLog.d("ext 메세지 :: " + e.getMessage());
            }
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

    // img url -> bitmap
    private static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showNotification(String messageTitle, String messageBody, String imgUrl) {
        GLog.d("messageTitle === " + messageTitle);
        GLog.d("messageBody === " + messageBody);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        String mChannelName = getString(R.string.default_notification_channel_name);
        String channelId = getString(R.string.default_notification_channel_id);

        NotificationCompat.Builder notificationBuilder;
        if (imgUrl != null) {
            GLog.d("imgUrl not null");
            GLog.d("imgUrl === " + imgUrl);
            notificationBuilder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setLargeIcon(getBitmapFromURL(imgUrl))
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(getBitmapFromURL(imgUrl))
                            .bigLargeIcon(null))
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent);
        } else {
            GLog.d("imgUrl null");
            notificationBuilder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent);
        }
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