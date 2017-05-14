package com.okason.diary.core.events;

/**
 * Created by Valentine on 5/10/2017.
 */

public class ItemDeletedEvent {
    private final String result;

    public ItemDeletedEvent(String deleteResult) {
        this.result = deleteResult;
    }

    public String getResult() {
        return result;
    }
}
