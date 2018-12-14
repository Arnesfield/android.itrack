package com.systematix.itrack.fcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.systematix.itrack.config.AppConfig;
import com.systematix.itrack.config.PreferencesList;
import com.systematix.itrack.items.Auth;
import com.systematix.itrack.models.api.SendUserFCMTokenApiModel;

public class FCMService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        saveAndSendToken(this, token);
        Log.d(AppConfig.TAG, "FCMService@token:" + token);
        Log.d(AppConfig.TAG, "FCMService@instanceId:" + FirebaseInstanceId.getInstance().getInstanceId());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // super.onMessageReceived(remoteMessage);
        Log.d(AppConfig.TAG, "FCMService@from:" + remoteMessage.getFrom());

        final int id;
        if (remoteMessage.getData().size() > 0) {
            id = Integer.parseInt(remoteMessage.getData().get("user_id"));
            Log.d(AppConfig.TAG, "FCMService@data:" + remoteMessage.getData());
        } else {
            // to avoid conflict with authId -1 default value
            id = -2;
        }

        final RemoteMessage.Notification n = remoteMessage.getNotification();
        if (n != null) {
            n.getTitle();
            final String title = n.getTitle();
            final String body = n.getBody();
            Log.d(AppConfig.TAG, "FCMService@notificationTitle:" + title);
            Log.d(AppConfig.TAG, "FCMService@notificationBody:" + body);
            // compare id to auth id
            final int authId = Auth.getSavedUserId(this);
            if (id == authId) {
                new Notificate(this, title, body).build();
            }
        }
    }

    public static void saveAndSendToken(Context context, String token) {
        final SharedPreferences preferences = getSharedPreferences(context);
        final SharedPreferences.Editor edit = preferences.edit();

        edit.putString(PreferencesList.PREF_USER_FCM_TOKEN, token);
        edit.apply();

        // also do update server
        SendUserFCMTokenApiModel.send(context, token);
    }

    public static String getToken(Context context) {
        final SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(PreferencesList.PREF_USER_FCM_TOKEN, null);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PreferencesList.PREF_APP, MODE_PRIVATE);
    }
}
