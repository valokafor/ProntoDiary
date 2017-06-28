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
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.models.ProntoDiaryUser;
import com.okason.diary.utils.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.SyncUser;

import static com.firebase.ui.auth.ui.ExtraConstants.EXTRA_IDP_RESPONSE;

public class AuthUiActivity extends AppCompatActivity {

    private static final String GOOGLE_TOS_URL = "https://www.google.com/policies/terms/";
    private static final int RC_SIGN_IN = 100;
    private Activity mActivity;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference mDatabase;
    private DatabaseReference mProntoDiaryUserRef;

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

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProntoDiaryUserRef = mDatabase.child(Constants.PRONTO_DIARY_USER_CLOUD_REFERENCE);


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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
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
        if (resultCode == RESULT_OK) {
            //Get Firebase User
            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();

            //Get Pronto Diary User
            if (mFirebaseUser != null){
                mProntoDiaryUserRef.orderByChild("firebaseUid").equalTo(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                        ProntoDiaryUser user = snapshot.getValue(ProntoDiaryUser.class);
                        if (user == null){
                            //If user does not exist, create one
                            user = new ProntoDiaryUser();
                            user.setEmailAddress(mFirebaseUser.getEmail());
                            user.setFirebaseUid(mFirebaseUser.getUid());
                            user.setId(mProntoDiaryUserRef.push().getKey());
                            mProntoDiaryUserRef.child(user.getId()).setValue(user);
                        }
                        if (TextUtils.isEmpty(user.getRealmJson())){
                            //If Realm account has not been created for this user
                            //Go to Realm Register Activity
                            startActivity(new Intent(AuthUiActivity.this, RegisterActivity.class));
                        } else {
                            //Get the Sync User
                            SyncUser syncUser = SyncUser.fromJson(user.getRealmJson());
                            if (syncUser == null){
                                //If Realm user cannot be retrieved, try logging in
                                Intent loginIntent = new Intent(AuthUiActivity.this, SignInActivity.class);
                                loginIntent.putExtra(Constants.EMAIL_ADDRESSS, user.getEmailAddress());
                                loginIntent.putExtra(Constants.PASSWORD, user.getRealmPassword());
                                startActivity(loginIntent);
                            }else {
                                //We have a valid Realm User, go to app
                                Intent in = new Intent(AuthUiActivity.this, NoteListActivity.class);
                                in.putExtra(EXTRA_IDP_RESPONSE, IdpResponse.fromResultIntent(data));
                                startActivity(in);
                                finish();
                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        } else {
            ProntoDiaryApplication.setCloudSyncEnabled(false);
            finish();
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


}
