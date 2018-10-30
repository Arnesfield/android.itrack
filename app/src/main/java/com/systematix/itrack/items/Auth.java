package com.systematix.itrack.items;

import android.content.Context;
import android.content.SharedPreferences;

import com.systematix.itrack.config.PreferencesList;

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
        edit.putString(PreferencesList.PREF_USER_JSON, user.getJson().toString());
        edit.putBoolean(PreferencesList.PREF_DID_LOG_IN, true);

        edit.apply();
    }

    public static int getSavedUserId(Context context) {
        final SharedPreferences preferences = context.getSharedPreferences(PreferencesList.PREF_APP, Context.MODE_PRIVATE);
        return preferences.getInt(PreferencesList.PREF_USER_ID, -1);
    }

    public static User getSavedUser(Context context) throws JSONException {
        final SharedPreferences preferences = context.getSharedPreferences(PreferencesList.PREF_APP, Context.MODE_PRIVATE);
        final String stringUser = preferences.getString(PreferencesList.PREF_USER_JSON, null);

        if (stringUser == null) {
            return null;
        }

        return new User(new JSONObject(stringUser));
    }

    public static void removeSavedUser(Context context) {
        final SharedPreferences preferences = context.getSharedPreferences(PreferencesList.PREF_APP, Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit = preferences.edit();

        edit.remove(PreferencesList.PREF_USER_ID);
        edit.remove(PreferencesList.PREF_USER_JSON);
        edit.remove(PreferencesList.PREF_DID_LOG_IN);
        edit.putBoolean(PreferencesList.PREF_DID_LOG_OUT, true);

        edit.apply();
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
