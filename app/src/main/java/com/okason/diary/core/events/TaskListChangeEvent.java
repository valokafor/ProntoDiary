package com.okason.diary.core.events;

import com.okason.diary.models.Task;

import java.util.List;

/**
 * Created by valokafor on 10/13/17.
 */

public class TaskListChangeEvent {
    private final List<Task> tasklList;


    public TaskListChangeEvent(List<Task> tasklList) {
        this.tasklList = tasklList;
    }

    public List<Task> getTasklList() {
        return tasklList;
    }
}
