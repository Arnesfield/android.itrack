package com.systematix.itrack;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.VolleyError;
import com.systematix.itrack.config.UrlsList;
import com.systematix.itrack.helpers.UserInfoViewHelper;
import com.systematix.itrack.helpers.ViewSwitcherHelper;
import com.systematix.itrack.items.User;
import com.systematix.itrack.utils.Api;

import org.json.JSONException;
import org.json.JSONObject;

public class MakeReportActivity extends AppCompatActivity implements Api.OnApiRespondListener {

    private String serial;
    private User user;
    private View vLoading;
    private View vMakeReport;
    private View vUserInfo;
    private View vNoUser;
    private ViewSwitcher vUserInfoSwitcher;
    private ViewSwitcherHelper viewSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_report);

        serial = getIntent().getStringExtra("serial");
        if (hasNoSerial()) { return; }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set back button
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        final LayoutInflater inflater = getLayoutInflater();

        final ViewGroup rootView = findViewById(R.id.make_report_root_layout);
        vLoading = inflater.inflate(R.layout.loading_layout, rootView, false);
        vMakeReport = inflater.inflate(R.layout.make_report_view, rootView, false);
        vUserInfoSwitcher = vMakeReport.findViewById(R.id.make_report_user_info_switcher);
        vUserInfo = vUserInfoSwitcher.findViewById(R.id.make_report_user_info);
        vNoUser = vUserInfoSwitcher.findViewById(R.id.make_report_no_user);

        // add reload action
        final Button btnNoUserReload = vNoUser.findViewById(R.id.make_report_no_user_reload_btn);
        btnNoUserReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });

        // set stuff
        viewSwitcher = new ViewSwitcherHelper(rootView);
        request();
    }

    private void request() {
        if (hasNoSerial()) { return; }
        viewSwitcher.switchTo(vLoading);
        // get da user with that serial!
        final JSONObject params = new JSONObject();
        try {
            params.put("serial", serial);
            // TODO: remove handler
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Api.post(MakeReportActivity.this)
                        .setUrl(UrlsList.GET_USER_URL)
                        .setApiListener(MakeReportActivity.this)
                        .request(params);
                }
            }, 5000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean hasNoSerial() {
        if (serial == null) {
            Toast.makeText(this, R.string.error_no_serial, Toast.LENGTH_LONG).show();
            finish();
            return true;
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    // OnApiSuccessListener
    @Override
    public void onApiSuccess(String tag, JSONObject response) throws JSONException {
        // check if successful
        if (!Api.isSuccessful(response)) {
            final String msg = response.getString("msg");
            if (msg != null && msg.length() > 0) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
            finish();
            return;
        }

        // get user
        final JSONObject jsonUser = response.getJSONObject("user");
        user = new User(jsonUser);

        viewSwitcher.switchTo(vMakeReport);
        if (vUserInfoSwitcher.getCurrentView() != vUserInfo) {
            vUserInfoSwitcher.showNext();
        }

        // set the user
        final ImageView ivUser = findViewById(R.id.make_report_user_iv);
        user.loadImage(this, ivUser, null);
        UserInfoViewHelper.init(this, user);
    }

    // OnApiErrorListener
    @Override
    public void onApiError(String tag, VolleyError error) {
        Toast.makeText(this, R.string.error_cannot_load_user, Toast.LENGTH_LONG).show();

        viewSwitcher.switchTo(vMakeReport);
        if (vUserInfoSwitcher.getCurrentView() != vNoUser) {
            vUserInfoSwitcher.showNext();
        }
    }

    // OnApiExceptionListener
    @Override
    public void onApiException(String tag, JSONException e) {
        e.printStackTrace();
        Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
        finish();
    }
}
