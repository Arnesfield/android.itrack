package com.systematix.itrack.helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

public final class ViewSwitcherHelper {
    private ViewGroup rootView;
    private View currView;

    public ViewSwitcherHelper(@NonNull ViewGroup rootView, @Nullable View currView) {
        this.rootView = rootView;
        this.currView = currView;
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
