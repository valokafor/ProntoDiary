package com.okason.diary.ui.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.ObjectServerError;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

import static android.text.TextUtils.isEmpty;

public class LoginActivity extends AppCompatActivity {

    private Activity activity;
    @BindView(R.id.register_form) View registerForm;
    @BindView(R.id.register_progress) View progressView;
    @BindView(R.id.username)
    EditText usernameView;
    @BindView(R.id.password) EditText passwordView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        activity = this;
    }

    @OnClick(R.id.sign_up_textview)
    public void onClickLogingTextView(View view){
        startActivity(new Intent(activity, SignupActivity.class));
    }

    @OnClick(R.id.forgot_password)
    public void onClickForgotPasswordextView(View view){
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.forgot_password)
                .content(R.string.forgot_passord_message)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .negativeText(getString(R.string.label_cancel))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .input(R.string.hint_enter_email, 0, false, (MaterialDialog.InputCallback) (dialog1, input) -> {
                    // Do something
                    Toast.makeText(activity, "Email " + input.toString(), Toast.LENGTH_SHORT).show();
                    String emailAddress = input.toString();
                    requestPasswdReset(emailAddress);
                }).show();
    }

    private void requestPasswdReset(String emailAddress) {
        SyncUser.requestPasswordResetAsync(emailAddress, Constants.REALM_AUTH_URL, new SyncUser.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(activity, "Check your email", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(ObjectServerError error) {
                String errorMessage = error.getErrorMessage();
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.email_login_button)
    public void onClickRegisterButton(View view){
        attemptLogin();
    }


    private void attemptLogin() {
        usernameView.setError(null);
        passwordView.setError(null);


        final String username = usernameView.getText().toString();
        final String password = passwordView.getText().toString();


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
                        navigateToListOfJournals();
                    }

                    @Override
                    public void onError(ObjectServerError error) {
                        String errorMsg;
                        switch (error.getErrorCode()) {
                            case UNKNOWN_ACCOUNT:
                                errorMsg = "Account does not exists.";
                                break;
                            case INVALID_CREDENTIALS:
                                errorMsg = "The provided credentials are invalid!"; // This message covers also expired account token
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
        Intent intent = new Intent(LoginActivity.this, NoteListActivity.class);
        startActivity(intent);
    }
}
