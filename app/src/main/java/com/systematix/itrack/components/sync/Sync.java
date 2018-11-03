package com.systematix.itrack.components.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

public final class Sync {
    private Context context;

    public Sync(Context context) {
        this.context = context;
        scheduleJob();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob() {
        final JobInfo myJob = new JobInfo.Builder(0, new ComponentName(context, NetworkSchedulerService.class))
            .setRequiresCharging(true)
            .setMinimumLatency(1000)
            .setOverrideDeadline(2000)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .build();

        final JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.schedule(myJob);
        }
    }

    public void onActivityStart() {
        // Start service and provide it a way to communicate with this class.
        if (context != null) {
            final Intent startServiceIntent = new Intent(context, NetworkSchedulerService.class);
            context.startService(startServiceIntent);
        }
    }

    public void onActivityStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        if (context != null) {
            context.stopService(new Intent(context, NetworkSchedulerService.class));
        }
    }

    // useful stuff
    private static OnSyncListener syncListener;

    public interface OnSyncListener {
        void onSync(Context context, boolean isConnected);
    }

    public static void setSyncListener(OnSyncListener listener) {
        syncListener = listener;
    }

    static void onNetworkConnectionChanged(Context context, boolean isConnected) {
        if (syncListener != null) {
            syncListener.onSync(context, isConnected);
        }
    }
}
