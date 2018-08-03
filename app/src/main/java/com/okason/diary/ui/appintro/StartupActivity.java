package com.okason.diary.ui.appintro;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.ui.auth.PremiumUpsellActivity;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.PermissionHelper;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

public class StartupActivity extends AppCompatActivity {
    public static final String TAG = "SplashScreenActivity";
    private ConnectivityManager connectivityManager;
    private SharedPreferences sharedPreferences;
    private CountDownTimer fcmTokenTimer;
    private long TOKEN_INTERVAL = 1000 * 6 ;;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        ButterKnife.bind(this);
        activity = this;

        if (SyncUser.current() != null){
            navigateToListOfJournals();
        }
    }



    @OnClick(R.id.btn_login)
    public void onClickLoginButton(View view){
//        boolean registeredUser = SettingsHelper.getHelper(activity).isRegisteredUser();
//        if (registeredUser){
//            startActivity(new Intent(activity, LoginActivity.class));
//        } else {
//            startActivity(new Intent(activity, SignupActivity.class));
//        }
        startActivity(new Intent(activity, PremiumUpsellActivity.class));
    }

    @OnClick(R.id.btn_get_started)
    public void onGetStartedButtonClicked(View view){
        navigateToListOfJournals();
    }




    private void loginProntoUser(String token) {
        SyncCredentials credentials = SyncCredentials.usernamePassword("val@okason.com", "abcd1234");
        SyncUser.logInAsync(credentials, Constants.REALM_AUTH_URL, new SyncUser.Callback<SyncUser>() {
            @Override
            public void onSuccess(SyncUser user) {
                setUpDefaultRealm();
                PermissionHelper.initializePermissions(() -> navigateToListOfJournals());

            }

            @Override
            public void onError(ObjectServerError error) {
                Log.d(TAG, "Sync Anonymous user failed " + error.getErrorMessage());
            }
        });
    }



    private void navigateToListOfJournals() {
        Intent intent = new Intent(StartupActivity.this, NoteListActivity.class);
        startActivity(intent);
    }

    private void setUpDefaultRealm() {
        Realm.setDefaultConfiguration(SyncUser.current().getDefaultConfiguration());
    }

}
