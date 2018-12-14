package com.systematix.itrack.items;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.support.annotation.Nullable;

import com.systematix.itrack.components.chip.Chip;
import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.database.DbEntity;
import com.systematix.itrack.database.daos.ViolationDao;
import com.systematix.itrack.utils.Callback;
import com.systematix.itrack.utils.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity
public final class Violation extends Chip implements DbEntity {
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
}
