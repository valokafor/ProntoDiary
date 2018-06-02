package com.okason.diary.core.listeners;


import com.okason.diary.models.Journal;

/**
 * Created by Valentine on 3/12/2016.
 */
public interface NoteItemListener {

    void onNoteClick(Journal clickedJournal);

    void onDeleteButtonClicked(Journal clickedJournal);

    void onAttachmentClicked(Journal clickedJournal, int position);
}
