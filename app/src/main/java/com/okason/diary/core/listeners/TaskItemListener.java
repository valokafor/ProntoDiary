package com.okason.diary.core.listeners;

import com.okason.diary.models.realmentities.TaskEntity;

/**
 * Created by valokafor on 7/4/17.
 */

public interface TaskItemListener {
    void onEditTaskButtonClicked(TaskEntity clickedTask);
    void onDeleteTaskButtonClicked(TaskEntity clickedTask);
    void onAddSubTasksButtonClicked(TaskEntity clickedTask);
    void onTaskChecked(TaskEntity selectedTag);
    void onTaskUnChecked(TaskEntity unSelectedTag);
}
