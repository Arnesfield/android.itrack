package com.systematix.itrack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.systematix.itrack.items.Auth;
import com.systematix.itrack.items.User;

import org.json.JSONException;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initContent();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    private void initContent() {
        try {
            final User user = Auth.getSavedUser(this);

            final boolean hasNoLevel = user.getLevel() == null;
            final boolean hasNoCourse = user.getCourse() == null;

            setTitle(user.getName(false));

            final ImageView ivProfile = findViewById(R.id.profile_iv);
            final View vName = findViewById(R.id.profile_name);
            final View vNumber = findViewById(R.id.profile_number);
            final View divider = findViewById(R.id.profile_divider);
            final View vLevel = findViewById(R.id.profile_level);
            final View vCourse = findViewById(R.id.profile_course);

            final TextView vNameTitle = vName.findViewById(R.id.component_info_title);
            final TextView vNameSubtitle = vName.findViewById(R.id.component_info_subtitle);
            final ImageView vNameImage = vName.findViewById(R.id.component_info_image);

            final TextView vNumberTitle = vNumber.findViewById(R.id.component_info_title);
            final TextView vNumberSubtitle = vNumber.findViewById(R.id.component_info_subtitle);

            final TextView vLevelTitle = vLevel.findViewById(R.id.component_info_title);
            final TextView vLevelSubtitle = vLevel.findViewById(R.id.component_info_subtitle);
            final ImageView vLevelImage = vLevel.findViewById(R.id.component_info_image);

            final TextView vCourseTitle = vCourse.findViewById(R.id.component_info_title);
            final TextView vCourseSubtitle = vCourse.findViewById(R.id.component_info_subtitle);
            final ImageView vCourseImage = vCourse.findViewById(R.id.component_info_image);

            user.loadImage(this, ivProfile, null);

            vNameTitle.setText(user.getName());
            vNameSubtitle.setText(R.string.profile_name);
            vNameImage.setImageResource(R.drawable.ic_id_number);

            vNumberTitle.setText(user.getNumber());
            vNumberSubtitle.setText(R.string.profile_number);

            divider.setVisibility(hasNoLevel && hasNoCourse ? View.GONE : View.VISIBLE);

            vLevel.setVisibility(hasNoLevel ? View.GONE : View.VISIBLE);
            if (!hasNoLevel) {
                vLevelTitle.setText(user.getOrdinalLevel() + " Year");
                vLevelSubtitle.setText(R.string.profile_level);
                vLevelImage.setImageResource(R.drawable.ic_year_level);
            }

            vCourse.setVisibility(hasNoCourse ? View.GONE : View.VISIBLE);
            if (!hasNoCourse) {
                vCourseTitle.setText(user.getCourse());
                vCourseSubtitle.setText(R.string.profile_course);
                vCourseImage.setImageResource(R.drawable.ic_school);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_profile, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
