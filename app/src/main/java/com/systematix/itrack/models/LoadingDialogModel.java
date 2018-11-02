package com.systematix.itrack.models;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.systematix.itrack.R;

public final class LoadingDialogModel {
    private final AlertDialog dialog;
    private TextView messageTextView;

    public LoadingDialogModel(Context context) {
        this(context, R.string.loading_title, R.string.loading_text);
    }

    public LoadingDialogModel(Context context, @StringRes int title, @StringRes int message) {
        dialog = builder(context, title, message).create();
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    public TextView getMessageTextView() {
        return messageTextView;
    }

    private AlertDialog.Builder builder(Context context, @StringRes int title, @StringRes int message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setCancelable(false);

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            final View view = inflater.inflate(R.layout.loading_dialog, null, false);
            messageTextView = view.findViewById(R.id.loading_dialog_text);
            messageTextView.setText(message);
            return builder.setView(view);
        } else {
            return builder.setMessage(message);
        }
    }
}
