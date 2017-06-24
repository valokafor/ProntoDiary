package com.okason.diary.data;

import android.net.Uri;

import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.events.DatabaseOperationCompletedEvent;
import com.okason.diary.core.events.ItemDeletedEvent;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Note;
import com.okason.diary.models.Tag;
import com.okason.diary.ui.addnote.AddNoteContract;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.StorageHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by valokafor on 6/22/17.
 */

public class NoteRealmRepository implements AddNoteContract.Repository {
    @Override
    public List<Note> getAllNotes() {
        List<Note> viewModels = new ArrayList<>();
        try(Realm realm = Realm.getDefaultInstance()) {
            RealmResults<Note> notes = realm.where(Note.class).findAll();
            if (notes != null && notes.size() > 0){
                viewModels = realm.copyFromRealm(notes);
            }
        }
        return viewModels;
    }

    @Override
    public List<Tag> getAllTags() {
        List<Tag> viewModels = new ArrayList<>();
        try(Realm realm = Realm.getDefaultInstance()) {
            RealmResults<Tag> tags = realm.where(Tag.class).findAll();
            if (tags != null && tags.size() > 0){
                viewModels = realm.copyFromRealm(tags);
            }
        }
        return viewModels;
    }



    @Override
    public int getNotePosition(String noteId) {
        List<Note> noteList = getAllNotes();
        for (int i = 0; i < noteList.size(); i++){
            if (noteList.get(i).getId().equals(noteId)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public Note getNoteById(String noteId) {
        Note selectedNote;
        try (Realm realm = Realm.getDefaultInstance()){
            selectedNote = realm.where(Note.class).equalTo("id", noteId).findFirst();
            selectedNote = realm.copyFromRealm(selectedNote);
        }catch (Exception e){
            selectedNote = null;
        }
        return selectedNote;
    }

    @Override
    public Note createNewNote() {
        String noteId = UUID.randomUUID().toString();
        Note note;
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            note = realm.createObject(Note.class, noteId);
            note.setDateCreated(System.currentTimeMillis());
            note.setDateModified(System.currentTimeMillis());
            realm.commitTransaction();
            note = realm.copyFromRealm(note);
        }
        return note;
    }

    @Override
    public void updatedNoteTitle(final String noteId, final String title) {
        try (Realm realm = Realm.getDefaultInstance()){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    Note selectedNote = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
                    if (selectedNote != null){
                        selectedNote.setTitle(title);
                        selectedNote.setDateModified(System.currentTimeMillis());
                    }
                }
            });
        }


    }

    @Override
    public void updatedNoteContent(final String noteId, final String content) {
        try (Realm realm = Realm.getDefaultInstance()){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    Note selectedNote = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
                    if (selectedNote != null){
                        selectedNote.setContent(content);
                        selectedNote.setDateModified(System.currentTimeMillis());
                    }
                }
            });
        }

    }

    @Override
    public void setFolder(final String folderId, final String noteId) {
        try (Realm realm = Realm.getDefaultInstance()){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    Note selectedNote = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
                    Folder selectedFolder = backgroundRealm.where(Folder.class).equalTo("id", folderId).findFirst();

                    if (selectedNote  != null && selectedFolder != null){
                        //Set folder
                        selectedNote.setFolder(selectedFolder);

                        //Check to see if Folder already contains the Note
                        if (!selectedFolder.getNotes().contains(selectedNote)){
                            selectedFolder.getNotes().add(selectedNote);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void addTag(final String noteId, final String tagId) {
        try (Realm realm = Realm.getDefaultInstance()){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    Note selectedNote = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
                    Tag selectedTag = backgroundRealm.where(Tag.class).equalTo("id", tagId).findFirst();

                    if (selectedNote != null && selectedTag != null){
                        //Add Tag to Note
                        selectedNote.getTags().add(selectedTag);
                        selectedTag.getNotes().add(selectedNote);
                    }

                }
            });

        }

    }

    @Override
    public void removeTag(final String noteId, final String tagId) {
        try (Realm realm = Realm.getDefaultInstance()){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    Note selectedNote = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
                    Tag selectedTag = backgroundRealm.where(Tag.class).equalTo("id", tagId).findFirst();

                    if (selectedNote != null && selectedTag != null){
                        //Remove Tag from Note
                        for (int i = 0; i<selectedNote.getTags().size(); i++){
                            Tag tempTag = selectedNote.getTags().get(i);
                            if (tempTag.getId().equals(tagId)){
                                selectedNote.getTags().remove(i);
                                break;
                            }
                        }
                        //Now Remove Note Id from Tag List of Note Ids
                        selectedNote.getTags().add(selectedTag);
                        for (int i = 0; i<selectedTag.getNotes().size(); i++){
                            String noteId = selectedTag.getNotes().get(i).getId();
                            if (noteId.equals(noteId)){
                                selectedTag.getNotes().remove(i);
                                break;
                            }
                        }
                    }

                }
            });

        }
        //Remove this tag from the list of Tags for the Note

    }

    @Override
    public void deleteNote(final String noteId) {
        String result;
        try (Realm realm = Realm.getDefaultInstance()){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst().deleteFromRealm();
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    EventBus.getDefault().post(new ItemDeletedEvent(Constants.RESULT_OK));
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    EventBus.getDefault().post(new ItemDeletedEvent(error.getLocalizedMessage()));
                }
            });
        }

    }

    @Override
    public void saveNote(Note note) {

    }

    @Override
    public Attachment getAttachmentbyId(String id) {
        Attachment attachment;
        try (Realm realm = Realm.getDefaultInstance()){
            attachment = realm.where(Attachment.class).equalTo("id", id).findFirst();
        }catch (Exception e){
            attachment = null;
        }
        return attachment;
    }

    @Override
    public void addAttachment(final String noteId, final Attachment attachment) {
        final String attachmentId = UUID.randomUUID().toString();

        try (Realm realm = Realm.getDefaultInstance()){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    Attachment savedAttachment = backgroundRealm.createObject(Attachment.class, attachmentId);
                    savedAttachment.update(attachment);

                    Note parentNote = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
                    parentNote.getAttachments().add(savedAttachment);

                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    EventBus.getDefault().post(new DatabaseOperationCompletedEvent(true, ""));
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    EventBus.getDefault().post(new DatabaseOperationCompletedEvent(false, error.getLocalizedMessage()));
                }
            });

        }
    }

    @Override
    public void addFileAttachment(final Uri uri, String filename, final String noteId) {

        final String attachmentId = UUID.randomUUID().toString();

        try (Realm realm = Realm.getDefaultInstance()){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    Attachment attachment = StorageHelper.createAttachmentFromUri(ProntoDiaryApplication.getAppContext(), uri);

                    Attachment savedAttachment = backgroundRealm.createObject(Attachment.class, attachmentId);
                    savedAttachment.update(attachment);

                    Note parentNote = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
                    parentNote.getAttachments().add(savedAttachment);

                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    EventBus.getDefault().post(new DatabaseOperationCompletedEvent(true, ""));
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    EventBus.getDefault().post(new DatabaseOperationCompletedEvent(false, error.getLocalizedMessage()));
                }
            });

        }

    }

}
