package com.systematix.itrack;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.items.Violation;
import com.systematix.itrack.utils.Task;

import java.util.List;

public class IncidentReportActivity extends AppCompatActivity {

    private String serial;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_report);

        // get serial
        serial = getIntent().getStringExtra("serial");
        userName = getIntent().getStringExtra("userName");

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

        // get violations here
        // make sure to show loading screen first
        getViolations();
    }

    private void getViolations() {
        // get from db
        final AppDatabase db = AppDatabase.getInstance(this);

        final Task.OnTaskFinishListener<List<Violation>> finishListener = new Task.OnTaskFinishListener<List<Violation>>() {
            @Override
            public void finish(List<Violation> result) {
                // TODO: show em results
            }
        };

        new Task<>(new Task.OnTaskExecuteListener<List<Violation>>() {
            @Override
            public List<Violation> execute() {
                return db.violationDao().getAll("minor");
            }
        }, finishListener).execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
}
