package com.systematix.itrack;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.systematix.itrack.helpers.UserInfoViewHelper;
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

        final ViewGroup rootView = findViewById(R.id.profile_root_layout);
        vLoading = getLayoutInflater().inflate(R.layout.loading_layout, rootView, false);
        vUserInfo = findViewById(R.id.profile_user_info);
        viewSwitcher = new ViewSwitcherHelper(rootView, null);

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
        UserInfoViewHelper.init(this, user);
    }
}
