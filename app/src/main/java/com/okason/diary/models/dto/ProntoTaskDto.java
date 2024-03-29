package com.okason.diary.models.dto;

import com.okason.diary.models.ProntoTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by valokafor on 6/1/18.
 */

public class ProntoTaskDto {

    private String id;
    private String title;
    private String description;
    private long dateCreated;
    private long dateModified;
    private boolean checked;
    private int priority;


    private FolderDto folder;
    private ReminderDto reminder;
    private List<ProntoTagDto> tags;
    private List<SubTaskDto> subTask;

    public ProntoTaskDto(){
        tags = new ArrayList<>();
        subTask = new ArrayList<>();
        dateCreated = System.currentTimeMillis();
    }

    public ProntoTaskDto(ProntoTask task){
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.dateCreated = task.getDateCreated();
        this.dateModified = task.getDateModified();
        this.checked = task.isChecked();
        this.priority = task.getPriority();
        this.tags = new ArrayList<>();
        this.subTask = new ArrayList<>();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public FolderDto getFolder() {
        return folder;
    }

    public void setFolder(FolderDto folder) {
        this.folder = folder;
    }

    public ReminderDto getReminder() {
        return reminder;
    }

    public void setReminder(ReminderDto reminder) {
        this.reminder = reminder;
    }

    public List<ProntoTagDto> getTags() {
        return tags;
    }

    public void setTags(List<ProntoTagDto> tags) {
        this.tags = tags;
    }

    public List<SubTaskDto> getSubTask() {
        return subTask;
    }

    public void setSubTask(List<SubTaskDto> subTask) {
        this.subTask = subTask;
    }
}
