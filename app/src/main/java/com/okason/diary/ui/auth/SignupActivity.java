package com.okason.diary.ui.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.SettingsHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.ObjectServerError;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

import static android.text.TextUtils.isEmpty;

public class SignupActivity extends AppCompatActivity {
    private Activity activity;
    @BindView(R.id.register_form) View registerForm;
    @BindView(R.id.register_progress) View progressView;
    @BindView(R.id.username) EditText usernameView;
    @BindView(R.id.password) EditText passwordView;
    @BindView(R.id.password_confirmation) EditText passwordConfirmationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        activity = this;
    }

    @OnClick(R.id.login_textview)
    public void onClickLogingTextView(View view){
        startActivity(new Intent(activity, LoginActivity.class));
    }

    @OnClick(R.id.email_register_button)
    public void onClickRegisterButton(View view){
        attemptRegister();
    }


    private void attemptRegister() {
        usernameView.setError(null);
        passwordView.setError(null);
        passwordConfirmationView.setError(null);

        final String username = usernameView.getText().toString();
        final String password = passwordView.getText().toString();
        final String passwordConfirmation = passwordConfirmationView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }

        if (isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        }

        if (isEmpty(passwordConfirmation)) {
            passwordConfirmationView.setError(getString(R.string.error_field_required));
            focusView = passwordConfirmationView;
            cancel = true;
        }

        if (!password.equals(passwordConfirmation)) {
            passwordConfirmationView.setError(getString(R.string.error_incorrect_password));
            focusView = passwordConfirmationView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            if (isValidEmail(username)) {
                showProgress(true);
                SyncCredentials credentials = SyncCredentials.usernamePassword(username, password, true);
                SyncUser.logInAsync(credentials, Constants.REALM_AUTH_URL, new SyncUser.Callback<SyncUser>() {
                    @Override
                    public void onSuccess(SyncUser user) {
                        showProgress(false);
                        SettingsHelper.getHelper(activity).setRegisteredUser(true);
                        navigateToListOfJournals();
                    }

                    @Override
                    public void onError(ObjectServerError error) {
                        String errorMsg;
                        switch (error.getErrorCode()) {
                            case EXISTING_ACCOUNT:
                                errorMsg = "Account already exists";
                                break;
                            default:
                                errorMsg = error.toString();
                        }
                        Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                Toast.makeText(activity, "Enter valid email address", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        registerForm.setVisibility(show ? View.GONE : View.VISIBLE);
        registerForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                registerForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void navigateToListOfJournals() {
        Intent intent = new Intent(SignupActivity.this, NoteListActivity.class);
        startActivity(intent);
    }
}
