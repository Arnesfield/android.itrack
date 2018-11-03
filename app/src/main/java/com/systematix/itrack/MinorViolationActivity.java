package com.systematix.itrack;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.google.android.flexbox.FlexboxLayout;
import com.systematix.itrack.components.chip.Chip;
import com.systematix.itrack.components.chip.Chipable;
import com.systematix.itrack.config.UrlsList;
import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.items.Violation;
import com.systematix.itrack.models.ButtonStateModel;
import com.systematix.itrack.models.LoadingDialogModel;
import com.systematix.itrack.models.SelectableChipsModel;
import com.systematix.itrack.models.ViewFlipperModel;
import com.systematix.itrack.models.api.GetViolationsApiModel;
import com.systematix.itrack.utils.Api;
import com.systematix.itrack.utils.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MinorViolationActivity extends AppCompatActivity implements Api.OnApiRespondListener {

    private String serial;
    private String userName;
    private ViewFlipperModel viewFlipperModel;
    private SelectableChipsModel<Violation> selectableChipsModel;
    private ButtonStateModel btnStateModel;
    private LoadingDialogModel loadingDialogModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minor_violation);

        // get serial
        serial = getIntent().getStringExtra("serial");
        userName = getIntent().getStringExtra("userName");
        userName = userName == null ? "this student" : userName;

        if (serial == null) {
            Toast.makeText(this, R.string.error_no_serial, Toast.LENGTH_LONG).show();
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

        // set empty state btn listener
        final Button btnEmptyState = findViewById(R.id.minor_violation_empty_reload_btn);
        btnEmptyState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchViolations();
            }
        });

        final Button btnReport = findViewById(R.id.minor_violation_button);
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        btnStateModel = new ButtonStateModel(btnReport);

        // get and set those textViews
        final TextView tvSubtitle = findViewById(R.id.minor_violation_subtitle);
        final TextView tvBottom = findViewById(R.id.minor_violation_bottom_text);

        final Resources resources = getResources();
        final String subtitleText = resources.getString(R.string.minor_violation_view_subtitle, userName);
        final String bottomText = resources.getString(R.string.minor_violation_view_bottom_text, userName);

        tvSubtitle.setText(subtitleText);
        tvBottom.setText(bottomText);

        final ViewFlipper viewFlipper = findViewById(R.id.minor_violation_view_flipper);
        viewFlipperModel = new ViewFlipperModel(viewFlipper, R.id.minor_violation_loading_layout);

        // get violations here
        getViolations();
    }

    private void fetchViolations() {
        viewFlipperModel.switchTo(R.id.minor_violation_loading_layout);
        GetViolationsApiModel.fetch(this, MinorViolationActivity.this);
    }

    private void submit() {
        final Violation violation = selectableChipsModel.getSelectedChip();
        if (violation == null) {
            return;
        }

        // show dialog first
        getLoadingDialog(violation).show();

        // request
        final JSONObject params = new JSONObject();
        try {
            params.put("serial", serial);
            params.put("violation_id", violation.getId());

            // TODO: remove handler
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Api.post(MinorViolationActivity.this)
                        .setTag("minorViolation")
                        .setUrl(UrlsList.SEND_MINOR_VIOLATION_URL)
                        .setApiListener(MinorViolationActivity.this)
                        .request(params);
                }
            }, 5000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private AlertDialog getLoadingDialog(Chipable chip) {
        if (loadingDialogModel == null) {
            loadingDialogModel = new LoadingDialogModel(this);
            final AlertDialog dialog = loadingDialogModel.getDialog();
            final TextView tvMessage = loadingDialogModel.getMessageTextView();

            final String violationText = chip.getChipText();
            final String msg = getResources().getString(R.string.minor_violation_send_dialog_message, userName, violationText);

            dialog.setTitle(R.string.minor_violation_send_dialog_title);
            tvMessage.setText(msg);
        }

        return loadingDialogModel.getDialog();
    }

    private void getViolations() {
        // get from db
        final AppDatabase db = AppDatabase.getInstance(this);

        final Task.OnTaskListener<List<Violation>> listener = new Task.OnTaskListener<List<Violation>>() {
            @Override
            public void preExecute() {
                // make sure to show loading screen first
                viewFlipperModel.switchTo(R.id.minor_violation_loading_layout);
            }

            @Override
            public List<Violation> execute() {
                return db.violationDao().getAll("minor");
            }

            @Override
            public void finish(List<Violation> result) {
                // also handle if there are no results :(
                if (result.isEmpty()) {
                    // show empty state
                    viewFlipperModel.switchTo(R.id.minor_violation_empty_state_view);
                    // fetch api via button and call this method again
                } else {
                    gotResults(result);
                }
            }
        };

        new Task<>(listener).execute();
    }

    // finally got results!
    private void gotResults(List<Violation> violations) {
        viewFlipperModel.switchTo(R.id.minor_violation_content_view);
        final FlexboxLayout layout = findViewById(R.id.minor_violation_flexbox_layout);

        selectableChipsModel = new SelectableChipsModel<>(layout, violations);
        // set chip click listener
        selectableChipsModel.collectionSetListener(new Chip.OnChipClickListener() {
            @Override
            public void onChipClick(Chipable chip) {
                checkForSelected();
            }
        });
        // then check for selected
        checkForSelected();
    }

    // update btn model
    private void checkForSelected() {
        btnStateModel.setEnabled(selectableChipsModel.hasSelectedChip());
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
        if (tag.equals("violations")) {
            // whatever happens, getViolations :D
            getViolations();
        } else if (tag.equals("minorViolation")) {
            loadingDialogModel.getDialog().dismiss();
            startActivity(getOnSubmitIntent(true));
            finish();
        }
    }

    // OnApiErrorListener
    @Override
    public void onApiError(String tag, VolleyError error) {
        if (tag.equals("violations")) {
            // whatever happens, getViolations :D
            getViolations();
        } else if (tag.equals("minorViolation")) {
            // TODO: save to db
            loadingDialogModel.getDialog().dismiss();
            startActivity(getOnSubmitIntent(false));
            finish();
        }
    }

    // OnApiExceptionListener
    @Override
    public void onApiException(String tag, JSONException e) {
        // whatever happens, getViolations :D
        // unlikely it will pass here
        if (tag.equals("violations")) {
            getViolations();
        } else if (tag.equals("minorViolation")) {
            loadingDialogModel.getDialog().dismiss();
            Toast.makeText(this, R.string.minor_violation_sent_exception_msg, Toast.LENGTH_SHORT).show();
        }
    }
}
