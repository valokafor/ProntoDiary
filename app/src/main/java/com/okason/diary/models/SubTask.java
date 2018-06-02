package com.okason.diary.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by valokafor on 7/4/17.
 */

public class SubTask extends RealmObject{

    @PrimaryKey
    private String id;
    private String title;
    private long dateCreated;
    private long dateModified;
    private boolean checked;
    private ProntoTask parentProntoTask;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public ProntoTask getParentProntoTask() {
        return parentProntoTask;
    }

    public void setParentProntoTask(ProntoTask parentProntoTask) {
        this.parentProntoTask = parentProntoTask;
    }
}
