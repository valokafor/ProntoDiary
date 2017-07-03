package com.okason.diary.core.events;

import io.realm.SyncUser;

/**
 * Created by valokafor on 6/29/17.
 */

public class RealmDatabaseRegistrationCompletedEvent {
    private final SyncUser mSyncUser;
    private final boolean isInProgress;

    public RealmDatabaseRegistrationCompletedEvent(SyncUser syncUser, boolean isInProgress) {
        mSyncUser = syncUser;
        this.isInProgress = isInProgress;
    }

    public SyncUser getSyncUser() {
        return mSyncUser;
    }

    public boolean isInProgress() {
        return isInProgress;
    }
}
