package com.okason.diary.models.realmentities;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valentine on 4/10/2017.
 */

public class TagEntity extends RealmObject {

    @PrimaryKey
    private String id;
    private String tagName;
    private long dateCreated;
    private long dateModified;

    private RealmList<NoteEntity> notes;
    private RealmList<TaskEntity> tasks;

    public TagEntity(){
        dateCreated = System.currentTimeMillis();
        dateModified = System.currentTimeMillis();

    }

    public TagEntity(String name){
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

    public RealmList<NoteEntity> getNotes() {
        return notes;
    }

    public void setNotes(RealmList<NoteEntity> notes) {
        this.notes = notes;
    }

    public RealmList<TaskEntity> getTasks() {
        return tasks;
    }

    public void setTasks(RealmList<TaskEntity> tasks) {
        this.tasks = tasks;
    }
}