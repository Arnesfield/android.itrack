package com.systematix.itrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.systematix.itrack.config.PreferencesList;
import com.systematix.itrack.items.User;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;

    private static final int[] MENU_ITEM_IDS = new int[]{
            R.id.nav_gallery
    };

    private static final String[] MENU_ITEM_AUTH = new String[]{
            "teacher",
            "student"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
        final SharedPreferences sharedPreferences = getSharedPreferences(PreferencesList.PREF_APP, MODE_PRIVATE);
        final boolean didLogin = sharedPreferences.getBoolean(PreferencesList.PREF_DID_LOG_IN, false);

        if (didLogin) {
            Snackbar.make(findViewById(R.id.fab), R.string.msg_log_in, Snackbar.LENGTH_LONG).show();
        }
        // then remove that prop
        sharedPreferences.edit().remove(PreferencesList.PREF_DID_LOG_IN).apply();
    }

    private void doLogout() {
        final SharedPreferences sharedPreferences = getSharedPreferences(PreferencesList.PREF_APP, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(PreferencesList.PREF_USER_ID);
        editor.remove(PreferencesList.PREF_USER_JSON);
        editor.remove(PreferencesList.PREF_DID_LOG_IN);
        editor.putBoolean(PreferencesList.PREF_DID_LOG_OUT, true);
        editor.apply();

        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateNavigationView() {
        final Menu menu = navigationView.getMenu();
        try {
            // get user auth
            final User user = User.getUserFromSharedPref(this);

            for (int i = 0; i < MENU_ITEM_IDS.length; i++) {
                final MenuItem item = menu.findItem(MENU_ITEM_IDS[i]);
                // depending on user access, reveal the menu item
                item.setVisible(user.checkAccess(MENU_ITEM_AUTH[i]));
            }

            // from here, set also the name of user
            final View headerView = navigationView.getHeaderView(0);
            final TextView tvTitle = headerView.findViewById(R.id.nav_title);
            final TextView tvSubtitle = headerView.findViewById(R.id.nav_subtitle);
            final ImageView imageView = headerView.findViewById(R.id.nav_image_view);
            final TextView textView = headerView.findViewById(R.id.nav_no_image_text);

            tvTitle.setText(user.getName());
            tvSubtitle.setText(user.getNumber());
            user.loadImage(this, imageView, textView);
        } catch (JSONException e) {
            e.printStackTrace();
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

        if (id == R.id.nav_profile) {
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

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
