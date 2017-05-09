package com.okason.diary.ui.addnote;

/**
 * Created by Valentine on 5/8/2017.
 */

public interface AddNoteContract {

    interface View{
        void populateJournal();
    }

    interface Action{
        void deleteJournal();
    }
}
