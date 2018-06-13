package com.okason.diary.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Reminder extends RealmObject {

    @PrimaryKey
    private int id;
    private ProntoTask parentProntoTask;
    private long dateAndTime;
    private int repeatType;
    private boolean indefinite;
    private int interval;
    private int numberToShow;
    private int numberShown;
    private long dateCreated;
    private long dateModified;
    private String daysOfWeek;

    public Reminder(){

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProntoTask getParentProntoTask() {
        return parentProntoTask;
    }

    public void setParentProntoTask(ProntoTask parentProntoTask) {
        this.parentProntoTask = parentProntoTask;
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