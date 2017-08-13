package com.okason.diary.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.services.DownloadFileFromFireaseIntentService;
import com.okason.diary.utils.Constants;

import java.io.File;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valentine on 4/10/2017.
 */

public class Attachment extends RealmObject{


    @PrimaryKey
    private String id;
    private String localFilePath;
    private String cloudFilePath;
    private String uri;
    private String name;
    private String comment;
    private long size;
    private long length;
    private String mime_type;
    private long dateCreated;


    public Attachment(){
        localFilePath = "";
        cloudFilePath = "";
        name = "";
        comment = "";
        size = 0;
        length = 0;
        mime_type = "";
        dateCreated = System.currentTimeMillis();

    }

    public Attachment(Uri uri, String imagePath, String mime_type) {
        this.uri = uri.toString();
        this.localFilePath = imagePath;
        this.mime_type = mime_type;
        this.name = "";
        this.comment = "";
        this.size = 0;
        this.length = 0;
        this.dateCreated  = System.currentTimeMillis();

    }

    public Attachment(Uri uri, String imagePath, String mime_type, String name) {
        this.uri = uri.toString();
        this.localFilePath = imagePath;
        this.mime_type = mime_type;
        this.name = name;
        this.comment = "";
        this.size = 0;
        this.length = 0;
        this.dateCreated  = System.currentTimeMillis();

    }

    public void update(Attachment attachment){
        this.uri = attachment.getUri();
        this.localFilePath = attachment.getLocalFilePath();
        this.mime_type = attachment.getMime_type();
        this.name = attachment.getName();
        this.comment = attachment.getComment();
        this.size = attachment.getSize();
        this.length = attachment.getLength();
        this.dateCreated = System.currentTimeMillis();
    }

    public String getFilePath(){
        String filePath;
        File file = new File(this.getLocalFilePath());
        if (file.exists()){
            filePath = this.getLocalFilePath();
        }else {
            filePath = this.getCloudFilePath();
            //If Attachment exists in the cloud, kick off an Intent Service that
            //Downloads that file from the cloud and save it locally
            if (!TextUtils.isEmpty(filePath)){
                Context appContext =  ProntoDiaryApplication.getAppContext();
                Intent downloadIntent = new Intent(appContext,
                        DownloadFileFromFireaseIntentService.class);
                downloadIntent.putExtra(Constants.ATTACHMENT_ID, id);
                appContext.startService(downloadIntent);

            }
        }
        return filePath;
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

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
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

    public String getCloudFilePath() {
        return cloudFilePath;
    }

    public void setCloudFilePath(String cloudFilePath) {
        this.cloudFilePath = cloudFilePath;
    }
}
