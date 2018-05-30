package com.okason.diary.core.listeners;


import com.okason.diary.models.Note;

/**
 * Created by Valentine on 3/12/2016.
 */
public interface NoteItemListener {

    void onNoteClick(Note clickedJournal);

    void onDeleteButtonClicked(Note clickedJournal);

    void onAttachmentClicked(Note clickedJournal, int position);
}
