package com.okason.diary.core.listeners;


import com.okason.diary.models.viewModel.FolderViewModel;

/**
 * Created by Valentine on 3/6/2016.
 */
public interface OnFolderSelectedListener {
    void onCategorySelected(FolderViewModel selectedCategory);
    void onEditCategoryButtonClicked(FolderViewModel selectedCategory);
    void onDeleteCategoryButtonClicked(FolderViewModel selectedCategory);
    void onAddCategoryButtonClicked();
}
