package com.okason.diary.models;

import com.okason.diary.utils.reminder.Reminder;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valentine on 4/10/2017.
 */

public class Task extends RealmObject {

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
    private boolean remind;
    private boolean reminderFired;
    private String repeatFrequency;
    private Double latitude;
    private Double longitude;


    private RealmList<History> historyList;
    private Folder folder;
    private RealmList<SubTask> subTask;

    public int getTaskCount(){
        if (getSubTask() != null && getSubTask().size() > 0){
            return getSubTask().size() + 1;
        }else {
            return 1;
        }
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

    public boolean isReminderFired() {
        return reminderFired;
    }

    public void setReminderFired(boolean reminderFired) {
        this.reminderFired = reminderFired;
    }

    public boolean isRemind() {
        return remind;
    }

    public void setRemind(boolean remind) {
        this.remind = remind;
    }

    public Reminder getRepeatFrequency() {
        return Reminder.valueOf(repeatFrequency);
    }

    public void setRepeatFrequency(Reminder val) {
        this.repeatFrequency = val.toString();
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


    public RealmList<History> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(RealmList<History> historyList) {
        this.historyList = historyList;
    }

    public RealmList<SubTask> getSubTask() {
        return subTask;
    }

    public void setSubTask(RealmList<SubTask> subTask) {
        this.subTask = subTask;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }
}
