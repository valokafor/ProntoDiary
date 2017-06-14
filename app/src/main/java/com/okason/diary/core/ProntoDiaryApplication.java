package com.okason.diary.core;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.okason.diary.BuildConfig;
import com.okason.diary.core.services.AddSampleDataIntentService;
import com.okason.diary.utils.Constants;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Valentine on 4/20/2017.
 */

public class ProntoDiaryApplication extends Application {
    public static final String AUTH_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/auth";
    public static final String REALM_URL = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/diary";

    private static Context mContext;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
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
        addDefaultData();

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

    //Checks if this is the first time this app is running and then
    //starts an Intent Services that adds some default data
    private void addDefaultData() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean(Constants.FIRST_RUN, true)) {
               startService(new Intent(this, AddSampleDataIntentService.class));
            editor.putBoolean(Constants.FIRST_RUN, false).commit();
        }

    }
}
