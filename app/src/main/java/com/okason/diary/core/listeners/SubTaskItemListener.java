package com.okason.diary.core.listeners;

import com.okason.diary.models.SubTask;

/**
 * Created by valokafor on 7/4/17.
 */

public interface SubTaskItemListener {
    void onSubTaskChecked(String taskId, String subTaskId);
    void onSubTaskUnChecked(String taskId, String subTaskId);
    void onSubTaskDeleted(String taskId, String subTaskId);
    void onEditSubTask(SubTask subTask);
}
