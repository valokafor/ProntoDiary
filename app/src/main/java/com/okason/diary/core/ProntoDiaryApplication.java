package com.okason.diary.core;

import android.app.Application;

import com.okason.diary.BuildConfig;

import io.realm.Realm;

/**
 * Created by Valentine on 4/20/2017.
 */

public class ProntoDiaryApplication extends Application {
    public static final String AUTH_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/auth";
    public static final String REALM_URL = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/realmtasks";
    public static final String DEFAULT_LIST_ID = "80EB1620-165B-4600-A1B1-D97032FDD9A0";
    public static String DEFAULT_LIST_NAME = "My Tasks";


    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
//        FacebookSdk.sdkInitialize(this);
//        RealmLog.setLevel(LogLevel.TRACE);
    }
}
