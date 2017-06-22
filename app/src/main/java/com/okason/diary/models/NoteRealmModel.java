package com.okason.diary.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by valokafor on 6/21/17.
 */

public class NoteRealmModel extends RealmObject {
    @PrimaryKey
    private String id;
    private String title;
    private String content;
    private long dateCreated;
    private long dateModified;


    public NoteRealmModel(){

        dateCreated = System.currentTimeMillis();
        dateModified = System.currentTimeMillis();
    }


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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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


}
