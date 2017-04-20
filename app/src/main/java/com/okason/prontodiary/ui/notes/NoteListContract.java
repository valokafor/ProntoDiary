package com.okason.prontodiary.ui.notes;

/**
 * Created by Valentine on 4/15/2017.
 */

public interface NoteListContract {
    interface View{
        void onDataSetChanged();
    }

    interface Actions{
        void loadNotes();

    }

    interface Repository{

    }
}
