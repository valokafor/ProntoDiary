package com.okason.diary.core.events;

import com.okason.diary.models.Journal;

/**
 * Created by valokafor on 6/24/17.
 */

public class OnAttachmentAddedToNoteEvent {
    private final Journal updatedJournal;
    private final String attachmentId;


    public OnAttachmentAddedToNoteEvent(Journal updatedJournal, String attachmentId) {
        this.updatedJournal = updatedJournal;
        this.attachmentId = attachmentId;
    }

    public Journal getUpdatedJournal() {
        return updatedJournal;
    }

    public String getAttachmentId() {
        return attachmentId;
    }
}
