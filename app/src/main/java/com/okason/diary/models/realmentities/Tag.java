package com.okason.diary.models.realmentities;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valentine on 4/10/2017.
 */

public class Tag extends RealmObject {

    @PrimaryKey
    private String id;
    private String tagName;
    private long dateCreated;
    private long dateModified;

    private RealmList<Note> notes;
    private RealmList<Task> tasks;

    public Tag(){
        dateCreated = System.currentTimeMillis();
        dateModified = System.currentTimeMillis();

    }

    public Tag(String name){
        tagName = name;
        dateCreated = System.currentTimeMillis();
        dateModified = System.currentTimeMillis();

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

    public RealmList<Note> getNotes() {
        return notes;
    }

    public void setNotes(RealmList<Note> notes) {
        this.notes = notes;
    }

    public RealmList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(RealmList<Task> tasks) {
        this.tasks = tasks;
    }
}