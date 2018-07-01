package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;

import com.okason.diary.data.Migration;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class LocalToSyncIntentService extends IntentService {


    public LocalToSyncIntentService() {
        super("LocalToSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .schemaVersion(3)
                .name("Pronto_Journal.realm")
                .migration(new Migration())
                .build();
        Realm.setDefaultConfiguration(configuration);


        Realm realm = Realm.getInstance(configuration);


    }


}
