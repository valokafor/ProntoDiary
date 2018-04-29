package com.okason.diary.models.realmentities;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valentine on 4/10/2017.
 */

public class TaskEntity extends RealmObject{

    @PrimaryKey
    private String id;
    private String title;
    private String description;
    private long dateCreated;
    private long dateModified;
    private boolean checked;
    private int priority;


    private long dueDateAndTime;
    private long repeatEndDate;
    private String repeatFrequency;
    private Double latitude;
    private Double longitude;


    private FolderEntity folder;
    private RealmList<SubTaskEntity> subTask;

    public TaskEntity(){
        dateCreated = System.currentTimeMillis();
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

    public long getDueDateAndTime() {
        return dueDateAndTime;
    }

    public void setDueDateAndTime(long dueDateAndTime) {
        this.dueDateAndTime = dueDateAndTime;
    }

    public long getRepeatEndDate() {
        return repeatEndDate;
    }

    public void setRepeatEndDate(long repeatEndDate) {
        this.repeatEndDate = repeatEndDate;
    }

    public String getRepeatFrequency() {
        return repeatFrequency;
    }

    public void setRepeatFrequency(String repeatFrequency) {
        this.repeatFrequency = repeatFrequency;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


    public FolderEntity getFolder() {
        return folder;
    }

    public void setFolder(FolderEntity folder) {
        this.folder = folder;
    }

    public RealmList<SubTaskEntity> getSubTask() {
        return subTask;
    }

    public void setSubTask(RealmList<SubTaskEntity> subTask) {
        this.subTask = subTask;
    }
}
