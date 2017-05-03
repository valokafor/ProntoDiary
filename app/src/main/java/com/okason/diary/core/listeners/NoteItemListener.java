package com.okason.diary.core.listeners;


import com.okason.diary.models.viewModel.NoteViewModel;

/**
 * Created by Valentine on 3/12/2016.
 */
public interface NoteItemListener {

    void onNoteClick(NoteViewModel clickedNote);

    void onDeleteButtonClicked(NoteViewModel clickedNote);
}
