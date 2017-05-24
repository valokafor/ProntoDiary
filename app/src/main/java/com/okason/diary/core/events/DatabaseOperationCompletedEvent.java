package com.okason.diary.core.events;

/**
 * Created by Valentine on 5/21/2017.
 */

public class DatabaseOperationCompletedEvent {
    private final boolean shouldUpdateUi;
    private final String message;

    public DatabaseOperationCompletedEvent(boolean shouldUpdateUi, String message) {
        this.shouldUpdateUi = shouldUpdateUi;
        this.message = message;
    }

    public boolean isShouldUpdateUi() {
        return shouldUpdateUi;
    }

    public String getMessage() {
        return message;
    }
}
