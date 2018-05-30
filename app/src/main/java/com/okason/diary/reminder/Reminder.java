package com.okason.diary.reminder;

import com.okason.diary.models.Task;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Reminder extends RealmObject {

    @PrimaryKey
    private String id;
    private Task parentTask;
    private long dateAndTime;
    private int repeatType;
    private boolean indefinite;
    private int interval;
    private int numberToShow;
    private int numberShown;
    private long dateCreated;
    private long dateModified;
    private RealmList<Boolean> daysOfWeek;

    public Reminder(){}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }

    public long getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(long dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public int getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }

    public boolean isIndefinite() {
        return indefinite;
    }

    public void setIndefinite(boolean indefinite) {
        this.indefinite = indefinite;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getNumberToShow() {
        return numberToShow;
    }

    public void setNumberToShow(int numberToShow) {
        this.numberToShow = numberToShow;
    }

    public int getNumberShown() {
        return numberShown;
    }

    public void setNumberShown(int numberShown) {
        this.numberShown = numberShown;
    }

    public RealmList<Boolean> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(RealmList<Boolean> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
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