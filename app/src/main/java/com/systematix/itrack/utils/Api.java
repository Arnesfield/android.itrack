package com.systematix.itrack.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.systematix.itrack.helpers.JSONObjectHelper;

import org.json.JSONException;
import org.json.JSONObject;

public final class Api {
    private static Api instance;
    private static RequestQueue requestQueue;

    private int method;
    private String url;
    private String tag;
    private OnApiSuccessListener successListener;
    private OnApiErrorListener errorListener;
    private OnApiExceptionListener exceptionListener;

    // implement this interface to activity
    //! screw that! let's implement this anywhere :D
    public interface OnApiSuccessListener {
        void onApiSuccess(String tag, JSONObject response, boolean success, String msg) throws JSONException;
    }

    public interface OnApiErrorListener {
        void onApiError(String tag, VolleyError error) throws JSONException;
    }

    public interface OnApiExceptionListener {
        void onApiException(String tag, JSONException e);
    }

    // use this for all listeners
    public interface OnApiRespondListener extends OnApiSuccessListener, OnApiErrorListener, OnApiExceptionListener {

    }

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
        if (instance == null) {
            instance = new Api();
        }

        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        return instance;
    }

    public static boolean isSuccessful(JSONObject response) throws JSONException {
        return response.getBoolean("success");
    }

    // instance
    private Api() {}

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

    public Api setSuccessListener(OnApiSuccessListener successListener) {
        this.successListener = successListener;
        return this;
    }

    public Api setErrorListener(OnApiErrorListener errorListener) {
        this.errorListener = errorListener;
        return this;
    }

    public Api setExceptionListener(OnApiExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
        return this;
    }

    public Api setApiListener(OnApiRespondListener listener) {
        this.successListener = listener;
        this.errorListener = listener;
        this.exceptionListener = listener;
        return this;
    }

    public JsonObjectRequest request() {
        return request(null);
    }

    public JsonObjectRequest request(@Nullable JSONObject params) {
        final Api api = this;

        final JsonObjectRequest request = new JsonObjectRequest(
                this.method,
                this.url,
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (successListener != null) {
                                // get success and message
                                final boolean success = Api.isSuccessful(response);
                                final String msg = success ? null : JSONObjectHelper.optString(response, "msg");
                                successListener.onApiSuccess(api.tag, response, success, msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("devtag", "Api@OnApiException");
                            Log.e("devtag", e.getMessage());
                            if (exceptionListener != null) {
                                exceptionListener.onApiException(api.tag, e);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            Log.e("devtag", "Api@OnApiError");
                            Log.e("devtag", error.toString());
                            if (errorListener != null) {
                                errorListener.onApiError(api.tag, error);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("devtag", "Api@OnApiException");
                            Log.e("devtag", e.getMessage());
                            if (exceptionListener != null) {
                                exceptionListener.onApiException(api.tag, e);
                            }
                        }
                    }
                }
        );

        requestQueue.add(request);
        return request;
    }
}
