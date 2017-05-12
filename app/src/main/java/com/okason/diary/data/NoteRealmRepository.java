package com.okason.diary.data;

import com.okason.diary.models.Folder;
import com.okason.diary.models.Note;
import com.okason.diary.ui.notes.NoteListContract;

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
    public Note getNoteById(String noteId) {
        return null;
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
    public void deleteNote(final Note note) {
        try (Realm realm = Realm.getDefaultInstance()){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    backgroundRealm.where(Note.class).equalTo("id", note.getId()).findFirst().deleteFromRealm();
                }
            });
        }

    }

    @Override
    public void saveNote(Note note) {

    }


}
