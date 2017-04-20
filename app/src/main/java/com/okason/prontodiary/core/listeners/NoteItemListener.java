package com.okason.prontodiary.core.listeners;


import com.okason.prontodiary.models.Note;

/**
 * Created by Valentine on 3/12/2016.
 */
public interface NoteItemListener {

    void onNoteClick(Note clickedNote);

    void onDeleteButtonClicked(Note clickedNote);
}
