package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.okason.diary.NoteListActivity;
import com.okason.diary.core.events.RealmDatabaseRegistrationCompletedEvent;
import com.okason.diary.models.ProntoDiaryUser;
import com.okason.diary.ui.auth.UserManager;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.SettingsHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

import io.realm.ObjectServerError;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

import static com.okason.diary.core.ProntoDiaryApplication.AUTH_URL;


public class HandleRealmLoginService extends IntentService implements SyncUser.Callback {

    private DatabaseReference mDatabase;
    private DatabaseReference mProntoDiaryUserRef;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private SyncUser mSyncUser;

    private String generatedPassword;
    private String providerType = "";

    private ProntoDiaryUser prontoDiaryUser;


    public HandleRealmLoginService() {
        super("HandleRealmLoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProntoDiaryUserRef = mDatabase.child(Constants.PRONTO_DIARY_USER_CLOUD_REFERENCE);

        if (intent != null && intent.hasExtra(Constants.LOGIN_PROVIDER)){
            providerType = intent.getStringExtra(Constants.LOGIN_PROVIDER);
        }

        if (mFirebaseUser != null){
            mProntoDiaryUserRef.orderByChild("firebaseUid").equalTo(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        try {
                            DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                            prontoDiaryUser = snapshot.getValue(ProntoDiaryUser.class);
                            attemptSignIn(prontoDiaryUser.getEmailAddress(), prontoDiaryUser.getRealmPassword());
                        } catch (Exception e) {
                            //Failed to retrieve Pronto Diary User object
                            e.printStackTrace();
                            Intent restartIntent = new Intent(getApplicationContext(), NoteListActivity.class);
                            restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(restartIntent);
                            return;
                        }
                    } else {
                        generatedPassword = UUID.randomUUID().toString();
                        attemptRegister(mFirebaseUser.getEmail(), generatedPassword );
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }


    private void attemptRegister(String email, String generatedPassword) {
        SyncUser.loginAsync(SyncCredentials.usernamePassword(email, generatedPassword, true), AUTH_URL, this);
    }

    private void attemptSignIn(String email, String realmPassword) {
        SyncUser.loginAsync(SyncCredentials.usernamePassword(email, realmPassword, false), AUTH_URL, this);
    }

    private void updatedProntoDiaryUser(FirebaseUser firebaseUser) {
        if (prontoDiaryUser == null) {
            //If user does not exist, create one
            prontoDiaryUser = new ProntoDiaryUser();
            prontoDiaryUser.setEmailAddress(firebaseUser.getEmail());
            prontoDiaryUser.setDisplayName(firebaseUser.getDisplayName());
            prontoDiaryUser.setLoginProvider(providerType);
            prontoDiaryUser.setFcmToken(SettingsHelper.getHelper(getApplication()).getMessagingToken());
            prontoDiaryUser.setPhotoUrl(firebaseUser.getPhotoUrl().toString());
            prontoDiaryUser.setFirebaseUid(firebaseUser.getUid());
            prontoDiaryUser.setRealmPassword(generatedPassword);
            prontoDiaryUser.setId(mProntoDiaryUserRef.push().getKey());
            mProntoDiaryUserRef.child(prontoDiaryUser.getId()).setValue(prontoDiaryUser);
        }

        EventBus.getDefault().post(new RealmDatabaseRegistrationCompletedEvent(false));


    }


    @Override
    public void onSuccess(SyncUser user) {
        SettingsHelper.getHelper(getApplicationContext()).setRegisteredUser(true);
        UserManager.setActiveUser(user);
        updatedProntoDiaryUser(mFirebaseUser);

    }

    @Override
    public void onError(ObjectServerError error) {
        //Restart
        Intent restartIntent = new Intent(getApplicationContext(), NoteListActivity.class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(restartIntent);
        return;

    }
}
