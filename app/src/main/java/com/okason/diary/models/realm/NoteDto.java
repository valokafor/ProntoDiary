package com.okason.diary.models.realm;

import com.okason.diary.models.viewModel.AttachmentViewModel;
import com.okason.diary.models.viewModel.FolderViewModel;
import com.okason.diary.models.viewModel.PeopleJournal;
import com.okason.diary.models.viewModel.TagViewModel;
import com.okason.diary.models.viewModel.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valentine on 4/10/2017.
 */

public class NoteDto {
    private String id;
    private String title;
    private String content;
    private String gratitude;
    private long dateCreated;
    private long dateModified;


    public NoteDto(){
        attachments = new ArrayList<>();
        taskViewModels = new ArrayList<>();
        tags = new ArrayList<>();
        peopleJournals = new ArrayList<>();

    }



    //Relationships
    private List<AttachmentViewModel> attachments;
    private List<TaskViewModel> taskViewModels;
    private List<TagViewModel> tags;
    private FolderViewModel category;
    private List<PeopleJournal> peopleJournals;



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

    public String getGratitude() {
        return gratitude;
    }

    public void setGratitude(String gratitude) {
        this.gratitude = gratitude;
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

    public List<AttachmentViewModel> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentViewModel> attachments) {
        this.attachments = attachments;
    }

    public List<TaskViewModel> getTaskViewModels() {
        return taskViewModels;
    }

    public void setTaskViewModels(List<TaskViewModel> taskViewModels) {
        this.taskViewModels = taskViewModels;
    }

    public List<TagViewModel> getTags() {
        return tags;
    }

    public void setTags(List<TagViewModel> tags) {
        this.tags = tags;
    }

    public FolderViewModel getCategory() {
        return category;
    }

    public void setCategory(FolderViewModel category) {
        this.category = category;
    }

    public List<PeopleJournal> getPeopleJournals() {
        return peopleJournals;
    }

    public void setPeopleJournals(List<PeopleJournal> peopleJournals) {
        this.peopleJournals = peopleJournals;
    }
}
