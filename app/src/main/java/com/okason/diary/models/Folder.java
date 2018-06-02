package com.okason.diary.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valentine on 4/10/2017.
 */

public class Folder extends RealmObject {

    @PrimaryKey
    private String id;
    private String folderName;
    private long dateCreated;
    private long dateModified;

    private RealmList<Journal> journals;
    private RealmList<ProntoTask> todoLists;

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

    public RealmList<ProntoTask> getTodoLists() {
        return todoLists;
    }

    public void setTodoLists(RealmList<ProntoTask> todoLists) {
        this.todoLists = todoLists;
    }

    public RealmList<Journal> getNoteEntitys() {
        return journals;
    }

    public void setNoteEntitys(RealmList<Journal> journals) {
        this.journals = journals;
    }
}