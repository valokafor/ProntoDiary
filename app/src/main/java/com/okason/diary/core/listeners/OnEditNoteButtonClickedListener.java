package com.okason.diary.core.listeners;


import com.okason.diary.models.realmentities.NoteEntity;

/**
 * Created by Valentine on 2/6/2017.
 */

public interface OnEditNoteButtonClickedListener {
    void onEditNote(NoteEntity clickedJournal);
}
