package com.okason.diary.core.events;

/**
 * Created by valokafor on 6/29/17.
 */

public class RealmDatabaseRegistrationCompletedEvent {

    private final boolean isInProgress;

    public RealmDatabaseRegistrationCompletedEvent(boolean isInProgress) {

        this.isInProgress = isInProgress;
    }


    public boolean isInProgress() {
        return isInProgress;
    }
}
