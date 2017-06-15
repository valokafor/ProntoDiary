package com.okason.diary.core;

import android.app.Application;
import android.content.Context;

import com.okason.diary.BuildConfig;
import com.squareup.leakcanary.LeakCanary;

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
