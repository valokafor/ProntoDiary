package com.okason.diary.ui.notedetails;

import com.okason.diary.models.Note;

/**
 * Created by Valentine on 5/13/2017.
 */

public interface NoteDetailContract {
    interface View{
        void displayNote(Note note);
        void showDeleteConfirmation(Note note);
        void displayPreviousActivity();
        void showMessage(String message);
    }

    interface Action{
        void onEditNoteClick();
        //void showNoteDetails(Note note);
        void onDeleteNoteButtonClicked();
        void deleteNote();
        String getCurrentNoteId();
        Note getCurrentNote();
    }
}
