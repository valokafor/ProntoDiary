package com.okason.diary.models.dto;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.okason.diary.models.Reminder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Class used to transfer Reminder Objects to Firebase
 */
public class ReminderDto{


    private int id;
    private String parentTaskId;
    private long dateAndTime;
    private int repeatType;
    private boolean indefinite;
    private int interval;
    private int numberToShow;
    private int numberShown;
    private long dateCreated;
    private long dateModified;
    private String daysOfWeek;

    public ReminderDto(){}


    public ReminderDto(Reminder reminder){
        this.id = reminder.getId();
        this.parentTaskId = reminder.getParentProntoTask().getId();
        this.dateAndTime = reminder.getDateAndTime();
        this.repeatType = reminder.getRepeatType();
        this.indefinite = reminder.isIndefinite();
        this.interval = reminder.getInterval();
        this.numberToShow = reminder.getNumberToShow();
        this.numberShown = reminder.getNumberShown();
        this.dateCreated = reminder.getDateCreated();
        this.dateModified = reminder.getDateModified();
        this.daysOfWeek = reminder.getDaysOfWeek();

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
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

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
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

    public List<Boolean> getDaysOfWeekList(){
        List<Boolean> daysOfWeekList = new ArrayList<>();
        Gson gson = new Gson();
        String daysOfWeekString = getDaysOfWeek();
        Type collectionType = new TypeToken<List<Boolean>>(){}.getType();
        daysOfWeekList= (List<Boolean>) gson.fromJson( daysOfWeekString , collectionType);
        return daysOfWeekList;
    }
}