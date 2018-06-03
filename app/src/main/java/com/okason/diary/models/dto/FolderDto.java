package com.okason.diary.models.dto;

import com.okason.diary.models.Folder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valentine on 4/10/2017.
 * Model class for transferring Folder objects to and From Firebase
 */

public class FolderDto {


    private String id;
    private String folderName;
    private long dateCreated;
    private long dateModified;
    private List<String> journalIds;
    private List<String> taskIds;


    public FolderDto(){

    }

    public FolderDto(Folder folder){
        this.id = folder.getId();
        this.folderName = folder.getFolderName();
        this.dateCreated = folder.getDateCreated();
        this.dateModified = folder.getDateModified();
        journalIds = new ArrayList<>();
        taskIds = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
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