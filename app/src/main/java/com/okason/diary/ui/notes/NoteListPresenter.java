package com.okason.diary.ui.notes;

import com.okason.diary.data.NoteRealmRepository;
import com.okason.diary.models.Note;

import java.util.List;

/**
 * Created by Valentine on 4/15/2017.
 */

public class NoteListPresenter implements NoteListContract.Actions{
    private final NoteListContract.View mView;
    private  NoteListContract.Repository mRepository;
    private Note mCurrentNote = null;

    private boolean isDualScreen = false;

    public NoteListPresenter(NoteListContract.View mView) {
        this.mView = mView;
        this.mRepository = new NoteRealmRepository();
    }


    @Override
    public void loadNotes(boolean forceUpdate) {
        mView.setProgressIndicator(true);
        List<Note> notes = mRepository.getAllNotes();
        mView.setProgressIndicator(false);
        if (notes != null && notes.size() > 0){
            mView.showEmptyText(false);
            mView.showNotes(notes);
        }else {
            mView.showEmptyText(true);
        }

    }

    @Override
    public void deleteNote(Note note) {
        mRepository.deleteNote(note);
    }




}
