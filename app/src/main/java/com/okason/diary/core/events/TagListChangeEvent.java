package com.okason.diary.core.events;

import com.okason.diary.models.Tag;

import java.util.List;

/**
 * Created by valokafor on 10/13/17.
 */

public class TagListChangeEvent {
    private final List<Tag> taglList;


    public TagListChangeEvent(List<Tag> taglList) {
        this.taglList = taglList;
    }

    public List<Tag> getTaglList() {
        return taglList;
    }
}
