package com.okason.diary.core.listeners;


import com.okason.diary.models.Note;

/**
 * Created by Valentine on 3/12/2016.
 */
public interface NoteItemListener {

    void onNoteClick(Note clickedNote);

    void onDeleteButtonClicked(Note clickedNote);
}
