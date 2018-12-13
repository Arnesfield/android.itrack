package com.systematix.itrack.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.systematix.itrack.R;
import com.systematix.itrack.SplashActivity;
import com.systematix.itrack.config.AppConfig;

public final class Notificate {
    private Context context;
    private String title;
    private String body;
    private NotificationCompat.Builder notificationBuilder;

    public Notificate(Context context, String title, String body) {
        this.context = context;
        this.title = title;
        this.body = body;
        this.notificationBuilder = buildNotification();
    }

    private NotificationCompat.Builder buildNotification() {
        // On click of notification it redirect to this Activity
        final Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        return new NotificationCompat.Builder(context, AppConfig.FCM_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(new long[] { 1000, 1000, 1000 })
            .setContentIntent(pendingIntent);
    }

    void build() {
        build(0);
    }

    void build(int id) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(id, notificationBuilder.build());
        }
    }
}
