package com.okason.diary.models.realmentities;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by valokafor on 4/28/18.
 */

public class NoteEntity extends RealmObject{

    @PrimaryKey
    private String id;
    private String title;
    private String content;
    private long dateCreated;
    private long dateModified;


    public NoteEntity(){
        dateCreated = System.currentTimeMillis();
        dateModified = System.currentTimeMillis();
    }


    //Relationships
    private RealmList<AttachmentEntity> attachments;
    private RealmList<TagEntity> tags;
    private FolderEntity folder;


    private RealmList<TaskEntity> tasks;
    private RealmList<PeopleJournalEntity> peopleJournals;
    private RealmList<HistoryEntity> historyList;

    public void update(NoteEntity note){
        this.title = note.getTitle();
        this.content = note.getContent();
        this.dateCreated = note.getDateCreated();
        this.dateModified = note.getDateModified();
        this.attachments = note.getAttachments();
        this.tags = note.getTags();
        this.folder = note.getFolder();
        this.tasks = note.getTasks();
        this.peopleJournals = note.getPeopleJournals();
        this.historyList = note.getHistoryList();
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

    public RealmList<AttachmentEntity> getAttachments() {
        return attachments;
    }

    public void setAttachments(RealmList<AttachmentEntity> attachments) {
        this.attachments = attachments;
    }

    public RealmList<TagEntity> getTags() {
        return tags;
    }

    public void setTags(RealmList<TagEntity> tags) {
        this.tags = tags;
    }

    public FolderEntity getFolder() {
        return folder;
    }

    public void setFolder(FolderEntity folder) {
        this.folder = folder;
    }

    public RealmList<TaskEntity> getTasks() {
        return tasks;
    }

    public void setTasks(RealmList<TaskEntity> tasks) {
        this.tasks = tasks;
    }

    public RealmList<PeopleJournalEntity> getPeopleJournals() {
        return peopleJournals;
    }

    public void setPeopleJournals(RealmList<PeopleJournalEntity> peopleJournals) {
        this.peopleJournals = peopleJournals;
    }

    public RealmList<HistoryEntity> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(RealmList<HistoryEntity> historyList) {
        this.historyList = historyList;
    }
}
