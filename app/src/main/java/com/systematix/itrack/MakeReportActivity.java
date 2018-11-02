package com.systematix.itrack;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.systematix.itrack.config.UrlsList;
import com.systematix.itrack.models.ViewFlipperModel;
import com.systematix.itrack.items.User;
import com.systematix.itrack.models.UserInfoViewModel;
import com.systematix.itrack.utils.Api;

import org.json.JSONException;
import org.json.JSONObject;

public class MakeReportActivity extends AppCompatActivity implements Api.OnApiRespondListener {

    private String serial;
    private User user;
    private View vUserInfo;
    private ViewFlipperModel viewFlipperModel;
    private ViewFlipperModel makeReportFlipperHelper;
    private Button btnMakeReport;
    private View.OnClickListener onBtnMakeReportClick;

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

        final ViewFlipper viewFlipper = findViewById(R.id.make_report_view_flipper);

        final View vMakeReport = findViewById(R.id.make_report_content_view);
        final ViewFlipper vUserInfoFlipper = vMakeReport.findViewById(R.id.make_report_user_info_view_flipper);
        final View vNoUser = vUserInfoFlipper.findViewById(R.id.make_report_no_user);

        btnMakeReport = vMakeReport.findViewById(R.id.make_report_button);
        vUserInfo = vUserInfoFlipper.findViewById(R.id.make_report_user_info);

        // click for btn
        onBtnMakeReportClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null) {
                    Toast.makeText(v.getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                final String name = user.getName(false);

                final Intent intent = new Intent(MakeReportActivity.this, IncidentReportActivity.class);
                intent.putExtra("serial", serial);
                if (name != null) {
                    intent.putExtra("userName", name);
                }
                startActivity(intent);
            }
        };

        // add reload action
        final Button btnNoUserReload = vNoUser.findViewById(R.id.make_report_no_user_reload_btn);
        btnNoUserReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });

        // set stuff
        viewFlipperModel = new ViewFlipperModel(viewFlipper);
        makeReportFlipperHelper = new ViewFlipperModel(vUserInfoFlipper, R.id.make_report_no_user);
        request();
    }

    private void request() {
        if (hasNoSerial()) { return; }
        viewFlipperModel.switchTo(R.id.make_report_loading_layout);
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
    public void onApiSuccess(String tag, JSONObject response, boolean success, String msg) throws JSONException {
        // check if successful
        if (!success) {
            if (msg != null && msg.length() > 0) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
            finish();
            return;
        }

        // get user
        final JSONObject jsonUser = response.getJSONObject("user");
        user = new User(jsonUser);

        viewFlipperModel.switchTo(R.id.make_report_content_view);
        makeReportFlipperHelper.switchTo(R.id.make_report_user_info);

        // set btn click
        btnMakeReport.setText(R.string.make_report_action);
        btnMakeReport.setOnClickListener(onBtnMakeReportClick);

        // set the user
        final ImageView ivUser = findViewById(R.id.make_report_user_iv);
        user.loadImage(this, ivUser, null);
        UserInfoViewModel.init(vUserInfo, user);
    }

    // OnApiErrorListener
    @Override
    public void onApiError(String tag, VolleyError error) {
        Toast.makeText(this, R.string.error_cannot_load_user, Toast.LENGTH_LONG).show();

        viewFlipperModel.switchTo(R.id.make_report_content_view);
        makeReportFlipperHelper.switchTo(R.id.make_report_no_user);

        // set btn click
        btnMakeReport.setText(R.string.make_report_action_anyway);
        btnMakeReport.setOnClickListener(onBtnMakeReportClick);
    }

    // OnApiExceptionListener
    @Override
    public void onApiException(String tag, JSONException e) {
        e.printStackTrace();
        Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
        finish();
    }
}
