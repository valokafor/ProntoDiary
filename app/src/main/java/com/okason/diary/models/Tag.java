package com.okason.diary.models;

import java.util.List;

/**
 * Created by Valentine on 4/10/2017.
 */

public class Tag{

    private String id;
    private String tagName;
    private long dateCreated;
    private long dateModified;

    private List<Note> notes;
    private List<Task> todoLists;

    public Tag(){

    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
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

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public List<Task> getTodoLists() {
        return todoLists;
    }

    public void setTodoLists(List<Task> todoLists) {
        this.todoLists = todoLists;
    }
}
