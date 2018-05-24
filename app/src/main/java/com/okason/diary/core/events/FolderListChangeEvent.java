package com.okason.diary.core.events;

import com.okason.diary.models.realmentities.FolderEntity;

import java.util.List;

/**
 * Created by valokafor on 10/13/17.
 */

public class FolderListChangeEvent {
    private final List<FolderEntity> folderlList;


    public FolderListChangeEvent(List<FolderEntity> folderlList) {
        this.folderlList = folderlList;
    }

    public List<FolderEntity> getFolderlList() {
        return folderlList;
    }
}
