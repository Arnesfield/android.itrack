package com.systematix.itrack.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public final class Api {
    private static Api instance;
    private static OnRespondListener listener;
    private static RequestQueue requestQueue;

    private Context context;
    private int method;
    private String url;
    private String tag;

    public interface OnRespondListener {
        void onResponse(String tag, JSONObject response) throws JSONException;
        void onErrorResponse(String tag, VolleyError error) throws JSONException;
        void onException(JSONException e);
    }

    private Api() {}

    public static Api get(Context context) {
        final Api api = create(context);
        api.setMethod(Request.Method.GET);
        return api;
    }

    public static Api post(Context context) {
        final Api api = create(context);
        api.setMethod(Request.Method.POST);
        return api;
    }

    private static Api create(Context context) {
        // check if context is an instance of the listener
        if (context instanceof OnRespondListener) {
            listener = (OnRespondListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnRespondListener");
        }

        if (instance == null) {
            instance = new Api();
        }

        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        instance.setContext(context);
        return instance;
    }

    public static boolean isSuccessful(JSONObject response) throws JSONException {
        return response.getBoolean("success");
    }

    // instance
    private void setContext(Context context) {
        this.context = context;
    }

    private void setMethod(int method) {
        this.method = method;
    }

    public Api setUrl(String url) {
        this.url = url;
        return this;
    }

    public Api setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public JsonObjectRequest request(JSONObject params) {
        final Api api = this;

        final JsonObjectRequest request = new JsonObjectRequest(
            this.method,
            this.url,
            params,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        listener.onResponse(api.tag, response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onException(e);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        listener.onErrorResponse(api.tag, error);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onException(e);
                    }
                }
            }
        );

        requestQueue.add(request);
        return request;
    }
}
