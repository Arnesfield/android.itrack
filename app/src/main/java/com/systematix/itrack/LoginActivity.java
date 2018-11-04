package com.systematix.itrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.systematix.itrack.config.UrlsList;
import com.systematix.itrack.items.Auth;
import com.systematix.itrack.items.User;
import com.systematix.itrack.models.ButtonStateModel;
import com.systematix.itrack.utils.Api;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements Api.OnApiRespondListener {

    @BindView(R.id.login_btn_login) Button btnLogin;
    @BindView(R.id.login_txt_username) TextInputEditText txtUsername;
    @BindView(R.id.login_txt_password) TextInputEditText txtPassword;
    @BindView(R.id.login_txt_username_container) TextInputLayout txtUsernameContainer;
    @BindView(R.id.login_txt_password_container) TextInputLayout txtPasswordContainer;

    private ButtonStateModel buttonStateModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.checkForUser()) {
            // do not continue below
            return;
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLoading(true);

                final String username = txtUsername.getText().toString();
                final String password = txtPassword.getText().toString();
                final JSONObject params = new JSONObject();

                try {
                    params.put("username", username);
                    params.put("password", password);

                    Api.post(view.getContext())
                        .setTag("login")
                        .setUrl(UrlsList.LOGIN_URL)
                        .setApiListener(LoginActivity.this)
                        .request(params);
                } catch (JSONException e) {
                    Snackbar.make(view, R.string.error, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        // set button state
        final ProgressBar progressBar = findViewById(R.id.login_progress_bar);
        buttonStateModel = new ButtonStateModel(btnLogin, progressBar);

        checkForLogOutMsg();
    }

    private boolean checkForUser() {
        final int uid = Auth.getSavedUserId(this);

        // if no id set, do nothing
        if (uid == -1) {
            return false;
        }

        // go to next activity here
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        return true;
    }

    private void checkForLogOutMsg() {
        if (Auth.didLogout(this)) {
            Snackbar.make(btnLogin, R.string.msg_log_out, Snackbar.LENGTH_LONG).show();
        }
    }

    private void doLoading(boolean loading) {
        // disable input fields and button
        final boolean enable = !loading;
        txtUsernameContainer.setEnabled(enable);
        txtPasswordContainer.setEnabled(enable);
        buttonStateModel.setLoading(loading, R.string.login_btn_login);

        // if loading, remove errors
        if (loading) {
            txtUsernameContainer.setError(null);
            txtPasswordContainer.setError(null);
        }
    }


    // OnApiSuccessListener
    @Override
    public void onApiSuccess(String tag, JSONObject response, boolean success, String msg) throws JSONException {
        // do not unload hehe

        // check if successful
        if (!success) {
            if (msg != null && msg.length() > 0) {
                txtUsernameContainer.setError("");
                txtPasswordContainer.setError(msg);
                Snackbar.make(btnLogin, msg, Snackbar.LENGTH_LONG).show();
            }
            // but unload here!
            doLoading(false);
            return;
        }

        // get user
        final JSONObject jsonUser = response.getJSONObject("user");
        final User user = new User(jsonUser);

        // save user to db
        Auth.saveUser(this, user);
        this.checkForUser();
    }

    // OnApiErrorListener
    @Override
    public void onApiError(String tag, VolleyError error) {
        doLoading(false);
        Snackbar.make(btnLogin, R.string.error_service_unavailable, Snackbar.LENGTH_LONG).show();
    }

    // OnApiExceptionListener
    @Override
    public void onApiException(String tag, JSONException e) {
        doLoading(false);
        Snackbar.make(btnLogin, R.string.error, Snackbar.LENGTH_LONG).show();
    }
}
