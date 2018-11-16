package com.systematix.itrack.models.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.systematix.itrack.config.AppConfig;
import com.systematix.itrack.config.UrlsList;
import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.items.Report;
import com.systematix.itrack.utils.Api;
import com.systematix.itrack.utils.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public final class SendReportsApiModel {
    public static void send(final Context context) {
        final AppDatabase db = AppDatabase.getInstance(context);

        final Api.OnApiRespondListener apiRespondListener = new Api.OnApiRespondListener() {
            @Override
            public void onApiSuccess(String tag, JSONObject response, boolean success, String msg) {
                // after sending the request,
                // empty the reports!
                Log.i(AppConfig.TAG, "syncModel@synced:" + success);
                if (!success) {
                    return;
                }
                new Task<>(new Task.OnTaskExecuteListener<Void>() {
                    @Override
                    public Void execute() {
                        db.reportDao().deleteAll();
                        return null;
                    }
                }).execute();
            }

            @Override
            public void onApiError(String tag, VolleyError error) {
                // if error, do it again hehehe
                Log.e(AppConfig.TAG, "syncModel@repeat:" + error.toString());
                send(context);
            }

            @Override
            public void onApiException(String tag, JSONException e) {
                // unlikely to pass here
            }
        };

        // get list of minor reports from db
        // then send api request
        new Task<>(new Task.OnTaskListener<List<Report>>() {
            @Override
            public void preExecute() {

            }

            @Override
            public List<Report> execute() {
                // get list of minor reports
                return db.reportDao().getAll();
            }

            @Override
            public void finish(List<Report> result) {
                // only continue on if result has something
                if (result.isEmpty()) {
                    return;
                }

                final JSONArray array = Api.collectionRequest(result);
                final JSONObject params = new JSONObject();
                try {
                    params.put("reports", array);

                    Api.post(context)
                        .setTag("sendViolationBatch")
                        .setUrl(UrlsList.SEND_VIOLATION_BATCH_URL)
                        .setApiListener(apiRespondListener)
                        .request(params);
                } catch (JSONException e) {
                    // unlikely to error
                    e.printStackTrace();
                }
            }
        }).execute();
    }
}
