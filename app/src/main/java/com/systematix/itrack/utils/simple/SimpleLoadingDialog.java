package com.systematix.itrack.utils.simple;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.systematix.itrack.R;
import com.systematix.itrack.helpers.ViewHelper;

public final class SimpleLoadingDialog {
    public static AlertDialog build(Context context) {
        return build(context, R.string.loading_title, R.string.loading_text);
    }

    public static AlertDialog build(Context context, @StringRes int title, @StringRes int message) {
        return builder(context, title, message).create();
    }

    public static AlertDialog show(Context context) {
        return show(context, R.string.loading_title, R.string.loading_text);
    }

    public static AlertDialog show(Context context, @StringRes int title, @StringRes int message) {
        return builder(context, title, message).show();
    }

    private static AlertDialog.Builder builder(Context context, @StringRes int title, @StringRes int message) {
        final View view = ViewHelper.getView((Activity) context, R.layout.loading_dialog, null);
        final TextView txtView = view.findViewById(R.id.loading_dialog_text);
        txtView.setText(message);

        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setCancelable(false)
                .setView(view);
    }
}
