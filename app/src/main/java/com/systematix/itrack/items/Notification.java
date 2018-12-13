package com.systematix.itrack.items;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.systematix.itrack.R;
import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.database.DbEntity;
import com.systematix.itrack.database.daos.NotificationDao;
import com.systematix.itrack.helpers.JSONObjectHelper;
import com.systematix.itrack.helpers.UnixHelper;
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

    @Ignore
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

    @Ignore
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

    // helpers
    public String getFormattedTimestamp() {
        return getFormattedTimestamp("MMMM dd, yyyy hh:ss a");
    }

    public String getFormattedTimestamp(String format) {
        return new UnixHelper(timestamp).convert(format);
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

    // RecyclerViewAdapter
    public static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private List<Notification> notifications;

        // item
        static class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(View itemView) {
                super(itemView);
            }

            void initView(Notification notification) {
                if (itemView == null) {
                    return;
                }

                final TextView tvTitle = itemView.findViewById(R.id.notification_item_title);
                final TextView tvSubtitle = itemView.findViewById(R.id.notification_item_subtitle);
                final TextView tvDatetime = itemView.findViewById(R.id.notification_item_datetime);

                tvTitle.setText(notification.getTitle());
                tvSubtitle.setText(notification.getBody());
                tvDatetime.setText(notification.getFormattedTimestamp());
            }
        }

        public Adapter(List<Notification> notifications) {
            this.notifications = notifications;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            // create a new view
            final View view = inflater.inflate(R.layout.notification_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.initView(notifications.get(position));
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }
    }
}
