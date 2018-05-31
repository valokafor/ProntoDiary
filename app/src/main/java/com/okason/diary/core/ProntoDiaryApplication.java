package com.okason.diary.core;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.okason.diary.R;
import com.okason.diary.models.ProntoDiaryUser;
import com.okason.diary.reminder.Reminder;
import com.squareup.leakcanary.LeakCanary;

import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Valentine on 4/20/2017.
 */

public class ProntoDiaryApplication extends Application {


    private static Context mContext;
    private static ProntoDiaryUser prontoDiaryUser;
    public static AtomicLong reminderPrimaryKey;







    @Override
    public void onCreate() {
        super.onCreate();
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

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
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
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
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

    public static ProntoDiaryUser getProntoDiaryUser() {
        return prontoDiaryUser;
    }

    public static void setProntoDiaryUser(ProntoDiaryUser prontoDiaryUser) {
        ProntoDiaryApplication.prontoDiaryUser = prontoDiaryUser;
    }


}
