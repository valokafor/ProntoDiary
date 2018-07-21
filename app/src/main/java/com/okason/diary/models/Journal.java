package com.okason.diary.models;

import com.okason.diary.models.dto.JournalDto;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by valokafor on 4/28/18.
 */

public class Journal extends RealmObject{

    @PrimaryKey
    private String id;
    private String title;
    private String content;
    private long dateCreated;
    private long dateModified;


    public Journal(){
        dateCreated = System.currentTimeMillis();
        dateModified = System.currentTimeMillis();
    }

    public Journal(JournalDto journal){
        this.id = journal.getId();
        this.title = journal.getTitle();
        this.content = journal.getContent();
        this.dateCreated = journal.getDateCreated();
        this.dateModified = journal.getDateModified();
    }


    //Relationships
    private RealmList<Attachment> attachments;
    private RealmList<ProntoTag> tags;
    private Folder folder;




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

    public RealmList<ProntoTag> getTags() {
        return tags;
    }

    public void setTags(RealmList<ProntoTag> prontoTags) {
        this.tags = prontoTags;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }


}
