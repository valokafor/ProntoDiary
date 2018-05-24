package com.okason.diary.core.listeners;

import com.okason.diary.models.realmentities.Task;

/**
 * Created by valokafor on 7/4/17.
 */

public interface TaskItemListener {
    void onEditTaskButtonClicked(Task clickedTask);
    void onDeleteTaskButtonClicked(Task clickedTask);
    void onAddSubTasksButtonClicked(Task clickedTask);
    void onTaskChecked(Task selectedTag);
    void onTaskUnChecked(Task unSelectedTag);
}
