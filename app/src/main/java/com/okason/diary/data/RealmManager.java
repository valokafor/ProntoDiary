package com.okason.diary.data;

import com.okason.diary.utils.Constants;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

public class RealmManager {

    public static SyncConfiguration getSyncConfig() {
        SyncConfiguration configuration = SyncUser.current()
                .getDefaultConfiguration();
        return configuration;
    }

    public static RealmConfiguration getLocalConfig() {
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name(Constants.REALM_DATABASE)
                .schemaVersion(5)
                .deleteRealmIfMigrationNeeded()
                .build();
        return configuration;
    }

    public static Realm setUpRealm() {
        if (SyncUser.current() == null) {
            Realm realm = Realm.getInstance(getLocalConfig());
            return realm;
        } else {
            String url = "realms://pronto-diary.us1.cloud.realm.io/~/journal";
            SyncConfiguration configuration = SyncUser.current()
                    .createConfiguration(url)
                    .fullSynchronization()
                    .build();
            Realm realm = Realm.getInstance(configuration);
            return realm;
        }


    }


}
