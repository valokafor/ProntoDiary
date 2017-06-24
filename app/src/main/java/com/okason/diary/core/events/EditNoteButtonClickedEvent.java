package com.okason.diary.core.events;

/**
 * Created by Valentine on 5/13/2017.
 */

public class EditNoteButtonClickedEvent {
    private String noteId;

    public EditNoteButtonClickedEvent(String noteId) {
        this.noteId = noteId;
    }

    public String getClickedNoteId() {
        return noteId;
    }
}
