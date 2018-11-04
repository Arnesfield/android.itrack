package com.systematix.itrack.models;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.systematix.itrack.R;

public final class ButtonStateModel {
    private final Button button;
    private final ProgressBar progressBar;
    private final Drawable background;
    private final ColorStateList colors;
    private final float elevation;
    private final int disabledColor;
    private final int disabledDrawable;
    private final float disabledElevation;

    public ButtonStateModel(Button button) {
        this(button, null);
    }

    public ButtonStateModel(Button button, ProgressBar progressBar) {
        this.button = button;
        this.progressBar = progressBar;
        colors = button.getTextColors();
        background = button.getBackground();
        elevation = button.getElevation();
        disabledElevation = 0F;
        disabledDrawable = R.color.colorButtonDisabled;
        disabledColor = ContextCompat.getColor(button.getContext(), R.color.colorButtonTextDisabled);

        // do not show progress first
        setProgressBarState(false);
    }

    public void setLoading(boolean loading, @StringRes int enabledRes) {
        setLoading(loading, enabledRes, -1);
    }

    public void setLoading(boolean loading, @StringRes int enabledRes, @StringRes int loadingRes) {
        loadingRes = loadingRes == -1 ? R.string.loading_text : loadingRes;
        button.setEnabled(!loading);
        button.setText(loading ? loadingRes : enabledRes);
        setProgressBarState(loading);
    }

    public void setEnabled(boolean enabled) {
        button.setEnabled(enabled);
        if (enabled) {
            enable();
        } else {
            disable();
        }
    }

    private void setProgressBarState(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void enable() {
        // simple reset all defaults! :D
        button.setElevation(elevation);
        button.setBackground(background);
        button.setTextColor(colors);
    }

    private void disable() {
        button.setElevation(disabledElevation);
        button.setBackgroundResource(disabledDrawable);
        button.setTextColor(disabledColor);
    }
}
