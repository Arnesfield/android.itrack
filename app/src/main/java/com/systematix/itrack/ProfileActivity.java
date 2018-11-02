package com.systematix.itrack;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.systematix.itrack.helpers.ViewFlipperHelper;
import com.systematix.itrack.items.Auth;
import com.systematix.itrack.items.User;
import com.systematix.itrack.models.UserInfoViewModel;
import com.systematix.itrack.utils.Task;

public class ProfileActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private ViewFlipperHelper viewFlipperHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        // set back button
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        final ViewFlipper viewFlipper = findViewById(R.id.profile_view_flipper);
        viewFlipperHelper = new ViewFlipperHelper(viewFlipper, R.id.profile_loading_layout);

        getUserThenInitContent();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    private void getUserThenInitContent() {
        // show loading first as always
        viewFlipperHelper.switchTo(R.id.profile_loading_layout);
        Auth.getSavedUser(this, new Task.OnTaskFinishListener<User>() {
            @Override
            public void finish(final User result) {
                // TODO: remove handler
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewFlipperHelper.switchTo(R.id.profile_user_info);
                        initContent(result);
                    }
                }, 5000);

            }
        });
    }

    private void initContent(final User user) {
        if (user == null) {
            Toast.makeText(this, R.string.error_profile, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // set the title
        final CollapsingToolbarLayout toolbarLayout = findViewById(R.id.profile_collapsing_toolbar_layout);
        toolbarLayout.setTitleEnabled(true);
        actionBar.setTitle(user.getName(false));

        final ImageView ivProfile = findViewById(R.id.profile_iv);
        user.loadImage(this, ivProfile, null);

        // set user stuff
        UserInfoViewModel.init(findViewById(R.id.profile_user_info), user);
    }
}
