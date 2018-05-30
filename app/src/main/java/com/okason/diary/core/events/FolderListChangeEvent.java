package com.okason.diary.core.events;

import com.okason.diary.models.Folder;

import java.util.List;

/**
 * Created by valokafor on 10/13/17.
 */

public class FolderListChangeEvent {
    private final List<Folder> folderlList;


    public FolderListChangeEvent(List<Folder> folderlList) {
        this.folderlList = folderlList;
    }

    public List<Folder> getFolderlList() {
        return folderlList;
    }
}
