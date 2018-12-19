package com.systematix.itrack.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import com.systematix.itrack.R;
import com.systematix.itrack.helpers.AlertDialogHelper;
import com.systematix.itrack.interfaces.Searchable;

import java.util.ArrayList;
import java.util.List;

public final class SearchableSpinnerModel<T extends Searchable> {
    private ArrayList<Spinner> spinners;
    private ArrayList<List<T>> searchablesList;
    private View dialogView;
    private AdapterView.OnItemClickListener listener;

    public SearchableSpinnerModel() {
        spinners = new ArrayList<>();
        searchablesList = new ArrayList<>();
    }

    public void setOnListItemClickListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
    }

    public View getDialogView() {
        return dialogView;
    }

    public SearchView getDialogSearchView() {
        return dialogView == null ? null : (SearchView) dialogView.findViewById(R.id.component_list_dialog_search);
    }

    public ListView getDialogListView() {
        return dialogView == null ? null : (ListView) dialogView.findViewById(R.id.component_list_dialog_list_view);
    }

    public SearchableSpinnerModel bind(
        Spinner spinner,
        List<T> list,
        ArrayAdapter adapter,
        @StringRes int titleRes
    ) {
        final List<T> listCopy = new ArrayList<>(list);
        // only put copy of list
        dialogView = buildDialogView(spinner, list, adapter);
        // list should be a subset of this.list
        final AlertDialog dialog = buildDialog(spinner, titleRes);

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // do not perform v click
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    dialog.show();
                }
                return true;
            }
        });

        // save original
        searchablesList.add(listCopy);
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

    private AlertDialog buildDialog(final Spinner spinner, @StringRes int titleRes) {
        final Context context = spinner.getContext();

        final AlertDialog dialog = new AlertDialog.Builder(context)
            .setTitle(titleRes)
            .setView(dialogView)
            .setPositiveButton(R.string.action_done, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .create();

        AlertDialogHelper.setPositiveColorPrimary(dialog);

        final ListView listView = getDialogListView();
        if (listView != null) {
            listView.setClickable(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    clear();
                    dialog.dismiss();
                    final int i = findIndex(spinner, id);
                    spinner.setSelection(i);

                    if (listener != null) {
                        listener.onItemClick(parent, view, position, id);
                    }
                }
            });
        }

        return dialog;
    }

    private int findIndex(Spinner spinner, long id) {
        final List<T> all = searchablesList.get(spinners.indexOf(spinner));
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == id) {
                return i + 1;
            }
        }
        return 0;
    }

    private View buildDialogView(final Spinner spinner, final List<T> list, final ArrayAdapter adapter) {
        final Context context = spinner.getContext();
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View listLayout = inflater.inflate(R.layout.component_list_dialog, null, false);

        final ListView listView = listLayout.findViewById(R.id.component_list_dialog_list_view);
        final SearchView searchView = listLayout.findViewById(R.id.component_list_dialog_search);

        // listView
        listView.setAdapter(adapter);

        // searchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query(spinner, listView, list, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query(spinner, listView, list, newText);
                return true;
            }
        });

        return listLayout;
    }

    private void query(Spinner spinner, ListView listView, List<T> list, String query) {
        final List<T> originalList = searchablesList.get(spinners.indexOf(spinner));
        filterItems(originalList, list, query);
        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
    }

    public void filterItems(List<T> originalList, List<T> list, String query) {
        list.clear();
        if (query.isEmpty()) {
            list.addAll(originalList);
        } else {
            for (final T item : originalList) {
                if (item.onSearch(query)) {
                    list.add(item);
                }
            }
        }
    }
}
