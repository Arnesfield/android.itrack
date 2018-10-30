package com.systematix.itrack;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.profile_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

            setTitle(user.getName(false));

            final ImageView ivProfile = findViewById(R.id.profile_iv);
            final View vName = findViewById(R.id.profile_name);
            final View vNumber = findViewById(R.id.profile_number);
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

            vLevelTitle.setText(user.getOrdinalLevel() + " Year");
            vLevelSubtitle.setText(R.string.profile_level);
            vLevelImage.setImageResource(R.drawable.ic_year_level);

            vCourseTitle.setText(user.getCourse());
            vCourseSubtitle.setText(R.string.profile_course);
            vCourseImage.setImageResource(R.drawable.ic_school);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_profile, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
