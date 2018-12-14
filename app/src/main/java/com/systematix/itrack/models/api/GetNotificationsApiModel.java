package com.systematix.itrack.models.api;

import android.content.Context;
import android.support.annotation.Nullable;

import com.android.volley.VolleyError;
import com.systematix.itrack.config.UrlsList;
import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.items.Auth;
import com.systematix.itrack.items.Notification;
import com.systematix.itrack.utils.Api;
import com.systematix.itrack.utils.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class GetNotificationsApiModel {
    public static void fetch(Context context) {
        fetch(context, null);
    }

    public static void fetch(final Context context, @Nullable final Api.OnApiRespondListener listener) {
        final AppDatabase db = AppDatabase.getInstance(context);
        final int uid = Auth.getSavedUserId(context);

        Api.post(context)
            .setTag("notifications")
            .setUrl(UrlsList.GET_USER_NOTIFICATIONS_URL(uid))
            .setApiListener(new Api.OnApiRespondListener() {
                @Override
                public void onApiSuccess(final String tag, final JSONObject response, final boolean success, final String msg) throws JSONException {
                    // remove this lol this is useless
                    /* if (!(success && response.has("notifications"))) {
                        if (listener != null) {
                            // well, yea it's not successful lol
                            listener.onApiSuccess(tag, response, false, msg);
                        }
                        return;
                    } */

                    // attempt to get notifications
                    final boolean has = response.has("notifications");
                    final List<Notification> notifications;
                    if (has) {
                        final JSONArray jsonNotifications = response.getJSONArray("notifications");
                        notifications = Notification.collection(jsonNotifications);
                    } else {
                        notifications = new ArrayList<>();
                    }

                    new Task<>(new Task.OnTaskListener<Void>() {
                        @Override
                        public void preExecute() {

                        }

                        @Override
                        public Void execute() {
                            // clear and insert
                            db.notificationDao().deleteAll();
                            if (!notifications.isEmpty()) {
                                db.notificationDao().insertAll(notifications);
                            }
                            return null;
                        }

                        @Override
                        public void finish(Void result) {
                            if (listener != null) {
                                try {
                                    listener.onApiSuccess(tag, response, success, msg);
                                } catch (JSONException e) {
                                    // unlikely to pass here
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).execute();
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
