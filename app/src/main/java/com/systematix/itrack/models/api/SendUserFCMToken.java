package com.systematix.itrack.models.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.VolleyError;
import com.systematix.itrack.config.AppConfig;
import com.systematix.itrack.config.PreferencesList;
import com.systematix.itrack.config.UrlsList;
import com.systematix.itrack.utils.Api;
import com.systematix.itrack.utils.Callback;

import org.json.JSONException;
import org.json.JSONObject;

public final class SendUserFCMToken {
    public static void send(final Context context, final String token) {
        final SharedPreferences preferences = context.getSharedPreferences(PreferencesList.PREF_APP, Context.MODE_PRIVATE);
        final int uid = preferences.getInt(PreferencesList.PREF_USER_ID, -1);
        send(context, uid, token);
    }

    public static void send(final Context context, final int userId, final String token) {
        final JSONObject params = new JSONObject();

        try {
            params.put("user_id", userId);
            params.put("token", token);
        } catch (JSONException e) {
            // unlikely to pass here
            e.printStackTrace();
            return;
        }

        Api.post(context)
            .setTag("sendFCMToken")
            .setUrl(UrlsList.SEND_USER_FCM_TOKEN_URL)
            .setApiListener(buildApiListener(context, new Callback<Void>() {
                @Override
                public void call(Void obj) {
                    // call again idc
                    send(context, userId, token);
                }
            }))
            .request(params);
    }

    private static Api.OnApiRespondListener buildApiListener(final Context context) {
        return buildApiListener(context, null);
    }

    private static Api.OnApiRespondListener buildApiListener(final Context context, @Nullable final Callback<Void> callback) {
        return new Api.OnApiRespondListener() {
            @Override
            public void onApiSuccess(String tag, JSONObject response, boolean success, String msg) {
                Log.i(AppConfig.TAG, "SendUserFCMToken@success:" + success);
            }

            @Override
            public void onApiError(String tag, VolleyError error) {
                // if error, do it again hehehe
                Log.e(AppConfig.TAG, "SendUserFCMToken@error:" + error.toString());
                if (callback != null) {
                    callback.call(null);
                }
            }

            @Override
            public void onApiException(String tag, JSONException e) {
                // unlikely to pass here
            }
        };
    }
}
