package com.systematix.itrack;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.items.Violation;
import com.systematix.itrack.models.ButtonStateModel;
import com.systematix.itrack.models.SearchableSpinnerModel;
import com.systematix.itrack.models.ViewFlipperModel;
import com.systematix.itrack.models.api.GetViolationsApiModel;
import com.systematix.itrack.utils.Api;
import com.systematix.itrack.utils.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ViolationActivity extends AppCompatActivity implements Api.OnApiRespondListener {

    private List<Violation> minorViolations;
    private List<Violation> majorViolations;
    private String serial;
    private String userName;
    private ViewFlipperModel viewFlipperModel;
    // private SelectableChipsModel<Violation> selectableChipsModel;
    private ButtonStateModel btnStateModel;
    private Spinner minorSpinner;
    private Spinner majorSpinner;
    private SearchableSpinnerModel spinnerModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation);

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

        minorSpinner = findViewById(R.id.violation_minor_spinner);
        majorSpinner = findViewById(R.id.violation_major_spinner);

        // set empty state btn listener
        final Button btnEmptyState = findViewById(R.id.violation_empty_reload_btn);
        btnEmptyState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchViolations();
            }
        });

        final Button btnNext = findViewById(R.id.violation_button);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        btnStateModel = new ButtonStateModel(btnNext);

        // get and set those textViews
        final TextView tvSubtitle = findViewById(R.id.violation_minor_subtitle);
        final TextView tvBottom = findViewById(R.id.violation_bottom_text);

        final Resources resources = getResources();
        final String subtitleText = resources.getString(R.string.violation_view_minor_subtitle, userName);
        final String bottomText = resources.getString(R.string.violation_view_bottom_text, userName);

        tvSubtitle.setText(subtitleText);
        tvBottom.setText(bottomText);

        final ViewFlipper viewFlipper = findViewById(R.id.violation_view_flipper);
        viewFlipperModel = new ViewFlipperModel(viewFlipper, R.id.violation_loading_layout);

        // get violations here
        getViolations();
    }

    private void fetchViolations() {
        viewFlipperModel.switchTo(R.id.violation_loading_layout);
        GetViolationsApiModel.fetch(this, this);
    }

    private void next() {
        final Violation violation = (Violation) spinnerModel.getSelected();
        if (violation == null) {
            return;
        }

        final Intent intent = new Intent(this, ViolationReportActivity.class);
        intent.putExtra("serial", serial);
        intent.putExtra("userName", userName);
        intent.putExtra("violationId", violation.getId());
        intent.putExtra("violationType", violation.getType());
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
                viewFlipperModel.switchTo(R.id.violation_loading_layout);
            }

            @Override
            public List<Violation> execute() {
                return db.violationDao().getAll();
            }

            @Override
            public void finish(List<Violation> result) {
                // also handle if there are no results :(
                if (result.isEmpty()) {
                    // show empty state
                    viewFlipperModel.switchTo(R.id.violation_empty_state_view);
                    // fetch api via button and call this method again
                } else {
                    gotResults(result);
                }
            }
        }).execute();
    }

    // finally got results!
    private void gotResults(List<Violation> violations) {
        viewFlipperModel.switchTo(R.id.violation_content_view);

        minorViolations = Violation.filterByType(violations, "minor");
        majorViolations = Violation.filterByType(violations, "major");

        final Resources resources = getResources();
        final String minorEmpty = resources.getString(R.string.violation_minor_spinner_empty_initial_text);
        final String majorEmpty = resources.getString(R.string.violation_major_spinner_empty_initial_text);

        final ArrayAdapter minorAdapter = Violation.Adapter.setMe(minorSpinner, minorViolations, minorEmpty);
        final ArrayAdapter majorAdapter = Violation.Adapter.setMe(majorSpinner, majorViolations, majorEmpty);

        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkForSelected();
            }
        };

        spinnerModel = new SearchableSpinnerModel();
        spinnerModel
            .bind(minorSpinner, minorAdapter, R.string.violation_minor_spinner_hint, listener)
            .bind(majorSpinner, majorAdapter, R.string.violation_major_spinner_hint, listener);

        // then check for selected
        checkForSelected();
    }

    // update btn model
    private void checkForSelected() {
        btnStateModel.setEnabled(spinnerModel.hasSelected());
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
