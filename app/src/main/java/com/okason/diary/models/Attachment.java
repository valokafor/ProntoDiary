package com.okason.diary.models;

import android.net.Uri;

/**
 * Created by Valentine on 4/10/2017.
 */

public class Attachment{

    private String id;
    private String filePath;
    private String uri;
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

    public Attachment(Uri uri, String imagePath, String mime_type) {
        this.uri = uri.toString();
        this.filePath = imagePath;
        this.mime_type = mime_type;
        this.name = "";
        this.comment = "";
        this.size = 0;
        this.length = 0;
        this.dateCreated  = System.currentTimeMillis();

    }

    public Attachment(Uri uri, String imagePath, String mime_type, String name) {
        this.uri = uri.toString();
        this.filePath = imagePath;
        this.mime_type = mime_type;
        this.name = name;
        this.comment = "";
        this.size = 0;
        this.length = 0;
        this.dateCreated  = System.currentTimeMillis();

    }

    public void update(Attachment attachment){
        this.uri = attachment.getUri();
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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
