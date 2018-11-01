package com.systematix.itrack.helpers;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import com.systematix.itrack.R;

public final class ViewHelper {
    public static View getLoadingView(Activity activity, ViewGroup rootView) {
        return getView(activity, R.layout.loading_layout, rootView);
    }

    public static View getLoadingView(Activity activity, @IdRes int res) {
        return getLoadingView(activity, (ViewGroup) activity.findViewById(res));
    }

    public static View getView(Activity activity, @LayoutRes int layoutRes, @IdRes int res) {
        return getView(activity, layoutRes, (ViewGroup) activity.findViewById(res));
    }

    public static View getView(Activity activity, @LayoutRes int layoutRes, ViewGroup rootView) {
        return activity.getLayoutInflater().inflate(layoutRes, rootView, false);
    }
}
