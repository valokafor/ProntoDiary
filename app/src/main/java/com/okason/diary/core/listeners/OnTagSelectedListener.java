package com.okason.diary.core.listeners;

import com.okason.diary.models.Tag;

/**
 * Created by valokafor on 6/20/17.
 */

public interface OnTagSelectedListener {
    void onTagSelected(Tag selectedTag);
    void onTagUnSelected(Tag unSelectedTag);
    void onAddCategoryButtonClicked();
}
