package com.systematix.itrack.models;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.systematix.itrack.components.sync.Sync;
import com.systematix.itrack.config.UrlsList;
import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.items.MinorReport;
import com.systematix.itrack.utils.Api;
import com.systematix.itrack.utils.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public final class SyncModel implements Sync.OnSyncListener {
    @Override
    public void onSync(Context context, boolean isConnected) {
        if (isConnected) {
            syncViolations(context);
        }
    }

    private void syncViolations(final Context context) {
        final AppDatabase db = AppDatabase.getInstance(context);

        final Api.OnApiRespondListener apiRespondListener = new Api.OnApiRespondListener() {
            @Override
            public void onApiSuccess(String tag, JSONObject response, boolean success, String msg) {
                // after sending the request,
                // empty the minor reports!
                if (!success) {
                    return;
                }
                Log.i("devtag", "syncModel@Synced!");
                new Task<>(new Task.OnTaskExecuteListener<Void>() {
                    @Override
                    public Void execute() {
                        db.minorReportDao().deleteAll();
                        return null;
                    }
                }).execute();
            }

            @Override
            public void onApiError(String tag, VolleyError error) {
                // if error, do it again hehehe
                syncViolations(context);
            }

            @Override
            public void onApiException(String tag, JSONException e) {
                // unlikely to pass here
            }
        };

        // get list of minor reports from db
        // then send api request
        new Task<>(new Task.OnTaskListener<List<MinorReport>>() {
            @Override
            public void preExecute() {

            }

            @Override
            public List<MinorReport> execute() {
                // get list of minor reports
                return db.minorReportDao().getAll();
            }

            @Override
            public void finish(List<MinorReport> result) {
                // only continue on if result has something
                if (result.isEmpty()) {
                    return;
                }

                final JSONArray array = Api.collectionRequest(result);
                final JSONObject params = new JSONObject();
                try {
                    params.put("minorReports", array);

                    Api.post(context)
                        .setUrl(UrlsList.SEND_MINOR_VIOLATION_BATCH_URL)
                        .setApiListener(apiRespondListener)
                        .request(params);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }
}
