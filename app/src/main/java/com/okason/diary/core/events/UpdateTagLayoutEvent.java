package com.okason.diary.core.events;

import com.okason.diary.models.ProntoTask;

import java.util.List;

/**
 * Created by valokafor on 7/2/17.
 */

public class UpdateTagLayoutEvent {
    private final List<ProntoTask> mTags;

    public UpdateTagLayoutEvent(List<ProntoTask> tags) {
        mTags = tags;
    }

    public List<ProntoTask> getTags() {
        return mTags;
    }
}
