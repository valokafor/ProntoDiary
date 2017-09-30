package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.okason.diary.NoteListActivity;
import com.okason.diary.models.Tag;
import com.okason.diary.utils.Constants;

import java.util.ArrayList;
import java.util.List;


public class MergeTagIntentService extends IntentService {

    private DatabaseReference database;
    private DatabaseReference tempTagRef;
    private DatabaseReference tagRef;
    private String annonymousUserId;


    List<Tag> tagsToBeMigrated;



    public MergeTagIntentService() {
        super("MergeTagIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d(NoteListActivity.TAG, "MergeTagIntentService called");

            annonymousUserId = intent.getStringExtra(Constants.ANONYMOUS_ACCOUNT_USER_ID);
            tagsToBeMigrated = new ArrayList<>();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null){
                final String currentUserId = user.getUid();
                database = FirebaseDatabase.getInstance().getReference();

                tempTagRef = database.child(Constants.USERS_CLOUD_END_POINT + annonymousUserId + Constants.TAG_CLOUD_END_POINT);
                tagRef = database.child(Constants.USERS_CLOUD_END_POINT + currentUserId + Constants.TAG_CLOUD_END_POINT);

                tempTagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot tagSnapshot : dataSnapshot.getChildren()) {
                            Tag tag = tagSnapshot.getValue(Tag.class);
                            tagsToBeMigrated.add(tag);

                        }
                        migrateTag();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }
    }

    private void migrateTag() {

        for (Tag tag: tagsToBeMigrated){
            String tempId = tag.getId();

            String newKey = tagRef.push().getKey();
            tag.setId(newKey);
            tagRef.child(newKey).setValue(tag);
            tempTagRef.child(tempId).removeValue();
        }

        Log.d(NoteListActivity.TAG, "Launching MergeNote Intent");
        Intent migrateIntent = new Intent(getApplicationContext(), MergeJournalIntentService.class);
        migrateIntent.putExtra(Constants.ANONYMOUS_ACCOUNT_USER_ID, annonymousUserId);
        startService(migrateIntent);

    }


}
