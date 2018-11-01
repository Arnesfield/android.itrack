package com.systematix.itrack.items;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.support.annotation.Nullable;

import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.database.DbEntity;
import com.systematix.itrack.utils.Callback;
import com.systematix.itrack.utils.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity
public final class Violation implements DbEntity {
    @PrimaryKey @ColumnInfo(name = "violation_id") private int id;
    @ColumnInfo(name = "violation_name") private String name;
    @ColumnInfo(name = "violation_type") private String type;
    @ColumnInfo(name = "violation_hours") private int hours;
    @ColumnInfo(name = "violation_category") private String category;

    public Violation() {}

    public Violation(JSONObject json) throws JSONException {
        this.id = json.getInt("violation_id");
        this.name = json.getString("violation_name");
        this.type = json.getString("violation_type");
        this.hours = json.getInt("violation_hours");
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

    public int getHours() {
        return hours;
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

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // DbEntity
    @Override
    public void save(Context context) {
        final AppDatabase db = AppDatabase.getInstance(context);
        new Task<>(new Task.OnTaskExecuteListener<Void>() {
            public Void execute() {
                db.violationDao().insertAll(Violation.this);
                return null;
            }
        }).execute();
    }

    @Override
    public void delete(Context context) {
        final AppDatabase db = AppDatabase.getInstance(context);
        new Task<>(new Task.OnTaskExecuteListener<Void>() {
            @Override
            public Void execute() {
                db.violationDao().delete(Violation.this);
                return null;
            }
        }).execute();
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
}