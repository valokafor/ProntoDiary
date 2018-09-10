package com.okason.diary.core.listeners;


import com.okason.diary.models.Folder;

/**
 * Created by Valentine on 3/6/2016.
 */
public interface OnFolderSelectedListener {
    void onFolderSelected(Folder selectedFolder);
    void onEditCategoryButtonClicked(Folder selectedFolder);
    void onDeleteFolderButtonClicked(Folder selectedFolder);
    void onAddFolderButtonClicked();
}
