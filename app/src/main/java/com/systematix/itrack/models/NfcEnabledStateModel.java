package com.systematix.itrack.models;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.util.Log;

public final class NfcEnabledStateModel {
    private NfcAdapter nfc;
    private PendingIntent pendingIntent;
    private static NfcEnabledStateModel model;

    public interface OnDiscoveredListener {
        void onDiscovered(String serial);
    }

    private NfcEnabledStateModel(NfcAdapter nfc, PendingIntent pendingIntent) {
        this.nfc = nfc;
        this.pendingIntent = pendingIntent;
    }

    private NfcAdapter getNfc() {
        return nfc;
    }

    private PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public static void init(Context context, NfcAdapter nfc) {
        final Intent intent = new Intent(context, context.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        model = new NfcEnabledStateModel(nfc, pendingIntent);
    }

    public static void onResume(Activity activity) {
        if (model != null) {
            Log.d("devtag", "enabledState@onResume");
            final NfcAdapter nfc = model.getNfc();
            final PendingIntent pendingIntent = model.getPendingIntent();
            nfc.enableForegroundDispatch(activity, pendingIntent, null, null);
        }
    }

    public static void onPause(Activity activity) {
        if (model != null) {
            Log.d("devtag", "enabledState@onPause");
            model.getNfc().disableForegroundDispatch(activity);
        }
    }

    public static void onNewIntent(Activity activity, Intent intent) {
        // activity should implement OnDiscoveredListener
        if (!(activity instanceof OnDiscoveredListener)) {
            throw new RuntimeException(activity.toString() + " must implement OnDiscoveredListener");
        }

        activity.setIntent(intent);
        final String action = intent.getAction();
        Log.d("devtag", "enabledState@onNewIntent");
        Log.d("devtag", action);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            final StringBuilder hex = new StringBuilder();
            final byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);

            for (byte anId : id) {
                String x = Integer.toHexString(((int) anId & 0xff));
                if (x.length() == 1) {
                    x = '0' + x;
                }
                hex.append(x);
            }
            final String serial = hex.toString().toUpperCase();

            Log.d("devtag", serial);
            // with serial, go to another activity
            ((OnDiscoveredListener) activity).onDiscovered(serial);
        }
    }
}
