package com.okason.diary.core.events;

import com.okason.diary.models.ProntoTask;

import java.util.List;

/**
 * Created by valokafor on 10/13/17.
 */

public class TaskListChangeEvent {
    private final List<ProntoTask> tasklList;


    public TaskListChangeEvent(List<ProntoTask> tasklList) {
        this.tasklList = tasklList;
    }

    public List<ProntoTask> getTasklList() {
        return tasklList;
    }
}
