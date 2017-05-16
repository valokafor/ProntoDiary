package com.okason.diary.core;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.okason.diary.BuildConfig;
import com.squareup.leakcanary.LeakCanary;

import io.realm.Realm;
import io.realm.log.LogLevel;
import io.realm.log.RealmLog;

/**
 * Created by Valentine on 4/20/2017.
 */

public class ProntoDiaryApplication extends Application {
    public static final String AUTH_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/auth";
    public static final String REALM_URL = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/prontodiary";

    private static Context mContext;



    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        mContext = getApplicationContext();
        LeakCanary.install(this);
        Realm.init(this);
        FacebookSdk.sdkInitialize(this);
        RealmLog.setLevel(LogLevel.TRACE);

    }

    public static Context getAppContext() {
        return ProntoDiaryApplication.mContext;
    }


}
