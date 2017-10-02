package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
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
import com.okason.diary.models.Note;
import com.okason.diary.models.Tag;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.SettingsHelper;

import java.util.ArrayList;
import java.util.List;


public class MergeJournalIntentService extends IntentService {

    public MergeJournalIntentService() {
        super("MergeJournalIntentService");
    }
    private DatabaseReference database;
    private DatabaseReference noteRef;
    private DatabaseReference tempNoteRef;
    private DatabaseReference tagRef;
    private DatabaseReference folderRef;

    final List<Note> notesToBeMigrated = new ArrayList<>();
    final List<Tag> allTags = new ArrayList<>();
    final List<Folder> allFolders = new ArrayList<>();





    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.hasExtra(Constants.ANONYMOUS_ACCOUNT_USER_ID)) {
            Log.d(NoteListActivity.TAG, "MergeJournalIntentService called");
            String annonymousUserId = intent.getStringExtra(Constants.ANONYMOUS_ACCOUNT_USER_ID);



            if (!TextUtils.isEmpty(annonymousUserId)) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    final String currentUserId = user.getUid();

                    //Now move Temp User Notes to Current User
                    database = FirebaseDatabase.getInstance().getReference();

                    tempNoteRef = database.child(Constants.USERS_CLOUD_END_POINT + annonymousUserId
                            + Constants.NOTE_CLOUD_END_POINT);
                    noteRef = database.child(Constants.USERS_CLOUD_END_POINT + currentUserId
                            + Constants.NOTE_CLOUD_END_POINT);

                    tagRef = database.child(Constants.USERS_CLOUD_END_POINT + currentUserId + Constants.TAG_CLOUD_END_POINT);
                    folderRef =  database.child(Constants.USERS_CLOUD_END_POINT + currentUserId + Constants.FOLDER_CLOUD_END_POINT);



                    folderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot folderSnapshot : dataSnapshot.getChildren()) {
                                Folder folder = folderSnapshot.getValue(Folder.class);
                                allFolders.add(folder);

                            }
                            getAllTags();
                        }

                        private void getAllTags() {
                            tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot tagSnapshot : dataSnapshot.getChildren()) {
                                        Tag tag = tagSnapshot.getValue(Tag.class);
                                        allTags.add(tag);

                                    }
                                    getAllJournals();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                        private void getAllJournals() {
                            tempNoteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                                        Note note = noteSnapshot.getValue(Note.class);
                                        notesToBeMigrated.add(note);

                                    }
                                    migrateNote();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }

            }
        }
    }


    private void migrateNote() {

        for (Note note: notesToBeMigrated){
            String tempId = note.getId();

            String newKey = noteRef.push().getKey();
            note.setId(newKey);
            noteRef.child(newKey).setValue(note);
            tempNoteRef.child(tempId).removeValue();

            //Update Folder
            for (int i = 0; i < allFolders.size(); i++){
                Folder folder = allFolders.get(i);
                if (note.getFolderName().equals(folder.getFolderName())){
                    note.setFolderId(folder.getId());

                    for (int j = 0; j< folder.getNotesIds().size(); j++){
                        String noteId = folder.getNotesIds().get(j);
                        if (noteId.equals(tempId)){
                            folder.getNotesIds().remove(j);
                            folder.getNotesIds().add(newKey);
                        }
                    }
                    folderRef.child(folder.getId()).setValue(folder);
                }
            }

            //Update Tad
            List<Tag> tagList = note.getTags();
            note.getTags().clear();
            for (int i = 0; i < tagList.size(); i++){
                Tag tag = tagList.get(i);
                for (int j = 0; j < allTags.size(); j++){
                    Tag selectedTag = allTags.get(j);
                    if (selectedTag.getTagName().equals(tag.getTagName())){
                        note.getTags().add(selectedTag);
                    }
                }
            }


            noteRef.child(newKey).setValue(note);
            SettingsHelper.getHelper(getApplicationContext()).saveAnonymousUserId("");

        }

    }

}