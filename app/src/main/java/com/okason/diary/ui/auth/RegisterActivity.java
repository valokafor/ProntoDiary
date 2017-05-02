package com.okason.diary.ui.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.services.CopyLocalDataToServerIntentService;
import com.okason.diary.utils.Constants;

import butterknife.BindView;
import io.realm.ObjectServerError;
import io.realm.SyncConfiguration;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

import static android.text.TextUtils.isEmpty;
import static com.okason.diary.core.ProntoDiaryApplication.AUTH_URL;


public class RegisterActivity extends AppCompatActivity implements SyncUser.Callback {

    private AutoCompleteTextView usernameView;
    private EditText passwordView;
    private EditText passwordConfirmationView;
    private View progressView;
    private View registerFormView;
    private FacebookAuth facebookAuth;
    private GoogleAuth googleAuth;
    private Activity mActivity;

    @BindView(R.id.root)
    View mRootView;

    private FirebaseAnalytics mFirebaseAnalytics;
    private String signMethod = Constants.AUTH_METHOD_EMAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mActivity = this;

        usernameView = (AutoCompleteTextView) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);
        passwordConfirmationView = (EditText) findViewById(R.id.password_confirmation);
        passwordConfirmationView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });


        final Button mailRegisterButton = (Button) findViewById(R.id.email_register_button);
        mailRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        registerFormView = findViewById(R.id.register_form);
        progressView = findViewById(R.id.register_progress);

        // Setup Facebook Authentication
        facebookAuth = new FacebookAuth((LoginButton) findViewById(R.id.login_button)) {
            @Override
            public void onRegistrationComplete(final LoginResult loginResult) {
                UserManager.setAuthMode(UserManager.AUTH_MODE.FACEBOOK);
                signMethod = Constants.AUTH_METHOD_FACEBOOK;
                SyncCredentials credentials = SyncCredentials.facebook(loginResult.getAccessToken().getToken());
                SyncUser.loginAsync(credentials, AUTH_URL, RegisterActivity.this);
            }
        };

        // Setup Google Authentication
        googleAuth = new GoogleAuth((SignInButton) findViewById(R.id.sign_in_button), this) {
            @Override
            public void onRegistrationComplete(GoogleSignInResult result) {
                UserManager.setAuthMode(UserManager.AUTH_MODE.GOOGLE);
                signMethod = Constants.AUTH_METHOD_GOOGLE;
                GoogleSignInAccount acct = result.getSignInAccount();
                SyncCredentials credentials = SyncCredentials.google(acct.getIdToken());
                SyncUser.loginAsync(credentials, AUTH_URL, RegisterActivity.this);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        googleAuth.onActivityResult(requestCode, resultCode, data);
        facebookAuth.onActivityResult(requestCode, resultCode, data);
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

        if (!Constants.isValidEmail(username)){
            usernameView.setError(getString(R.string.error_invalid_email));
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
            showProgress(true);

            SyncUser.loginAsync(SyncCredentials.usernamePassword(username, password, true), AUTH_URL, new SyncUser.Callback() {
                @Override
                public void onSuccess(SyncUser user) {
                    registrationComplete(user);
                }

                @Override
                public void onError(ObjectServerError error) {
                    showProgress(false);
                    String errorMsg;
                    switch (error.getErrorCode()) {
                        case EXISTING_ACCOUNT: errorMsg = getString(R.string.error_account_already_exist);
                    //        makeToast(errorMsg);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(mActivity, SignInActivity.class));
                                }
                            }, 100);
                        break;
                        default:
                            errorMsg = error.toString();
                        //    makeToast(errorMsg);
                            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                            final Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(mActivity, SignInActivity.class));
                                }
                            }, 100);
                            break;
                    }

                }
            });
        }
    }

    private void registrationComplete(SyncUser user) {
        //Set the newly registered user as Active thereby creating new Synchronised Realm
        UserManager.setActiveUser(user);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean unregisteredUser = preferences.getBoolean(Constants.UNREGISTERED_USER, true);
        if (unregisteredUser){
            preferences.edit().putBoolean(Constants.UNREGISTERED_USER, false).commit();
            startService(new Intent(this, CopyLocalDataToServerIntentService.class));
        }

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, signMethod);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);



        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showProgress(final boolean show) {
        final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        registerFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    @Override
    public void onSuccess(SyncUser user) {
        registrationComplete(user);
    }

    @Override
    public void onError(ObjectServerError error) {
        String errorMsg;
        switch (error.getErrorCode()) {
            case EXISTING_ACCOUNT:
                errorMsg = getString(R.string.error_account_already_exist);
                makeToast(errorMsg);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(mActivity, SignInActivity.class));
                    }
                }, 500);

                break;
            default:
                errorMsg = error.toString();
        }
        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
    }

    private void makeToast(String message){
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.primary));
        TextView tv = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void syncExistingDataIfNeeded(){
        SyncConfiguration configuration = new SyncConfiguration.Builder(SyncUser.currentUser(), ProntoDiaryApplication.AUTH_URL)
               .build();

    }
}
