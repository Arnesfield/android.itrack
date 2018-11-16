package com.systematix.itrack;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ViewFlipper;

import com.systematix.itrack.components.sync.Sync;
import com.systematix.itrack.config.RequestCodesList;
import com.systematix.itrack.fragments.NfcFragment;
import com.systematix.itrack.fragments.AttendanceFragment;
import com.systematix.itrack.helpers.AlertDialogHelper;
import com.systematix.itrack.items.Auth;
import com.systematix.itrack.items.User;
import com.systematix.itrack.models.FragmentModel;
import com.systematix.itrack.models.LoadingDialogModel;
import com.systematix.itrack.models.NavDrawerModel;
import com.systematix.itrack.models.NfcEnabledStateModel;
import com.systematix.itrack.models.NfcNoPermissionStateModel;
import com.systematix.itrack.models.SyncModel;
import com.systematix.itrack.models.ViewFlipperModel;
import com.systematix.itrack.utils.Task;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NfcEnabledStateModel.OnDiscoveredListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;

    private Sync sync;
    private User user;
    private FragmentModel fragmentModel;
    private ViewFlipperModel viewFlipperModel;
    private NavDrawerModel navDrawerModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // make sure to do loading screen first hehehe
        final ViewFlipper viewFlipper = findViewById(R.id.main_view_flipper);
        viewFlipperModel = new ViewFlipperModel(viewFlipper, R.id.main_loading_layout);

        checkForMinorViolationSent();

        // make sync
        sync = new Sync(this);
        Sync.setSyncListener(new SyncModel());

        // get user auth
        Auth.getSavedUser(this, new Task.OnTaskFinishListener<User>() {
            @Override
            public void finish(final User result) {
                user = result;
                initContent();
            }
        });
    }

    // check for minor violation sent
    private void checkForMinorViolationSent() {
        final Intent intent = getIntent();
        if (intent.getBooleanExtra("minorViolationSent", false)) {
            // if sent successfully
            final boolean success = intent.getBooleanExtra("minorViolationSuccess", false);

            final int title = success ? R.string.violation_report_sent_success_dialog_title : R.string.violation_report_sent_fail_dialog_title;
            final int message = success ? R.string.violation_report_sent_success_dialog_message : R.string.violation_report_sent_fail_dialog_message;

            final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.action_dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

            AlertDialogHelper.setPositiveColorPrimary(dialog);

            // show et
            dialog.show();
        }
    }

    private void initContent() {
        if (user == null) {
            // logout when there is no user :/
            doLogout();
            return;
        }

        // get violations if teacher hehe
        final boolean isTeacher = user.checkAccess("teacher");

        // switch to actual content
         viewFlipperModel.switchTo(R.id.main_content_layout);

        // create fragmentModel
        // set whatever the current is
        getFragmentModel().setCurrFragment();

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
        final LoadingDialogModel dialogModel = new LoadingDialogModel(this, R.string.logout_title, R.string.msg_please_wait);
        final AlertDialog dialog = dialogModel.getDialog();

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
                final MainActivity activity = MainActivity.this;
                final Intent intent = new Intent(activity, LoginActivity.class);

                dialog.dismiss();
                startActivity(intent);
                activity.finish();
            }
        }).execute();
    }

    private void updateNavigationView() {
        if (user == null) {
            return;
        }

        final boolean isTeacher = user.checkAccess("teacher");

        // set menu to use!
        final int navMenu = isTeacher ? R.menu.menu_main_teacher : R.menu.menu_main_student;
        // from here, set also the name of user
        // and the menu, duh

        // set navDrawerModel
        getNavDrawerModel();
        navDrawerModel.setMenu(navMenu);
        navDrawerModel.setHeader(user);

        // update nav item selected
        navDrawerModel.setNavItemSelected(getFragmentModel());

        // now, update dat menu!
        invalidateOptionsMenu();
    }

    // model getters
    private NavDrawerModel getNavDrawerModel() {
        return navDrawerModel = navDrawerModel == null ? new NavDrawerModel(navigationView) : navDrawerModel;
    }

    private FragmentModel getFragmentModel() {
        // create fragmentModel
        if (user != null && fragmentModel == null) {
            // NfcFragment is the default fragment for teacher
            // AttendanceFragment for student
            final boolean isTeacher = user.checkAccess("teacher");
            final Fragment fragment = isTeacher ? new NfcFragment() : new AttendanceFragment();
            fragmentModel = new FragmentModel(this, fragment, R.id.main_content_layout, true);
        }

        return fragmentModel;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // set fragment here also
        if (user != null) {
            getFragmentModel().setCurrFragment();
        }
        // update nav item selected
        getNavDrawerModel().setNavItemSelected(getFragmentModel());
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

    // sync
    @Override
    protected void onStop() {
        sync.onActivityStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sync.onActivityStart();
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

        if (id == R.id.nav_attendance) {
            newFragment = new AttendanceFragment();
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
        getFragmentModel().setFragment(newFragment);

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
}
