package com.dki.hybridapptest.fcm;

import androidx.annotation.NonNull;

import com.dki.hybridapptest.utils.GLog;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
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
            GLog.d("Message data payload: " + message.getData());

            if (true) {
            } else {
                handleNow();
            }
        }

        if (message.getNotification() != null) {
            GLog.d("Message Notification Title: " + message.getNotification().getTitle());
            GLog.d("Message Notification Body: " + message.getNotification().getBody());
        }
    }

    private void handleNow() {
        GLog.d("Short lived task is done.");
    }
}
