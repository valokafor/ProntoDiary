package com.okason.diary.core;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.okason.diary.NoteListActivity;
import com.okason.diary.core.events.OnFirebaseTokenRefreshed;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.PermissionHelper;
import com.okason.diary.utils.SettingsHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.UUID;

import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

public class SplashScreenActivity extends AppCompatActivity {

    public static final String TAG = "SplashScreenActivity";
    private ConnectivityManager connectivityManager;
    private SharedPreferences sharedPreferences;
    private CountDownTimer fcmTokenTimer;
    private long TOKEN_INTERVAL = 1000 * 6 ;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fcmTokenTimer = new CountDownTimer(TOKEN_INTERVAL, 1) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                String id = UUID.randomUUID().toString();
                id = id.replace("-", "");
                loginProntoUser(id);
            }
        };

        SyncUser user = SyncUser.current();
        if (user != null) {
            navigateToListOfJournals();
        } else {

            //Check for FCM Token
            String token = SettingsHelper.getHelper(this).getMessagingToken();
            if (token == null) {
                //If No FCM Token, request one and wait
                fcmTokenTimer.start();
                FirebaseInstanceId.getInstance().getToken();
            } else {
                //If FCM Token proceed
                loginProntoUser(token);
            }

        }






    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTokenRefreshed(OnFirebaseTokenRefreshed event){
        fcmTokenTimer.cancel();
        SettingsHelper.getHelper(getApplicationContext()).setMessagingToken(event.getToken());
        loginProntoUser(event.getToken());

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

    //Check if user data needs to be copied from local Realm to sync
    private void copyLocalRealmToSyncRealm(String token) {
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean needDataMigration = sharedPreferences.getBoolean("local_to_sync", true);
//        if (needDataMigration){
//            startService(new Intent(SplashScreenActivity.this, LocalToSyncIntentService.class));
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("local_to_sync", false).commit();
//        }
        navigateToListOfJournals( );

    }


    private void navigateToListOfJournals() {
        Intent intent = new Intent(SplashScreenActivity.this, NoteListActivity.class);
        startActivity(intent);
    }

    private void setUpDefaultRealm() {
        Realm.setDefaultConfiguration(SyncUser.current().getDefaultConfiguration());
    }







}
