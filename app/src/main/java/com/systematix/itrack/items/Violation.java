package com.systematix.itrack.items;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.systematix.itrack.components.chip.Chip;
import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.database.DbEntity;
import com.systematix.itrack.database.daos.ViolationDao;
import com.systematix.itrack.interfaces.Searchable;
import com.systematix.itrack.utils.Callback;
import com.systematix.itrack.utils.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity
public final class Violation extends Chip implements DbEntity, Searchable {
    @PrimaryKey @ColumnInfo(name = "violation_id") private int id;
    @ColumnInfo(name = "violation_name") private String name;
    @ColumnInfo(name = "violation_type") private String type;
    @ColumnInfo(name = "violation_category") private String category;

    public Violation() {}

    public Violation(JSONObject json) throws JSONException {
        this.id = json.getInt("violation_id");
        this.name = json.getString("violation_name");
        this.type = json.getString("violation_type");
        this.category = json.getString("violation_category");
    }

    // getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // DbEntity
    @Override
    public void save(Context context, @Nullable Task.OnTaskPreExecuteListener preExecuteListener, @Nullable Task.OnTaskFinishListener<Void> finishListener) {
        final AppDatabase db = AppDatabase.getInstance(context);
        new Task<>(preExecuteListener, new Task.OnTaskExecuteListener<Void>() {
            public Void execute() {
                final ViolationDao dao = db.violationDao();
                if (id == 0 || dao.findById(id) == null) {
                    dao.insertAll(Violation.this);
                } else {
                    dao.update(Violation.this);
                }
                return null;
            }
        }, finishListener).execute();
    }

    @Override
    public void delete(Context context, @Nullable Task.OnTaskPreExecuteListener preExecuteListener, @Nullable Task.OnTaskFinishListener<Void> finishListener) {
        final AppDatabase db = AppDatabase.getInstance(context);
        new Task<>(preExecuteListener, new Task.OnTaskExecuteListener<Void>() {
            @Override
            public Void execute() {
                db.violationDao().delete(Violation.this);
                return null;
            }
        }, finishListener).execute();
    }

    // static
    public static List<Violation> collection(JSONArray array) throws JSONException {
        return collection(array, null);
    }

    public static List<Violation> collection(JSONArray array, @Nullable Callback<Violation> callback) throws JSONException {
        final ArrayList<Violation> violations = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            final Violation violation = new Violation(array.getJSONObject(i));
            violations.add(violation);
            if (callback != null) {
                callback.call(violation);
            }
        }
        return violations;
    }

    // Chip
    @Override
    public String getChipText() {
        return name;
    }

    @Override
    public boolean isChipClickable() {
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    // static
    public static List<Violation> filterByType(List<Violation> violations, String type) {
        final List<Violation> list = new ArrayList<>();
        for (final Violation violation : violations) {
            if (violation.getType().toLowerCase().equals(type)) {
                list.add(violation);
            }
        }
        return list;
    }

    // Searchable
    @Override
    public boolean onSearch(String query) {
        return name.trim().toLowerCase().contains(query.trim().toLowerCase());
    }

    // static
    public static List<Violation> filterByQuery(List<Violation> violations, String query) {
        // create new list
        final List<Violation> newList = new ArrayList<>();
        for (final Violation violation : violations) {
            if (violation.onSearch(query)) {
                newList.add(violation);
            }
        }
        return newList;
    }

    public static class Adapter extends ArrayAdapter<Violation> {
        private boolean emptyInitial;
        private String emptyName;

        public Adapter(@NonNull Context context, @NonNull List<Violation> objects, boolean emptyInitial, String emptyName) {
            this(context, android.R.layout.simple_list_item_1, objects);
            this.emptyInitial = emptyInitial;
            this.emptyName = emptyName;
        }

        public Adapter(@NonNull Context context, int resource, @NonNull List<Violation> objects) {
            super(context, resource, objects);
            emptyInitial = false;
        }

        @Nullable
        @Override
        public Violation getItem(int position) {
            if (emptyInitial && position == 0) {
                final Violation violation = new Violation();
                violation.setId(0);
                violation.setName(emptyName);
                return violation;
            }
            return super.getItem(emptyInitial ? position - 1 : position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(getItem(position).getId());
        }

        // static
        public static ArrayAdapter setMe(Spinner spinner, List<Violation> violations, String emptyName) {
            if (spinner.getAdapter() == null) {
                spinner.setAdapter(new Adapter(spinner.getContext(), violations, true, emptyName));
            } else {
                ((ArrayAdapter) spinner.getAdapter()).notifyDataSetChanged();
            }
            return (ArrayAdapter) spinner.getAdapter();
        }
    }
}
