package com.okason.diary.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valentine on 4/10/2017.
 */

public class Note{
    private String id;
    private String title;
    private String content;
    private long dateCreated;
    private long dateModified;


    public Note(){
        attachments = new ArrayList<>();
        dateCreated = System.currentTimeMillis();
        dateModified = System.currentTimeMillis();
    }


   //Relationships
    private List<Attachment> attachments;
    private List<Task> tasks;
    private List<Tag> tags;
    private Folder folder;
    private List<PeopleJournal> peopleJournals;
    private List<History> historyList;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public List<PeopleJournal> getPeopleJournals() {
        return peopleJournals;
    }

    public void setPeopleJournals(List<PeopleJournal> peopleJournals) {
        this.peopleJournals = peopleJournals;
    }

    public List<History> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
    }
}
