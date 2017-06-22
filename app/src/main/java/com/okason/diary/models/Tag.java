package com.okason.diary.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valentine on 4/10/2017.
 */

public class Tag{

    private String id;
    private String tagName;
    private long dateCreated;
    private long dateModified;

    private List<String> listOfNoteIds;
    private List<Task> todoLists;

    public Tag(){
        listOfNoteIds = new ArrayList<>();
        dateCreated = System.currentTimeMillis();
        dateModified = System.currentTimeMillis();

    }

    public Tag(String name){
        tagName = name;
        listOfNoteIds = new ArrayList<>();
        dateCreated = System.currentTimeMillis();
        dateModified = System.currentTimeMillis();

    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
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

    public List<String> getListOfNoteIds() {
        return listOfNoteIds;
    }

    public void setListOfNoteIds(List<String> listOfNoteIds) {
        this.listOfNoteIds = listOfNoteIds;
    }

    public List<Task> getTodoLists() {
        return todoLists;
    }

    public void setTodoLists(List<Task> todoLists) {
        this.todoLists = todoLists;
    }
}
