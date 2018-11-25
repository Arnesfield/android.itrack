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
import com.systematix.itrack.config.AppConfig;
import com.systematix.itrack.helpers.JSONObjectHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public final class Api {
    private static RequestQueue requestQueue;

    private int method;
    private String url;
    private String tag;
    private OnApiSuccessListener successListener;
    private OnApiErrorListener errorListener;
    private OnApiExceptionListener exceptionListener;

    // for your objects
    public interface ApiRequestable {
        JSONObject toApiJson();
    }

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
        Log.i(AppConfig.TAG, "Api@create");
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        // RETURN NEW INSTANCE
        return new Api();
    }

    public static boolean isSuccessful(JSONObject response) throws JSONException {
        return response.getBoolean("success");
    }

    // arrayify your requestables
    public static <T extends ApiRequestable> JSONArray collectionRequest(final List<T> requestables) {
        final JSONArray array = new JSONArray();
        for (final ApiRequestable requestable : requestables) {
            array.put(requestable.toApiJson());
        }
        return array;
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

    public JsonObjectRequest request(@Nullable final JSONObject params) {
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
                                final String logMsg = JSONObjectHelper.optString(response, "msg");
                                final String outMsg = success ? null : logMsg;

                                Log.d(AppConfig.TAG, "Api@OnApiSuccess:" + success + "::" + tag);

                                if (logMsg != null) {
                                    Log.d(AppConfig.TAG, "Api@successMsg:" + tag + "::" + logMsg);
                                }

                                if (params != null) {
                                    Log.d(AppConfig.TAG, "Api@successParams:" + tag + "::" + params.toString());
                                }

                                successListener.onApiSuccess(api.tag, response, success, outMsg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Log.e(AppConfig.TAG, "Api@OnApiException:" + tag);
                            Log.e(AppConfig.TAG, e.getMessage());

                            if (params != null) {
                                Log.d(AppConfig.TAG, "Api@exceptionParams:" + tag + "::" + params.toString());
                            }

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
                            Log.e(AppConfig.TAG, "Api@OnApiError:" + tag);
                            Log.e(AppConfig.TAG, error.toString());

                            if (params != null) {
                                Log.d(AppConfig.TAG, "Api@errorParams:" + tag + "::" + params.toString());
                            }

                            if (errorListener != null) {
                                errorListener.onApiError(api.tag, error);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(AppConfig.TAG, "Api@OnApiException:" + tag);
                            Log.e(AppConfig.TAG, e.getMessage());

                            if (params != null) {
                                Log.d(AppConfig.TAG, "Api@exceptionParams:" + tag + "::" + params.toString());
                            }

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
