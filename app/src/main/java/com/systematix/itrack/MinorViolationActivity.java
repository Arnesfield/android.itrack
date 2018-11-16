package com.systematix.itrack;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.items.Violation;
import com.systematix.itrack.models.ButtonStateModel;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minor_violation);

        // get serial
        final Intent intent = getIntent();
        serial = intent.getStringExtra("serial");
        userName = intent.getStringExtra("userName");
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

        final Button btnNext = findViewById(R.id.minor_violation_button);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        btnStateModel = new ButtonStateModel(btnNext);

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
        GetViolationsApiModel.fetch(this, this);
    }

    private void next() {
        final Violation violation = selectableChipsModel.getSelectedChip();
        if (violation == null) {
            return;
        }

        final Intent intent = new Intent(this, MinorViolationExtrasActivity.class);
        intent.putExtra("serial", serial);
        intent.putExtra("userName", userName);
        intent.putExtra("violationId", violation.getId());
        intent.putExtra("violationText", violation.getName());

        startActivity(intent);
    }

    private void getViolations() {
        // get from db
        final AppDatabase db = AppDatabase.getInstance(this);
        new Task<>(new Task.OnTaskListener<List<Violation>>() {
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
        }).execute();
    }

    // finally got results!
    private void gotResults(List<Violation> violations) {
        viewFlipperModel.switchTo(R.id.minor_violation_content_view);
        final FlexboxLayout layout = findViewById(R.id.minor_violation_flexbox_layout);

        selectableChipsModel = new SelectableChipsModel<>(violations);
        selectableChipsModel.init(layout);
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

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    // OnApiSuccessListener
    @Override
    public void onApiSuccess(String tag, JSONObject response, boolean success, String msg) {
        // whatever happens, getViolations :D
        getViolations();
    }

    // OnApiErrorListener
    @Override
    public void onApiError(String tag, VolleyError error) {
        // whatever happens, getViolations :D
        getViolations();
    }

    // OnApiExceptionListener
    @Override
    public void onApiException(String tag, JSONException e) {
        // whatever happens, getViolations :D
        // unlikely it will pass here
        getViolations();
    }
}
