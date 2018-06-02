package com.okason.diary.core.listeners;

import com.okason.diary.models.ProntoTask;

/**
 * Created by valokafor on 7/4/17.
 */

public interface TaskItemListener {
    void onEditTaskButtonClicked(ProntoTask clickedProntoTask);
    void onDeleteTaskButtonClicked(ProntoTask clickedProntoTask);
    void onAddSubTasksButtonClicked(ProntoTask clickedProntoTask);
    void onTaskChecked(ProntoTask selectedTag);
    void onTaskUnChecked(ProntoTask unSelectedTag);
}
