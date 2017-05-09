package com.okason.diary.models.realm;

import com.okason.diary.models.viewModel.FolderViewModel;
import com.okason.diary.models.viewModel.HistoryViewModel;

import java.util.List;

/**
 * Created by Valentine on 4/10/2017.
 */

public class TaskDto {

    private long id;
    private String title;
    private String description;
    private FolderViewModel category;
    private long dateCreated;
    private long duetime;
    private long dateModified;
    private int priority;
    private String repeat;
    private boolean remind;
    private boolean complete;
    private int status;
    private String recurrenceRule;
    private boolean checked;

    private List<TaskDto> tasks;
    private List<HistoryViewModel> historyList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public FolderViewModel getCategory() {
        return category;
    }

    public void setCategory(FolderViewModel category) {
        this.category = category;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getDuetime() {
        return duetime;
    }

    public void setDuetime(long duetime) {
        this.duetime = duetime;
    }

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public boolean isRemind() {
        return remind;
    }

    public void setRemind(boolean remind) {
        this.remind = remind;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public void setRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public List<TaskDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDto> tasks) {
        this.tasks = tasks;
    }

    public List<HistoryViewModel> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<HistoryViewModel> historyList) {
        this.historyList = historyList;
    }
}
