package com.okason.diary.ui.notedetails;

import com.okason.diary.core.events.EditNoteButtonClickedEvent;
import com.okason.diary.data.NoteRealmRepository;
import com.okason.diary.models.Note;
import com.okason.diary.ui.notes.NoteListContract;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Valentine on 5/13/2017.
 */

public class NoteDetailPresenter implements NoteDetailContract.Action {
    private final NoteDetailContract.View mView;
    private final NoteListContract.Repository mRepository;
    private final String noteId;

    public NoteDetailPresenter(NoteDetailContract.View mView, String noteId) {
        this.mView = mView;
        this.noteId = noteId;
        mRepository = new NoteRealmRepository();
    }

    @Override
    public void onEditNoteClick() {
        EventBus.getDefault().post(new EditNoteButtonClickedEvent(mRepository.getNoteById(noteId)));
    }

    @Override
    public void showNoteDetails() {
        Note selectedNote = mRepository.getNoteById(noteId);
        mView.displayNote(selectedNote);

    }

    @Override
    public void onDeleteNoteButtonClicked() {
        mView.showDeleteConfirmation(mRepository.getNoteById(noteId));

    }

    @Override
    public void deleteNote() {
        mRepository.deleteNote(noteId);
    }
}
