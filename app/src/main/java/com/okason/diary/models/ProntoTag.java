package com.okason.diary.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valentine on 4/10/2017.
 */

public class ProntoTag extends RealmObject {

    @PrimaryKey
    private String id;
    private String tagName;
    private long dateCreated;
    private long dateModified;

    private RealmList<Journal> journals;
    private RealmList<ProntoTask> prontoTasks;

    public ProntoTag(){
        dateCreated = System.currentTimeMillis();
        dateModified = System.currentTimeMillis();

    }

    public ProntoTag(String name){
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

    public RealmList<Journal> getJournals() {
        return journals;
    }

    public void setJournals(RealmList<Journal> journals) {
        this.journals = journals;
    }

    public RealmList<ProntoTask> getTasks() {
        return prontoTasks;
    }

    public void setTasks(RealmList<ProntoTask> prontoTasks) {
        this.prontoTasks = prontoTasks;
    }
}