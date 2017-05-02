package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.okason.diary.utils.Constants;


public class MergeAnonymousDataIntentService extends IntentService {

    public MergeAnonymousDataIntentService() {
        super("MergeAnonymousDataIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean unregisteredUser = preferences.getBoolean(Constants.UNREGISTERED_USER, true);
            if (unregisteredUser){
                
            }


            preferences.edit().putBoolean(Constants.UNREGISTERED_USER, false).commit();


        }
    }


}
