package com.okason.diary.models.dto;

import com.okason.diary.models.ProntoTag;

/**
 * Created by Valentine on 4/10/2017.
 */

public class TagDto{

    private String id;
    private String tagName;
    private long dateCreated;
    private long dateModified;


    public TagDto(){
        dateCreated = System.currentTimeMillis();
        dateModified = System.currentTimeMillis();

    }

    public TagDto(ProntoTag prontoTag){
        this.id = prontoTag.getId();
        this.tagName = prontoTag.getTagName();
        this.dateCreated = prontoTag.getDateCreated();
        this.dateModified = prontoTag.getDateModified();

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




}