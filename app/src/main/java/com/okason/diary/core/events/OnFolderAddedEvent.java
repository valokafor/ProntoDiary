package com.okason.diary.core.events;

import com.okason.diary.models.viewModel.FolderViewModel;

/**
 * Created by Valentine on 5/8/2017.
 */

public class OnFolderAddedEvent {
    private final FolderViewModel addedFolder;

    public OnFolderAddedEvent(FolderViewModel addedFolder) {
        this.addedFolder = addedFolder;
    }

    public FolderViewModel getAddedFolder() {
        return addedFolder;
    }
}
