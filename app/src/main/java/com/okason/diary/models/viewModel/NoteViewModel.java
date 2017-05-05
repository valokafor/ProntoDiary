package com.okason.diary.models.viewModel;

import com.okason.diary.models.Category;
import com.okason.diary.models.Friend;
import com.okason.diary.models.TodoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valentine on 4/10/2017.
 */

public class NoteViewModel {
    private String id;
    private String title;
    private String content;
    private String gratitude;
    private long dateCreated;
    private long dateModified;


    public NoteViewModel(){

        attachments = new ArrayList<>();
        todoItems = new ArrayList<>();
        tags = new ArrayList<>();
        friends = new ArrayList<>();

    }



    

    //Relationships
    private List<AttachmentViewModel> attachments;
    private List<TodoItem> todoItems;
    private List<TagViewModel> tags;
    private Category category;
    private List<Friend> friends;



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

    public List<TodoItem> getTodoItems() {
        return todoItems;
    }

    public void setTodoItems(List<TodoItem> todoItems) {
        this.todoItems = todoItems;
    }

    public List<TagViewModel> getTags() {
        return tags;
    }

    public void setTags(List<TagViewModel> tags) {
        this.tags = tags;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }
}
