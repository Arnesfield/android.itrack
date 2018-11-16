package com.systematix.itrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.systematix.itrack.config.UrlsList;
import com.systematix.itrack.items.Auth;
import com.systematix.itrack.items.Report;
import com.systematix.itrack.models.EditTextModel;
import com.systematix.itrack.models.LoadingDialogModel;
import com.systematix.itrack.utils.Api;
import com.systematix.itrack.utils.Task;

import org.json.JSONException;
import org.json.JSONObject;

public class ViolationReportActivity extends AppCompatActivity implements Api.OnApiRespondListener {

    private Report report;
    private String serial;
    private String userName;
    private String violationType;
    private String violationText;
    private int violationId;
    private int reporterId;
    private LoadingDialogModel loadingDialogModel;
    private TextInputEditText txtLocation;
    private TextInputEditText txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_report);

        // get serial
        final Intent intent = getIntent();
        serial = intent.getStringExtra("serial");
        userName = intent.getStringExtra("userName");
        violationId = intent.getIntExtra("violationId", -1);
        reporterId = Auth.getSavedUserId(this);
        violationType = intent.getStringExtra("violationType");
        violationText = intent.getStringExtra("violationText");

        // just to make sure hehe
        if (violationId == -1 || reporterId == -1) {
            final int msg = violationId == -1 ? R.string.error_no_violation_selected : R.string.error_no_auth_user;
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set back button
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // set btn
        final View btnSubmit = findViewById(R.id.violation_report_button);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        // set textView
        final String msg = getResources().getString(R.string.violation_report_subtitle, userName, violationText);
        final TextView tvSubtitle = findViewById(R.id.violation_report_subtitle);
        tvSubtitle.setText(msg);

        // set edit text fields
        txtLocation = findViewById(R.id.violation_report_txt_location);
        txtMessage = findViewById(R.id.violation_report_txt_message);

        // add placeholders
        EditTextModel.setOnFocusPlaceholder(txtLocation, R.string.violation_report_txt_location_placeholder);
        EditTextModel.setOnFocusPlaceholder(txtMessage, R.string.violation_report_txt_message_placeholder);
    }

    private void submit() {
        // show dialog first
        getLoadingDialog().show();

        // request
        final String location = txtLocation.getText().toString();
        final String message = txtMessage.getText().toString();
        final long timestamp = System.currentTimeMillis() / 1000;

        // make report
        report = new Report(violationId, reporterId, serial, location, message, timestamp);
        final JSONObject params = report.toApiJson();

        Api.post(this)
            .setTag("sendMinorViolation")
            .setUrl(UrlsList.SEND_VIOLATION_URL)
            .setApiListener(this)
            .request(params);
    }

    private AlertDialog getLoadingDialog() {
        if (loadingDialogModel == null) {
            loadingDialogModel = new LoadingDialogModel(this);
            final AlertDialog dialog = loadingDialogModel.getDialog();
            final TextView tvMessage = loadingDialogModel.getMessageTextView();

            final String msg = getResources().getString(R.string.violation_report_send_dialog_message, userName, violationText);

            dialog.setTitle(R.string.violation_report_send_dialog_title);
            tvMessage.setText(msg);
        }

        return loadingDialogModel.getDialog();
    }

    // intent after submit
    private Intent getOnSubmitIntent(boolean success) {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("minorViolationSent", true);
        intent.putExtra("minorViolationSuccess", success);
        return intent;
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    // OnApiSuccessListener
    @Override
    public void onApiSuccess(String tag, JSONObject response, boolean success, String msg) {
        getLoadingDialog().dismiss();
        if (success) {
            startActivity(getOnSubmitIntent(true));
            finish();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    // OnApiErrorListener
    @Override
    public void onApiError(String tag, VolleyError error) {
        // save properly!
        report.save(this, null, new Task.OnTaskFinishListener<Void>() {
            @Override
            public void finish(Void result) {
                getLoadingDialog().dismiss();
                startActivity(getOnSubmitIntent(false));
                ViolationReportActivity.this.finish();
            }
        });
    }

    // OnApiExceptionListener
    @Override
    public void onApiException(String tag, JSONException e) {
        // unlikely to pass here
        getLoadingDialog().dismiss();
        Toast.makeText(this, R.string.violation_report_sent_exception_msg, Toast.LENGTH_SHORT).show();
    }
}
