package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.okason.diary.models.Note;
import com.okason.diary.utils.Constants;

import java.util.ArrayList;
import java.util.List;


public class MergeAnonymousDataIntentService extends IntentService {

    public MergeAnonymousDataIntentService() {
        super("MergeAnonymousDataIntentService");
    }
    private DatabaseReference mDatabase;
    private DatabaseReference annonymousCloudReference;
    private DatabaseReference registeredCloudReference;


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.hasExtra(Constants.ANONYMOUS_ACCOUNT_USER_ID)) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String annonymousUserId = intent.getStringExtra(Constants.ANONYMOUS_ACCOUNT_USER_ID);


            final List<Note> notesToBeMigrated = new ArrayList<>();

            if (!TextUtils.isEmpty(annonymousUserId)) {


                if (!TextUtils.isEmpty(annonymousUserId)) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        final String currentUserId = user.getUid();

                        //Now move Temp User Notes to Current User
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        annonymousCloudReference = mDatabase.child(Constants.USERS_CLOUD_END_POINT + annonymousUserId
                                + Constants.NOTE_CLOUD_END_POINT);
                        registeredCloudReference = mDatabase.child(Constants.USERS_CLOUD_END_POINT + currentUserId
                                + Constants.NOTE_CLOUD_END_POINT);

                        annonymousCloudReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                                    Note note = noteSnapshot.getValue(Note.class);
                                    notesToBeMigrated.add(note);

                                }
                                migrateNote(notesToBeMigrated, registeredCloudReference, annonymousCloudReference);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }

            }
        }
    }

    private void migrateNote(List<Note> notesToBeMigrated, DatabaseReference CloudReference, DatabaseReference tempReference) {

        for (Note note: notesToBeMigrated){
            String tempId = note.getId();

            String newKey = CloudReference.push().getKey();
            note.setId(newKey);
            CloudReference.child(newKey).setValue(note);
            tempReference.child(tempId).removeValue();
        }

    }

    }