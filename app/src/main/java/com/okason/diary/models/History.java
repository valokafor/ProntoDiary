package com.okason.diary.models;

/**
 * Created by Valentine on 4/14/2017.
 */

public class History{

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
