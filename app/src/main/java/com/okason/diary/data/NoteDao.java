package com.okason.diary.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.okason.diary.BuildConfig;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.models.realmentities.AttachmentEntity;
import com.okason.diary.models.realmentities.FolderEntity;
import com.okason.diary.models.realmentities.NoteEntity;
import com.okason.diary.utils.FileHelper;
import com.okason.diary.utils.StorageHelper;

import java.io.File;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by valokafor on 4/28/18.
 */

public class NoteDao {
    private Realm realm;

    public NoteDao(Realm realm) {
        this.realm = realm;
    }

    public RealmResults<NoteEntity> getAllNoteEntitys() {
        RealmResults<NoteEntity> notes = realm.where(NoteEntity.class).findAll();
        return notes;
    }


    public int getNoteEntityPosition(String noteId) {
        RealmResults<NoteEntity> notes = realm.where(NoteEntity.class).findAll();
        for (int i = 0; i < notes.size(); i++){
            if (notes.get(i).getId().equals(noteId)){
                return i;
            }
        }
        return -1;
    }

    public NoteEntity getNoteEntityById(String noteId) {
        try {
            NoteEntity selectedNoteEntity = realm.where(NoteEntity.class).equalTo("id", noteId).findFirst();
            return selectedNoteEntity;
        } catch (Exception e) {
            return null;
        }
    }

    public NoteEntity copyOrUpdate(NoteEntity noteEntity){
        try {
            realm.beginTransaction();
            noteEntity = realm.copyToRealmOrUpdate(noteEntity);
            realm.commitTransaction();
        } catch (Exception exception) {
            realm.cancelTransaction();
            throw exception;
        }
        return noteEntity;
    }


    public void deleteNote(String noteId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                NoteEntity noteEntity = backgroundRealm.where(NoteEntity.class).equalTo("id", noteId).findFirst();
                noteEntity.deleteFromRealm();
            }
        });


    }

    public NoteEntity createNewNote() {
        String noteId = UUID.randomUUID().toString();
        realm.beginTransaction();
        NoteEntity note = realm.createObject(NoteEntity.class, noteId);
        note.setDateCreated(System.currentTimeMillis());
        note.setDateModified(System.currentTimeMillis());
        realm.commitTransaction();
        return note;
    }

    public void onAttachmentAdded(String attachmentId, String noteId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                NoteEntity note = backgroundRealm.where(NoteEntity.class).equalTo("id", noteId).findFirst();
                AttachmentEntity attachment = backgroundRealm.where(AttachmentEntity.class).equalTo("id", attachmentId).findFirst();
                if (note != null && attachment != null){
                    note.getAttachments().add(attachment);
                    note.setDateModified(System.currentTimeMillis());
                }
            }
        });
    }

    public void setFolder(String noteId, String folderId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                NoteEntity note = backgroundRealm.where(NoteEntity.class).equalTo("id", noteId).findFirst();
                FolderEntity folder = backgroundRealm.where(FolderEntity.class).equalTo("id", folderId).findFirst();
                note.setFolder(folder);
                folder.getNoteEntitys().add(note);
                note.setDateModified(System.currentTimeMillis());
            }
        });
    }

    public void createNewAttachment(Uri attachmentUri, String filPath, String mimeType, String noteId) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                String attachmentId = UUID.randomUUID().toString();
                AttachmentEntity attachment = backgroundRealm.createObject(AttachmentEntity.class, attachmentId);
                attachment.setUri(attachmentUri.toString());
                attachment.setLocalFilePath(filPath);
                attachment.setMime_type(mimeType);

                NoteEntity note = backgroundRealm.where(NoteEntity.class).equalTo("id", noteId).findFirst();
                if (note != null && attachment != null){
                    note.getAttachments().add(attachment);
                    note.setDateModified(System.currentTimeMillis());
                }
            }
        });
    }

    public void updatedNoteContent(String noteId, String content, String title ) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                NoteEntity note = backgroundRealm.where(NoteEntity.class).equalTo("id", noteId).findFirst();
                if (note != null){
                    note.setTitle(title);
                    note.setContent(content);
                    note.setDateModified(System.currentTimeMillis());
                }
            }
        });

    }

    public void createAttachmentFromUri(Context mContext, Uri uri, String noteId) {
        String name = FileHelper.getNameFromUri(mContext, uri);
        String extension = FileHelper.getFileExtension(FileHelper.getNameFromUri(mContext, uri)).toLowerCase(
                Locale.getDefault());
        File f = StorageHelper.createExternalStoragePrivateFile(mContext, uri, extension);

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {

                if (f != null) {
                    String attachmentId = UUID.randomUUID().toString();
                    Uri fileUri = FileProvider.getUriForFile(ProntoDiaryApplication.getAppContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            f);
                    String filePath = f.getAbsolutePath();
                    String mimeType = StorageHelper.getMimeTypeInternal(mContext, uri);


                    AttachmentEntity attachment = backgroundRealm.createObject(AttachmentEntity.class, attachmentId);
                    attachment.setUri(fileUri.toString());
                    attachment.setLocalFilePath(filePath);
                    attachment.setMime_type(mimeType);
                    attachment.setName(name);
                    attachment.setSize(f.length());

                    NoteEntity note = backgroundRealm.where(NoteEntity.class).equalTo("id", noteId).findFirst();
                    if (note != null && attachment != null){
                        note.getAttachments().add(attachment);
                        note.setDateModified(System.currentTimeMillis());
                    }
                }
    ;
            }
        });

    }


}
