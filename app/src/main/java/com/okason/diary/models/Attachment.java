package com.okason.diary.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valentine on 4/10/2017.
 */

public class Attachment extends RealmObject{

    @PrimaryKey
    private String id;
    private String filePath;
    private String name;
    private String comment;
    private long size;
    private long length;
    private String mime_type;
    private long dateCreated;


    public Attachment(){
        filePath = "";
        name = "";
        comment = "";
        size = 0;
        length = 0;
        mime_type = "";
        dateCreated = System.currentTimeMillis();

    }

    public Attachment(String imagePath, String mime_type) {
        this.filePath = imagePath;
        this.mime_type = mime_type;
        this.name = "";
        this.comment = "";
        this.size = 0;
        this.length = 0;
        this.dateCreated  = System.currentTimeMillis();

    }

    public void update(Attachment attachment){
        this.filePath = attachment.getFilePath();
        this.mime_type = attachment.getMime_type();
        this.name = attachment.getName();
        this.comment = attachment.getComment();
        this.size = attachment.getSize();
        this.length = attachment.getLength();
        this.dateCreated = System.currentTimeMillis();
    }


    public Attachment(String ext){
        mime_type = ext;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
