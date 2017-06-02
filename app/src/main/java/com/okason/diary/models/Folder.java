package com.okason.diary.models;

import java.util.List;

/**
 * Created by Valentine on 4/10/2017.
 */

public class Folder{

    private String id;
    private String folderName;
    private long dateCreated;
    private long dateModified;

    private List<Note> notes;
    private List<Task> todoLists;

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

    public List<Task> getTodoLists() {
        return todoLists;
    }

    public void setTodoLists(List<Task> todoLists) {
        this.todoLists = todoLists;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
