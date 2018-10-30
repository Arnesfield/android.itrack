package com.systematix.itrack.models;

import android.view.View;
import android.widget.Button;

import com.systematix.itrack.R;

public final class NfcNoPermissionStateModel {

    public interface Model {
        void askPermission();
    }

    public static void init(final Model model, View view) {
        final Button btn = view.findViewById(R.id.nfc_no_permission_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.askPermission();
            }
        });
    }
}
