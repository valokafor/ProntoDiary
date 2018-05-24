package com.okason.diary.core.listeners;

import com.okason.diary.models.realmentities.Tag;

/**
 * Created by valokafor on 6/20/17.
 */

public interface OnTagSelectedListener {
    void onTagChecked(Tag selectedTag);
    void onTagUnChecked(Tag unSelectedTag);
    void onAddTagButtonClicked();
    void onTagClicked(Tag clickedTag);
    void onEditTagButtonClicked(Tag clickedTag);
    void onDeleteTagButtonClicked(Tag clickedTag);
}
