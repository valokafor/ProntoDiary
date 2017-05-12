package com.okason.diary.core.events;

import com.okason.diary.models.Folder;

/**
 * Created by Valentine on 5/8/2017.
 */

public class FolderAddedEvent {
    private final Folder addedFolder;

    public FolderAddedEvent(Folder addedFolder) {
        this.addedFolder = addedFolder;
    }

    public Folder getAddedFolder() {
        return addedFolder;
    }
}
