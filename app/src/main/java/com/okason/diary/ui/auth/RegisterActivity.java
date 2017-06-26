package com.okason.diary.ui.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.core.services.HandleRealmLoginService;
import com.okason.diary.utils.Constants;

import java.util.UUID;

import butterknife.BindView;
import io.realm.ObjectServerError;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

import static com.okason.diary.core.ProntoDiaryApplication.AUTH_URL;


public class RegisterActivity extends AppCompatActivity implements SyncUser.Callback {


    private AutoCompleteTextView usernameView;
    private EditText passwordView;
    private EditText passwordConfirmationView;
    private View progressView;
    private View registerFormView;
    private Activity mActivity;
    private String emailAddress;
    private String generatedPassword;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;




    @BindView(R.id.root)
    View mRootView;

    private FirebaseAnalytics mFirebaseAnalytics;
    private String signMethod = Constants.AUTH_METHOD_EMAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();


        mActivity = this;

        if (mFirebaseUser == null){
            finish();
        }

        try {
            emailAddress = mFirebaseUser.getEmail();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        if (TextUtils.isEmpty(emailAddress)){
            finish();
        }

        generatedPassword = getRandomPassword();
        progressView = findViewById(R.id.register_progress);





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
        attemptRegister();



    }

    private String getRandomPassword() {
        return UUID.randomUUID().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle("Enable Private Cloud Sync");
    }

    private void attemptRegister() {

        showProgress(true);

        SyncUser.loginAsync(SyncCredentials.usernamePassword(emailAddress, generatedPassword, true), AUTH_URL, new SyncUser.Callback() {
            @Override
            public void onSuccess(SyncUser user) {
                registrationComplete(user);
            }

            @Override
            public void onError(ObjectServerError error) {
                showProgress(false);
                Intent intent = new Intent(mActivity, SignInActivity.class);
                intent.putExtra(Constants.EMAIL_ADDRESSS, emailAddress);
                intent.putExtra(Constants.PASSWORD, generatedPassword);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


            }
        });
    }

    private void registrationComplete(final SyncUser user) {
        //Set the newly registered user as Active thereby creating new Synchronised Realm
        UserManager.setActiveUser(user);

        Intent completeLoginService = new Intent(mActivity, HandleRealmLoginService.class);
        completeLoginService.putExtra(Constants.PASSWORD, generatedPassword);
        completeLoginService.putExtra(Constants.REALM_USER_JSON, user.toJson());
        startService(completeLoginService);


        showProgress(false);
        Intent intent = new Intent(this, NoteListActivity.class);
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
        Intent intent = new Intent(this, SignInActivity.class);
        intent.putExtra(Constants.EMAIL_ADDRESSS, emailAddress);
        intent.putExtra(Constants.PASSWORD, generatedPassword);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void makeToast(String message){
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.primary));
        TextView tv = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }


}
