package com.systematix.itrack.items;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.database.DbEntity;
import com.systematix.itrack.database.daos.ReportDao;
import com.systematix.itrack.helpers.ImageHelper;
import com.systematix.itrack.utils.Api;
import com.systematix.itrack.utils.Task;

import org.json.JSONException;
import org.json.JSONObject;

@Entity
public final class Report implements DbEntity, Api.ApiRequestable {
    @PrimaryKey(autoGenerate = true) private int id;
    @ColumnInfo(name = "violation_id") private int violationId;
    @ColumnInfo(name = "reporter_id") private int reporterId;
    @ColumnInfo private String serial;
    @ColumnInfo private String location;
    @ColumnInfo private String message;
    @ColumnInfo @Nullable private String imgSrc;
    @ColumnInfo private int age;
    @ColumnInfo(name = "year_section") private String yearSection;
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER) private long timestamp;
    @Ignore private String violationType;

    public Report(int violationId, int reporterId, String serial, String location, String message) {
        this(violationId, "minor", reporterId, serial, location, message, System.currentTimeMillis() / 1000);
    }

    public Report(int violationId, String violationType, int reporterId, String serial, String location, String message) {
        this(violationId, violationType, reporterId, serial, location, message, System.currentTimeMillis() / 1000);
    }

    public Report(int violationId, String violationType, int reporterId, String serial, String location, String message, long timestamp) {
        this(violationId, violationType, reporterId, serial, location, message, (String) null, timestamp);
    }

    public Report(int violationId, String violationType, int reporterId, String serial, String location, String message, @Nullable Bitmap imgSrc) {
        this(violationId, violationType, reporterId, serial, location, message, ImageHelper.stringify(imgSrc), System.currentTimeMillis() / 1000);
    }

    public Report(int violationId, String violationType, int reporterId, String serial, String location, String message, @Nullable String imgSrc, long timestamp) {
        this.violationId = violationId;
        this.violationType = violationType;
        this.reporterId = reporterId;
        this.serial = serial;
        this.location = location;
        this.message = message;
        this.imgSrc = imgSrc;
        this.timestamp = timestamp;
    }

    // getters
    public int getId() {
        return id;
    }

    public int getViolationId() {
        return violationId;
    }

    public int getReporterId() {
        return reporterId;
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

    public String getImgSrc() {
        return imgSrc;
    }

    public int getAge() {
        return age;
    }

    public String getYearSection() {
        return yearSection;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getViolationType() {
        return violationType;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setViolationId(int violationId) {
        this.violationId = violationId;
    }

    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
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

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public void setImgSrc(Bitmap imgSrc) {
        this.imgSrc = ImageHelper.stringify(imgSrc);
    }

    public void setAge(String age) {
        try {
            this.age = Integer.parseInt(age);
        } catch (Exception e) {
            this.age = 0;
        }
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setYearSection(String yearSection) {
        this.yearSection = yearSection;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }

    // DbEntity
    @Override
    public void save(Context context, @Nullable Task.OnTaskPreExecuteListener preExecuteListener, @Nullable Task.OnTaskFinishListener<Void> finishListener) {
        final AppDatabase db = AppDatabase.getInstance(context);
        new Task<>(preExecuteListener, new Task.OnTaskExecuteListener<Void>() {
            public Void execute() {
                final ReportDao dao = db.reportDao();
                if (id == 0 || dao.findById(id) == null) {
                    dao.insertAll(Report.this);
                } else {
                    dao.update(Report.this);
                }
                return null;
            }
        }, finishListener).execute();
    }

    @Override
    public void delete(Context context, @Nullable Task.OnTaskPreExecuteListener preExecuteListener, @Nullable Task.OnTaskFinishListener<Void> finishListener) {
        final AppDatabase db = AppDatabase.getInstance(context);
        new Task<>(preExecuteListener, new Task.OnTaskExecuteListener<Void>() {
            public Void execute() {
                db.reportDao().delete(Report.this);
                return null;
            }
        }, finishListener).execute();
    }

    // ApiRequestable
    @Override
    public JSONObject toApiJson() {
        final JSONObject params = new JSONObject();
        try {
            params.put("serial", serial);
            params.put("violation_id", violationId);
            params.put("reporter_id", reporterId);
            params.put("location", location);
            params.put("message", message);
            params.put("img_src", imgSrc);
            params.put("timestamp", timestamp);

            if (violationType.equals("major")) {
                params.put("age", age);
                params.put("year_section", yearSection);
            }

            return params;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
