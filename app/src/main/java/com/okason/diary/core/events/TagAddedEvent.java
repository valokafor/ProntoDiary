package com.okason.diary.core.events;

import com.okason.diary.models.Tag;

/**
 * Created by Valentine on 5/8/2017.
 */

public class TagAddedEvent {
    private final Tag addedTag;

    public TagAddedEvent(Tag tag) {
        this.addedTag = tag;
    }

    public Tag getAddedTag() {
        return addedTag;
    }
}
