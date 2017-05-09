package com.okason.diary.models.viewModel;

import java.util.List;

/**
 * Created by Valentine on 4/10/2017.
 */

public class FolderViewModel {

    private String id;
    private String categoryName;
    private long dateCreated;
    private long dateModified;
    private List<NoteViewModel> notes;
    private List<TaskViewModel> todoLists;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public List<TaskViewModel> getTodoLists() {
        return todoLists;
    }

    public void setTodoLists(List<TaskViewModel> todoLists) {
        this.todoLists = todoLists;
    }

    public List<NoteViewModel> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteViewModel> notes) {
        this.notes = notes;
    }
}
