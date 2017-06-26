package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.okason.diary.models.ProntoDiaryUser;
import com.okason.diary.utils.Constants;

import io.realm.SyncUser;


public class HandleRealmLoginService extends IntentService {

    private DatabaseReference mDatabase;
    private DatabaseReference mProntoDiaryUserRef;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private SyncUser mSyncUser;





    public HandleRealmLoginService() {
        super("HandleRealmLoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProntoDiaryUserRef = mDatabase.child(Constants.PRONTO_DIARY_USER_CLOUD_REFERENCE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.edit().putBoolean(Constants.UNREGISTERED_USER, false).commit();

        final String realmJson = intent.getStringExtra(Constants.REALM_USER_JSON);
        final String generatedPassword = intent.getStringExtra(Constants.PASSWORD);
        mSyncUser = SyncUser.fromJson(realmJson);

        mProntoDiaryUserRef.orderByChild("firebaseUid").equalTo(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get Pronto User Id from Firebase
                DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                ProntoDiaryUser prontoDiaryUser = snapshot.getValue(ProntoDiaryUser.class);
                if (prontoDiaryUser != null){
                    //Update
                    prontoDiaryUser.setRealmJson(realmJson);
                    prontoDiaryUser.setRealmPassword(generatedPassword);
                    mProntoDiaryUserRef.child(prontoDiaryUser.getId()).setValue(prontoDiaryUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        Realm localRealm = Realm.getInstance(UserManager.getLocalConfig());
//        Realm syncRealm = Realm.getInstance(UserManager.getSyncConfig(mSyncUser));
//        AddNoteContract.Repository noteRepository = new NoteRealmRepository();
//
//        RealmResults<Note> localNotes = localRealm.where(Note.class).findAll();
//        if (localNotes != null && localNotes.size() >0 ){
//            //There are local Notes that need to be copied over to Synced Realm
//            syncRealm.beginTransaction();
//            for (Note note: localNotes){
//                Note syncNote = noteRepository.createNewNote();
//                syncNote.update(note);
//            }
//            syncRealm.commitTransaction();
//
//        }




    }


}
