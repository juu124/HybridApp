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
import com.dki.hybridapptest.dto.PushApsAlertData;
import com.dki.hybridapptest.dto.PushMpsImgUrl;
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

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        GLog.d("message === " + message);
        JSONObject jsonObject = null;
        String msgData = null;
        String mps = null;
        String aps = null;
        String ext = null;
        String imgUrl = null;
        String title = null;
        String body = null;

        if (message.getData().size() > 0) {
            Map<String, String> dataMap = message.getData();
            GLog.d("Message data payload: " + dataMap);

            msgData = dataMap.get("message");
            try {
                if (!TextUtils.isEmpty(msgData)) {
                    GLog.d("Message data msgData: " + msgData);
                    jsonObject = new JSONObject(msgData);

                    // ---------------------------------------------------------------------------------------------------------
                    mps = jsonObject.getString("mps");
                    aps = jsonObject.getString("aps");

                    // mps
                    JSONObject jsonMps = new JSONObject(mps);
                    ext = jsonMps.getString("ext");
                    JSONObject jsonExt = new JSONObject(ext);
                    PushMpsImgUrl pushMpsImgUrl = new PushMpsImgUrl(jsonExt.getString("imageUrl"));
                    imgUrl = pushMpsImgUrl.getImageUrl();

                    // aps
                    JSONObject jsonAps = new JSONObject(aps);
                    String alert = jsonAps.getString("alert");
                    JSONObject jsonAlert = new JSONObject(alert);
                    PushApsAlertData pushApsAlertData = new PushApsAlertData(jsonAlert.getString("title"), jsonAlert.getString("body"));
                    title = pushApsAlertData.getTitle();
                    body = pushApsAlertData.getBody();
                    //-------------------------------------------------------------------------------------------------------

                    // dto cast 오류(org.json.JSONObject cannot be cast to com.dki.hybridapptest.dto.PushMpsExtData)----------
//                    PushMpsExtData pushMpsExtData = (PushMpsExtData) jsonObject.get("mps");
//                    PushMpsImgUrl pushMpsImgUrl = pushMpsExtData.getExt();
//                    imgUrl = pushMpsImgUrl.getImageUrl();
//
//                    PushApsData pushApsData = (PushApsData) jsonObject.get("aps");
//                    PushApsAlertData pushApsAlertData = pushApsData.getAlert();
//                    title = pushApsAlertData.getTitle();
//                    body = pushApsAlertData.getBody();
                    // ----------------------------------------------------------------------------------------------------------

                    // dto 사용 안한 경우-----------------------------------------------------------------------------------------
//                    String mps = jsonObject.getString("mps");
//                    JSONObject jsonMps = new JSONObject(mps);
//                    String aps = jsonObject.getString("aps");
//                    JSONObject jsonAps = new JSONObject(aps);
//
//                    GLog.d("Message jsonMps: " + jsonMps);
//                    GLog.d("Message jsonAps: " + jsonAps);
//
//                    ext = jsonMps.getString("ext");
//                    JSONObject jsonExt = new JSONObject(ext);
//                    imgUrl = jsonExt.getString("imageUrl");
//                    GLog.d("Message imgUrl: " + imgUrl);
//
//                    String alert = jsonAps.getString("alert");
//                    JSONObject jsonAlert = new JSONObject(alert);
//                    title = jsonAlert.getString("title");
//                    body = jsonAlert.getString("body");
//                    GLog.d("Message title: " + title);
//                    GLog.d("Message body: " + body);
                    // ----------------------------------------------------------------------------------------------------------

                    showNotification(title, body, imgUrl);
                }
            } catch (JSONException e) {
                try {
                    // aps-------------------------------------------------------------------------------------------------------
                    JSONObject jsonAps = new JSONObject(aps);
                    String alert = jsonAps.getString("alert");
                    JSONObject jsonAlert = new JSONObject(alert);
                    PushApsAlertData pushApsAlertData = new PushApsAlertData(jsonAlert.getString("title"), jsonAlert.getString("body"));
                    title = pushApsAlertData.getTitle();
                    body = pushApsAlertData.getBody();
                    // ----------------------------------------------------------------------------------------------------------

                    // dto-------------------------------------------------------------------------------------------------------
//                    PushApsData pushApsData = (PushApsData) jsonObject.get("aps");
//                    PushApsAlertData pushApsAlertData = pushApsData.getAlert();
//                    title = pushApsAlertData.getTitle();
//                    body = pushApsAlertData.getBody();
                    //-------------------------------------------------------------------------------------------------------

                    // dto 사용 안한 경우 (사진 없을 때)-------------------------------------------------------------------------
//                    String mps = jsonObject.getString("mps");
//                    JSONObject jsonMps = new JSONObject(mps);
//                    String aps = jsonObject.getString("aps");
//                    JSONObject jsonAps = new JSONObject(aps);
//
//                    GLog.d("Message jsonMps: " + jsonMps);
//                    GLog.d("Message jsonAps: " + jsonAps);
//
//                    String alert = jsonAps.getString("alert");
//                    JSONObject jsonAlert = new JSONObject(alert);
//                    title = jsonAlert.getString("title");
//                    body = jsonAlert.getString("body");
//                    GLog.d("Message title: " + title);
//                    GLog.d("Message body: " + body);
                    //-------------------------------------------------------------------------------------------------------

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
        if (!TextUtils.isEmpty(imgUrl)) {
            GLog.d("imgUrl not null");

            notificationBuilder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setLargeIcon(getBitmapFromURL(imgUrl))
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(getBitmapFromURL(imgUrl))
                            .bigLargeIcon(null))
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
        } else {
            GLog.d("imgUrl null");
            notificationBuilder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setContentIntent(pendingIntent);
        }
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, mChannelName, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(Constant.NOTIFICATION_ID_FOR_PEDOMETER, notificationBuilder.build());
        startForeground(Constant.NOTIFICATION_ID_FOR_PEDOMETER, notificationBuilder.build());
    }
}