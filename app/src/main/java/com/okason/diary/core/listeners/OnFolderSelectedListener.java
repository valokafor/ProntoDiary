package com.okason.diary.core.listeners;


import com.okason.diary.models.realmentities.Folder;

/**
 * Created by Valentine on 3/6/2016.
 */
public interface OnFolderSelectedListener {
    void onCategorySelected(Folder selectedCategory);
    void onEditCategoryButtonClicked(Folder selectedCategory);
    void onDeleteCategoryButtonClicked(Folder selectedCategory);
    void onAddCategoryButtonClicked();
}
