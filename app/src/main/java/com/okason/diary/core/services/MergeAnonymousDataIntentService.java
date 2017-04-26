package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.okason.diary.utils.Constants;


public class MergeAnonymousDataIntentService extends IntentService {

    public MergeAnonymousDataIntentService() {
        super("MergeAnonymousDataIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String anonymousUserId = preferences.getString(Constants.ANONYMOUS_ACCOUNT_USER_ID, "");
            if (!TextUtils.isEmpty(anonymousUserId)){
                //Get All Data that belongs to this Id

                //Change the key of these objects to be the new signed in user Id
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    String userId = user.getUid();
                }
            }

        }
    }


}
