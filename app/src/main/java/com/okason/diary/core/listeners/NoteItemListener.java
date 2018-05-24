package com.okason.diary.core.listeners;


import com.okason.diary.models.realmentities.NoteEntity;

/**
 * Created by Valentine on 3/12/2016.
 */
public interface NoteItemListener {

    void onNoteClick(NoteEntity clickedJournal);

    void onDeleteButtonClicked(NoteEntity clickedJournal);

    void onAttachmentClicked(NoteEntity clickedJournal, int position);
}
