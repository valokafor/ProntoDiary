package com.okason.diary.core.events;

import com.okason.diary.models.Tag;

import java.util.List;

/**
 * Created by valokafor on 7/2/17.
 */

public class UpdateTagLayoutEvent {
    private final List<Tag> mTags;

    public UpdateTagLayoutEvent(List<Tag> tags) {
        mTags = tags;
    }

    public List<Tag> getTags() {
        return mTags;
    }
}
