package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.okason.diary.data.FolderDao;
import com.okason.diary.data.NoteDao;
import com.okason.diary.data.TagDao;
import com.okason.diary.data.TaskDao;
import com.okason.diary.models.dto.FolderDto;
import com.okason.diary.models.dto.JournalDto;
import com.okason.diary.models.dto.ProntoTagDto;
import com.okason.diary.models.dto.ProntoTaskDto;
import com.okason.diary.models.inactive.ProntoJournalUser;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.SettingsHelper;
import com.okason.diary.utils.date.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Get Firebase user
 * Get Pronto Journal User with the Firebase User Id
 * Get this device FCM Token
 * If FCM Token does not exist, request FCM Token
 * Fetch All Journal from Firebase
 * For each Journal check if the Journal with the same ID exists locally
 * If it does not exist, then write the Journal to Realm
 * If it exists, check last modified date
 * If last modified date of the Cloud Journal is after the Local Journal
 * Then overwrite local with the Cloud Copy
 *
 */

public class DataDownloadIntentService extends IntentService {

    private final static String TAG = "DataDownload";

    private CollectionReference journalCloudReference;
    private CollectionReference folderCloudReference;
    private CollectionReference tagCloudReference;
    private CollectionReference taskCloudReference;
    private DocumentReference profileCloudReference;
    private FirebaseUser firebaseUser;
    private String userId;
    private FirebaseFirestore database;


    public DataDownloadIntentService() {
        super("DataDownloadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null){
                userId = firebaseUser.getUid();
                database = FirebaseFirestore.getInstance();
                journalCloudReference = database.collection(Constants.SYNC_CLOUD_END_POINT).document(userId).collection(Constants.NOTE_CLOUD_END_POINT);
                folderCloudReference = database.collection(Constants.SYNC_CLOUD_END_POINT).document(userId).collection(Constants.FOLDER_CLOUD_END_POINT);
                tagCloudReference = database.collection(Constants.SYNC_CLOUD_END_POINT).document(userId).collection(Constants.TAG_CLOUD_END_POINT);
                taskCloudReference = database.collection(Constants.SYNC_CLOUD_END_POINT).document(userId).collection(Constants.TASK_CLOUD_END_POINT);
                profileCloudReference = database.collection(Constants.PRONTO_DIARY_USER_CLOUD_REFERENCE).document(userId);

                profileCloudReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            if (!snapshot.exists()){
                                createNewProntoJournalUser();
                            }
                        }
                    }
                });

                downloadData();
            } 
        }
    }

    private void downloadData() {
        getAllJournal(getApplicationContext());
        getAllTasks(getApplicationContext());
//        getAllTags(getApplicationContext());
//        getAllFolders(getApplicationContext());
    }

    private void createNewProntoJournalUser() {
        ProntoJournalUser user = new ProntoJournalUser();
        user.setFirebaseUid(userId);
        user.setEmailAddress(firebaseUser.getEmail());
        user.setName(firebaseUser.getDisplayName());
        String fcmToken = SettingsHelper.getHelper(getApplicationContext()).getMessagingToken();
        if (!TextUtils.isEmpty(fcmToken)) {
            user.getFcmTokens().add(fcmToken);
        }
        user.setDateCreated(TimeUtils.getReadableDateWithoutTime(System.currentTimeMillis()));
        user.setDateModified(System.currentTimeMillis());
        profileCloudReference.set(user);

    }

    public void getAllTasks(Context context) {
        final List<ProntoTaskDto> tasks = new ArrayList<>();
        try {
            taskCloudReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            ProntoTaskDto item = snapshot.toObject(ProntoTaskDto.class);
                            if (item != null) {
                                tasks.add(item);
                            }
                        }
                        if (tasks.size() > 0){
                            logDataDownloadCount(tasks.size(), context, "Tasks Download");
                            try(Realm realm = Realm.getDefaultInstance()) {
                                TaskDao taskDao = new TaskDao(realm);
                                for (ProntoTaskDto taskDto: tasks){
                                    taskDao.addTaskFromCloud(taskDto);
                                    //  deleteTask(taskDto );
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Data access failure: " + e.getLocalizedMessage());
        }

    }

    public void getAllJournal(Context context) {
        final List<JournalDto> journals = new ArrayList<>();
        journalCloudReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        Log.d(TAG, task.getResult().size() + " " + "Journals downloaded");
                        JournalDto journal = snapshot.toObject(JournalDto.class);
                        if (journal != null) {
                            journals.add(journal);
                        }
                    }
                    if (journals.size() > 0){
                        logDataDownloadCount(journals.size(), context, "Journals Sync");

                        try(Realm realm = Realm.getDefaultInstance()) {
                            NoteDao noteDao = new NoteDao(realm);
                            for (JournalDto journalDto: journals){
                                if (journalDto != null) {
                                    noteDao.addJournalFromCloud(journalDto);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });


    }

    public void getAllTags(Context context) {
        final List<ProntoTagDto> tags = new ArrayList<>();
        try {
            tagCloudReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            ProntoTagDto item = snapshot.toObject(ProntoTagDto.class);
                            if (item != null) {
                                tags.add(item);
                            }
                        }
                        if (tags.size() > 0){
                            logDataDownloadCount(tags.size(), context, "Tags Download");
                            try(Realm realm = Realm.getDefaultInstance()) {
                                TagDao tagDao = new TagDao(realm);
                                for (ProntoTagDto tagDto: tags){
                                    tagDao.addTagFromCloud(tagDto);
                                    //  deleteTask(taskDto );
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Data access failure: " + e.getLocalizedMessage());
        }

    }

    public void getAllFolders(Context context) {
        final List<FolderDto> folders = new ArrayList<>();
        try {
            folderCloudReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            FolderDto item = snapshot.toObject(FolderDto.class);
                            if (item != null) {
                                folders.add(item);
                            }
                        }
                        if (folders.size() > 0){
                            try(Realm realm = Realm.getDefaultInstance()) {
                                FolderDao folderDao = new FolderDao(realm);
                                for (FolderDto folderDto: folders){
                                    folderDao.addFolderFromCloud(folderDto);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Data access failure: " + e.getLocalizedMessage());
        }

    }


    private void logDataDownloadCount(int size, Context context, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, userId);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.QUANTITY, String.valueOf(size));
        FirebaseAnalytics.getInstance(context).logEvent("data_sync_download", bundle);
    }


}
