package com.okason.diary.models;

import java.util.List;

/**
 * Created by Valentine on 4/10/2017.
 */

public class Folder{

    private String id;
    private String folderName;
    private long dateCreated;
    private long dateModified;
    private List<String> notesIds;
    private List<String> taskIds;

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

    public List<String> getNotesIds() {
        return notesIds;
    }

    public void setNotesIds(List<String> notesIds) {
        this.notesIds = notesIds;
    }

    public List<String> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<String> taskIds) {
        this.taskIds = taskIds;
    }
}
