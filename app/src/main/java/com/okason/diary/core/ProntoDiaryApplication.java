package com.okason.diary.core;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.okason.diary.BuildConfig;
import com.okason.diary.models.Category;
import com.okason.diary.models.Note;
import com.okason.diary.models.TodoItem;
import com.okason.diary.utils.Constants;

import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.log.LogLevel;
import io.realm.log.RealmLog;

/**
 * Created by Valentine on 4/20/2017.
 */

public class ProntoDiaryApplication extends Application {
    public static final String AUTH_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/auth";
    public static final String REALM_URL = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/prontodiary";



    public static AtomicLong notePrimaryKey;
    public static AtomicLong categoryPrimaryKey;
    public static AtomicLong todoitemPrimaryKey;



    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        FacebookSdk.sdkInitialize(this);
        RealmLog.setLevel(LogLevel.TRACE);
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration configuration  = new RealmConfiguration.Builder()
                .name(Constants.REALM_DATABASE)
                .schemaVersion(5)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(configuration);

        //Get the instance of this Realm that you just instantiated
        //And use it to get the Primary Key for the NoteViewModel and Category Tables
        Realm realm = Realm.getInstance(configuration);



        try {
            //Attempt to get the last id of the last entry in the NoteViewModel class and use that as the
            //Starting point of your primary key. If your NoteViewModel table is not created yet, then this
            //attempt will fail, and then in the catch clause you want to create a table
            notePrimaryKey = new AtomicLong(realm.where(Note.class).max("id").longValue() + 1);
        } catch (Exception e) {
            //All write transaction should happen within a transaction, this code block
            //Should only be called the first time your app runs
            realm.beginTransaction();

            //Create temp NoteViewModel so as to create the table
            Note note = realm.createObject(Note.class, 0);

            //Now set the primary key again
            notePrimaryKey = new AtomicLong(realm.where(Note.class).max("id").longValue() + 1);

            //remove temp note
            RealmResults<Note> results = realm.where(Note.class).equalTo("id", 0).findAll();
            results.deleteAllFromRealm();
            realm.commitTransaction();
        }

        try {
            todoitemPrimaryKey = new AtomicLong(realm.where(TodoItem.class).max("id").longValue() + 1);
        } catch (Exception e) {
            realm.beginTransaction();
            TodoItem todoItem = realm.createObject(TodoItem.class, 0);
            todoitemPrimaryKey = new AtomicLong(realm.where(TodoItem.class).max("id").longValue() + 1);
            RealmResults<TodoItem> results = realm.where(TodoItem.class).equalTo("id", 0).findAll();
            results.deleteAllFromRealm();
            realm.commitTransaction();
        }



        try {
            //Do the same for the Category table
            categoryPrimaryKey = new AtomicLong(realm.where(Category.class).max("id").longValue() + 1);
        } catch (Exception e) {
            realm.beginTransaction();

            Category category = realm.createObject(Category.class, 0);


            //Now set the primary key again
            categoryPrimaryKey = new AtomicLong(realm.where(Category.class).max("id").longValue() + 1);

            //remove temp category
            RealmResults<Category> categoryResult = realm.where(Category.class).equalTo("id", 0).findAll();
            categoryResult.deleteAllFromRealm();
            realm.commitTransaction();

        } finally {
            //Close realm once you are done
            realm.close();
        }

    }
}
