package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.okason.diary.NoteListActivity;
import com.okason.diary.data.FolderDao;
import com.okason.diary.data.JournalDao;
import com.okason.diary.data.RealmManager;
import com.okason.diary.data.TagDao;
import com.okason.diary.data.TaskDao;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Journal;
import com.okason.diary.models.ProntoTag;
import com.okason.diary.models.ProntoTask;
import com.okason.diary.models.SubTask;
import com.okason.diary.models.dto.AttachmentDto;
import com.okason.diary.models.dto.FolderDto;
import com.okason.diary.models.dto.JournalDto;
import com.okason.diary.models.dto.ProntoTagDto;
import com.okason.diary.models.dto.ProntoTaskDto;
import com.okason.diary.models.dto.SubTaskDto;

import io.realm.Realm;
import io.realm.RealmResults;


public class LocalToSyncIntentService extends IntentService {
    private final static String TAG = "LocalToSyncService";
    private Realm localRealm;
    private Realm syncRealm;



    public LocalToSyncIntentService() {
        super("LocalToSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Crashlytics.log(Log.DEBUG, TAG, "LocalToSyncIntentService called");

        Bundle bundle = new Bundle();
        FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("copy_local_to_sync_started", bundle);

        //Get an instance of Local Realm
        localRealm = Realm.getInstance(RealmManager.getLocalConfig());

        //Get an instance of Sync Realm
        syncRealm = Realm.getInstance(RealmManager.getSyncConfig());

        //Get All local Tags and add to Sync Tag
        RealmResults<ProntoTag> tags = localRealm.where(ProntoTag.class).findAll();
        if (tags.size() > 0){
            copyLocalTagsToSync(tags);
        }


        //Get All local Folders and add to Sync Folder
        RealmResults<Folder> folders = localRealm.where(Folder.class).findAll();
        if (folders.size() > 0){
            copyLocalFoldersToSync(folders);
        }

        //Get all local Journals and add to Sync Journal
        RealmResults<Journal> journals = localRealm.where(Journal.class).findAll();
        if (journals.size() > 0){
            copyLocalJournalsToSync(journals);
        }

        //Get all local Tasks and add to Sync Task
        RealmResults<ProntoTask> tasks = localRealm.where(ProntoTask.class).findAll();
        if (tasks.size() > 0){
            copyLocalTasksToSync(tasks);
        }


        localRealm.close();
        syncRealm.close();

        Bundle bundleFinish = new Bundle();
        FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("copy_local_to_sync_finished", bundleFinish);

        Intent restartIntent = new Intent(getApplicationContext(), NoteListActivity.class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(restartIntent);


    }

    private void copyLocalTasksToSync(RealmResults<ProntoTask> tasks) {
        TaskDao taskDao = new TaskDao(syncRealm);
        for (ProntoTask task: tasks){
            ProntoTaskDto dto = new ProntoTaskDto(task);
            if (task.getSubTask().size() > 0){
                for (SubTask subTask: task.getSubTask()){
                    SubTaskDto subTaskDto = new SubTaskDto(subTask);
                    dto.getSubTask().add(subTaskDto);
                }
            }

            if (task.getTags().size() > 0){
                for (ProntoTag tag: task.getTags()){
                    ProntoTagDto tagDto = new ProntoTagDto(tag);
                    dto.getTags().add(tagDto);
                }
            }
            if (task.getFolder() != null){
                Folder folder = task.getFolder();
                FolderDto folderDto = new FolderDto(folder);
                dto.setFolder(folderDto);
            }

            taskDao.adddTaskDtoToRealm(dto);
        }
    }

    private void copyLocalJournalsToSync(RealmResults<Journal> journals) {
        //Get an instance of Journal Data Access Object
        JournalDao journalDao = new JournalDao(syncRealm);
        for (Journal journal: journals){
            JournalDto dto = getJournalDto(journal);
            journalDao.addJournalDtoToRealm(dto);

        }
    }

    private void copyLocalFoldersToSync(RealmResults<Folder> folders) {
        //Get an instance of Folder Data Access Object
        FolderDao folderDao = new FolderDao(syncRealm);
        for (Folder folder: folders){
            folderDao.getOrCreateFolder(folder.getFolderName());
        }
    }

    private void copyLocalTagsToSync(RealmResults<ProntoTag> tags) {
        //Get an instance of Tag Data Access Object
        TagDao tagDao = new TagDao(syncRealm);
        for (ProntoTag tag: tags){
            tagDao.getOrCreateTag(tag.getTagName());
        }

    }


    private JournalDto getJournalDto(Journal journal) {
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

        return dto;


    }


}
