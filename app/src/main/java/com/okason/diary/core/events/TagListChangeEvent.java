package com.okason.diary.core.events;

import com.okason.diary.models.realmentities.TagEntity;

import java.util.List;

/**
 * Created by valokafor on 10/13/17.
 */

public class TagListChangeEvent {
    private final List<TagEntity> taglList;


    public TagListChangeEvent(List<TagEntity> taglList) {
        this.taglList = taglList;
    }

    public List<TagEntity> getTaglList() {
        return taglList;
    }
}
