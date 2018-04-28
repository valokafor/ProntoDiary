package com.okason.diary.core;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.SettingsHelper;

public class SplashScreenActivity extends AppCompatActivity {

    public static final String TAG = "SplashScreenActivity";
    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        checkNetworkConnected();

    }

    /**
     * r
     * Check network conenctivity and direct user to settings if no network.
     */
    private void checkNetworkConnected() {
        if (!isActiveNetworkConnected()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.noNetwork)
                    .setPositiveButton(R.string.fixNetworking, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setNeutralButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            // next check google play
            checkPlayServices();
        }
    }


    private boolean isActiveNetworkConnected() {
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    /**
     * Check Google Play services and provide a fix-it dialog if not up to date.
     */
    private void checkPlayServices() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int result = api.isGooglePlayServicesAvailable(this);
        if (result == 0) {
            // next step start service
            setupMessagingService();
            return;
        }

        if (api.isUserResolvableError(result)) {
            // show the fix-it dialog
            Dialog dialog = api.getErrorDialog(this, result, 0);
            dialog.show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.playServicesError)
                    .setNeutralButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }
    }


    private void setupMessagingService() {
        String token = SettingsHelper.getHelper(this).getMessagingToken();
        if (TextUtils.isEmpty(token)) {
            // need to retrieve a messaging token
            Log.d(TAG, "No FCM token defined. Requesting new token.");
            token = FirebaseInstanceId.getInstance().getToken();
            SettingsHelper.getHelper(getApplicationContext()).setMessagingToken(token);
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(this, NoteListActivity.class);
            startActivity(intent);
            finish();
        }else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(Constants.EMAIL_LOGIN, Constants.EMAIL_PASSWORD)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null){
                                    SettingsHelper.getHelper(SplashScreenActivity.this).setRegisteredUser(false);
                                    SettingsHelper.getHelper(SplashScreenActivity.this).saveAnonymousUserId(user.getUid());
                                }

                                Intent intent = new Intent(SplashScreenActivity.this, NoteListActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());
                            }

                        }
                    });
        }





    }

}
