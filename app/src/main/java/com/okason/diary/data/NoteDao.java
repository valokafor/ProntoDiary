package com.okason.diary.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.okason.diary.BuildConfig;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.events.AttachingFileCompleteEvent;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Note;
import com.okason.diary.models.Tag;
import com.okason.diary.utils.FileHelper;
import com.okason.diary.utils.StorageHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by valokafor on 4/28/18.
 */

public class NoteDao {
    private Realm realm;
    private final static String TAG = "NoteDao";

    public NoteDao(Realm realm) {
        this.realm = realm;
    }

    public RealmResults<Note> getAllNotes(String tagName) {
        RealmResults<Note> notes;
        if (TextUtils.isEmpty(tagName)) {
            notes = realm.where(Note.class).findAll();
        } else {
            notes = realm.where(Note.class).equalTo("tags.tagName", tagName).findAll();
        }

        for (Note note: notes){
            if (note != null && TextUtils.isEmpty(note.getContent())
                    && TextUtils.isEmpty(note.getTitle())){
                deleteNote(note.getId());
            }
        }
        return notes;
    }


    public int getNoteEntityPosition(String noteId) {
        RealmResults<Note> notes = realm.where(Note.class).findAll();
        for (int i = 0; i < notes.size(); i++){
            if (notes.get(i).getId().equals(noteId)){
                return i;
            }
        }
        return -1;
    }

    public Note getNoteEntityById(String noteId) {
        try {
            Note selectedNote = realm.where(Note.class).equalTo("id", noteId).findFirst();
            return selectedNote;
        } catch (Exception e) {
            return null;
        }
    }

    public Note copyOrUpdate(Note note){
        try {
            realm.beginTransaction();
            note = realm.copyToRealmOrUpdate(note);
            realm.commitTransaction();
        } catch (Exception exception) {
            realm.cancelTransaction();
            throw exception;
        }
        return note;
    }


    public void deleteNote(String noteId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                Note note = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
                if (note != null){
                    note.deleteFromRealm();
                }
            }
        });


    }

    public Note createNewNote() {
        String noteId = UUID.randomUUID().toString();
        realm.beginTransaction();
        Note note = realm.createObject(Note.class, noteId);
        note.setDateCreated(System.currentTimeMillis());
        note.setDateModified(System.currentTimeMillis());
        realm.commitTransaction();
        return note;
    }


    public void setFolder(String noteId, String folderId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                Note note = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
                Folder folder = backgroundRealm.where(Folder.class).equalTo("id", folderId).findFirst();
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
                Attachment attachment = backgroundRealm.createObject(Attachment.class, attachmentId);
                attachment.setUri(attachmentUri.toString());
                attachment.setLocalFilePath(filPath);
                attachment.setMime_type(mimeType);

                Note note = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
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
                Note note = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
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


                    Attachment attachment = backgroundRealm.createObject(Attachment.class, attachmentId);
                    attachment.setUri(fileUri.toString());
                    attachment.setLocalFilePath(filePath);
                    attachment.setMime_type(mimeType);
                    attachment.setName(name);
                    attachment.setSize(f.length());

                    Note note = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
                    if (note != null && attachment != null){
                        note.getAttachments().add(attachment);
                        note.setDateModified(System.currentTimeMillis());
                        EventBus.getDefault().post(new AttachingFileCompleteEvent(attachment.getId()));
                    }
                }
    ;
            }
        });

    }

    public void addTag(final String noteId, final String tagId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                Note selectedNote = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
                Tag selectedTag = backgroundRealm.where(Tag.class).equalTo("id", tagId).findFirst();

                if (noteContainsTag(selectedNote, selectedTag)){
                    return;
                }

                if (selectedNote != null && selectedTag != null){
                    //Add Tag to Note
                    selectedNote.getTags().add(selectedTag);
                    selectedTag.getNotes().add(selectedNote);
                }

            }
        });

    }

    //Preventds adding duplicate tag to Note
    private boolean noteContainsTag(Note selectedNote, Tag selectedTag) {
        List<Tag> tags = selectedNote.getTags();
        for (Tag t: tags){
            if (t.getId().equals(selectedTag.getId())){
                return true;
            }
        }
        return false;
    }

    public void removeTag(final String noteId, final String tagId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                Note selectedNote = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
                Tag selectedTag = backgroundRealm.where(Tag.class).equalTo("id", tagId).findFirst();
                selectedNote.getTags().remove(selectedTag);
                selectedTag.getNotes().remove(selectedNote);
            }
        });
    }

    public List<Note> filterNotes(String query, String tagName) {
        List<Note> journals = new ArrayList<>();
        for (Note journal: getAllNotes(tagName)){
            String title = journal.getTitle().toLowerCase();
            String content = journal.getContent().toLowerCase();
            query = query.toLowerCase();
            if (title.contains(query) || content.contains(query)){
                journals.add(journal);
            }
        }
        return journals;
    }


    public Attachment getAttachmentById(String attachmentId) {
        try {
            Attachment attachment = realm.where(Attachment.class).equalTo("id", attachmentId).findFirst();
            return attachment;
        } catch (Exception e) {
            return null;
        }
    }

    public void updateAttachmentUrl(String attachmentId, String downloadUrl) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Attachment attachment = getAttachmentById(attachmentId);
                attachment.setCloudFilePath(downloadUrl);
            }
        });
    }

    public RealmResults<Note> getNotesByFolder(String folderId) {
        RealmResults<Note> notes = realm.where(Note.class).equalTo("folder.id", folderId).findAll();
        return notes;
    }
}
