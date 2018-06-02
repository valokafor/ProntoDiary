package com.okason.diary.core.listeners;

import com.okason.diary.models.ProntoTag;

/**
 * Created by valokafor on 6/20/17.
 */

public interface OnTagSelectedListener {
    void onTagChecked(ProntoTag selectedProntoTag);
    void onTagUnChecked(ProntoTag unSelectedProntoTag);
    void onAddTagButtonClicked();
    void onTagClicked(ProntoTag clickedProntoTag);
    void onEditTagButtonClicked(ProntoTag clickedProntoTag);
    void onDeleteTagButtonClicked(ProntoTag clickedProntoTag);
}
