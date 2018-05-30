package com.okason.diary.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by valokafor on 4/28/18.
 */

public class Note extends RealmObject{

    @PrimaryKey
    private String id;
    private String title;
    private String content;
    private long dateCreated;
    private long dateModified;


    public Note(){
        dateCreated = System.currentTimeMillis();
        dateModified = System.currentTimeMillis();
    }


    //Relationships
    private RealmList<Attachment> attachments;
    private RealmList<Tag> tags;
    private Folder folder;


    private RealmList<Task> tasks;


    public void update(Note note){
        this.title = note.getTitle();
        this.content = note.getContent();
        this.dateCreated = note.getDateCreated();
        this.dateModified = note.getDateModified();
        this.attachments = note.getAttachments();
        this.tags = note.getTags();
        this.folder = note.getFolder();
        this.tasks = note.getTasks();

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

    public RealmList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(RealmList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public RealmList<Tag> getTags() {
        return tags;
    }

    public void setTags(RealmList<Tag> tags) {
        this.tags = tags;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public RealmList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(RealmList<Task> tasks) {
        this.tasks = tasks;
    }

}
