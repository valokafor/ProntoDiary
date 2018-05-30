package com.okason.diary.core.events;

import com.okason.diary.models.Task;

import java.util.List;

/**
 * Created by valokafor on 7/2/17.
 */

public class UpdateTagLayoutEvent {
    private final List<Task> mTags;

    public UpdateTagLayoutEvent(List<Task> tags) {
        mTags = tags;
    }

    public List<Task> getTags() {
        return mTags;
    }
}
