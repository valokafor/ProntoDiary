package com.okason.diary.core.events;

import com.okason.diary.models.Note;

/**
 * Created by Valentine on 5/13/2017.
 */

public class EditNoteButtonClickedEvent {
    private final Note clickedNote;

    public EditNoteButtonClickedEvent(Note clickedNote) {
        this.clickedNote = clickedNote;
    }

    public Note getClickedNote() {
        return clickedNote;
    }
}
