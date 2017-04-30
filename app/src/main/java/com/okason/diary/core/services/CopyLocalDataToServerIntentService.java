package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;

import com.okason.diary.models.Note;
import com.okason.diary.ui.auth.UserManager;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.SyncUser;


public class CopyLocalDataToServerIntentService extends IntentService {

    public CopyLocalDataToServerIntentService() {
        super("CopyLocalDataToServerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Get current user
        SyncUser user = SyncUser.currentUser();
        if (user != null) {
            //Get the local and cloud synched Realms
            Realm localRealm = Realm.getInstance(UserManager.getLocalConfig());
            Realm syncRealm = Realm.getDefaultInstance();

            //Copy Local Notes to Server
            final RealmResults<Note> localNotes = localRealm.where(Note.class).findAll();
            if (localNotes != null && localNotes.size() > 0) {
                for (Note localNote : localNotes) {
                    //Detach Note from the Local Realm because a managed object can
                    //Only be accessed from the same Realm that created it
                    final Note unManagedNote = localRealm.copyFromRealm(localNote);
                    syncRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Note serverNote = realm.createObject(Note.class, unManagedNote.getId());
                            serverNote.update(unManagedNote);
                        }
                    });
                }

                localRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        for (Note synchedNote: localNotes){
                            synchedNote.deleteFromRealm();
                        }
                    }
                });


            }

            //Copy Next Object



        }

    }


}
