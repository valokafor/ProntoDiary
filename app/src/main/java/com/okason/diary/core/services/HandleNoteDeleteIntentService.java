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
import com.okason.diary.models.Folder;
import com.okason.diary.models.Tag;
import com.okason.diary.utils.Constants;

import java.util.ArrayList;
import java.util.List;


/**
 * This Intent Service exists to serve a cascade delete for Folders and Tags that
 * Contain a deleted Journal
 *
 */
public class HandleNoteDeleteIntentService extends IntentService {

    private List<Tag> allTags;
    private List<Folder> allFolders;


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference database;
    private DatabaseReference tagCloudReference;
    private DatabaseReference folderCloudReference;


    public HandleNoteDeleteIntentService() {
        super("HandleNoteDeleteIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.hasExtra(Constants.NOTE_ID)) {

            final String deletedNoteId = intent.getStringExtra(Constants.NOTE_ID);

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            allTags = new ArrayList<>();
            allFolders = new ArrayList<>();

            if (firebaseUser != null) {
                database = FirebaseDatabase.getInstance().getReference();
                tagCloudReference = database.child(Constants.USERS_CLOUD_END_POINT + firebaseUser.getUid() + Constants.TAG_CLOUD_END_POINT);
                folderCloudReference =  database.child(Constants.USERS_CLOUD_END_POINT + firebaseUser.getUid() + Constants.FOLDER_CLOUD_END_POINT);


                tagCloudReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            allTags.clear();
                            for (DataSnapshot folderSnapshot: dataSnapshot.getChildren()){
                                Tag tag = folderSnapshot.getValue(Tag.class);
                                allTags.add(tag);
                            }
                            for (int i = 0; i < allTags.size(); i++){
                                Tag tag = allTags.get(i);
                                List<String> noteIds = tag.getNoteIds();
                                for (int j = 0; j < noteIds.size(); j++){
                                    String noteId = noteIds.get(j);
                                    if (noteId.equals(deletedNoteId)){
                                        tag.getNoteIds().remove(j);
                                        tagCloudReference.child(tag.getId()).setValue(tag);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("HandleDeleteNote", "Error fetching tags " + databaseError.getMessage());
                    }
                });

                folderCloudReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            allFolders.clear();
                            for (DataSnapshot folderSnapshot: dataSnapshot.getChildren()){
                                Folder folder = folderSnapshot.getValue(Folder.class);
                                allFolders.add(folder);
                            }
                            for (int i = 0; i < allFolders.size(); i++){
                                Folder folder = allFolders.get(i);
                                List<String> noteIds = folder.getNotesIds();
                                for (int j = 0; j < noteIds.size(); j++){
                                    String noteId = noteIds.get(j);
                                    if (noteId.equals(deletedNoteId)){
                                        folder.getNotesIds().remove(j);
                                        folderCloudReference.child(folder.getId()).setValue(folder);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("HandleDeleteNote", "Error fetching folders " + databaseError.getMessage());
                    }
                });
            }




        }
    }


}
