package com.okason.diary.data;

import com.okason.diary.core.events.DatabaseOperationCompletedEvent;
import com.okason.diary.core.events.ItemDeletedEvent;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Note;
import com.okason.diary.ui.notes.NoteListContract;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Valentine on 4/26/2017.
 */

public class NoteRealmRepository implements NoteListContract.Repository{
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


}
