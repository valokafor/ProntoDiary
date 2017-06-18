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
import com.okason.diary.R;
import com.okason.diary.models.Folder;
import com.okason.diary.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;


public class AddSampleDataIntentService extends IntentService {

    public AddSampleDataIntentService() {
        super("AddSampleDataIntentService");
    }
    private List<String> sampleFolderNames;

    private DatabaseReference mDatabase;
    private DatabaseReference folderCloudReference;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onHandleIntent(Intent intent) {
        sampleFolderNames = Arrays.asList(getApplicationContext().getResources().getStringArray(R.array.sampleFolderNames));

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        folderCloudReference =  mDatabase.child(Constants.USERS_CLOUD_END_POINT + mFirebaseUser.getUid() + Constants.FOLDER_CLOUD_END_POINT);
        folderCloudReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Folder> existingFolders = new ArrayList<Folder>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Folder folder = snapshot.getValue(Folder.class);
                    existingFolders.add(folder);
                }
                addNonDuplicateFolders(existingFolders);
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addNonDuplicateFolders(List<Folder> existingFolders) {
        if (existingFolders.size() > 0){
            //Void creating Folders that may already exist in the cloud
            //Because the user have more than one device
            for ( int i = 0;  i < sampleFolderNames.size(); i++){
                for (Folder folder: existingFolders){
                    String tempName = sampleFolderNames.get(i);
                    if (folder.getFolderName().equals(tempName)){
                        try {
                            sampleFolderNames.remove(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }



        //Now add distinct Folders
        if (sampleFolderNames.size() > 0){
            for (String name: sampleFolderNames){
                String key = folderCloudReference.push().getKey();
                Folder folder = new Folder();
                folder.setId(key);
                folder.setFolderName(name);
                folderCloudReference.child(key).setValue(folder);
            }
        }

    }


}
