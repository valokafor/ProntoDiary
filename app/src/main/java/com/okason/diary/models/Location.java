package com.okason.diary.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Location extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String address;
    private long dateCreated;
    private long dateModified;

    private RealmList<Journal> journals;
    private RealmList<ProntoTask> tasks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        return tasks;
    }

    public void setTasks(RealmList<ProntoTask> tasks) {
        this.tasks = tasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

