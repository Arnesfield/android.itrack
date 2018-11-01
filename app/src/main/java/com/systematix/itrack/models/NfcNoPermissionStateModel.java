package com.systematix.itrack.models;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.systematix.itrack.R;
import com.systematix.itrack.config.RequestCodesList;

import java.util.Timer;
import java.util.TimerTask;

public final class NfcNoPermissionStateModel {
    private static Model model;
    private static Timer timer;

    public interface Model {
        void noPermissionStateUpdateView();
        Activity noPermissionStateGetActivity();
    }

    public static void init(final Model model, View view) {
        NfcNoPermissionStateModel.model = model;
        final Button btn = view.findViewById(R.id.nfc_no_permission_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermission();
            }
        });
    }

    public static void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public static void startTimer() {
        stopTimer();
        if (model == null) {
            return;
        }

        // create new timer
        timer = new Timer(true);

        // call this every 3 seconds to check for nfc looooll
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                model.noPermissionStateUpdateView();
            }
        };

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final Activity activity = model.noPermissionStateGetActivity();
                if (activity != null) {
                    activity.runOnUiThread(runnable);
                } else {
                    timer.cancel();
                }
            }
        }, 0, 3);
    }

    public static void onRequestPermissionsResult(Context context, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permission Granted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show();
        }
    }

    private static void askPermission() {
        if (model == null) {
            return;
        }

        final Activity activity = model.noPermissionStateGetActivity();
        if (activity == null) {
            return;
        }

        Log.d("devtag", "askPermission");

        // Here, activity is the current activity
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.NFC)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            // app: NO
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{ Manifest.permission.NFC },
                    RequestCodesList.NFC);
        } else {
            // Permission has already been granted
            Log.d("devtag", "askPermission@granted");
            activity.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }
}
