package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.okason.diary.NoteListActivity;
import com.okason.diary.core.events.RealmDatabaseRegistrationCompletedEvent;
import com.okason.diary.data.NoteRealmRepository;
import com.okason.diary.models.Note;
import com.okason.diary.models.ProntoDiaryUser;
import com.okason.diary.ui.addnote.AddNoteContract;
import com.okason.diary.ui.auth.UserManager;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

import static com.okason.diary.core.ProntoDiaryApplication.AUTH_URL;


public class CreateRealmDatabaseAccountService extends IntentService {

    public CreateRealmDatabaseAccountService() {
        super("CreateRealmDatabaseAccountService");
    }

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference mDatabase;
    private DatabaseReference mProntoDiaryUserRef;

    private String emailAddress;
    private String generatedPassword;
    ProntoDiaryUser user;


    @Override
    protected void onHandleIntent(Intent intent) {
        //Get Firebase User
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProntoDiaryUserRef = mDatabase.child(Constants.PRONTO_DIARY_USER_CLOUD_REFERENCE);

        //Get Pronto Diary User
        if (mFirebaseUser != null){


        }else {
            //We have a Firebase user that has a null Id
        }

    }


    private void attemptLogin() {

    }

    private void attemptRegister() {
        EventBus.getDefault().post(new RealmDatabaseRegistrationCompletedEvent(null, true));
        SyncUser.loginAsync(SyncCredentials.usernamePassword(emailAddress, generatedPassword, true), AUTH_URL, new SyncUser.Callback() {
            @Override
            public void onSuccess(SyncUser user) {
                registrationComplete(user);
            }

            @Override
            public void onError(ObjectServerError error) {
                FirebaseCrash.log(emailAddress + ": Realm register failed");
                FirebaseCrash.log(emailAddress + ": generated password: " + generatedPassword);
            }
        });
    }

    private void registrationComplete(final SyncUser user) {
        //Set the newly registered user as Active thereby creating new Synchronised Realm
        UserManager.setActiveUser(user);
        updateProntoUserRecord(user);
        migrateDataFromLocalRealmToSyncRealm(user);
        EventBus.getDefault().post(new RealmDatabaseRegistrationCompletedEvent(user, false));
        Intent restartIntent = new Intent(getApplicationContext(), NoteListActivity.class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(restartIntent);
    }

    //Copies data from Local Realm to Sync Real
    private void migrateDataFromLocalRealmToSyncRealm(SyncUser user) {
        Realm localRealm = Realm.getInstance(UserManager.getLocalConfig());
        Realm syncRealm = Realm.getInstance(UserManager.getSyncConfig(user));
        AddNoteContract.Repository noteRepository = new NoteRealmRepository();

        RealmResults<Note> localNotes = localRealm.where(Note.class).findAll();
        if (localNotes != null && localNotes.size() >0 ){
            //There are local Notes that need to be copied over to Synced Realm

            for (Note note: localNotes){
                //Check to see if this Note has already been migrated to ROS
                if (!noteRepository.noteExists(syncRealm, note.getId())){
                    //If Not, begin Transaction
                    syncRealm.beginTransaction();
                    Note syncNote = syncRealm.createObject(Note.class, note.getId());
                    syncNote.update(note);
                    syncRealm.commitTransaction();

                    //Begin another transaction to delete this Note from local Realm
                    localRealm.beginTransaction();
                    note.deleteFromRealm();
                    localRealm.commitTransaction();
                }

            }


        }
    }

    private void updateProntoUserRecord(final SyncUser user) {
        mProntoDiaryUserRef.orderByChild("firebaseUid").equalTo(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get Pronto User Id from Firebase
                if (dataSnapshot.exists()){
                    DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                    ProntoDiaryUser prontoDiaryUser = snapshot.getValue(ProntoDiaryUser.class);
                    if (prontoDiaryUser != null){
                        //Update
                        prontoDiaryUser.setRealmPassword(generatedPassword);
                        mProntoDiaryUserRef.child(prontoDiaryUser.getId()).setValue(prontoDiaryUser);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getRandomPassword() {
        return UUID.randomUUID().toString();
    }


}
