package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.okason.diary.models.Folder;
import com.okason.diary.models.SampleData;
import com.okason.diary.ui.addnote.DataAccessManager;

import java.util.List;


public class AddSampleDataIntentService extends IntentService {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore database;
    private DataAccessManager dataAccessManager;


    public AddSampleDataIntentService() {
        super("AddSampleDataIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getDisplayName())) {
            dataAccessManager = new DataAccessManager(firebaseUser.getUid());
            List<String> sampleFolderNames = SampleData.getSampleCategories();
            for (String name : sampleFolderNames) {
                final Folder folder = new Folder();
                folder.setFolderName(name);
                folder.setDateModified(System.currentTimeMillis());
                DocumentReference newFolder = dataAccessManager.getFolderPath().document();
                folder.setId(newFolder.getId());
                newFolder.set(folder);
            }

        }
    }


}
