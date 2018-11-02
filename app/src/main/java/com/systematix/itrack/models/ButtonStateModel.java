package com.systematix.itrack.models;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

import com.systematix.itrack.R;

public final class ButtonStateModel {
    private final Button button;
    private final Drawable background;
    private final ColorStateList colors;
    private final float elevation;
    private final int disabledColor;
    private final int disabledDrawable;
    private final float disabledElevation;

    public ButtonStateModel(Button button) {
        this.button = button;
        colors = button.getTextColors();
        background = button.getBackground();
        elevation = button.getElevation();
        disabledElevation = 0F;
        disabledDrawable = R.color.colorButtonDisabled;
        disabledColor = ContextCompat.getColor(button.getContext(), R.color.colorButtonTextDisabled);
    }

    public void setEnabled(boolean enabled) {
        button.setEnabled(enabled);
        if (enabled) {
            enable();
        } else {
            disable();
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
