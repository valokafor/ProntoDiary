package com.okason.diary.core.events;

/**
 * Created by Valentine on 5/8/2017.
 */

public class FolderAddedEvent {
    private final String folderId;

    public FolderAddedEvent(String folderId) {
        this.folderId = folderId;
    }

    public String getAddedFolderId() {
        return folderId;
    }
}
