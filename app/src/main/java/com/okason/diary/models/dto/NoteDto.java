package com.okason.diary.models.dto;

/**
 * Created by valokafor on 4/28/18.
 */

public class NoteDto{

    private String id;
    private String title;
    private String content;
    private long dateCreated;
    private long dateModified;


    public NoteDto(){
        dateCreated = System.currentTimeMillis();
        dateModified = System.currentTimeMillis();
    }






}
