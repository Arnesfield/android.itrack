package com.systematix.itrack.components.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class ConnectivityReceiver extends BroadcastReceiver {

    private final OnNetworkConnectionChangedListener listener;

    ConnectivityReceiver(OnNetworkConnectionChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        listener.onNetworkConnectionChanged(context, isConnected(context));
    }

    public static boolean isConnected(Context context) {
        final ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    public interface OnNetworkConnectionChangedListener {
        void onNetworkConnectionChanged(Context context, boolean isConnected);
    }
}
