package com.systematix.itrack.items;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.support.annotation.Nullable;

import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.database.DbEntity;
import com.systematix.itrack.database.daos.NotificationDao;
import com.systematix.itrack.helpers.JSONObjectHelper;
import com.systematix.itrack.utils.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Notification implements DbEntity {
    @PrimaryKey private int id;
    @ColumnInfo(name = "user_id") private int userId;
    @ColumnInfo private String title;
    @ColumnInfo private String body;
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER) private long timestamp;

    public Notification(int id, int userId, String title, String body) {
        this(id, userId, title, body, System.currentTimeMillis() / 1000);
    }

    public Notification(int id, int userId, String title, String body, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
    }

    public Notification(JSONObject json) throws JSONException {
        this.id = json.getInt("id");
        this.userId = json.getInt("user_id");
        this.title = JSONObjectHelper.optString(json, "title");
        this.body = JSONObjectHelper.optString(json, "body");
        this.timestamp = json.getLong("created_at");
    }

    // getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // DbEntity
    @Override
    public void save(Context context, @Nullable Task.OnTaskPreExecuteListener preExecuteListener, @Nullable Task.OnTaskFinishListener<Void> finishListener) {
        final AppDatabase db = AppDatabase.getInstance(context);
        new Task<>(preExecuteListener, new Task.OnTaskExecuteListener<Void>() {
            public Void execute() {
                // insert if no id, and if obj does not exist
                final NotificationDao dao = db.notificationDao();
                if (id == 0 || dao.findById(id) == null) {
                    dao.insertAll(Notification.this);
                } else {
                    dao.update(Notification.this);
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
                db.notificationDao().delete(Notification.this);
                return null;
            }
        }, finishListener).execute();
    }

    // class
    public static List<Notification> collection(JSONArray array) throws JSONException {
        final List<Notification> notifications = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            final JSONObject jsonNotification = array.getJSONObject(i);
            notifications.add(new Notification(jsonNotification));
        }
        return notifications;
    }
}
