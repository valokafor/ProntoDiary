package com.okason.diary.core.listeners;

import com.okason.diary.models.realmentities.TagEntity;

/**
 * Created by valokafor on 6/20/17.
 */

public interface OnTagSelectedListener {
    void onTagChecked(TagEntity selectedTag);
    void onTagUnChecked(TagEntity unSelectedTag);
    void onAddTagButtonClicked();
    void onTagClicked(TagEntity clickedTag);
    void onEditTagButtonClicked(TagEntity clickedTag);
    void onDeleteTagButtonClicked(TagEntity clickedTag);
}
