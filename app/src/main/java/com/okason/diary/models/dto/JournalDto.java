package com.okason.diary.models.dto;

import com.okason.diary.models.Journal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by valokafor on 4/28/18.
 */

public class JournalDto {

    private String id;
    private String title;
    private String content;
    private long dateCreated;
    private long dateModified;
    private List<AttachmentDto> attachments;
    private FolderDto folder;
    private List<TagDto> tags;


    public JournalDto(){
        attachments = new ArrayList<>();
        dateCreated = System.currentTimeMillis();
        dateModified = System.currentTimeMillis();
        tags = new ArrayList<>();
        attachments = new ArrayList<>();
    }


    public JournalDto(Journal journal){
        this.id = journal.getId();
        this.title = journal.getTitle();
        this.content = journal.getContent();
        this.dateCreated = journal.getDateCreated();
        this.dateModified = journal.getDateModified();
        tags = new ArrayList<>();
        attachments = new ArrayList<>();

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

    public List<AttachmentDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDto> attachments) {
        this.attachments = attachments;
    }

    public FolderDto getFolder() {
        return folder;
    }

    public void setFolder(FolderDto folder) {
        this.folder = folder;
    }

    public List<TagDto> getTags() {
        return tags;
    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }
}
