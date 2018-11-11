package com.systematix.itrack.models.api;

import android.content.Context;
import android.support.annotation.Nullable;

import com.android.volley.VolleyError;
import com.systematix.itrack.config.UrlsList;
import com.systematix.itrack.items.Attendance;
import com.systematix.itrack.items.Auth;
import com.systematix.itrack.utils.Api;
import com.systematix.itrack.utils.Task;

import org.json.JSONException;
import org.json.JSONObject;

public final class GetAttendanceApiModel {
    private static Task.OnTaskFinishListener<Void> fetchListener;

    public static void fetch(Context context) {
        fetch(context, null);
    }

    public static void setOnFetchListener(Task.OnTaskFinishListener<Void> listener) {
        fetchListener = listener;
    }

    public static void fetch(final Context context, @Nullable final Api.OnApiRespondListener listener) {
        // don't care even if uid == -1 :D
        final int uid = Auth.getSavedUserId(context);

        Api.post(context)
            .setTag("attendance")
            .setUrl(UrlsList.GET_ATTENDANCE_HOURS_URL(uid))
            .setApiListener(new Api.OnApiRespondListener() {
                @Override
                public void onApiSuccess(String tag, JSONObject response, boolean success, String msg) throws JSONException {
                    if (success) {
                        // attempt to get attendance
                        if (response.has("attendance")) {
                            // save attendance
                            final JSONObject jsonAttendance = response.getJSONObject("attendance");
                            new Attendance(jsonAttendance).save(context, null, fetchListener);
                        } else if (fetchListener != null) {
                            fetchListener.finish(null);
                        }
                    }

                    if (listener != null) {
                        listener.onApiSuccess(tag, response, success, msg);
                    }
                }

                @Override
                public void onApiError(String tag, VolleyError error) throws JSONException {
                    if (listener != null) {
                        listener.onApiError(tag, error);
                    }
                }

                @Override
                public void onApiException(String tag, JSONException e) {
                    if (listener != null) {
                        listener.onApiException(tag, e);
                    }
                }
            })
            .request();
    }
}
