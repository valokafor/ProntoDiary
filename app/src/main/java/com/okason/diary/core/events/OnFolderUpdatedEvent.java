package com.okason.diary.core.events;

import com.okason.diary.models.Folder;

/**
 * Created by Valentine on 5/8/2017.
 */

public class OnFolderUpdatedEvent {
    private final Folder updatedFolder;

    public OnFolderUpdatedEvent(Folder updatedFolder) {
        this.updatedFolder = updatedFolder;
    }

    public Folder getAddedFolder() {
        return updatedFolder;
    }
}
