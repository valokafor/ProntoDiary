package com.okason.diary.models.dto;

import com.okason.diary.models.SubTask;

/**
 * Created by valokafor on 5/31/18.
 * Class used to transfer SubTasks to and from Firebase
 * Contains the Id of the Parent Class instead of the Parent class object
 */

public class SubTaskDto {
    private String id;
    private String title;
    private long dateCreated;
    private long dateModified;
    private boolean checked;
    private String parentTaskId;

    public SubTaskDto(){}

    public SubTaskDto(SubTask subTask){
        this.id = subTask.getId();
        this.title = subTask.getTitle();
        this.dateCreated = subTask.getDateCreated();
        this.dateModified = subTask.getDateModified();
        this.checked = subTask.isChecked();
        this.parentTaskId = subTask.getTask().getId();
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

    public String getParentTaskName() {
        return parentTaskId;
    }

    public void setParentTaskName(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

}
