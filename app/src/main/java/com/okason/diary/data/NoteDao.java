package com.okason.diary.data;

import com.okason.diary.models.realmentities.NoteEntity;

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
        NoteEntity noteEntity = getNoteEntityById(noteId);
        if (noteEntity != null);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                noteEntity.deleteFromRealm();
            }
        });


    }
}
