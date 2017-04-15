package com.okason.prontodiary.models;

import io.realm.RealmObject;

/**
 * Created by Valentine on 4/14/2017.
 */

public class History extends RealmObject{
    private long id;
    private String title;
    private long dateAdded;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }
}
