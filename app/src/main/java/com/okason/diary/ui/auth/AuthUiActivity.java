package com.okason.diary.ui.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.android.gms.common.Scopes;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.services.CreateRealmDatabaseAccountService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthUiActivity extends AppCompatActivity {

    private static final String GOOGLE_TOS_URL = "https://www.google.com/policies/terms/";
    private static final int RC_SIGN_IN = 100;
    private Activity mActivity;


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




        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.okason.prontodiary",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        showSignInScreen();

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
                        .setTheme(R.style.GreenTheme)
                        .setLogo(R.mipmap.ic_launcher)
                        .setProviders(getSelectedProviders())
                        .setTosUrl(GOOGLE_TOS_URL)
                        .setIsSmartLockEnabled(true)
                        .setTosUrl(GOOGLE_TOS_URL)
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
                        .setPermissions(getFacebookPermissions())
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
        result.add("user_friends");
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
            ProntoDiaryApplication.setCloudSyncEnabled(true);
            startService(new Intent(mActivity, CreateRealmDatabaseAccountService.class));
            //Restart
            Intent restartIntent = new Intent(mActivity, NoteListActivity.class);
            restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(restartIntent);
            return;
        } else {
            if (response == null){
                ProntoDiaryApplication.setCloudSyncEnabled(false);
                // User pressed back button
                showSnackbar(R.string.sign_in_cancelled);
                finish();
                return;
            }

        }

        if (resultCode == RESULT_CANCELED) {
            makeToast(getString(R.string.sign_in_cancelled));
            ProntoDiaryApplication.setCloudSyncEnabled(false);
            return;
        }

        if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
            makeToast(getString(R.string.no_internet_connection));
            ProntoDiaryApplication.setCloudSyncEnabled(false);
            return;
        }

        makeToast(getString(R.string.unknown_sign_in_response));
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
