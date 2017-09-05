package com.okason.diary.ui.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.core.services.HandleRealmLoginService;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.SettingsHelper;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.ObjectServerError;
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
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //If user is already registered, send user to Sign In Screen
        if (SettingsHelper.getHelper(this).isRegisteredUser()) {
            startActivity(new Intent(this, SignInActivity.class));
        }

        activity = this;


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
                SyncCredentials credentials = SyncCredentials.facebook(loginResult.getAccessToken().getToken());

                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.v("LoginActivity Response ", response.toString());

                                try {
                                    String name = object.getString("name");
                                    String email = object.getString("email");
                                    String  userId = object.getString("id");
                                    String photoUrl = "https://graph.facebook.com/" + userId+ "/picture?type=large";

                                    Intent completeRegisterIntent = new Intent(activity, HandleRealmLoginService.class);
                                    completeRegisterIntent.putExtra(Constants.DISPLAY_NAME, name);
                                    completeRegisterIntent.putExtra(Constants.EMAIL_ADDRESSS, email);
                                    completeRegisterIntent.putExtra(Constants.PHOTO_URL, photoUrl);
                                    completeRegisterIntent.putExtra(Constants.LOGIN_PROVIDER, "Facebook");
                                    startService(completeRegisterIntent);
                                    Log.d(NoteListActivity.TAG, "Name: " + name + ", Email: " + email + ", UserId: " + userId + ", Photo Url: " + photoUrl);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
                SyncUser.loginAsync(credentials, AUTH_URL, RegisterActivity.this);
            }
        };

        // Setup Google Authentication
        googleAuth = new GoogleAuth((SignInButton) findViewById(R.id.sign_in_button), this) {
            @Override
            public void onRegistrationComplete(GoogleSignInResult result) {
                UserManager.setAuthMode(UserManager.AUTH_MODE.GOOGLE);
                GoogleSignInAccount acct = result.getSignInAccount();
                Intent completeRegisterIntent = new Intent(activity, HandleRealmLoginService.class);

                completeRegisterIntent.putExtra(Constants.DISPLAY_NAME, acct.getDisplayName());
                completeRegisterIntent.putExtra(Constants.EMAIL_ADDRESSS, acct.getEmail());
                if (acct.getPhotoUrl() != null) {
                    completeRegisterIntent.putExtra(Constants.PHOTO_URL, acct.getPhotoUrl().toString());
                }
                completeRegisterIntent.putExtra(Constants.LOGIN_PROVIDER, "Google");
                startService(completeRegisterIntent);
                SyncCredentials credentials = SyncCredentials.google(acct.getIdToken());
                SyncUser.loginAsync(credentials, AUTH_URL, RegisterActivity.this);
            }
        };
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
                    Intent completeRegisterIntent = new Intent(activity, HandleRealmLoginService.class);
                    completeRegisterIntent.putExtra(Constants.DISPLAY_NAME, "");
                    completeRegisterIntent.putExtra(Constants.EMAIL_ADDRESSS, usernameView.getText());
                    completeRegisterIntent.putExtra(Constants.PHOTO_URL, "");
                    completeRegisterIntent.putExtra(Constants.LOGIN_PROVIDER, "Email");
                    startService(completeRegisterIntent);
                    registrationComplete(user);
                }

                @Override
                public void onError(ObjectServerError error) {
                    showProgress(false);
                    String errorMsg;
                    switch (error.getErrorCode()) {
                        case EXISTING_ACCOUNT: errorMsg = "Account already exists"; break;
                        default:
                            errorMsg = error.toString();
                    }
                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void registrationComplete(SyncUser user) {
        UserManager.setActiveUser(user);
        SettingsHelper.getHelper(RegisterActivity.this).setRegisteredUser(true);
        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
            case EXISTING_ACCOUNT: errorMsg = "Account already exists"; break;
            default:
                errorMsg = error.toString();
        }
        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
    }

}
