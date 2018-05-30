package com.okason.diary.data;

import com.okason.diary.core.events.FolderAddedEvent;
import com.okason.diary.models.Folder;

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
}
