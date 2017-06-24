package com.okason.diary.data;

import com.okason.diary.models.Folder;
import com.okason.diary.ui.addnote.AddNoteContract;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by valokafor on 6/22/17.
 */

public class FolderRealmRepository implements AddNoteContract.FolderRepository {
    @Override
    public List<Folder> getAllFolders() {
        List<Folder> viewModels = new ArrayList<>();
        try(Realm realm = Realm.getDefaultInstance()) {
            RealmResults<Folder> folders = realm.where(Folder.class).findAll();
            if (folders != null && folders.size() > 0){
                viewModels = realm.copyFromRealm(folders);
            }
        }
        return viewModels;
    }

    @Override
    public Folder getFolderById(String id) {
        Folder selectedFolder;
        try (Realm realm = Realm.getDefaultInstance()){
            selectedFolder = realm.where(Folder.class).equalTo("id", id).findFirst();
            selectedFolder = realm.copyFromRealm(selectedFolder);
        }catch (Exception e){
            selectedFolder = null;
        }
        return selectedFolder;
    }

    @Override
    public Folder createNewFolder() {
        String id = UUID.randomUUID().toString();
        Folder folder;
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            folder = realm.createObject(Folder.class, id);
            folder.setDateCreated(System.currentTimeMillis());
            folder.setDateModified(System.currentTimeMillis());
            realm.commitTransaction();
            folder = realm.copyFromRealm(folder);
        }
        return folder;
    }

    @Override
    public void updatedFolderTitle(final String id, final String title) {
        try (Realm realm = Realm.getDefaultInstance()){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    Folder selectedFolder = backgroundRealm.where(Folder.class).equalTo("id", id).findFirst();
                    if (selectedFolder != null){
                        selectedFolder.setFolderName(title);
                        selectedFolder.setDateModified(System.currentTimeMillis());
                    }
                }
            });
        }

    }

    @Override
    public void deleteFolder(final String folderId) {
        try (Realm realm = Realm.getDefaultInstance()){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    backgroundRealm.where(Folder.class).equalTo("id", folderId).findFirst().deleteFromRealm();
                }
            });
        }
    }
}
