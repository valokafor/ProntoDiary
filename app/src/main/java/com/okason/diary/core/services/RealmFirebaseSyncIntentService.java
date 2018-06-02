package com.okason.diary.core.services;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Journal;
import com.okason.diary.models.ProntoTask;
import com.okason.diary.models.ProntoTag;
import com.okason.diary.models.dto.AttachmentDto;
import com.okason.diary.models.dto.FolderDto;
import com.okason.diary.models.dto.JournalDto;
import com.okason.diary.models.dto.TagDto;
import com.okason.diary.models.dto.TaskDto;
import com.okason.diary.utils.Constants;

import io.realm.Realm;
import io.realm.RealmResults;


public class RealmFirebaseSyncIntentService extends JobIntentService {

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
    private final static String TAG = "DataAccessManager";



//    public RealmFirebaseSyncIntentService() {
//        super("RealmFirebaseSyncIntentService");
//    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            userId = firebaseUser.getUid();
            journalCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.NOTE_CLOUD_END_POINT);
            folderCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.FOLDER_CLOUD_END_POINT);
            tagCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.TAG_CLOUD_END_POINT);
            taskCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.TASK_CLOUD_END_POINT);


        }

        if (intent.hasExtra(Constants.INITIAL_SYNC)){
            try (Realm realm = Realm.getDefaultInstance()){

                //Get All Journals and Upload to Firebase
                RealmResults<Journal> journals = realm.where(Journal.class).findAll();
                if (journals.size() > 0){
                    for (Journal journal: journals){
                        uploadJournalToFirebase(journal);
                    }
                }

                //Get All Tasks
                RealmResults<ProntoTask> prontoTasks = realm.where(ProntoTask.class).findAll();
                if (prontoTasks.size() > 0){
                    for (ProntoTask prontoTask : prontoTasks){
                        uploadTaskToFirebase(prontoTask);
                    }
                }


            }

        }

    }

    private void uploadTaskToFirebase(ProntoTask prontoTask) {
        TaskDto taskDto = new TaskDto(prontoTask);
    }

    private void uploadJournalToFirebase(Journal journal) {
        JournalDto dto = new JournalDto(journal);
        if (journal.getAttachments().size() > 0){
            for (Attachment attachment: journal.getAttachments()){
                AttachmentDto attachmentDto = new AttachmentDto(attachment);
                dto.getAttachments().add(attachmentDto);
            }
        }
        if (journal.getProntoTags().size() > 0){
            for (ProntoTag prontoTag : journal.getProntoTags()){
                TagDto tagDto = new TagDto(prontoTag);
                dto.getTags().add(tagDto);
            }
        }

        if (journal.getFolder() != null){
            Folder folder = journal.getFolder();
            FolderDto folderDto = new FolderDto(folder);
            dto.setFolder(folderDto);
        }

        journalCloudReference.document(dto.getId()).set(dto);


    }


    protected void onHandleIntent(Intent intent) {
        if (intent != null) {


        }
    }


}
