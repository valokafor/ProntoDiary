package com.okason.diary.core.events;

import com.okason.diary.models.realmentities.NoteEntity;

/**
 * Created by valokafor on 6/24/17.
 */

public class OnAttachmentAddedToNoteEvent {
    private final NoteEntity updatedJournal;
    private final String attachmentId;


    public OnAttachmentAddedToNoteEvent(NoteEntity updatedJournal, String attachmentId) {
        this.updatedJournal = updatedJournal;
        this.attachmentId = attachmentId;
    }

    public NoteEntity getUpdatedJournal() {
        return updatedJournal;
    }

    public String getAttachmentId() {
        return attachmentId;
    }
}
