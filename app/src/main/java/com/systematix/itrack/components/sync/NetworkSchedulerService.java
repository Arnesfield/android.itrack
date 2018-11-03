package com.systematix.itrack.components.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.systematix.itrack.config.AppConfig;

public final class NetworkSchedulerService extends JobService implements ConnectivityReceiver.OnNetworkConnectionChangedListener {
    private ConnectivityReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(AppConfig.TAG, "service@created");
        receiver = new ConnectivityReceiver(this);
    }

    /**
     * When the app's Activity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(AppConfig.TAG, "service@onStartCommand");
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(AppConfig.TAG, "service@onStartJob:" + receiver);
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(AppConfig.TAG, "service@onStopJob");
        unregisterReceiver(receiver);
        return true;
    }

    // OnNetworkConnectionChangedListener
    @Override
    public void onNetworkConnectionChanged(Context context, boolean isConnected) {
        Sync.onNetworkConnectionChanged(context, isConnected);
    }
}
