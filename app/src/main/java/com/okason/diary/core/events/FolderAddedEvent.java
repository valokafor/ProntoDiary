package com.okason.diary.core.events;

import com.okason.diary.models.Folder;

/**
 * Created by Valentine on 5/8/2017.
 */

public class FolderAddedEvent {
    private final Folder folder;

    public FolderAddedEvent(Folder folder) {
        this.folder = folder;
    }

    public Folder getFolder() {
        return folder;
    }
}
