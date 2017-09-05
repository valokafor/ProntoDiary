package com.okason.diary.ui.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.Scopes;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.core.events.RealmDatabaseRegistrationCompletedEvent;
import com.okason.diary.core.services.HandleRealmLoginService;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.SettingsHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthUiActivity extends AppCompatActivity {

    private static final String GOOGLE_TOS_URL = "https://www.google.com/policies/terms/";
    private static final int RC_SIGN_IN = 100;
    private Activity mActivity;
    private View progressView;


    @BindView(android.R.id.content)
    View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_ui);
        ButterKnife.bind(this);
        mActivity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressView = findViewById(R.id.register_progress);


        showSignInScreen();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app launcher in action bar clicked; go home
                int count = getFragmentManager().getBackStackEntryCount();
                if (count == 0) {
                    onBackPressed();
                } else {
                    getFragmentManager().popBackStack();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showSignInScreen() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setLogo(R.drawable.app_icon)
                        .setAvailableProviders(getSelectedProviders())
                        .setTosUrl(GOOGLE_TOS_URL)
                        .setIsSmartLockEnabled(true, true)
                        .setAllowNewEmailAccounts(true)
                        .build(),
                RC_SIGN_IN);
    }

    @MainThread
    private List<AuthUI.IdpConfig> getSelectedProviders() {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();

        selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());

        selectedProviders.add(
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                        .setPermissions(getGooglePermissions())
                        .build());
        selectedProviders.add(
                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER)
                        .build());


        return selectedProviders;
    }

    @MainThread
    private List<String> getGooglePermissions() {
        List<String> result = new ArrayList<>();
        result.add(Scopes.DRIVE_FILE);
        return result;
    }

    private List<String> getFacebookPermissions() {
        List<String> result = new ArrayList<>();
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }

        makeToast(getString(R.string.unknown_response));
    }

    @MainThread
    private void handleSignInResponse(int resultCode, final Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);


        if (resultCode == RESULT_OK) {

            if (progressView != null){
                showProgress(true);
            } else{
               progressView = findViewById(R.id.register_progress);
                showProgress(true);
            }



            Intent realmIntent = new Intent(mActivity, HandleRealmLoginService.class);
            if (response != null){
                realmIntent.putExtra(Constants.LOGIN_PROVIDER, response.getProviderType());
            }
            startService(realmIntent);
            SettingsHelper.getHelper(mActivity).setRegisteredUser(true);

            showSnackbar(R.string.syncing_data);
            return;
        }

        if (resultCode == RESULT_CANCELED) {
            makeToast(getString(R.string.sign_in_cancelled));
            return;
        }

        if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
            makeToast(getString(R.string.no_internet_connection));
            return;
        }

        if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
            showSnackbar(R.string.unknown_error);
            return;
        }

    }

    private void showProgress(final boolean show) {
        final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @MainThread
    private void makeToast(String message){
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
        TextView tv = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRealmDatabaseRegistrationComplete(RealmDatabaseRegistrationCompletedEvent event){
        if (event.isInProgress()){
            showProgress(true);
        }else {
            showProgress(false);
            //Restart
            Intent restartIntent = new Intent(mActivity, NoteListActivity.class);
            restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(restartIntent);
        }

    }

    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, AuthUiActivity.class);
        return in;
    }



    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }


}
