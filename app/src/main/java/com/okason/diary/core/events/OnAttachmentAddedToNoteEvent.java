package com.okason.diary.core.events;

import com.okason.diary.models.Note;

/**
 * Created by valokafor on 6/24/17.
 */

public class OnAttachmentAddedToNoteEvent {
    private final Note updatedNote;
    private final String attachmentId;


    public OnAttachmentAddedToNoteEvent(Note updatedNote, String attachmentId) {
        this.updatedNote = updatedNote;
        this.attachmentId = attachmentId;
    }

    public Note getUpdatedNote() {
        return updatedNote;
    }

    public String getAttachmentId() {
        return attachmentId;
    }
}
