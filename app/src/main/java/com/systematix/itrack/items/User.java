package com.systematix.itrack.items;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.systematix.itrack.config.UrlsList;
import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.database.DbEntity;
import com.systematix.itrack.database.daos.UserDao;
import com.systematix.itrack.helpers.JSONObjectHelper;
import com.systematix.itrack.utils.Task;

import org.json.JSONException;
import org.json.JSONObject;

@Entity
public class User implements DbEntity {
    @PrimaryKey @ColumnInfo(name = "user_id") private int id;
    @ColumnInfo(name = "user_number") private String number;
    @ColumnInfo(name = "user_serial_no") private String serial;
    @ColumnInfo(name = "user_firstname") private String firstName;
    @ColumnInfo(name = "user_middlename") private String middleName;
    @ColumnInfo(name = "user_lastname") private String lastName;
    @ColumnInfo(name = "user_picture") private String picture;
    @ColumnInfo(name = "user_course") private String course;
    @ColumnInfo(name = "user_level") private int level;
    @ColumnInfo(name = "user_access") private String access;

    public User(JSONObject json) throws JSONException {
        this.id = json.getInt("user_id");
        this.serial = json.getString("user_serial_no");
        this.firstName = json.getString("user_firstname");
        this.middleName = JSONObjectHelper.optString(json, "user_middlename");
        this.lastName = json.getString("user_lastname");
        this.number = JSONObjectHelper.optString(json, "user_number");
        this.picture = JSONObjectHelper.optString(json, "user_picture");
        this.access = json.getString("user_access");
        this.course = JSONObjectHelper.optString(json, "user_course");
        this.level = json.optInt("user_level", -1);
    }

    public User(String serial) {
        this.serial = serial;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    // getters
    public int getId() {
        return id;
    }

    public String getSerial() {
        return serial;
    }

    public String getNumber() {
        return number;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return getName(true);
    }

    public String getName(boolean withMiddleName) {
        if (firstName == null || lastName == null) {
            return null;
        }

        String name = firstName + " ";
        if (withMiddleName) {
            name += middleName != null && middleName.length() > 0 ? middleName + " " : "";
        }
        name += lastName;
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public String getCourse() {
        return course;
    }

    public String getAccess() {
        return access;
    }

    public int getLevel() {
        return level;
    }

    public String getLevelStr() {
        return level > 0 ? String.valueOf(level) : null;
    }

    public String getOrdinalLevel() {
        if (level < 1) {
            return null;
        }

        int mod100 = level % 100;
        int mod10 = level % 10;
        if(mod10 == 1 && mod100 != 11) {
            return level + "st";
        } else if(mod10 == 2 && mod100 != 12) {
            return level + "nd";
        } else if(mod10 == 3 && mod100 != 13) {
            return level + "rd";
        } else {
            return level + "th";
        }
    }

    public boolean checkAccess(String access) {
        return this.access.trim().toLowerCase().equals(access.trim().toLowerCase());
    }

    public void loadImage(Context context, ImageView imageView, @Nullable TextView textView) {
        loadImage(context, imageView, textView, false);
    }

    public void loadImage(final Context context, final ImageView imageView, @Nullable final TextView textView, boolean forceDefault) {
        final boolean hasNoPicture = forceDefault || picture == null || picture.trim().isEmpty();

        if (hasNoPicture) {
            if (textView != null) {
                textView.setText(String.valueOf(firstName.toUpperCase().charAt(0)));
            }
        } else {
            try {
                Glide.with(context).load(UrlsList.PICTURE_URL(picture))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // if load failed, force default
                            User.this.loadImage(context, imageView, textView, true);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        imageView.setVisibility(hasNoPicture ? View.GONE : View.VISIBLE);
        if (textView != null) {
            textView.setVisibility(!hasNoPicture ? View.GONE: View.VISIBLE);
        }
    }

    // DbEntity
    @Override
    public void save(Context context, @Nullable Task.OnTaskPreExecuteListener preExecuteListener, @Nullable Task.OnTaskFinishListener<Void> finishListener) {
        final AppDatabase db = AppDatabase.getInstance(context);
        new Task<>(preExecuteListener, new Task.OnTaskExecuteListener<Void>() {
            public Void execute() {
                // insert if no id, and if obj does not exist
                final UserDao dao = db.userDao();
                if (id == 0 || dao.findById(id) == null) {
                    dao.insertAll(User.this);
                } else {
                    dao.update(User.this);
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
                db.userDao().delete(User.this);
                return null;
            }
        }, finishListener).execute();
    }
}
