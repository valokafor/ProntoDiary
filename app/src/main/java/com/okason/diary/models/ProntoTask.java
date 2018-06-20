package com.okason.diary.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valentine on 4/10/2017.
 * This model class is used to work with Tasks
 * Renamed to ProntoTask to avoid name collision with framework Task
 */

public class ProntoTask extends RealmObject{

    @PrimaryKey
    private String id;
    private String title;
    private String description;
    private long dateCreated;
    private long dateModified;
    private boolean checked;
    private int priority;
    private Double latitude;
    private Double longitude;


    private Folder folder;
    private Reminder reminder;
    private Location location;
    private RealmList<ProntoTag> tags;
    private RealmList<SubTask> subTask;

    public ProntoTask(){
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


    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public RealmList<ProntoTag> getTags() {
        return tags;
    }

    public void setTags(RealmList<ProntoTag> prontoTags) {
        this.tags = prontoTags;
    }

    public RealmList<SubTask> getSubTask() {
        return subTask;
    }

    public void setSubTask(RealmList<SubTask> subTask) {
        this.subTask = subTask;
    }

    public Reminder getReminder() {
        return reminder;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
