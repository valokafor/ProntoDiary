package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.okason.diary.models.dto.DataAccessManager;


public class FirebaseToRealmIntentService extends IntentService {


    public FirebaseToRealmIntentService() {
        super("FirebaseToRealmIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            DataAccessManager dataAccessManager = new DataAccessManager(user.getUid());
            Log.d("FirebaseToRealm", "Starting firebase data download");
            dataAccessManager.getAllJournal(getApplicationContext());
            dataAccessManager.getAllTasks(getApplicationContext());
        }

    }


}
