package com.okason.diary.core.services;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Journal;
import com.okason.diary.models.ProntoTag;
import com.okason.diary.models.ProntoTask;
import com.okason.diary.models.Reminder;
import com.okason.diary.models.SubTask;
import com.okason.diary.models.dto.AttachmentDto;
import com.okason.diary.models.dto.FolderDto;
import com.okason.diary.models.dto.JournalDto;
import com.okason.diary.models.dto.ProntoTagDto;
import com.okason.diary.models.dto.ProntoTaskDto;
import com.okason.diary.models.dto.ReminderDto;
import com.okason.diary.models.dto.SubTaskDto;
import com.okason.diary.utils.Constants;

import io.realm.Realm;
import io.realm.RealmResults;


public class DataUploadIntentService extends JobIntentService {

    private static final int JOB_ID = 987;

    private final static String TAG = "SyncIntentService";

    //Get date of last sync
    //Get a List of All Journals whose date of last modification is after last sync
    //Upload or overwrite those objects
    //Get all Journals from Cloud, if date of last update on the journal is greater than the date
    //Of last change of the journal locally, then overwrite local
    //If date of last update is zero on local device



    private CollectionReference journalCloudReference;
    private CollectionReference folderCloudReference;
    private CollectionReference tagCloudReference;
    private CollectionReference taskCloudReference;
    private FirebaseUser firebaseUser;
    private String userId;
    private FirebaseFirestore database;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Oncreate called");
    }

    public static void enqueueWork(Context context, Intent intent) {
        Log.d(TAG, "enqueueWork  called");
        enqueueWork(context, DataUploadIntentService.class, JOB_ID, intent);
    }




    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            userId = firebaseUser.getUid();
            database = FirebaseFirestore.getInstance();
            journalCloudReference = database.collection(Constants.SYNC_CLOUD_END_POINT).document(userId).collection(Constants.NOTE_CLOUD_END_POINT);
            folderCloudReference = database.collection(Constants.SYNC_CLOUD_END_POINT).document(userId).collection(Constants.FOLDER_CLOUD_END_POINT);
            tagCloudReference = database.collection(Constants.SYNC_CLOUD_END_POINT).document(userId).collection(Constants.TAG_CLOUD_END_POINT);
            taskCloudReference = database.collection(Constants.SYNC_CLOUD_END_POINT).document(userId).collection(Constants.TASK_CLOUD_END_POINT);
        }

        if (intent.hasExtra(Constants.INITIAL_SYNC)){
            Log.d(TAG, "Initial data sync called");
            try (Realm realm = Realm.getDefaultInstance()){

                //Get All Journals and upload to Firebase
                RealmResults<Journal> journals = realm.where(Journal.class).findAll();
                if (journals.size() > 0){
                    Log.d(TAG, "Syncing " + journals.size() + " journals");
                    for (Journal journal: journals){
                        uploadJournalToFirebase(journal);
                    }
                }

                //Get All Tasks and upload to Firebase
                RealmResults<ProntoTask> prontoTasks = realm.where(ProntoTask.class).findAll();
                if (prontoTasks.size() > 0){
                    Log.d(TAG, "Syncing " + prontoTasks.size() + " tasks");
                    for (ProntoTask prontoTask : prontoTasks){
                        uploadTaskToFirebase(prontoTask);
                    }
                }

                //Get All Folders and upload to Firebase
                RealmResults<Folder> folders = realm.where(Folder.class).findAll();
                if (folders.size() > 0){
                    Log.d(TAG, "Syncing " + folders.size() + " folders");
                    for (Folder folder: folders){
                        uploadFolderToFirebase(folder);
                    }
                }

                //Get All Tags and upload to Firebase
                RealmResults<ProntoTag> tags = realm.where(ProntoTag.class).findAll();
                if (tags.size() > 0){
                    Log.d(TAG, "Syncing " + tags.size() + " tags");
                    for (ProntoTag tag: tags){
                        uploadTagsToFirebase(tag);
                    }
                }


            }

        } else if (intent.hasExtra(Constants.JOURNAL_ID)){
            try(Realm realm = Realm.getDefaultInstance()) {
                String noteId = intent.getStringExtra(Constants.JOURNAL_ID);
                Journal updatedJournal = realm.where(Journal.class).equalTo("id", noteId).findFirst();
                if (updatedJournal != null){
                    uploadJournalToFirebase(updatedJournal);
                }

            }
        } else if (intent.hasExtra(Constants.TASK_ID)){
            try(Realm realm = Realm.getDefaultInstance()) {
                String taskId = intent.getStringExtra(Constants.TASK_ID);
                ProntoTask updatedTask = realm.where(ProntoTask.class).equalTo("id", taskId).findFirst();
                if (updatedTask != null){
                    uploadTaskToFirebase(updatedTask);
                }

            }
        } else if (intent.hasExtra(Constants.FOLDER_ID)){
            try(Realm realm = Realm.getDefaultInstance()) {
                String folderId = intent.getStringExtra(Constants.FOLDER_ID);
                Folder updatedFolder = realm.where(Folder.class).equalTo("id", folderId).findFirst();
                if (updatedFolder != null){
                    uploadFolderToFirebase(updatedFolder);
                }

            }
        } else if (intent.hasExtra(Constants.TAG_ID)){
            try(Realm realm = Realm.getDefaultInstance()) {
                String tagId = intent.getStringExtra(Constants.TAG_ID);
                ProntoTag updatedTag = realm.where(ProntoTag.class).equalTo("id", tagId).findFirst();
                if (updatedTag != null){
                    uploadTagsToFirebase(updatedTag);
                }

            }
        }

        //Kick of download of Data from
        new android.os.Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startService(new Intent(getApplicationContext(), DataDownloadIntentService.class));
            }
        }, 5000);

    }

    private void uploadTagsToFirebase(ProntoTag tag) {

        //Convert Realm Object to POJO
        ProntoTagDto tagDto = new ProntoTagDto(tag);

        //Copy Journal Ids if exist
        if (tag.getJournals().size() > 0){
            for (Journal journal: tag.getJournals()){
                tagDto.getJournalIds().add(journal.getId());
            }
        }

        //Copy Task Ids if exist
        if (tag.getTasks().size() > 0){
            for (ProntoTask task: tag.getTasks()){
                tagDto.getTaskIds().add(task.getId());
            }
        }

        //Write POJO to Firebase
        tagCloudReference.document(tagDto.getId()).set(tagDto);

    }


    private void uploadFolderToFirebase(Folder folder) {
        //Convert Realm Folder Object to POJO
        FolderDto folderDto = new FolderDto(folder);

        if (folder.getTasks().size() > 0){
            for (ProntoTask task: folder.getTasks()){
                folderDto.getTaskIds().add(task.getId());
            }
        }

        if (folder.getJournals().size() > 0){
            for (Journal journal: folder.getJournals()){
                folderDto.getJournalIds().add(journal.getId());
            }
        }
        folderCloudReference.document(folderDto.getId()).set(folderDto);

    }

    private void uploadTaskToFirebase(ProntoTask task) {

        ProntoTaskDto taskDto = new ProntoTaskDto(task);

        if (task.getFolder() != null){
            Folder folder = task.getFolder();
            FolderDto folderDto = new FolderDto(folder);
            taskDto.setFolder(folderDto);
        }

        if (task.getReminder() != null){
            Reminder reminder = task.getReminder();
            ReminderDto reminderDto = new ReminderDto(reminder);
            taskDto.setReminder(reminderDto);
        }

        if (task.getProntoTags().size() > 0){
            for (ProntoTag tag: task.getProntoTags()){
                ProntoTagDto tagDto = new ProntoTagDto(tag);
                taskDto.getTags().add(tagDto);
            }
        }

        if (task.getSubTask().size() > 0){
            for (SubTask subTask: task.getSubTask()){
                SubTaskDto subTaskDto = new SubTaskDto(subTask);
                taskDto.getSubTask().add(subTaskDto);
            }
        }

        taskCloudReference.document(taskDto.getId()).set(taskDto);
    }

    private void uploadJournalToFirebase(Journal journal) {
        JournalDto dto = new JournalDto(journal);
        if (journal.getAttachments().size() > 0){
            for (Attachment attachment: journal.getAttachments()){
                AttachmentDto attachmentDto = new AttachmentDto(attachment);
                dto.getAttachments().add(attachmentDto);
            }
        }
        if (journal.getTags().size() > 0){
            for (ProntoTag prontoTag : journal.getTags()){
                ProntoTagDto prontoTagDto = new ProntoTagDto(prontoTag);
                dto.getTags().add(prontoTagDto);
            }
        }

        if (journal.getFolder() != null){
            Folder folder = journal.getFolder();
            FolderDto folderDto = new FolderDto(folder);
            dto.setFolder(folderDto);
        }

        journalCloudReference.document(dto.getId()).set(dto);


    }



}
