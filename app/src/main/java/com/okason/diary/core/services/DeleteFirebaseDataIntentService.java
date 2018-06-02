package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.okason.diary.utils.Constants;


public class DeleteFirebaseDataIntentService extends IntentService {


    private FirebaseFirestore database;
    private CollectionReference journalCloudReference;
    private CollectionReference folderCloudReference;
    private CollectionReference tagCloudReference;
    private CollectionReference taskCloudReference;

    public DeleteFirebaseDataIntentService() {
        super("DeleteFirebaseDataIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            String userId = user.getUid();
            database = FirebaseFirestore.getInstance();
            journalCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.NOTE_CLOUD_END_POINT);
            folderCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.FOLDER_CLOUD_END_POINT);
            tagCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.TAG_CLOUD_END_POINT);
            taskCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.TASK_CLOUD_END_POINT);


            journalCloudReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            snapshot.getReference().delete();
                        }
                    }

                }
            });

            folderCloudReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            snapshot.getReference().delete();
                        }
                    }

                }
            });

            tagCloudReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            snapshot.getReference().delete();
                        }
                    }

                }
            });

            taskCloudReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            snapshot.getReference().delete();
                        }
                    }

                }
            });
        }
    }


}
