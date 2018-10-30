package com.systematix.itrack.helpers;

import org.json.JSONObject;

public final class JSONObjectHelper {
    public static String optString(JSONObject json, String key) {
        return json.isNull(key) ? null : json.optString(key, null);
    }
}
