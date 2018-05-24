package com.okason.diary.models.realmentities;

/**
 * Created by valokafor on 6/8/17.
 */

public class StorageRecordEntity {
    private String id;
    private String uid;
    private String downloadUri;
    private long fileSizes;

    public StorageRecordEntity(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }

    public long getFileSizes() {
        return fileSizes;
    }

    public void setFileSizes(long fileSizes) {
        this.fileSizes = fileSizes;
    }
}
