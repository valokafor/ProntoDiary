package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

    private String displayName;
    private String emailAddress;
    private String signInMethod;

    private ProntoDiaryUser prontoDiaryUser;


    public HandleRealmLoginService() {
        super("HandleRealmLoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null && intent.hasExtra(Constants.DISPLAY_NAME) &&
                intent.hasExtra(Constants.EMAIL_ADDRESSS) &&
                intent.hasExtra(Constants.SIGN_IN_METHOD)) {


            displayName = intent.getStringExtra(Constants.DISPLAY_NAME);
            emailAddress = intent.getStringExtra(Constants.EMAIL_ADDRESSS);
            signInMethod = intent.getStringExtra(Constants.SIGN_IN_METHOD);

            //If there is no email do not proceed
            if (TextUtils.isEmpty(emailAddress)) {
                return;
            }


            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mProntoDiaryUserRef = mDatabase.child(Constants.PRONTO_DIARY_USER_CLOUD_REFERENCE);

            if (mFirebaseUser == null) {
                //If Firebase user is null, create one anonymously
                mFirebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            registerOrUpdateUserInfo();
                        }

                    }
                });
            } else {
                registerOrUpdateUserInfo();
            }
        }
    }

    private void registerOrUpdateUserInfo() {
        mProntoDiaryUserRef.orderByChild("emailAddress").equalTo(emailAddress).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                    prontoDiaryUser = snapshot.getValue(ProntoDiaryUser.class);
                }

                if (prontoDiaryUser == null) {
                    //If user does not exist, create one
                    prontoDiaryUser = new ProntoDiaryUser();
                    prontoDiaryUser.setEmailAddress(emailAddress);
                    prontoDiaryUser.setDisplayName(displayName);
                    prontoDiaryUser.setLoginProvider(signInMethod);
                    prontoDiaryUser.setId(mProntoDiaryUserRef.push().getKey());
                    mProntoDiaryUserRef.child(prontoDiaryUser.getId()).setValue(prontoDiaryUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
