package com.systematix.itrack.models;

import android.content.Context;

import com.systematix.itrack.components.sync.Sync;
import com.systematix.itrack.items.Auth;
import com.systematix.itrack.items.User;
import com.systematix.itrack.models.api.GetAttendanceApiModel;
import com.systematix.itrack.models.api.GetViolationsApiModel;
import com.systematix.itrack.models.api.SendReportsApiModel;
import com.systematix.itrack.utils.Task;

public final class SyncModel implements Sync.OnSyncListener {
    @Override
    public void onSync(Context context, boolean isConnected) {
        if (isConnected) {
            syncReports(context);
            syncViolations(context);
            syncAttendance(context);
        }
    }

    private void syncReports(final Context context) {
        SendReportsApiModel.send(context);
    }

    private void syncViolations(final Context context) {
        // also get violations if teacher!
        Auth.getSavedUser(context, new Task.OnTaskFinishListener<User>() {
            @Override
            public void finish(User user) {
                // user.checkAccess("teacher")
                //! no need for teacher as student also has tap?
                if (user != null) {
                    GetViolationsApiModel.fetch(context);
                }
            }
        });
    }

    private void syncAttendance(Context context) {
        GetAttendanceApiModel.fetch(context);
    }
}
