package com.systematix.itrack.items;

import android.content.Context;
import android.content.SharedPreferences;

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
        final SharedPreferences preferences = context.getSharedPreferences(PreferencesList.PREF_APP, Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit = preferences.edit();

        edit.putInt(PreferencesList.PREF_USER_ID, user.getId());
        // edit.putString(PreferencesList.PREF_USER_JSON, user.getJson().toString());
        // replace user json pref to db
        user.save(context);
        edit.putBoolean(PreferencesList.PREF_DID_LOG_IN, true);

        edit.apply();
    }

    public static int getSavedUserId(Context context) {
        final SharedPreferences preferences = context.getSharedPreferences(PreferencesList.PREF_APP, Context.MODE_PRIVATE);
        return preferences.getInt(PreferencesList.PREF_USER_ID, -1);
    }

    public static void getSavedUser(Context context, Task.OnTaskFinishListener<User> listener) {
        final AppDatabase db = AppDatabase.getInstance(context);
        final int uid = getSavedUserId(context);

        if (uid == -1) {
            return;
        }

        new Task<>(new Task.OnTaskExecuteListener<User>() {
            @Override
            public User execute() {
                return db.userDao().findById(uid);
            }
        }, listener).execute();
    }

    public static Task<Void> removeSavedUser(Context context) {
        final AppDatabase db = AppDatabase.getInstance(context);

        final SharedPreferences preferences = context.getSharedPreferences(PreferencesList.PREF_APP, Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit = preferences.edit();

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
        final SharedPreferences preferences = context.getSharedPreferences(PreferencesList.PREF_APP, Context.MODE_PRIVATE);
        final boolean didLogin = preferences.getBoolean(PreferencesList.PREF_DID_LOG_IN, false);

        // remove didLogin
        preferences.edit().remove(PreferencesList.PREF_DID_LOG_IN).apply();

        return didLogin;
    }

    public static boolean didLogout(Context context) {
        final SharedPreferences preferences = context.getSharedPreferences(PreferencesList.PREF_APP, Context.MODE_PRIVATE);
        final boolean didLogout = preferences.getBoolean(PreferencesList.PREF_DID_LOG_OUT, false);

        // remove didLogout
        preferences.edit().remove(PreferencesList.PREF_DID_LOG_OUT).apply();

        return didLogout;
    }
}
