package com.okason.diary.models.dto;

import com.okason.diary.models.ProntoTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valentine on 4/10/2017.
 */

public class ProntoTagDto {

    private String id;
    private String tagName;
    private long dateCreated;
    private long dateModified;
    private List<String> journalIds;
    private List<String> taskIds;



    public ProntoTagDto(){
        dateCreated = System.currentTimeMillis();
        dateModified = System.currentTimeMillis();

    }

    public ProntoTagDto(ProntoTag prontoTag){
        this.id = prontoTag.getId();
        this.tagName = prontoTag.getTagName();
        this.dateCreated = prontoTag.getDateCreated();
        this.dateModified = prontoTag.getDateModified();
        journalIds = new ArrayList<>();
        taskIds = new ArrayList<>();

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

    public List<String> getJournalIds() {
        return journalIds;
    }

    public void setJournalIds(List<String> journalIds) {
        this.journalIds = journalIds;
    }

    public List<String> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<String> taskIds) {
        this.taskIds = taskIds;
    }
}