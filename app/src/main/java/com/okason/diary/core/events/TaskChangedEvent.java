package com.okason.diary.core.events;

import com.okason.diary.models.realmentities.TaskEntity;

/**
 * Created by valokafor on 10/17/17.
 */

public class TaskChangedEvent {
    private final TaskEntity changedTask;

    public TaskChangedEvent(TaskEntity changedTask) {
        this.changedTask = changedTask;
    }

    public TaskEntity getChangedTask() {
        return changedTask;
    }
}
