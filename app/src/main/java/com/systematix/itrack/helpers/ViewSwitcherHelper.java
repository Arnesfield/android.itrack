package com.systematix.itrack.helpers;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public final class ViewSwitcherHelper {
    private ViewGroup rootView;
    private View currView;

    // constructors
    public ViewSwitcherHelper(@NonNull ViewGroup rootView) {
        this(rootView, null);
    }

    public ViewSwitcherHelper(@NonNull ViewGroup rootView, @Nullable View currView) {
        this.rootView = rootView;
        switchTo(currView);
    }

    public ViewSwitcherHelper(@NonNull Activity activity, @IdRes int rootViewRes) {
        this(activity, rootViewRes, null);
    }

    public ViewSwitcherHelper(@NonNull Activity activity, @IdRes int rootViewRes, @Nullable View currView) {
        this((ViewGroup) activity.findViewById(rootViewRes), currView);
    }

    public ViewSwitcherHelper(@NonNull LayoutInflater inflater, @LayoutRes int rootViewRes, @Nullable ViewGroup container) {
        this(inflater, rootViewRes, container, null);
    }

    public ViewSwitcherHelper(@NonNull LayoutInflater inflater, @LayoutRes int rootViewRes, @Nullable ViewGroup container, @Nullable View currView) {
        this((ViewGroup) inflater.inflate(rootViewRes, container, false), currView);
    }

    public void clear() {
        switchTo(null);
    }

    public void switchTo(@Nullable final View to) {
        // if currView is different from to,
        // then remove all da views and set
        if (isNotCurrent(to)) {
            currView = to;
            rootView.removeAllViews();
            if (currView != null) {
                rootView.addView(currView);
            }
        }
    }

    public boolean isNotCurrent(@NonNull final View view) {
        return currView != view;
    }
}
