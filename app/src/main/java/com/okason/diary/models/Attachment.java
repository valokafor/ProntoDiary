package com.okason.diary.models;

import io.realm.RealmObject;

/**
 * Created by Valentine on 4/10/2017.
 */

public class Attachment extends RealmObject {

    private long id;
    private String uriLocalPath;
    private String uriCloudPath;
    private String name;
    private long size;
    private long length;
    private String mime_type;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUriLocalPath() {
        return uriLocalPath;
    }

    public void setUriLocalPath(String uriLocalPath) {
        this.uriLocalPath = uriLocalPath;
    }

    public String getUriCloudPath() {
        return uriCloudPath;
    }

    public void setUriCloudPath(String uriCloudPath) {
        this.uriCloudPath = uriCloudPath;
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
}
