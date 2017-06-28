package com.okason.diary.core.events;

import com.okason.diary.models.Note;

/**
 * Created by valokafor on 6/24/17.
 */

public class OnAttachmentAddedToNoteEvent {
    private final Note updatedNote;

    public OnAttachmentAddedToNoteEvent(Note updatedNote) {
        this.updatedNote = updatedNote;
    }

    public Note getUpdatedNote() {
        return updatedNote;
    }
}
