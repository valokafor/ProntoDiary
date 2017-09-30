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
import com.okason.diary.models.Folder;
import com.okason.diary.utils.Constants;

import java.util.ArrayList;
import java.util.List;


public class MergeFolderIntentService extends IntentService {

    private DatabaseReference database;
    private DatabaseReference tempFolderRef;
    private DatabaseReference folderRef;
    private String annonymousUserId;


    List<Folder> foldersToBeMigrated;



    public MergeFolderIntentService() {
        super("MergeFolderIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            annonymousUserId = intent.getStringExtra(Constants.ANONYMOUS_ACCOUNT_USER_ID);
            foldersToBeMigrated = new ArrayList<>();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null){
                final String currentUserId = user.getUid();
                database = FirebaseDatabase.getInstance().getReference();

                tempFolderRef = database.child(Constants.USERS_CLOUD_END_POINT + annonymousUserId + Constants.FOLDER_CLOUD_END_POINT);
                folderRef = database.child(Constants.USERS_CLOUD_END_POINT + currentUserId + Constants.FOLDER_CLOUD_END_POINT);

                tempFolderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot tagSnapshot : dataSnapshot.getChildren()) {
                            Folder folder = tagSnapshot.getValue(Folder.class);
                            foldersToBeMigrated.add(folder);
                        }
                        migrateFolder();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }
    }

    private void migrateFolder() {

        for (Folder folder: foldersToBeMigrated){
            String tempId = folder.getId();

            String newKey = folderRef.push().getKey();
            folder.setId(newKey);
            folderRef.child(newKey).setValue(folder);
            tempFolderRef.child(tempId).removeValue();
        }

        Log.d(NoteListActivity.TAG, "Launching MergeTag Intent");
        Intent migrateIntent = new Intent(getApplicationContext(), MergeTagIntentService.class);
        migrateIntent.putExtra(Constants.ANONYMOUS_ACCOUNT_USER_ID, annonymousUserId);
        startService(migrateIntent);

    }


}
