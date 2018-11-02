package com.systematix.itrack;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.systematix.itrack.config.RequestCodesList;
import com.systematix.itrack.config.UrlsList;
import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.database.daos.ViolationDao;
import com.systematix.itrack.fragments.NfcFragment;
import com.systematix.itrack.fragments.StudentFragment;
import com.systematix.itrack.helpers.FragmentHelper;
import com.systematix.itrack.helpers.ViewFlipperHelper;
import com.systematix.itrack.items.Auth;
import com.systematix.itrack.items.User;
import com.systematix.itrack.items.Violation;
import com.systematix.itrack.models.NavDrawerModel;
import com.systematix.itrack.models.NfcEnabledStateModel;
import com.systematix.itrack.models.NfcNoPermissionStateModel;
import com.systematix.itrack.utils.Api;
import com.systematix.itrack.utils.Task;
import com.systematix.itrack.utils.simple.SimpleLoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NfcEnabledStateModel.OnDiscoveredListener, Api.OnApiRespondListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;

    private User user;
    private FragmentHelper fragmentHelper;
    private ViewFlipperHelper viewFlipperHelper;
    private NavDrawerModel navDrawerModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // make sure to do loading screen first hehehe
        final ViewFlipper viewFlipper = findViewById(R.id.main_view_flipper);
        viewFlipperHelper = new ViewFlipperHelper(viewFlipper, R.id.main_loading_layout);

        // get user auth
        Auth.getSavedUser(this, new Task.OnTaskFinishListener<User>() {
            @Override
            public void finish(final User result) {
                // TODO: remove handlers lol
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        user = result;
                        initContent();
                    }
                }, 5000);
            }
        });
    }

    private void initContent() {
        if (user == null) {
            // logout when there is no user :/
            doLogout();
            return;
        }

        // get violations if teacher hehe
        final boolean isTeacher = user.checkAccess("teacher");
        if (isTeacher) {
            getViolations();
        }

        // switch to actual content
         viewFlipperHelper.switchTo(R.id.main_content_layout);

        // create fragmentHelper
        if (fragmentHelper == null) {
            // NfcFragment is the default fragment for teacher
            // StudentFragment for student
            final Fragment fragment = isTeacher ? new NfcFragment() : new StudentFragment();
            fragmentHelper = new FragmentHelper(this, fragment, R.id.main_content_layout, true);
        }
        // set whatever the current is
        fragmentHelper.setCurrFragment();

        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        updateNavigationView();
        navigationView.setNavigationItemSelectedListener(this);

        checkForLogInMsg();
    }

    private void checkForLogInMsg() {
        // if did login, then show snackbar
        if (Auth.didLogin(this)) {
            Snackbar.make(findViewById(R.id.fab), R.string.msg_log_in, Snackbar.LENGTH_LONG).show();
        }
    }

    private void doLogout() {
        // build dialog
        final AlertDialog dialog = SimpleLoadingDialog.build(this, R.string.logout_title, R.string.msg_please_wait);

        Task<Void> task = Auth.removeSavedUser(this);
        task.setPreExecuteListener(new Task.OnTaskPreExecuteListener() {
            @Override
            public void preExecute() {
                // show loading
                dialog.show();
            }
        }).setFinishListener(new Task.OnTaskFinishListener<Void>() {
            @Override
            public void finish(Void result) {
                // TODO: remove handler
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final MainActivity activity = MainActivity.this;
                        final Intent intent = new Intent(activity, LoginActivity.class);

                        dialog.dismiss();
                        startActivity(intent);
                        activity.finish();
                    }
                }, 5000);
            }
        }).execute();
    }

    private void updateNavigationView() {
        if (user == null) {
            return;
        }

        // set menu to use!
        final int navMenu = user.checkAccess("teacher") ? R.menu.menu_main_teacher : R.menu.menu_main_student;
        // from here, set also the name of user
        // and the menu, duh

        // set navDrawerModel
        getNavDrawerModel();
        navDrawerModel.setMenu(navMenu);
        navDrawerModel.setHeader(user);

        // update nav item selected
        navDrawerModel.setNavItemSelected(fragmentHelper);

        // now, update dat menu!
        invalidateOptionsMenu();
    }

    private NavDrawerModel getNavDrawerModel() {
        return navDrawerModel = navDrawerModel == null ? new NavDrawerModel(navigationView) : navDrawerModel;
    }

    private void getViolations() {
        Api.post(this)
            .setTag("violations")
            .setUrl(UrlsList.GET_MINOR_VIOLATIONS_URL)
            .setApiListener(this)
            .request();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // update nav item selected
        getNavDrawerModel().setNavItemSelected(fragmentHelper);
        NfcEnabledStateModel.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcEnabledStateModel.onPause(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        NfcEnabledStateModel.onNewIntent(this, intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCodesList.NFC:
                NfcNoPermissionStateModel.onRequestPermissionsResult(this, permissions, grantResults);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment newFragment = null;

        if (id == R.id.nav_dashboard) {
            newFragment = new StudentFragment();
        } else if (id == R.id.nav_nfc) {
            newFragment = new NfcFragment();
        } else if (id == R.id.nav_profile) {
            final Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            doLogout();
            return true;
        }

        // set fragment
        fragmentHelper.setFragment(newFragment);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // OnDiscoveredListener
    @Override
    public void onDiscovered(String serial) {
        final Intent intent = new Intent(this, MakeReportActivity.class);
        intent.putExtra("serial", serial);
        startActivity(intent);
    }

    // OnApiSuccessListener
    @Override
    public void onApiSuccess(String tag, JSONObject response, boolean success, String msg) throws JSONException {
        if (!success) {
            if (msg != null || !msg.isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
            finish();
            return;
        }

        final List<Violation> violations = Violation.collection(response.getJSONArray("violations"));
        final AppDatabase db = AppDatabase.getInstance(this);
        // task
        new Task<>(new Task.OnTaskExecuteListener<Void>() {
            @Override
            public Void execute() {
                final ViolationDao dao = db.violationDao();
                // clear all violations hehe
                // then insert new ones just to make sure
                // you also don't need to tell your user about this i think
                dao.deleteAll();
                dao.insertAll(violations);
                return null;
            }
        }).execute();
    }

    // OnApiErrorListener
    @Override
    public void onApiError(String tag, VolleyError error) throws JSONException {

    }

    // OnApiExceptionListener
    @Override
    public void onApiException(String tag, JSONException e) {

    }
}
