package com.okason.diary.ui.notes;

import android.content.Context;

import com.okason.diary.models.viewModel.NoteViewModel;

import java.util.List;

/**
 * Created by Valentine on 4/15/2017.
 */

public interface NoteListContract {
    interface View{
        void onDataSetChanged();
        void showNotes(List<NoteViewModel> notes);
        void showEmptyText(boolean showText);
        void showDeleteConfirmation(NoteViewModel note);
        void setProgressIndicator(boolean active);
        Context getContext();
    }

    interface Actions{
        void loadNotes(boolean forceUpdate);
        void deleteNote(NoteViewModel note);

    }

    interface Repository{
        List<NoteViewModel> getAllNotes();
        void addNote(NoteViewModel note);
        void deleteNote(NoteViewModel note);
        void updateNote(NoteViewModel note);
    }
}
