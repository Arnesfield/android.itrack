package com.systematix.itrack.items;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.systematix.itrack.config.PreferencesList;
import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.utils.Task;

import org.json.JSONException;
import org.json.JSONObject;

public final class Auth {
    // static
    public static void saveUser(Context context, String jsonUser) throws JSONException {
        saveUser(context, new User(new JSONObject(jsonUser)));
    }

    public static void saveUser(Context context, User user) {
        saveUser(context, user, null, null);
    }

    public static void saveUser(Context context, User user, @Nullable Task.OnTaskPreExecuteListener preExecuteListener) {
        saveUser(context, user, preExecuteListener, null);
    }

    public static void saveUser(Context context, User user, @Nullable Task.OnTaskFinishListener<Void> finishListener) {
        saveUser(context, user, null, finishListener);
    }

    public static void saveUser(final Context context, final User user, @Nullable Task.OnTaskPreExecuteListener preExecuteListener, @Nullable final Task.OnTaskFinishListener<Void> finishListener) {
        user.save(context, preExecuteListener, new Task.OnTaskFinishListener<Void>() {
            @Override
            public void finish(Void result) {
                //! ONLY THEN SAVE SHARED PREFS STUFF WHEN FINISHED SAVING!
                final SharedPreferences.Editor edit = getSharedPrefsEditor(context);

                edit.putInt(PreferencesList.PREF_USER_ID, user.getId());
                edit.putBoolean(PreferencesList.PREF_DID_LOG_IN, true);
                edit.apply();

                if (finishListener != null) {
                    finishListener.finish(result);
                }
            }
        });
    }

    public static int getSavedUserId(Context context) {
        return getSharedPrefs(context).getInt(PreferencesList.PREF_USER_ID, -1);
    }

    public static void getSavedUser(Context context, Task.OnTaskFinishListener<User> listener) {
        getSavedUser(context, null, listener);
    }

    public static void getSavedUser(Context context, @Nullable Task.OnTaskPreExecuteListener preExecuteListener, Task.OnTaskFinishListener<User> finishListener) {
        final AppDatabase db = AppDatabase.getInstance(context);
        final int uid = getSavedUserId(context);

        // continue even if uid is -1 so callbacks can be called
        new Task<>(preExecuteListener, new Task.OnTaskExecuteListener<User>() {
            @Override
            public User execute() {
                return db.userDao().findById(uid);
            }
        }, finishListener).execute();
    }

    public static Task<Void> removeSavedUser(Context context) {
        final AppDatabase db = AppDatabase.getInstance(context);
        final SharedPreferences.Editor edit = getSharedPrefsEditor(context);

        final int uid = getSavedUserId(context);

        edit.remove(PreferencesList.PREF_USER_ID);
        edit.remove(PreferencesList.PREF_DID_LOG_IN);
        edit.putBoolean(PreferencesList.PREF_DID_LOG_OUT, true);
        edit.apply();

        // change this to db
        // edit.remove(PreferencesList.PREF_USER_JSON);
        Task<Void> task = null;
        if (uid != -1) {
            task = new Task<>(new Task.OnTaskExecuteListener<Void>() {
                @Override
                public Void execute() {
                    db.userDao().deleteById(uid);
                    return null;
                }
            });
        }
        return task;
    }

    public static boolean didLogin(Context context) {
        final SharedPreferences preferences = getSharedPrefs(context);
        final boolean didLogin = preferences.getBoolean(PreferencesList.PREF_DID_LOG_IN, false);

        // remove didLogin
        preferences.edit().remove(PreferencesList.PREF_DID_LOG_IN).apply();

        return didLogin;
    }

    public static boolean didLogout(Context context) {
        final SharedPreferences preferences = getSharedPrefs(context);
        final boolean didLogout = preferences.getBoolean(PreferencesList.PREF_DID_LOG_OUT, false);

        // remove didLogout
        preferences.edit().remove(PreferencesList.PREF_DID_LOG_OUT).apply();

        return didLogout;
    }

    // helpers
    private static SharedPreferences getSharedPrefs(Context context) {
        return context.getSharedPreferences(PreferencesList.PREF_APP, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getSharedPrefsEditor(Context context) {
        return getSharedPrefs(context).edit();
    }
}
