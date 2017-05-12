package com.okason.diary.core;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.okason.diary.BuildConfig;

import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.log.LogLevel;
import io.realm.log.RealmLog;

/**
 * Created by Valentine on 4/20/2017.
 */

public class ProntoDiaryApplication extends Application {
    public static final String AUTH_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/auth";
    public static final String REALM_URL = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/prontodiary";

    public static AtomicLong notePrimaryKey;
    public static AtomicLong folderPrimaryKey;
    public static AtomicLong taskPrimaryKey;






    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        FacebookSdk.sdkInitialize(this);
        RealmLog.setLevel(LogLevel.TRACE);
    }


}
