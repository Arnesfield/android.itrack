package com.systematix.itrack;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.systematix.itrack.helpers.ViewHelper;
import com.systematix.itrack.models.UserInfoViewModel;
import com.systematix.itrack.helpers.ViewSwitcherHelper;
import com.systematix.itrack.items.Auth;
import com.systematix.itrack.items.User;
import com.systematix.itrack.utils.Task;

public class ProfileActivity extends AppCompatActivity {

    private View vLoading;
    private View vUserInfo;
    private ViewSwitcherHelper viewSwitcher;
    private ActionBar actionBar;

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

        vLoading = ViewHelper.getLoadingView(this, R.id.profile_root_layout);
        vUserInfo = findViewById(R.id.profile_user_info);
        viewSwitcher = new ViewSwitcherHelper(this, R.id.profile_root_layout);

        getUserThenInitContent();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    private void getUserThenInitContent() {
        // show loading first as always
        viewSwitcher.switchTo(vLoading);
        Auth.getSavedUser(this, new Task.OnTaskFinishListener<User>() {
            @Override
            public void finish(final User result) {
                // TODO: remove handler
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewSwitcher.switchTo(vUserInfo);
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
