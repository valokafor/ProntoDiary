package com.okason.diary.models.viewModel;

import com.okason.diary.models.Attachment;
import com.okason.diary.models.Category;
import com.okason.diary.models.Friend;
import com.okason.diary.models.Tag;
import com.okason.diary.models.TodoItem;

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
    

    //Relationships
    private List<Attachment> attachments;
    private List<TodoItem> todoItems;
    private List<Tag> tags;
    private Category category;
    private List<Friend> friends;

    public void update(NoteViewModel copy){
        title = copy.getTitle();
        content = copy.getContent();
        gratitude = copy.getGratitude();
        dateCreated = dateCreated;
        dateModified = dateModified;

        attachments = copy.getAttachments();
        todoItems = copy.getTodoItems();
        tags = copy.getTags();
        category = copy.getCategory();
        friends = copy.getFriends();
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

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<TodoItem> getTodoItems() {
        return todoItems;
    }

    public void setTodoItems(List<TodoItem> todoItems) {
        this.todoItems = todoItems;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
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
