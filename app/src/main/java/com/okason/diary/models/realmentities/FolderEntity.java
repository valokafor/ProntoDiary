package com.okason.diary.models.realmentities;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valentine on 4/10/2017.
 */

public class FolderEntity extends RealmObject {

    @PrimaryKey
    private String id;
    private String folderName;
    private long dateCreated;
    private long dateModified;

    private RealmList<NoteEntity> notes;
    private RealmList<TaskEntity> todoLists;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public RealmList<TaskEntity> getTodoLists() {
        return todoLists;
    }

    public void setTodoLists(RealmList<TaskEntity> todoLists) {
        this.todoLists = todoLists;
    }

    public RealmList<NoteEntity> getNoteEntitys() {
        return notes;
    }

    public void setNoteEntitys(RealmList<NoteEntity> notes) {
        this.notes = notes;
    }
}