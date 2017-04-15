package com.okason.prontodiary.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valentine on 4/10/2017.
 */

public class Tag extends RealmObject {
    @PrimaryKey
    private long id;
    private String tagName;
    private long dateCreated;
    private long dateModified;
    private RealmList<Note> notes;
    private RealmList<TodoItem> todoLists;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public RealmList<Note> getNotes() {
        return notes;
    }

    public void setNotes(RealmList<Note> notes) {
        this.notes = notes;
    }

    public RealmList<TodoItem> getTodoLists() {
        return todoLists;
    }

    public void setTodoLists(RealmList<TodoItem> todoLists) {
        this.todoLists = todoLists;
    }
}
