package com.systematix.itrack.models;

import android.support.annotation.StringRes;
import android.view.View;
import android.widget.EditText;

public final class EditTextModel {
    public static void setOnFocusPlaceholder(final EditText editText, @StringRes final int res) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText.setHint(res);
                } else {
                    editText.setHint("");
                }
            }
        });
    }
}
