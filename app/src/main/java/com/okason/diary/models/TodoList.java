package com.okason.diary.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by valokafor on 6/26/17.
 */

public class TodoList extends RealmObject {

    @PrimaryKey
    private String id;
    private String title;
    private String description;
    private long dateCreated;
    private long dateModified;

    private RealmList<Task> taskList;
    private RealmList<History> historyList;

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

    public RealmList<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(RealmList<Task> taskList) {
        this.taskList = taskList;
    }

    public RealmList<History> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(RealmList<History> historyList) {
        this.historyList = historyList;
    }
}
