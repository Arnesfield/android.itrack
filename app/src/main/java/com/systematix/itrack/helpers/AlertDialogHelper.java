package com.systematix.itrack.helpers;

import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.systematix.itrack.R;

public final class AlertDialogHelper {
    public static void setPositiveColorPrimary(final AlertDialog dialog) {
        // make the button primary color hehe
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                final int color = ContextCompat.getColor(dialog.getContext(), R.color.colorPrimary);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
            }
        });
    }
}
