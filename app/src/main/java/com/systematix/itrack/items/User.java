package com.systematix.itrack.items;

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
import com.systematix.itrack.helpers.JSONObjectHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private int id;
    private String number;
    private String firstName;
    private String middleName;
    private String lastName;
    private String picture;
    private String course;
    private int level;
    private String access;
    private JSONObject json;

    public User(JSONObject json) throws JSONException {
        this.json = json;
        this.id = json.getInt("user_id");
        this.firstName = json.getString("user_firstname");
        this.middleName = JSONObjectHelper.optString(json, "user_middlename");
        this.lastName = json.getString("user_lastname");
        this.number = JSONObjectHelper.optString(json, "user_number");
        this.picture = JSONObjectHelper.optString(json, "user_picture");
        this.access = json.getString("user_access");
        this.course = JSONObjectHelper.optString(json, "user_course");
        this.level = json.optInt("user_level", -1);
    }

    // getters
    public JSONObject getJson() {
        return json;
    }

    public int getId() {
        return id;
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

    public String getLevel() {
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
            Glide
                .with(context)
                .load(UrlsList.BASE_URL + picture)
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
        }
        imageView.setVisibility(hasNoPicture ? View.GONE : View.VISIBLE);
        if (textView != null) {
            textView.setVisibility(!hasNoPicture ? View.GONE: View.VISIBLE);
        }
    }
}
