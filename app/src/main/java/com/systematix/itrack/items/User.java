package com.systematix.itrack.items;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.systematix.itrack.config.PreferencesList;
import com.systematix.itrack.config.UrlsConfig;

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
    private String access;

    public User(JSONObject json) throws JSONException {
        this.id = json.getInt("user_id");
        this.firstName = json.getString("user_firstname");
        this.middleName = json.getString("user_middlename");
        this.lastName = json.getString("user_lastname");
        this.number = json.getString("user_number");
        this.picture = json.getString("user_picture");
        this.access = json.getString("user_access");
        this.course = json.getString("user_course");
    }

    // getters
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
        String name = firstName + " ";
        name += middleName != null && middleName.length() > 0 ? middleName + " " : "";
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

    public boolean checkAccess(String access) {
        return this.access.trim().toLowerCase().equals(access.trim().toLowerCase());
    }

    public void loadImage(Context context, ImageView imageView, TextView textView) {
        loadImage(context, imageView, textView, false);
    }

    public void loadImage(final Context context, final ImageView imageView, final TextView textView, boolean forceDefault) {
        final boolean hasNoPicture = forceDefault || picture == null || picture.trim().isEmpty();

        if (hasNoPicture) {
            textView.setText(String.valueOf(firstName.toUpperCase().charAt(0)));
        } else {
            Glide
                .with(context)
                .load(UrlsConfig.UPLOADED_IMAGES_URL + access + "/" + picture)
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
        textView.setVisibility(!hasNoPicture ? View.GONE: View.VISIBLE);
    }

    // static
    public static User getUserFromSharedPref(Context context) throws JSONException {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(PreferencesList.PREF_APP, Context.MODE_PRIVATE);
        final String prefUser = sharedPreferences.getString(PreferencesList.PREF_USER_JSON, null);
        if (prefUser == null) {
            return null;
        }
        final JSONObject jsonUser = new JSONObject(prefUser);
        return new User(jsonUser);
    }
}
