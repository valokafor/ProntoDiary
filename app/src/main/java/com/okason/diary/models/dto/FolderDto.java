package com.okason.diary.models.dto;

import com.okason.diary.models.Folder;

/**
 * Created by Valentine on 4/10/2017.
 */

public class FolderDto {


    private String id;
    private String folderName;
    private long dateCreated;
    private long dateModified;


    public FolderDto(){

    }

    public FolderDto(Folder folder){
        this.id = folder.getId();
        this.folderName = folder.getFolderName();
        this.dateCreated = folder.getDateCreated();
        this.dateModified = folder.getDateModified();
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


}