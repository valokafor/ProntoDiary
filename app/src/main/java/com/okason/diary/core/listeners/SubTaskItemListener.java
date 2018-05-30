package com.okason.diary.core.listeners;

import com.okason.diary.models.SubTask;

/**
 * Created by valokafor on 7/4/17.
 */

public interface SubTaskItemListener {
    void onSubTaskChecked(String subTaskId);
    void onSubTaskUnChecked(String subTaskId);
    void onSubTaskDeleted(String subTaskId);
    void onEditSubTask(SubTask subTask);
}
