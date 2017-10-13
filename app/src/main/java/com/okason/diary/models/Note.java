package com.okason.diary.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Valentine on 4/10/2017.
 */

public class Note {

    private String id;
    private String title;
    private String content;
    private long dateCreated;
    private long dateModified;
    private List<Attachment> attachments;
    private List<Tag> tags;
    private String folderId;
    private String folderName;
    private Folder folder;
    private Map<String, Boolean> filterTags;


    public Note(){
        attachments = new ArrayList<>();
        tags = new ArrayList<>();
        dateCreated = System.currentTimeMillis();
        dateModified = System.currentTimeMillis();
        filterTags = new HashMap<>();
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

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Map<String, Boolean> getFilterTags() {
        return filterTags;
    }

    public void setFilterTags(Map<String, Boolean> filterTags) {
        this.filterTags = filterTags;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }
}
