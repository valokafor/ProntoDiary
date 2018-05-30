package com.okason.diary.core.events;

import com.okason.diary.models.Note;

/**
 * Created by valokafor on 6/24/17.
 */

public class OnAttachmentAddedToNoteEvent {
    private final Note updatedJournal;
    private final String attachmentId;


    public OnAttachmentAddedToNoteEvent(Note updatedJournal, String attachmentId) {
        this.updatedJournal = updatedJournal;
        this.attachmentId = attachmentId;
    }

    public Note getUpdatedJournal() {
        return updatedJournal;
    }

    public String getAttachmentId() {
        return attachmentId;
    }
}
