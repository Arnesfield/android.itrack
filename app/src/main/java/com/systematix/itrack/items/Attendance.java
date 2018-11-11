package com.systematix.itrack.items;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.support.annotation.Nullable;

import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.database.DbEntity;
import com.systematix.itrack.database.daos.AttendanceDao;
import com.systematix.itrack.utils.Task;

import org.json.JSONException;
import org.json.JSONObject;

@Entity
public final class Attendance implements DbEntity {

    // use userId for id
    @PrimaryKey private int id;
    @ColumnInfo(name = "hours_rendered") private int hoursRendered;
    @ColumnInfo(name = "violation_hours") private int violationHours;

    public Attendance(int id, int hoursRendered, int violationHours) {
        this.id = id;
        this.hoursRendered = hoursRendered;
        this.violationHours = violationHours;
    }

    public Attendance(JSONObject json) throws JSONException {
        this.id = json.getInt("user_id");
        this.hoursRendered = json.getInt("hours_rendered");
        this.violationHours = json.getInt("violation_hours");
    }

    public int getId() {
        return id;
    }

    public int getHoursRendered() {
        return hoursRendered;
    }

    public int getViolationHours() {
        return violationHours;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHoursRendered(int hoursRendered) {
        this.hoursRendered = hoursRendered;
    }

    public void setViolationHours(int violationHours) {
        this.violationHours = violationHours;
    }

    // DbEntity
    @Override
    public void save(Context context, @Nullable Task.OnTaskPreExecuteListener preExecuteListener, @Nullable Task.OnTaskFinishListener<Void> finishListener) {
        final AppDatabase db = AppDatabase.getInstance(context);
        new Task<>(preExecuteListener, new Task.OnTaskExecuteListener<Void>() {
            public Void execute() {
                // insert if no id, and if obj does not exist
                final AttendanceDao dao = db.attendanceDao();
                if (id == 0 || dao.findById(id) == null) {
                    dao.insertAll(Attendance.this);
                } else {
                    dao.update(Attendance.this);
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
                db.attendanceDao().delete(Attendance.this);
                return null;
            }
        }, finishListener).execute();
    }
}
