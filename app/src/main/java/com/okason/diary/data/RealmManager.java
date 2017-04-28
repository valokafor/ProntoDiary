package com.okason.diary.data;

import com.okason.diary.models.Note;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Valentine on 4/27/2017.
 */

public class RealmManager {
    public static long getNextNoteId(Realm realm) {
        long id = 0;
        try {
            //Attempt to get the last id of the last entry in the Note class and use that as the
            //Starting point of your primary key. If your Note table is not created yet, then this
            //attempt will fail, and then in the catch clause you want to create a table
            id = realm.where(Note.class).max("id").longValue() + 1;
        } catch (Exception e) {



            Note note = realm.createObject(Note.class, 0);

            //Now set the primary key again
            id = realm.where(Note.class).max("id").longValue() + 1;

            //remove temp note
            RealmResults<Note> results = realm.where(Note.class).equalTo("id", 0).findAll();
            results.deleteAllFromRealm();

        }

        return id;
    }
}
