package com.okason.diary.data;

import com.okason.diary.models.realmentities.FolderEntity;

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

    public RealmResults<FolderEntity> getAllFolders() {
        RealmResults<FolderEntity> folders = realm.where(FolderEntity.class).findAll();
        return folders;
    }

    public FolderEntity getFolderById(String id) {
        FolderEntity folder = realm.where(FolderEntity.class).equalTo("id", id).findFirst();
        return folder;
    }

    public FolderEntity createNewFolder() {
        String id = UUID.randomUUID().toString();
        realm.beginTransaction();
        FolderEntity folder = realm.createObject(FolderEntity.class, id);
        folder.setDateCreated(System.currentTimeMillis());
        folder.setDateModified(System.currentTimeMillis());
        realm.commitTransaction();
        return folder;
    }

    public void updatedFolderTitle(final String id, final String title) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                FolderEntity selectedFolder = backgroundRealm.where(FolderEntity.class).equalTo("id", id).findFirst();
                if (selectedFolder != null) {
                    selectedFolder.setFolderName(title);
                    selectedFolder.setDateModified(System.currentTimeMillis());
                }
            }
        });
    }

    public void deleteFolder(final String folderId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                FolderEntity folder =  backgroundRealm.where(FolderEntity.class).equalTo("id", folderId).findFirst();
                if (folder != null) {
                    folder.deleteFromRealm();
                }
            }
        });
    }


    public FolderEntity getFolderByName(String query) {
        FolderEntity folders = realm.where(FolderEntity.class).equalTo("folderName", query, Case.INSENSITIVE).findFirst();
        return folders;
    }

    public FolderEntity getOrCreateFolder(String foldername) {
        FolderEntity folder = getFolderByName(foldername);
        if (folder == null){
            folder = createNewFolder();
        }
        return folder;
    }

    public RealmResults<FolderEntity> filterFolder(String query) {
        RealmResults<FolderEntity> results = realm.where(FolderEntity.class).contains("folderName", query, Case.INSENSITIVE).findAll();
        return results;
    }
}
