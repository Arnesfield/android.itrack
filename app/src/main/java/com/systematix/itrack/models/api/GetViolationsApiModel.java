package com.systematix.itrack.models.api;

import android.content.Context;
import android.support.annotation.Nullable;

import com.systematix.itrack.config.UrlsList;
import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.database.daos.ViolationDao;
import com.systematix.itrack.items.Violation;
import com.systematix.itrack.utils.Api;
import com.systematix.itrack.utils.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public final class GetViolationsApiModel {
    private static Api build(Context context) {
        return Api.post(context)
            .setTag("violations")
            .setUrl(UrlsList.GET_MINOR_VIOLATIONS_URL);
    }

    private static Api.OnApiSuccessListener buildSuccessListener(final Context context, @Nullable final Api.OnApiSuccessListener listener) {
        return new Api.OnApiSuccessListener() {
            @Override
            public void onApiSuccess(String tag, JSONObject response, boolean success, String msg) throws JSONException {
                if (success) {
                    final List<Violation> violations = Violation.collection(response.getJSONArray("violations"));
                    final AppDatabase db = AppDatabase.getInstance(context);
                    // task
                    new Task<>(new Task.OnTaskExecuteListener<Void>() {
                        @Override
                        public Void execute() {
                            final ViolationDao dao = db.violationDao();
                            // clear all violations hehe
                            // then insert new ones just to make sure
                            // you also don't need to tell your user about this i think
                            dao.deleteAll();
                            dao.insertAll(violations);
                            return null;
                        }
                    }).execute();
                }

                // call this last
                if (listener != null) {
                    listener.onApiSuccess(tag, response, success, msg);
                }
            }
        };
    }

    public static void fetch(Context context) {
        fetch(context, null);
    }

    public static void fetch(Context context, @Nullable Api.OnApiRespondListener listener) {
        fetch(context, listener, listener, listener);
    }

    public static void fetch(
            Context context,
            @Nullable Api.OnApiSuccessListener successListener,
            @Nullable Api.OnApiErrorListener errorListener,
            @Nullable Api.OnApiExceptionListener exceptionListener
    ) {
        build(context)
            .setSuccessListener(buildSuccessListener(context, successListener))
            .setErrorListener(errorListener)
            .setExceptionListener(exceptionListener)
            .request();
    }
}
