package com.systematix.itrack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.IOException;

public class ViolationReportActivity extends AppCompatActivity implements Api.OnApiRespondListener {

    private static final int IMG_CAMERA_REQUEST_CODE = 16;
    private static final int IMG_GALLERY_REQUEST_CODE = 17;

    private Report report;
    private String serial;
    private String userName;
    private String violationType;
    private String violationText;
    private String cameraImagePath;
    private int violationId;
    private int reporterId;
    private Bitmap bitmap;
    private View imgContainer;
    private ImageView imgProof;
    private LoadingDialogModel loadingDialogModel;
    private TextInputEditText txtLocation;
    private TextInputEditText txtMessage;
    private TextInputEditText txtAge;
    private TextInputEditText txtYearSection;

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

        // set edit text fields
        final int visibility = violationType.equals("major") ? View.VISIBLE : View.GONE;
        findViewById(R.id.violation_report_txt_age_layout).setVisibility(visibility);
        findViewById(R.id.violation_report_txt_year_section_layout).setVisibility(visibility);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set back button
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // set btn
        final Button btnSubmit = findViewById(R.id.violation_report_button);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        // image buttons
        // final Button btnCamera = findViewById(R.id.violation_report_btn_camera);
        final Button btnGallery = findViewById(R.id.violation_report_btn_gallery);

        /*btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });*/
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

        // set textView
        final String msg = getResources().getString(R.string.violation_report_subtitle, userName, violationText);
        final TextView tvSubtitle = findViewById(R.id.violation_report_subtitle);
        tvSubtitle.setText(msg);
        
        // set stuff
        imgContainer = findViewById(R.id.violation_report_img_container);
        imgProof = findViewById(R.id.violation_report_img_proof);

        // set edit text fields
        txtLocation = findViewById(R.id.violation_report_txt_location);
        txtMessage = findViewById(R.id.violation_report_txt_message);
        txtAge = findViewById(R.id.violation_report_txt_age);
        txtYearSection = findViewById(R.id.violation_report_txt_year_section);

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

        // make report
        report = new Report(violationId, violationType, reporterId, serial, location, message, bitmap);

        if (violationType.equals("major")) {
            final String age = txtAge.getText().toString();
            final String yearSection = txtYearSection.getText().toString();
            report.setAge(age);
            report.setYearSection(yearSection);
        }

        final JSONObject params = report.toApiJson();

        Api.post(this)
            .setTag("sendViolation")
            .setUrl(UrlsList.SEND_VIOLATION_URL)
            .setApiListener(this)
            .request(params);
    }

    // image
    /*private void startCamera() {
        final Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File imageFile = null;
            try {
                imageFile = ImageHelper.createImageFile(this);
                cameraImagePath = ImageHelper.getImagePath(imageFile);
            } catch (IOException e) {
                // Error occurred while creating the File
                e.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (imageFile != null) {
                // intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                final Uri uri = Uri.fromFile(imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, IMG_CAMERA_REQUEST_CODE);
            }
        }
    }*/

    private void selectImageFromGallery() {
        // final Intent intent = new Intent();
        final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // intent.setType("image/*");
        // intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_GALLERY_REQUEST_CODE);
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
        intent.putExtra("violationSent", true);
        intent.putExtra("violationSuccess", success);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // skip if not ok or if no data
        if (resultCode != RESULT_OK) {
            return;
        }

        // FIXME: this
        if (false && requestCode == IMG_CAMERA_REQUEST_CODE && cameraImagePath != null) {
            /*final Bundle extras = data.getExtras();
            if (extras != null) {
                bitmap = (Bitmap) extras.get("data");
                imgContainer.setVisibility(View.VISIBLE);
                imgProof.setImageBitmap(bitmap);
            }*/
            /*final Uri path = Uri.parse(cameraImagePath);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imgContainer.setVisibility(View.VISIBLE);
                imgProof.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        } else if (requestCode == IMG_GALLERY_REQUEST_CODE && data != null) {
            final Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imgContainer.setVisibility(View.VISIBLE);
                imgProof.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
