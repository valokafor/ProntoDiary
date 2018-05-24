package com.okason.diary.core.events;

import com.okason.diary.models.realmentities.TaskEntity;

import java.util.List;

/**
 * Created by valokafor on 7/2/17.
 */

public class UpdateTagLayoutEvent {
    private final List<TaskEntity> mTags;

    public UpdateTagLayoutEvent(List<TaskEntity> tags) {
        mTags = tags;
    }

    public List<TaskEntity> getTags() {
        return mTags;
    }
}
