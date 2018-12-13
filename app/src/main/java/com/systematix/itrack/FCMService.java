package com.systematix.itrack;

import android.util.Log;

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
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // super.onMessageReceived(remoteMessage);
        Log.d(AppConfig.TAG, "FCMService@from:" + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(AppConfig.TAG, "FCMService@data:" + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(AppConfig.TAG, "FCMService@notification:" + remoteMessage.getNotification().getBody());
        }
    }
}
