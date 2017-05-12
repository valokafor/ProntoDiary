package com.okason.diary.core.events;

/**
 * Created by Valentine on 5/10/2017.
 */

public class ItemDeletedEvent {
    private final boolean shouldUpdatedList;

    public ItemDeletedEvent(boolean shouldUpdatedList) {
        this.shouldUpdatedList = shouldUpdatedList;
    }

    public boolean isShouldUpdatedList() {
        return shouldUpdatedList;
    }
}
