package com.okason.diary.core.listeners;


import com.okason.diary.models.realmentities.FolderEntity;

/**
 * Created by Valentine on 3/6/2016.
 */
public interface OnFolderSelectedListener {
    void onCategorySelected(FolderEntity selectedCategory);
    void onEditCategoryButtonClicked(FolderEntity selectedCategory);
    void onDeleteCategoryButtonClicked(FolderEntity selectedCategory);
    void onAddCategoryButtonClicked();
}
