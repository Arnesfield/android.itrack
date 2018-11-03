package com.systematix.itrack.models;

import android.content.Context;

import com.systematix.itrack.components.sync.Sync;
import com.systematix.itrack.items.Auth;
import com.systematix.itrack.items.User;
import com.systematix.itrack.models.api.GetViolationsApiModel;
import com.systematix.itrack.models.api.SendMinorReportsApiModel;
import com.systematix.itrack.utils.Task;

public final class SyncModel implements Sync.OnSyncListener {
    @Override
    public void onSync(Context context, boolean isConnected) {
        if (isConnected) {
            syncMinorReports(context);
            syncViolations(context);
        }
    }

    private void syncMinorReports(final Context context) {
        SendMinorReportsApiModel.send(context);
    }

    private void syncViolations(final Context context) {
        // also get violations if teacher!
        Auth.getSavedUser(context, new Task.OnTaskFinishListener<User>() {
            @Override
            public void finish(User user) {
                if (user != null && user.checkAccess("teacher")) {
                    GetViolationsApiModel.fetch(context);
                }
            }
        });
    }
}
