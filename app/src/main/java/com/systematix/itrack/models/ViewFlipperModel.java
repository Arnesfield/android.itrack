package com.systematix.itrack.models;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ViewFlipper;

public final class ViewFlipperModel {
    @IdRes private int res;
    private ViewFlipper viewFlipper;

    // constructors
    public ViewFlipperModel(@NonNull ViewFlipper viewFlipper) {
        this.viewFlipper = viewFlipper;
    }

    public ViewFlipperModel(@NonNull ViewFlipper viewFlipper, @IdRes int res) {
        this(viewFlipper);
        switchTo(res);
    }

    public View getCurrView() {
        return viewFlipper.findViewById(res);
    }

    public void switchTo(View view) {
        switchTo(view.getId());
    }

    public void switchTo(@IdRes int res) {
        if (isNotCurrent(res)) {
            this.res = res;
            viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(getCurrView()));
        }
    }

    public boolean isNotCurrent(@IdRes int res) {
        return this.res != res;
    }
}
