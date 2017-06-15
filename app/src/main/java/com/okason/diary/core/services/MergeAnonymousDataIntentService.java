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
import com.okason.diary.core.events.AddDefaultDataEvent;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Note;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class MergeAnonymousDataIntentService extends IntentService {

    public MergeAnonymousDataIntentService() {
        super("MergeAnonymousDataIntentService");
    }
    private DatabaseReference mDatabase;
    private DatabaseReference annonymousNoteCloudReference;
    private DatabaseReference registeredNoteCloudReference;

    private DatabaseReference annonymousFolderCloudReference;
    private DatabaseReference registeredFolderCloudReference;


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.hasExtra(Constants.ANONYMOUS_ACCOUNT_USER_ID)) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String annonymousUserId = intent.getStringExtra(Constants.ANONYMOUS_ACCOUNT_USER_ID);


            final List<Note> notesToBeMigrated = new ArrayList<>();
            final List<String> categoriesToBeMigratedIds = new ArrayList<>();

            if (!TextUtils.isEmpty(annonymousUserId)) {


                if (!TextUtils.isEmpty(annonymousUserId)) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        final String currentUserId = user.getUid();

                        //Now move Temp User Notes to Current User
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        annonymousNoteCloudReference = mDatabase.child(Constants.USERS_CLOUD_END_POINT + annonymousUserId
                                + Constants.NOTE_CLOUD_END_POINT);
                        registeredNoteCloudReference = mDatabase.child(Constants.USERS_CLOUD_END_POINT + currentUserId
                                + Constants.NOTE_CLOUD_END_POINT);

                        annonymousFolderCloudReference = mDatabase.child(Constants.USERS_CLOUD_END_POINT + annonymousUserId
                                + Constants.CATEGORY_CLOUD_END_POINT);
                        registeredFolderCloudReference = mDatabase.child(Constants.USERS_CLOUD_END_POINT + currentUserId
                                + Constants.CATEGORY_CLOUD_END_POINT);

                        annonymousNoteCloudReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                                    Note note = noteSnapshot.getValue(Note.class);
                                    notesToBeMigrated.add(note);
                                    categoriesToBeMigratedIds.add(note.getFolderId());
                                    Intent uploadServiceIntent = new Intent( getApplicationContext(), AttachmentUploadService.class)
                                            .putExtra(AttachmentUploadService.NOTE_ID, note.getId())
                                            .setAction(AttachmentUploadService.ACTION_UPLOAD);
                                    getApplicationContext().startService(uploadServiceIntent);

                                }
                                migrateNote(notesToBeMigrated);

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



    private void migrateNote(List<Note> notesToBeMigrated) {

        for (final Note note: notesToBeMigrated){
            String tempId = note.getId();

            //Migrate the Note from Anonymous
            final String newKey = registeredNoteCloudReference.push().getKey();
            note.setId(newKey);
            registeredNoteCloudReference.child(newKey).setValue(note);
            annonymousNoteCloudReference.child(tempId).removeValue();

            //Migrate Folder content from Anonuymous to Registered
            final String folderId = note.getFolderId();
            if (!TextUtils.isEmpty(folderId)){
                annonymousFolderCloudReference.child(folderId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Folder folder = dataSnapshot.getValue(Folder.class);

                        String newFolderKey = registeredFolderCloudReference.push().getKey();
                        folder.setId(newFolderKey);
                        registeredFolderCloudReference.child(newFolderKey).setValue(folder);
                        note.setFolderId(newFolderKey);
                        registeredNoteCloudReference.child(newKey).setValue(note);
                        annonymousFolderCloudReference.child(folderId).removeValue();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        EventBus.getDefault().post(new AddDefaultDataEvent());

    }

    }