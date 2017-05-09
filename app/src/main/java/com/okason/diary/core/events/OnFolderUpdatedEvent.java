package com.okason.diary.core.events;

import com.okason.diary.models.viewModel.FolderViewModel;

/**
 * Created by Valentine on 5/8/2017.
 */

public class OnFolderUpdatedEvent {
    private final FolderViewModel updatedFolder;

    public OnFolderUpdatedEvent(FolderViewModel updatedFolder) {
        this.updatedFolder = updatedFolder;
    }

    public FolderViewModel getAddedFolder() {
        return updatedFolder;
    }
}
