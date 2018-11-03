package com.systematix.itrack.items;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;

import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.database.DbEntity;
import com.systematix.itrack.database.daos.MinorReportDao;
import com.systematix.itrack.interfaces.ApiRequestable;
import com.systematix.itrack.utils.Task;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "minor_report")
public final class MinorReport implements DbEntity, ApiRequestable {
    @PrimaryKey(autoGenerate = true) private int id;
    @ColumnInfo(name = "violation_id") private int violationId;
    @ColumnInfo private String serial;
    @ColumnInfo private String location;
    @ColumnInfo private String message;
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER) private long timestamp;

    public MinorReport() {

    }

    public MinorReport(int violationId, String serial, String location, String message, long timestamp) {
        this.violationId = violationId;
        this.serial = serial;
        this.location = location;
        this.message = message;
        this.timestamp = timestamp;
    }

    // getters
    public int getId() {
        return id;
    }

    public int getViolationId() {
        return violationId;
    }

    public String getSerial() {
        return serial;
    }

    public String getLocation() {
        return location;
    }

    public String getMessage() {
        return message;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setViolationId(int violationId) {
        this.violationId = violationId;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // DbEntity
    @Override
    public void save(Context context) {
        final AppDatabase db = AppDatabase.getInstance(context);
        new Task<>(new Task.OnTaskExecuteListener<Void>() {
            public Void execute() {
                final MinorReportDao dao = db.minorReportDao();
                if (id == 0 || dao.findById(id) == null) {
                    dao.insertAll(MinorReport.this);
                } else {
                    dao.update(MinorReport.this);
                }
                return null;
            }
        }).execute();
    }

    @Override
    public void delete(Context context) {
        final AppDatabase db = AppDatabase.getInstance(context);
        new Task<>(new Task.OnTaskExecuteListener<Void>() {
            public Void execute() {
                db.minorReportDao().delete(MinorReport.this);
                return null;
            }
        }).execute();
    }

    // ApiRequestable
    @Override
    public JSONObject toApiJson() {
        final JSONObject params = new JSONObject();
        try {
            params.put("serial", serial);
            params.put("violation_id", violationId);
            params.put("location", location);
            params.put("message", message);
            params.put("timestamp", timestamp);

            return params;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
