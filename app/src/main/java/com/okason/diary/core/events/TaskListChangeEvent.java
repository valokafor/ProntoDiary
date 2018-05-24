package com.okason.diary.core.events;

import com.okason.diary.models.realmentities.TaskEntity;

import java.util.List;

/**
 * Created by valokafor on 10/13/17.
 */

public class TaskListChangeEvent {
    private final List<TaskEntity> tasklList;


    public TaskListChangeEvent(List<TaskEntity> tasklList) {
        this.tasklList = tasklList;
    }

    public List<TaskEntity> getTasklList() {
        return tasklList;
    }
}
