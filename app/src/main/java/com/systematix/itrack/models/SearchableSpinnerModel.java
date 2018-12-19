package com.systematix.itrack.models;

import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.systematix.itrack.R;

import java.util.ArrayList;
import java.util.List;

public final class SearchableSpinnerModel<T> {
    private List<T> list;
    private ArrayList<Spinner> spinners;

    public SearchableSpinnerModel(List<T> list) {
        this.list = list;
        spinners = new ArrayList<>();
    }

    public SearchableSpinnerModel<T> bind(
        Spinner spinner,
        final List<T> list,
        ArrayAdapter adapter,
        @StringRes int titleRes
    ) {
        return bind(spinner, list, adapter, titleRes, null);
    }

    public SearchableSpinnerModel<T> bind(
        Spinner spinner,
        final List<T> list,
        ArrayAdapter adapter,
        @StringRes int titleRes,
        @Nullable DialogInterface.OnClickListener listener
    ) {
        // list should be a subset of this.list
        final AlertDialog dialog = buildDialog(spinner, titleRes, adapter, listener);

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dialog.show();
                return true;
            }
        });

        spinners.add(spinner);
        return this;
    }

    public void clear() {
        for (final Spinner spinner : spinners) {
            spinner.setSelection(0);
        }
    }

    public Object getSelected() {
        for (final Spinner spinner : spinners) {
            if (spinner.getSelectedItemPosition() > 0) {
                return spinner.getSelectedItem();
            }
        }
        return null;
    }

    public boolean hasSelected() {
        return getSelected() != null;
    }

    private AlertDialog buildDialog(
            final Spinner spinner,
            @StringRes int titleRes,
            ArrayAdapter adapter,
            @Nullable final DialogInterface.OnClickListener listener
    ) {
        return new AlertDialog.Builder(spinner.getContext())
            .setTitle(titleRes)
            .setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clear();
                    dialog.dismiss();
                    spinner.setSelection(which);

                    if (listener != null) {
                        listener.onClick(dialog, which);
                    }
                }
            })
            .setPositiveButton(R.string.action_done, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .create();
    }
}
