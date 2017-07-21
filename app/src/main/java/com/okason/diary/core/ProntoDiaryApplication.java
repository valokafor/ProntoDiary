package com.okason.diary.core;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.okason.diary.BuildConfig;
import com.okason.diary.core.services.AddSampleDataIntentService;
import com.squareup.leakcanary.LeakCanary;

import io.realm.Realm;

/**
 * Created by Valentine on 4/20/2017.
 */

public class ProntoDiaryApplication extends Application {
    public static final String AUTH_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/auth";
    public static final String REALM_URL = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/diary";

    private static Context mContext;

    private static boolean cloudSyncEnabled;



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

    }

    public static Context getAppContext() {
        return ProntoDiaryApplication.mContext;
    }

    public static boolean isCloudSyncEnabled() {
        return cloudSyncEnabled;
    }

    public static void setCloudSyncEnabled(boolean cloudSyncEnabled) {
        ProntoDiaryApplication.cloudSyncEnabled = cloudSyncEnabled;
    }


}
