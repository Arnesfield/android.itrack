package com.systematix.itrack.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.systematix.itrack.config.AppConfig;

public class FCMService extends FirebaseMessagingService {
    public FCMService() {
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(AppConfig.TAG, "FCMService@token:" + token);
        Log.d(AppConfig.TAG, "FCMService@instanceId:" + FirebaseInstanceId.getInstance().getInstanceId());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // super.onMessageReceived(remoteMessage);
        Log.d(AppConfig.TAG, "FCMService@from:" + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(AppConfig.TAG, "FCMService@data:" + remoteMessage.getData());
        }

        final RemoteMessage.Notification n = remoteMessage.getNotification();
        if (n != null) {
            n.getTitle();
            final String title = n.getTitle();
            final String body = n.getBody();
            Log.d(AppConfig.TAG, "FCMService@notificationTitle:" + title);
            Log.d(AppConfig.TAG, "FCMService@notificationBody:" + body);
            new Notificate(this, title, body).build();
        }
    }
}
