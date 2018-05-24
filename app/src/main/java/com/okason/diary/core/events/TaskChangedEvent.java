package com.okason.diary.core.events;

import com.okason.diary.models.realmentities.Task;

/**
 * Created by valokafor on 10/17/17.
 */

public class TaskChangedEvent {
    private final Task changedTask;

    public TaskChangedEvent(Task changedTask) {
        this.changedTask = changedTask;
    }

    public Task getChangedTask() {
        return changedTask;
    }
}
