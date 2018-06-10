package com.okason.diary.core;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.multidex.MultiDexApplication;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.okason.diary.BuildConfig;
import com.okason.diary.R;
import com.okason.diary.core.services.DataDownloadIntentService;
import com.okason.diary.models.Reminder;
import com.okason.diary.models.inactive.ProntoJournalUser;
import com.squareup.leakcanary.LeakCanary;

import java.util.concurrent.atomic.AtomicLong;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Valentine on 4/20/2017.
 */

public class ProntoDiaryApplication extends MultiDexApplication {


    private static Context mContext;
    private static ProntoJournalUser prontoJournalUser;
    public static AtomicLong reminderPrimaryKey;

    @Override
    public void onCreate() {
        super.onCreate();
        configureCrashlytics();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        initDrawer();
        initRealm();
        mContext = getApplicationContext();
        LeakCanary.install(this);

    }

    private void configureCrashlytics() {

        CrashlyticsCore crashlyticsCore = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build();
        Fabric.with(this, new Crashlytics.Builder().core(crashlyticsCore).build());

    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .name("Pronto_Journal.realm")
                .build();
        Realm.setDefaultConfiguration(configuration);


        Realm realm = Realm.getInstance(configuration);

        try {
            reminderPrimaryKey = new AtomicLong(realm.where(Reminder.class).max("id").longValue() + 1);
        } catch (Exception e) {

            realm.beginTransaction();
            Reminder reminder = realm.createObject(Reminder.class, 0);
            reminderPrimaryKey = new AtomicLong(realm.where(Reminder.class).max("id").longValue() + 1);
            RealmResults<Reminder> results = realm.where(Reminder.class).equalTo("id", 0).findAll();
            results.deleteAllFromRealm();
            realm.commitTransaction();
        }

    }

    private void initDrawer() {
        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                GlideApp.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
               // GlideApp.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });
    }

    public static Context getAppContext() {
        return ProntoDiaryApplication.mContext;
    }

    public static ProntoJournalUser getProntoJournalUser() {
        return prontoJournalUser;
    }

    public static void setProntoJournalUser(ProntoJournalUser prontoJournalUser) {
        ProntoDiaryApplication.prontoJournalUser = prontoJournalUser;
    }


}
