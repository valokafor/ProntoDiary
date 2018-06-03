package com.okason.diary.models.inactive;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.okason.diary.NoteListActivity;
import com.okason.diary.data.NoteDao;
import com.okason.diary.data.TaskDao;
import com.okason.diary.models.dto.JournalDto;
import com.okason.diary.models.dto.ProntoTaskDto;
import com.okason.diary.models.dto.SubTaskDto;
import com.okason.diary.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by valokafor on 5/31/18.
 */

public class DataAccessManager {

    private final String userId;
    private FirebaseFirestore database;
    private final static String TAG = "DataAccessManager";


    private CollectionReference journalCloudReference;
    private CollectionReference folderCloudReference;
    private CollectionReference tagCloudReference;
    private CollectionReference taskCloudReference;



    public DataAccessManager(String userId) {
        this.userId = userId;
        database = FirebaseFirestore.getInstance();


        journalCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.NOTE_CLOUD_END_POINT);
        folderCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.FOLDER_CLOUD_END_POINT);
        tagCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.TAG_CLOUD_END_POINT);
        taskCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.TASK_CLOUD_END_POINT);

        String path = journalCloudReference.getPath();
        Log.d(NoteListActivity.TAG, "Cloud Journal Path: " + path);

    }






    public void deleteNote(String noteId) {
        if (!TextUtils.isEmpty(noteId)) {
            DocumentReference noteToBeDeletedRef = journalCloudReference.document(noteId);
            if (noteToBeDeletedRef != null) {
                noteToBeDeletedRef.delete();
            }
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
                        logDataDownloadCount(journals.size(), context, "Journals");

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

    private void logDataDownloadCount(int size, Context context, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, userId);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.QUANTITY, String.valueOf(size));
        FirebaseAnalytics.getInstance(context).logEvent("data_migration_fb_to_realm", bundle);
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
                            logDataDownloadCount(tasks.size(), context, "Tasks");
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




    public void deleteFolder(String folderId) {
        folderCloudReference.document(folderId).delete();

    }

    public void deleteTag(String tagId) {
        tagCloudReference.document(tagId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                   // getAllTags();
                }
            }
        });

    }


    public void deleteSubTask(final ProntoTaskDto parentTask, String subTaskTitle) {
        for (int i = 0; i < parentTask.getSubTask().size(); i++){
            SubTaskDto subTask = parentTask.getSubTask().get(i);
            if (subTask.getTitle().equals(subTaskTitle)){
                parentTask.getSubTask().remove(i);
                break;
            }
        }
        taskCloudReference.document(parentTask.getId()).set(parentTask);
    }

    public void deleteTask(ProntoTaskDto clickedTask) {
        taskCloudReference.document(clickedTask.getId()).delete();

    }
}
