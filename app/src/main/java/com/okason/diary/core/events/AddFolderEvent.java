package com.okason.diary.core.events;

import com.okason.diary.models.Folder;

/**
 * Created by valokafor on 6/13/17.
 */

public class AddFolderEvent {
    private final Folder folder;

    public AddFolderEvent(Folder folder) {
        this.folder = folder;
    }

    public Folder getFolder() {
        return folder;
    }
}
