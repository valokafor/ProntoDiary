package com.okason.diary.data;

import android.content.Intent;
import android.util.Log;

import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.events.FolderAddedEvent;
import com.okason.diary.core.services.DataUploadIntentService;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Journal;
import com.okason.diary.models.ProntoTask;
import com.okason.diary.models.dto.FolderDto;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.date.TimeUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by valokafor on 5/19/18.
 */

public class FolderDao {

    private Realm realm;
    private final static String TAG = "FolderDao";

    public FolderDao(Realm realm) {
        this.realm = realm;
    }

    public RealmResults<Folder> getAllFolders() {
        RealmResults<Folder> folders = realm.where(Folder.class).findAll();
        return folders;
    }

    public Folder getFolderById(String id) {
        Folder folder = realm.where(Folder.class).equalTo("id", id).findFirst();
        return folder;
    }

    public Folder createNewFolder() {
        String id = UUID.randomUUID().toString();
        realm.beginTransaction();
        Folder folder = realm.createObject(Folder.class, id);
        folder.setDateCreated(System.currentTimeMillis());
        folder.setDateModified(System.currentTimeMillis());
        realm.commitTransaction();
        return folder;
    }

    public void updatedFolderTitle(final String id, final String title) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                Folder selectedFolder = backgroundRealm.where(Folder.class).equalTo("id", id).findFirst();
                if (selectedFolder != null) {
                    selectedFolder.setFolderName(title);
                    selectedFolder.setDateModified(System.currentTimeMillis());
                    EventBus.getDefault().post(new FolderAddedEvent(selectedFolder.getId()));

                    //Update Firebase Record
                    Intent intent = new Intent(ProntoDiaryApplication.getAppContext(), DataUploadIntentService.class);
                    intent.putExtra(Constants.FOLDER_ID, id);
                    DataUploadIntentService.enqueueWork(ProntoDiaryApplication.getAppContext(), intent);
                }
            }
        });
    }

    public void deleteFolder(final String folderId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                Folder folder =  backgroundRealm.where(Folder.class).equalTo("id", folderId).findFirst();
                if (folder != null) {
                    folder.deleteFromRealm();

                    Intent intent = new Intent(ProntoDiaryApplication.getAppContext(), DataUploadIntentService.class);
                    intent.putExtra(Constants.DELETE_EVENT, true);
                    intent.putExtra(Constants.DELETE_EVENT_TYPE, Constants.FOLDERS);
                    intent.putExtra(Constants.ITEM_ID, folderId);
                    DataUploadIntentService.enqueueWork(ProntoDiaryApplication.getAppContext(), intent);
                }
            }
        });
    }


    public Folder getFolderByName(String query) {
        Folder folders = realm.where(Folder.class).equalTo("folderName", query, Case.INSENSITIVE).findFirst();
        return folders;
    }

    public Folder getOrCreateFolder(String foldername) {
        Folder folder = getFolderByName(foldername);
        if (folder == null){
            folder = createNewFolder();
        }
        return folder;
    }

    public RealmResults<Folder> filterFolder(String query) {
        RealmResults<Folder> results = realm.where(Folder.class).contains("folderName", query, Case.INSENSITIVE).findAll();
        return results;
    }

    public void addFolderFromCloud(FolderDto folderDto) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                String fId = folderDto.getId();
                Log.d(TAG, "Folder Id: " + fId);
                Folder folder = realm.where(Folder.class).equalTo("id", fId).findFirst();

                if (folder != null && !TimeUtils.isCloudModifiedDateAfterLocalModifiedDate(folder.getDateModified(),
                        folder.getDateModified())){
                    return;
                }


                if (folder == null) {
                    String folderId = UUID.randomUUID().toString();
                    folder = realm.createObject(Folder.class, folderId);
                }
                
                folder.setDateCreated(folderDto.getDateCreated());
                folder.setDateModified(folderDto.getDateModified());
                folder.setFolderName(folderDto.getFolderName());

                if (folderDto.getTaskIds().size() > 0){
                    for (String taskId: folderDto.getTaskIds()){
                        ProntoTask task = new TaskDao(realm).getTaskById(taskId);
                        if (task != null){
                            folder.getTasks().add(task);
                            task.setFolder(folder);
                        }
                    }
                }

                if (folderDto.getJournalIds().size() > 0){
                    for (String journalId: folderDto.getJournalIds()){
                        Journal note = new JournalDao(realm).getJournalById(journalId);
                        if (note != null){
                            folder.getJournals().add(note);
                            note.setFolder(folder);
                        }
                    }
                }

            }
        });

    }
}
