package com.okason.diary.core.listeners;

import com.okason.diary.models.SubTask;

/**
 * Created by valokafor on 7/4/17.
 */

public interface SubTaskItemListener {
    void onSubTaskStatus(SubTask subTask, boolean checked);
    void onSubTaskDeleted(SubTask subTask);
    void onEditSubTask(SubTask subTask);
}
