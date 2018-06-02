package com.okason.diary.core.events;

import com.okason.diary.models.ProntoTask;

/**
 * Created by valokafor on 10/17/17.
 */

public class TaskChangedEvent {
    private final ProntoTask changedProntoTask;

    public TaskChangedEvent(ProntoTask changedProntoTask) {
        this.changedProntoTask = changedProntoTask;
    }

    public ProntoTask getChangedProntoTask() {
        return changedProntoTask;
    }
}
