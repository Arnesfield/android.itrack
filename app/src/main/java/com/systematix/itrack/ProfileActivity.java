package com.systematix.itrack;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;

import com.systematix.itrack.helpers.UserInfoViewHelper;
import com.systematix.itrack.items.Auth;
import com.systematix.itrack.items.User;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set back button
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

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
            user.loadImage(this, ivProfile, null);

            // set user stuff
            UserInfoViewHelper.init(this, user);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_profile, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
