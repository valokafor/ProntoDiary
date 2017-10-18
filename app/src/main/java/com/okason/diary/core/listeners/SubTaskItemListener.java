package com.okason.diary.core.listeners;

import com.okason.diary.models.SubTask;

/**
 * Created by valokafor on 7/4/17.
 */

public interface SubTaskItemListener {
    void onSubTaskChecked(String subTaskName);
    void onSubTaskUnChecked(String subTaskName);
    void onSubTaskDeleted(String subTaskName);
    void onEditSubTask(SubTask subTask);
}
